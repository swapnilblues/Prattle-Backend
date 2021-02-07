package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.neu.prattle.exceptions.NoSuchMessageException;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/***
 * Class that exposes APIs for CRUD operations on Messages.
 */
@Path(value = "/message")
public class MessageController {

    private static String idconst = "\"id\":";
    private static String user = "\"toUser\":";
    private static String fromUser = "\"fromUser\":";
    private static String content = "\"content\":";
    private static String dateTime = "\"dateTime\":";

    private MessageService accountService = MessageServiceImpl.getInstance();

    private UserService userService = UserServiceImpl.getInstance();

    /**
     * Method to add a message.
     * @param message the message to be added
     * @return status code
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMessage(Message message) {
        accountService.addMessage(message);
        return Response.ok().build();
    }

    /**
     * Method that returns all the messages.
     * @return all the messages
     */
    @GET
    @Path("/getAllMessages")
    public String getAllMessages() {
        List<Message> messages = accountService.getAllMessages();
        StringBuilder sb = new StringBuilder("[");
        for(Message m : messages) {
            sb.append("{").append(idconst).append(m.getId()).append(",").append(user).append("\"").append(m.getTo().getUsername()).append("\"").append(",").append(fromUser).append("\"").append(m.getFrom().getUsername()).append("\"").append(",").append(content).append("\"").append(m.getContent()).append("\"").append(",").append(dateTime).append("\"").append(m.getDateTime()).append("\"").append("}").append(",");
        }
        String sb1 = sb.substring(0,sb.length()-1);
        sb1+="]";
        return sb1;
    }

    /**
     * Returns all the replies of a message.
     * @param id the id of the message whose replies has to be returned
     * @return all the replies of a message
     */
    @GET
    @Path("/getRepliesForMessage/{id}")
    public String getRepliesForMessage(@PathParam("id") int id) {
        Message message = accountService.getMessageById(id);
        List<Message> replies = message.getReplies();
        StringBuilder sb = new StringBuilder("[");
        for(Message m :replies){
            sb.append("{").append(idconst).append(m.getId()).append(",").append(user).append("\"").append(m.getTo().getUsername()).append("\"").append(",").append(fromUser).append("\"").append(m.getFrom().getUsername()).append("\"").append(",").append(content).append("\"").append(m.getContent()).append("\"").append(",").append(dateTime).append("\"").append(m.getDateTime()).append("\"").append("}").append(",");
        }
        String sb1 = sb.substring(0,sb.length()-1);
        sb1+="]";
        Gson gson = new Gson();
        return gson.toJson(sb1);
    }

    /**
     * Get all the exchange messages between two users.
     * @param username1 the username of the first user
     * @param username2 the username of the first user
     * @return all the exchange messages between two users
     */
    @GET
    @Path("/getExchangedMessages/user1/{username1}/user2/{username2}")
    public String getExchangedMessages(@PathParam("username1") String username1, @PathParam("username2") String username2){
        List<Message> messages = accountService.getMessagesExchanged(username1, username2);
        User user1 = userService.getUserByUsername(username1);
        User user2 = userService.getUserByUsername(username2);

        int count = 0;
        List<Message> result = new ArrayList<>();
        if(!user1.getPlan().equals("premium") || !user2.getPlan().equals("premium")){
            count = messages.size()>5?5:messages.size();
        }
        else{
            count = messages.size();
        }
        for(int i=messages.size()-count;i<messages.size();i++){
            result.add(messages.get(i));
        }

        Gson gson = new Gson();
        for (Message m: result){
            m.getFrom().setFromMessage(new ArrayList<>());
            m.getFrom().setToMessage(new ArrayList<>());
            m.getTo().setFromMessage(new ArrayList<>());
            m.getTo().setToMessage(new ArrayList<>());
        }
        return gson.toJson(result);
    }

