package com.neu.prattle.service;

import com.neu.prattle.hibernate.util.HibernateUtil;
import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupServiceImpl.class.getName());

    private GroupServiceImpl() {

    }

    private static GroupServiceImpl groupService;

    static {
        groupService = new GroupServiceImpl();
    }

    public static GroupService getInstance() {

        return groupService;
    }

    private Session session = null;

    @Override
    public void createGroup(GroupMessage group) {
        Transaction transaction;

        checkForGroup(group);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            // start a transaction
            transaction = session.beginTransaction();

            // save the student objects
            session.save(group);

            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        } finally {
            session.close();
        }


    }

    @Override
    public int updateGroupById(int id, GroupMessage group, User admin) {

        checkForNullAndAdmin(group, admin);

        Transaction transaction;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.update(group);
            transaction.commit();
            return 1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
            return -1;
        }
        finally {
            session.close();
        }
    }

    @Override
    public int addMemberToGroup(GroupMessage group, User user, User admin) {

        checkForNullAndAdmin(group, user, admin);

        if (user.equals(admin))
            throw new IllegalArgumentException("Cannot delete admin from group");

        group.addUsers(user);

        return groupService.updateGroupById(group.getId(), group, admin);
    }

    @Override
    public int removeMemberFromGroup(GroupMessage group, User user, User admin) {

        checkForNullAndAdmin(group, user, admin);

        if (user.equals(admin))
            throw new IllegalArgumentException("Cannot delete admin from group");

        group.removeUsers(user);

        return groupService.updateGroupById(group.getId(), group, admin);
    }

    @Override
    public void deleteGroupByAdmin(GroupMessage group, User admin) {

        checkForNullAndAdmin(group, admin);
        Transaction transaction;

        int id = group.getId();

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();
        String hql = "DELETE FROM GroupMessage g where g.id = :id";
        Query<Message> query = session.createQuery(hql);
        query.setParameter("id", id);
        int res = query.executeUpdate();
        if (res < 1) {
            session.close();
            throw new IllegalArgumentException("Group not present");
        }
        transaction.commit();
        session.close();
    }

    @Override
    public boolean isAdmin(GroupMessage group, User admin) {
        return group.getAdmin().equals(admin);
    }

    @Override
    public GroupMessage getGroupById(int id) {
        if (id < 1)
            throw new IllegalArgumentException("Id cannot be less than one");

        session = null;

        session = HibernateUtil.getSessionFactory().openSession();
        Query<GroupMessage> query = session.createQuery("SELECT m from GroupMessage m where m.id = :id");
        query.setParameter("id", id);
        List<GroupMessage> list = query.list();
        if (!list.isEmpty()) {
            session.close();
            return list.get(0);
        } else {
            session.close();
            throw new IllegalArgumentException("Group with id " + Integer.toString(id) + " not present in database.");
        }
    }

    @Override
    public GroupMessage getGroupByName(String name) {
        List<GroupMessage> list;
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<GroupMessage> query = session.createQuery("SELECT m from GroupMessage m where m.name = :name");
            query.setParameter("name", name);
            list = query.list();
        } finally {
            session.close();
        }

        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("Group not found");
        }
    }

    private void checkForNullAndAdmin(GroupMessage group, User user, User admin) {

        if (admin == null)
            throw new IllegalArgumentException("Admin cannot be null");

        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        checkForGroup(group);

        if (!isAdmin(group, admin))
            throw new IllegalArgumentException("Admin entered is not the admin of the group");
    }

    private void checkForNullAndAdmin(GroupMessage group, User admin) {

        if (admin == null)
            throw new IllegalArgumentException("Admin cannot be null");

        checkForGroup(group);

        if (!isAdmin(group, admin))
            throw new IllegalArgumentException("Admin entered is not the admin of the group");
    }

    private void checkForGroup(GroupMessage group) {

        if (group == null)
            throw new IllegalArgumentException("Group cannot be null");

        if (group.getAdmin() == null)
            throw new IllegalArgumentException("Admin of group cannot be null");

        if (group.getName() == null)
            throw new IllegalArgumentException("Name of group cannot be null");

    }

    @Override
    public List<GroupMessage> seeAllGroups(User user) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<GroupMessage> groups = session.createQuery("SELECT a FROM GroupMessage a", GroupMessage.class).getResultList();
            List<GroupMessage> newGroups = new ArrayList<>();
            for (GroupMessage group : groups) {
                if (group.isFind()
                        || group.getMembers().contains(user)
                )
                    newGroups.add(group);
            }
            session.close();
            return newGroups;
        } catch (Exception e) {
            session.close();
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return new ArrayList<>();
    }

}


