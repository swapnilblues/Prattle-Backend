package com.neu.prattle.service;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.hibernate.util.HibernateUtil;
import com.neu.prattle.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Implementation of {@link UserService}
 *
 * It stores the user accounts in-memory, which means any user accounts
 * created will be deleted once the application has been restarted.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());



    /***
     * UserServiceImpl is a Singleton class.
     */
    private UserServiceImpl() {

    }

    private static UserService accountService;

    static {
        accountService = new UserServiceImpl();
    }

    /**
     * Call this method to return an instance of this service.
     *
     * @return this
     */
    public static UserService getInstance() {
        return accountService;
    }


    private Session session = null;


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addUser(User user) {
        Transaction transaction = null;

        try {
            accountService.getUserByUsername(user.getUsername());
        }
        catch (IllegalArgumentException ex) {

            if (user.getEmail() == null || user.getUsername() == null || user.getPassword() == null) {
                throw new IllegalArgumentException("User must have email. username and password");
            }

            try {


                if (!user.getEmail().contains("@") || !user.getEmail().contains(".com")) {
                    throw new IllegalStateException("The email is not valid");
                }

                session = HibernateUtil.getSessionFactory().openSession();
                // start a transaction
                transaction = session.beginTransaction();
                // save the student objects

                user.setPassword(user.getPassword());

                session.save(user);

                // commit transaction
                transaction.commit();
            }  finally {
                session.close();

            }
            return;
        }
        throw new UserAlreadyPresentException("User Already present");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAllUsers() {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            return session.createQuery("SELECT a FROM User a", User.class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateUserById(int id, User user) {
        Transaction transaction = null;
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            User user1 = session.get(User.class, id);

            if (user1 == null) {
                throw new IllegalArgumentException("User with userId: " + id + " doesn't exist");
            }
            if (user.getEmail() != null) {
                user1.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                user1.setName(user.getName());
            }
            if (user.getUsername() != null) {
                user1.setUsername(user.getUsername());
            }
            if (user.getPassword() != null) {
                user1.setPassword(user.getPassword());
            }
            if (user.getStatus() != null) {
                user1.setStatus(user.getStatus());
            }
            if (user.getImage() != null) {
                user1.setImage(user.getImage());
            }
            user1.setFind(user.isFind());
            user1.setFollowers(user.getFollowers());
            user1.setFollowings(user.getFollowings());
            
            session.update(user1);
            transaction.commit();
            return 1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        Session session1 = null;
        List<User> list;
        try {
            session1 = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session1.createQuery("SELECT u from User u where u.username = :username");
            query.setParameter("username", userName);
            list = query.list();
        }
        finally {
            if (session1 != null){
                session1.close();
            }
        }
        if (!list.isEmpty()){
            return list.get(0);
        }
        else {
            throw new IllegalArgumentException("User does not exist");
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteUserById(int userId) {
        Transaction transaction = null;
        try {
            if (userId < 1)
                throw new IllegalArgumentException("User id cannot be less than 1");
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            String hql = "DELETE FROM User where id=:userId";
            Query<User> query = session.createQuery(hql);
            query.setParameter("userId", userId);
            int res = query.executeUpdate();
            transaction.commit();
            return res;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUserIdByUserName(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session.createQuery("SELECT u from User u where u.username = :username");
            query.setParameter("username", userName);
            List<User> list = query.list();
            if (!list.isEmpty())
                return list.get(0).getId();
            else
                return -1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return -1;
    }

    @Override
    public void addFollower(User user, User follower) {
        user.addFollowers(follower);
        updateUserById(user.getId(),user);
    }

    @Override
    public void addFollowing(User user, User following) {
        user.addFollowings(following);
        updateUserById(user.getId(),user);
    }

    @Override
    public void removeFollower(User user, User follower) {
        user.removeFollowers(follower);
        updateUserById(user.getId(),user);
    }

    @Override
    public void removeFollowing(User user, User following) {
        user.removeFollowings(following);
        updateUserById(user.getId(),user);
    }

    @Override
    public String getInformation(User user, User follower) {

        if(!(user.getFollowers().contains(follower.getUsername())
                && follower.getFollowings().contains(user.getUsername()))
        )
            throw new IllegalArgumentException("Follower user is not a follower of current user");

        return user.toString();
    }

    @Override
    public List<User> seeAllOtherUsers(User currentUser) {

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<User> users = session.createQuery("SELECT a FROM User a", User.class).getResultList();
            List<User> newUsers = new ArrayList<>();
            for(User user : users) {
                if(user.isFind()
                    || user.getFollowers().contains(currentUser.getUsername())
                        && currentUser.getFollowings().contains(user.getUsername())
                )
                    newUsers.add(user);
            }
            session.close();
            return newUsers;
        } catch (Exception e) {
            session.close();
            LOGGER.log(Level.INFO, e.getMessage());
        }
        return new ArrayList<>();
    }
}
