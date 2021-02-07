package com.neu.prattle.websocket;

/**
 * A simple chat client based on websockets.
 * 
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;

/**
 * The Class ChatEndpoint.
 * 
 * This class handles Messages that arrive on the server.
 */
@ServerEndpoint(value = "/chat/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class.getName());
    /** The User Service for user operations. */
    private UserService userService = UserServiceImpl.getInstance();
    /** The Message Service for user operations. */
    private MessageService messageService = MessageServiceImpl.getInstance();

    private GroupService groupService = GroupServiceImpl.getInstance();
    
    /** The session. */
    private Session session;
    
    /** The Constant chatEndpoints. */
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    
    /** The users. */
    private static HashMap<String, User> users = new HashMap<>();

    /**
	 * On open.
	 * 
	 * Handles opening a new session (websocket connection). If the user is a known
	 * user (user management), the session added to the pool of sessions and an
	 * announcement to that pool is made informing them of the new user.
	 * 
	 * If the user is not known, the pool is not augmented and an error is sent to
	 * the originator.
	 *
	 * @param session  the web-socket (the connection)
	 * @param username the name of the user (String) used to find the associated
	 *                 UserService object
	 * @throws IOException     Signals that an I/O exception has occurred.
	 * @throws EncodeException the encode exception
	 */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        User user;
        try {
            user = userService.getUserByUsername(username);
        }
        catch (Exception e){
            Message error = Message.messageBuilder()
                    .setMessageContent(String.format("User %s could not be found", username))
                    .build();
            session.getBasicRemote().sendObject(error);
            return;
        }

        user.setPassword("");
        addEndpoint(session, user);
        Message message = createConnectedMessage(user);
        broadcast(message);
    }

    /**
     * Creates a Message that some user is now connected - that is, a Session was opened
     * successfully.
     *
     * @param user the user who connected
     * @return Message
     */
    private Message createConnectedMessage(User user) {
        return Message.messageBuilder()
                .setFrom(user)
                .setMessageContent("Connected!")
                .build();
    }

    /**
     * Adds a newly opened session to the pool of sessions.
     *
     * @param session    the newly opened session
     * @param user   the user who connected
     */
    private void addEndpoint(Session session, User user) {
        this.session = session;
        chatEndpoints.add(this);
        /* users is a hashmap between session ids and users */
        users.put(session.getId(), user);
    }

    /**
     * On message.   
     * 
     * When a message arrives, broadcast it to all connected users.
     *
     * @param session the session originating the message
     * @param message the text of the inbound message
     */
    @OnMessage
    public void onMessage(Session session, Message message) {
        message.setFrom(users.get(session.getId()));
        if (message.isContainsGroup()){
            if (authenticateGroup(message)) {
                sendMessageToGroup(message);
            }
            else{
                StringBuilder sb = new StringBuilder().append("Enter the correct password for Group ").append(message.getChatGroup().getName());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        else {
            sendMessageToUser(message);
        }
    }

    private boolean authenticateGroup(Message message) {
        String enteredPassword = message.getChatGroup().getPassword();
        GroupMessage persistedGroup = getGroup(message.getChatGroup());
        if(persistedGroup.getPassword() == null){
            return true;
        }
        return persistedGroup.getPassword().equals(enteredPassword);
    }

    private void sendMessageToGroup(Message message) {
        message.setChatGroup(getGroup(message.getChatGroup()));
        for(Message reply: message.getReplies()){
            reply.setChatGroup(getGroup(message.getChatGroup()));
            reply.setFrom(getUser(reply.getFrom()));
        }
        messageService.addMessage(message);
        broadcastToGroup(message);
    }

    private void sendMessageToUser(Message message) {
        message.setTo(getUser(message.getTo()));
        for(Message reply: message.getReplies()){
            reply.setTo(getUser(reply.getTo()));
            reply.setFrom(getUser(reply.getFrom()));
        }
        messageService.addMessage(message);
        broadcast(message);
    }

    private User getUser(User user){
        if (user == null){
            return null;
        }
        User persistedUser = userService.getUserByUsername(user.getUsername());
        persistedUser.setPassword("");
        return persistedUser;
    }

    private GroupMessage getGroup(GroupMessage group){
        GroupMessage persistedGroup = groupService.getGroupByName(group.getName());
        for(User user: persistedGroup.getMembers()){
            user.setPassword("");
            user.setToMessage(new ArrayList<>());
            user.setFromMessage(new ArrayList<>());
        }
        persistedGroup.getAdmin().setPassword("");
        persistedGroup.getAdmin().setToMessage(new ArrayList<>());
        persistedGroup.getAdmin().setFromMessage(new ArrayList<>());
        return persistedGroup;
    }

    /**
     * On close.  
     * 
     * Closes the session by removing it from the pool of sessions and 
     * broadcasting the news to everyone else.
     *
     * @param session the session
     */
    @OnClose
    public void onClose(Session session) {
        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    /**
     * On error.
     *
     * Handles situations when an error occurs.  Not implemented.
     * 
     * @param session the session with the problem
     * @param throwable the action to be taken.
     */
    @OnError
    public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
        // Do error handling here
        Message error = Message.messageBuilder()
                .setMessageContent(throwable.toString())
                .build();
        session.getBasicRemote().sendObject(error);
    }

    /**
     * Broadcast.
     * 
     * Send a Message to each session in the pool of sessions.
     * The Message sending action is synchronized.  That is, if another
     * Message tries to be sent at the same time to the same endpoint,
     * it is blocked until this Message finishes being sent..
     *
     * @param message 
     */
    private static void broadcast(Message message) {
        chatEndpoints.forEach(endpoint -> {
            if (message.getTo() == null || message.getTo().equals(users.get(endpoint.session.getId())) ||  message.getFrom().equals(users.get(endpoint.session.getId()))) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);
                } catch (IOException | EncodeException e) {
                    /* note: in production, who exactly is looking at the console.  This exception's
                     *       output should be moved to a logger.
                     */
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        });
    }

    /**
     * Broadcast.
     *
     * Send a Message to each session in the pool of sessions of the users in a group.
     * The Message sending action is synchronized.  That is, if another
     * Message tries to be sent at the same time to the same endpoint,
     * it is blocked until this Message finishes being sent..
     *
     * @param message
     */

    private static void broadcastToGroup(Message message) {
        chatEndpoints.forEach(endpoint -> {
            if (message.getChatGroup().getMembers().contains(users.get(endpoint.session.getId()))) {
                try {
                    endpoint.session.getBasicRemote()
                            .sendObject(message);
                } catch (IOException | EncodeException e) {
                    /* note: in production, who exactly is looking at the console.  This exception's
                     *       output should be moved to a logger.
                     */
                    LOGGER.log(Level.INFO, e.getMessage());
                }
            }
        });
    }
}