    /**
     * Get all the messages in a group.
     * @param groupName name of the group
     * @return all the messages in a group
     */
    @GET
    @Path("/getExchangedMessages/group/{groupName}")
    public String getExchangedMessagesInGroup(@PathParam("groupName") String groupName){
        List<Message> messages = accountService.getMessagesExchangedInGroup(groupName);
        Gson gson = new Gson();
        for (Message m: messages){
            m.getFrom().setFromMessage(new ArrayList<>());
            m.getFrom().setToMessage(new ArrayList<>());
            m.getChatGroup().setAdmin(null);
            m.getChatGroup().setMembers(new ArrayList<>());
            for(Message reply: m.getReplies()){
                reply.getFrom().setFromMessage(new ArrayList<>());
                reply.getFrom().setToMessage(new ArrayList<>());
                reply.getChatGroup().setAdmin(null);
                reply.getChatGroup().setMembers(new ArrayList<>());
            }
        }
        return gson.toJson(messages);
    }

    /**
     * Method to get message by id.
     * @param id the id of the message to be returned
     * @return the message who id had been given
     */
    @GET
    @Path("/getMessageById/{id}")
    public String getMessageById(@PathParam("id") int id) {
        Message m;
        try {
            m = accountService.getMessageById(id);
        }catch (NoSuchMessageException e) {
            return "500";
        }
        StringBuilder sb = new StringBuilder("{");
        sb.append(idconst).append(m.getId()).append(",").append(user).append("\"").append(m.getTo().getUsername()).append("\"").append(",").append(fromUser).append("\"").append(m.getFrom().getUsername()).append("\"").append(",").append(content).append("\"").append(m.getContent()).append("\"").append(",").append(dateTime).append("\"").append(m.getDateTime()).append("\"").append("}");
        return sb.toString();
    }

    /**
     * Updates a message whose id has been given.
     * @param id id of the message to be updated
     * @param message the new message to which the old message is to be updated
     * @return the status code
     */
    @PUT
    @Path("/updateMessageById/{id}")
    public int updateMessageById(@PathParam("id") int id, Message message) {
        if(message == null)
            throw new IllegalArgumentException("Message cannot be null");
        if(message.getFrom() == null){
            throw new IllegalArgumentException("Message from user cannot be null");
        }
        if(message.getContent() == null){
            throw new IllegalArgumentException("Message content cannot be null");
        }
        return accountService.updateMessageById(id,message);

    }

    /**
     * Method that deletes a message by its id.
     * @param id id of the message to be deleted
     * @return the status code
     */
    @DELETE
    @Path("/deleteMessageById/{id}")
    public int deleteMessageById(@PathParam("id") int id) {
        accountService.deleteMessageById(id);
        return 1;
    }

    /**
     * Returns all messages of a user based on start date and current date.
     * @param user username of the user
     * @param startDate start date from when all the messages are returned
     * @return all messages of a user based on start date and current date
     */
    @GET
    @Path("/calcea/{username}/{startDate}")
    public String getMessagesCalceaOnlyStartDate(@PathParam("username") String user,@PathParam("startDate") String startDate){
        String newStartDate = startDate.substring(0,10) + " " + startDate.substring(10);
        List<Message> messages = accountService.getAllMessagesOfAUser(user,newStartDate,null);
        Gson gson = new Gson();
        for (Message m: messages){
            m.getFrom().setFromMessage(new ArrayList<>());
            m.getFrom().setToMessage(new ArrayList<>());
            m.getTo().setFromMessage(new ArrayList<>());
            m.getTo().setToMessage(new ArrayList<>());
        }
        return gson.toJson(messages);

    }

    /**
     * Returns all messages of a user based on start and end date.
     * @param user username of the user
     * @param startDate start date from when all the messages are returned
     * @param endDate end date to when all the messages are returned
     * @return all messages of a user based on start and end date
     */
    @GET
    @Path("/calcea/{username}/{startDate}/{endDate}")
    public String getMessagesCalceaStartAndEndDate(@PathParam("username") String user, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate){
        String newStartDate = startDate.substring(0,10) + " " + startDate.substring(10);
        String newEndDate = endDate.substring(0,10) + " " + endDate.substring(10);
        List<Message> messages = accountService.getAllMessagesOfAUser(user,newStartDate,newEndDate);
        Gson gson = new Gson();
        for (Message m: messages){
            m.getFrom().setFromMessage(new ArrayList<>());
            m.getFrom().setToMessage(new ArrayList<>());
            m.getTo().setFromMessage(new ArrayList<>());
            m.getTo().setToMessage(new ArrayList<>());
        }
        return gson.toJson(messages);
    }


}
