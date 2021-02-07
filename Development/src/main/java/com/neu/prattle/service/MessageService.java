package com.neu.prattle.service;

import com.neu.prattle.model.Message;

import java.util.List;

/**
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The service is responsible for interfacing with this instance
 * to perform all the CRUD operations on messages accounts.
 *
 */
public interface MessageService {

    /**
     * Adds a message to the database
     * @param message the message to be added to the database.
     */
    void addMessage(Message message);

    /**
     * Returns all the messages present in the database.
     * @return all the messages present in the database
     */
    List<Message> getAllMessages();

    /**
     * Returns a message by its id.
     * @param id the id of the message to be returned
     * @return a message by its id
     */
    Message getMessageById(int id);

    /**
     * Updates a message by its id.
     * @param id the id of the message to be updated
     * @param message the new message
     * @return the status code
     */
    int updateMessageById(int id, Message message);

    /**
     * Deletes a message by its id.
     * @param id the id of the message to be deleted.
     */
    void deleteMessageById(int id);

    /**
     * Returns all the message exchanged between two users.
     * @param username1 the username of the first user
     * @param username2 the username of the first user
     * @return all the message exchanged between two users
     */
    List<Message> getMessagesExchanged(String username1, String username2);

    /**
     * Returns all the messages exchanged in a group.
     * @param groupName name of the group
     * @return all the messages exchanged in a group
     */
    List<Message> getMessagesExchangedInGroup(String groupName);


    /**
     * Returns all messages of a user based on start and end date.
     * @param username username of the user
     * @param startDate start date from when all the messages are returned
     * @param endDate end date to when all the messages are returned
     * @return all messages of a user based on start and end date
     */
    List<Message> getAllMessagesOfAUser(String username, String startDate, String endDate);
}
