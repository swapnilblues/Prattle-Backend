package com.neu.prattle.exceptions;

/***
 * An representation of an error which is thrown where a request has been made
 * for getting a message when the message does not exist
 * Refer {@link com.neu.prattle.model.Message#equals}
 * Refer {@link com.neu.prattle.service.MessageService#getMessageById(int)}
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
public class NoSuchMessageException extends RuntimeException {

    private static final long serialVersionUID = -4845176561270017896L;

    public NoSuchMessageException(String message)  {
        super(message);
    }
}
