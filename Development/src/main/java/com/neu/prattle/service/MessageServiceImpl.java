package com.neu.prattle.service;

import com.neu.prattle.exceptions.NoSuchMessageException;
import com.neu.prattle.hibernate.util.HibernateUtil;
import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import org.hibernate.Transaction;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MessageService}
 * <p>
 * Class that contains methods that performs CRUD operation on the Messages in the database.
 */
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = Logger.getLogger(MessageServiceImpl.class.getName());

    /***
     * MessageServiceImpl is a Singleton class.
     */
    private MessageServiceImpl() {

    }

    private static MessageService messageService;
    private UserService userService = UserServiceImpl.getInstance();
    private GroupService groupService = GroupServiceImpl.getInstance();

    static {
        messageService = new MessageServiceImpl();
    }

    /**
     * Returns an instance of the MessageService interface.
     *
     * @return an instance of the MessageService interface
     */
    public static MessageService getInstance() {
        return messageService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addMessage(Message message) {
        Transaction transaction = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        // start a transaction
        transaction = session.beginTransaction();

        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        if (message.getContent() == null) {
            throw new IllegalArgumentException("Message content cannot be null");
        }

        if (message.getFrom() == null) {
            throw new IllegalArgumentException("Message from user cannot be null");
        }
        // save the student objects
        message.setDateTime();
        saveReplies(session, message);
        if (checkIfMessageExists(message)) {
            transaction.commit();
            updateMessageById(message.getId(), message);
        } else {
            session.save(message);
            transaction.commit();
        }
        session.close();
    }

    private void saveReplies(Session session, Message message) {
        for (Message reply : message.getReplies()) {
            if (!checkIfMessageExists(reply)) {
                reply.setDateTime();
                session.save(reply);
            }
        }
    }

    private boolean checkIfMessageExists(Message message) {
        try {
            getMessageById(message.getId());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getAllMessages() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<Message> result = session.createQuery("SELECT a FROM Message a", Message.class).getResultList();
            session.close();
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        finally {
            if (session != null)
                session.close();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Message> getAllMessagesOfAUser(String username, String startDate, String endDate) {
        Session session = null;
        if(startDate==null && endDate==null){
            throw new IllegalArgumentException("Either Start date or End date must be mentioned");
        }
        try{
             session = HibernateUtil.getSessionFactory().openSession();
            User user = userService.getUserByUsername(username);
            Query<Message> query = session.createQuery("SELECT m from Message m where m.fromUser = :user OR m.toUser = :user");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.setParameter("user",user);
            List<Message> list = query.list();
            List<Message> messages = getMessagesWithOnlyStartDate(list,startDate,dateFormat);
            if(endDate==null){
                session.close();
                return messages;
            }
            else{
                List<Message> newMessageList = getMessagesWithOnlyEndDate(messages,endDate,dateFormat);
                session.close();
                return newMessageList;
            }
        }
        catch (Exception e){
            LOGGER.log(Level.INFO, e.getMessage());
        }
        finally {
            if(session!=null){
                session.close();
            }
        }

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message getMessageById(int id) {
        Session session = null;

            session = HibernateUtil.getSessionFactory().openSession();
            Query<Message> query = session.createQuery("SELECT m from Message m where m.id = :id");
            query.setParameter("id", id);
            List<Message> list = query.list();
            if (!list.isEmpty()) {
                session.close();
                return list.get(0);
            }
            else {
                session.close();
                throw new NoSuchMessageException("Message with id " + Integer.toString(id) + " not present in database.");
            }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int updateMessageById(int id, Message message) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            if (message == null) {
                throw new IllegalArgumentException("Message cannot be null");
            }
            if (message.getFrom() == null) {
                throw new IllegalArgumentException("Message from user cannot be null");
            }
            if (message.getContent() == null) {
                throw new IllegalArgumentException("Message content cannot be null");
            }

            Message message1 = session.get(Message.class, id);

            if (message.getDateTime() != null) {
                message1.setDateTime(message.getDateTime());
            } else {
                message1.setDateTime();
            }

            message1.setContent(message.getContent());
            message1.setTo(message.getTo());
            message1.setFrom(message.getFrom());
            message1.setReplies(message.getReplies());
            session.update(message1);
            transaction.commit();
            return 1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        finally {
            if  (session != null){
                session.close();
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteMessageById(int id) {
        Session session;
        session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Message message = getMessageById(id);
        List<Message> replies = message.getReplies();
        message.setReplies(new ArrayList<>());

        removeMessage(session, id);
        for(Message reply: replies){
            removeMessage(session, reply.getId());
        }


        transaction.commit();
        session.close();

    }

    @Override
    public List<Message> getMessagesExchanged(String username1, String username2) {
        User user1 = userService.getUserByUsername(username1);
        User user2 = userService.getUserByUsername(username2);
        List <Integer> replyIds = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        extractMessages(user1, user2, replyIds, messages);

        removeDuplicateReplies(replyIds, messages);

        Collections.sort(messages);
        return messages;
    }

    @Override
    public List<Message> getMessagesExchangedInGroup(String groupName) {
        GroupMessage group = groupService.getGroupByName(groupName);
        List <Integer> replyIds = new ArrayList<>();
        List<Message> messages = new ArrayList<>();

        extractMessagesToGroup(group, replyIds, messages);

        removeDuplicateReplies(replyIds, messages);

        Collections.sort(messages);
        return messages;
    }



    private void removeDuplicateReplies(List<Integer> replyIds, List<Message> messages) {
        List<Message> duplicateMessages = new ArrayList<>();
        for(Message msg: messages){
            if (replyIds.contains(msg.getId())){
                duplicateMessages.add(msg);
            }
        }
        for(Message msg: duplicateMessages){
            messages.remove(msg);
        }
    }

    private void extractMessages(User user1, User user2, List<Integer> replyIds, List<Message> messages) {
        for (Message m : user1.getToMessage()) {
            if (m.getFrom().equals(user2)) {
                messages.add(m);
                for (Message reply : m.getReplies()) {
                    replyIds.add(reply.getId());
                }
                Collections.sort(m.getReplies());
            }
        }
        for (Message m : user2.getToMessage()) {
            if (m.getFrom().equals(user1)) {
                messages.add(m);
                for (Message reply : m.getReplies()) {
                    replyIds.add(reply.getId());
                }
                Collections.sort(m.getReplies());
            }
        }
    }

    private void extractMessagesToGroup(GroupMessage group, List<Integer> replyIds, List<Message> messages) {

        Session session = null;
        List<Message> messagesToGroup;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Message> query = session.createQuery("SELECT m from Message m where m.chatGroup = :group");
            query.setParameter("group", group);
            messagesToGroup = query.list();
        }
        finally {
            if(session != null){
                session.close();
            }
        }


        for (Message m : messagesToGroup) {
            messages.add(m);
            for (Message reply : m.getReplies()) {
                replyIds.add(reply.getId());
            }
            Collections.sort(m.getReplies());

        }
    }

    private void removeMessage(Session session, int id) {
        String hql = "DELETE FROM Message m where m.id = :id";
        Query<Message> query = session.createQuery(hql);
        query.setParameter("id", id);
        int res = query.executeUpdate();
        if (res < 1) {
            throw new NoSuchMessageException("Message with id " + Integer.toString(id) + " not present in database.");
        }
    }

    private List<Message> getMessagesWithOnlyStartDate( List<Message> list, String startDate, DateFormat dateFormat){
      return  list.stream().filter(message -> {
            try {
                return dateFormat.parse(startDate).before(dateFormat.parse(message.getDateTime()));
            } catch (ParseException e) {
                LOGGER.log(Level.INFO, e.getMessage());
            }
            return false;
        }).collect(Collectors.toList());
    }

    private List<Message> getMessagesWithOnlyEndDate(List<Message> list, String endDate, DateFormat dateFormat){
       return list.stream().filter(message -> {
            try {
                return dateFormat.parse(message.getDateTime()).before(dateFormat.parse(endDate));
            } catch (ParseException e) {
                LOGGER.log(Level.INFO, e.getMessage());
            }
            return false;
        }).collect(Collectors.toList());
    }
}
