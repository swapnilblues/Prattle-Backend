package com.neu.prattle.model;

import  org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Table;

/***
 * A Basic POJO for Message.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Entity
@Table(appliesTo = "Message")
public class Message implements Serializable, Comparable<Message> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    /***
     * The name of the user who sent this message.
     */
    @ManyToOne
    @JsonIgnore
    private User fromUser;
    /***
     * The name of the user to whom the message is sent.
     */
    @ManyToOne
    @JsonIgnore
    private User toUser;
    /***
     * It represents the contents of the message.
     */
    @Column(name="content",nullable = false)
    private String content;

    /**
     * Represents the time when the message was sent.
     */
    @Column(name = "timeStamp",nullable = false)
    private String timeStamp;

    @Column(name = "isGroup", nullable = false)
    private boolean containsGroup;

    /***
     * The Group object to whom the message is sent.
     */
    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    private GroupMessage chatGroup;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Message> replies;

    @Column(name="attachment")
    private String attachment;

    public Message(){
        replies = new ArrayList<>();
        containsGroup = false;
    }

    /**
     * Method that returns the properties of a message in String format.
     * @return the properties of a message in String format
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("From: ");
        if(fromUser == null)
            stringBuilder.append("null");
        else
            stringBuilder.append(fromUser.getUsername());

        stringBuilder.append(" To: ");
        if (isContainsGroup()){
            stringBuilder.append(chatGroup.getName());
        }
        else {
            if (toUser == null)
                stringBuilder.append("null");
            else
                stringBuilder.append(toUser.getUsername());
        }
        stringBuilder
                .append(" Content: ").append(content)
                .append(" At time:").append(timeStamp)
                .append(" Replies: [");
        for (Message rep: replies){
            stringBuilder.append("{")
                    .append(rep.toString())
                    .append("}");
        }
        return stringBuilder.append("]").toString();
    }

    /**
     * Getter method that returns the sender of the message.
     * @return the sender of the message
     */
    public User getFrom() {
        return fromUser;
    }

    /**
     * Setter method that sets the sender of the message.
     * @param from sender of the message
     */
    public void setFrom(User from) {
        this.fromUser = from;
    }

    /**
     * Getter method that returns the receiver of the message.
     * @return the receiver of the message
     */
    public User getTo() {
        return toUser;
    }

    /**
     * Setter method that sets the receiver of the message.
     * @param to receiver of the message
     */
    public void setTo(User to) {
        this.toUser = to;
    }

    /**
     * Getter method that returns the content of the message.
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter method that sets the content of the message.
     * @param content content of the message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Setter method that sets the timestamp of the message to current time and date.
     */
    public void setDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        this.timeStamp = dateFormat.format(date);
    }

    /**
     * Setter method that sets the timestamp of the message to the parameter.
     * @param timeStamp timestamp of the message
     */
    public void setDateTime(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Getter method that returns the timestamp of the message.
     * @return the timestamp of the message
     */
    public String getDateTime() {
        return this.timeStamp;
    }

    /**
     * Getter method that returns the sender of the message.
     * @return the sender of the message
     */
    public int getId() {
        return id;
    }

    /**
     * Method that returns an instance of the MessageBuilder class.
     * @return an instance of the MessageBuilder class
     */
    public static MessageBuilder messageBuilder()   {
        return new MessageBuilder();
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }

    public boolean addReply(Message reply){
        return replies.add(reply);
    }


    /**
     * Compares the two messages this and argument by their time stamp and returns positive if first message was send later and vice versa.
     * @param message the message to be compared of this
     * @return positive if first message was send later and vice versa
     */
    @Override
    public int compareTo(Message message) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date date1;
        Date date2;
        try {
            date1 = dateFormat.parse(this.getDateTime());
            date2 = dateFormat.parse(message.getDateTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error with Date time while sorting.");
        }
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.compareTo(cal2);
    }

    /**
     * Checks if two messages are equal or not.
     * @param o the other message to be compared to this
     * @return true if two messages are equal otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    /**
     * Returns the attachment of a message.
     * @return the attachment of a message
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * Sets the attachment of a message.
     * @param attachment attachment of a message
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    /**
     * Sets the chat -roup of the message.
     * @param chatGroup chat-group where the message belongs
     */
    public void setChatGroup(GroupMessage chatGroup) {
        setContainsGroup(true);
        this.chatGroup = chatGroup;
    }

    /**
     * Checks where a message belongs in a group or not.
     * @return true if a message belongs in a group otherwise false
     */
    public boolean isContainsGroup() {
        return containsGroup;
    }

    /**
     * Sets whether a message belongs in a group or not.
     * @param containsGroup the parameter which determines whether a message belongs in a group or not
     */
    public void setContainsGroup(boolean containsGroup) {
        this.containsGroup = containsGroup;

    }

    /**
     * Returns the chat-group where the message is present.
     * @return the chat-group where the message is present
     */
    public GroupMessage getChatGroup() {
        return chatGroup;
    }


    /***
     * A Builder helper class to create instances of {@link Message}
     */
    public static class MessageBuilder    {
        /***
         * Invoking the build method will return this message object.
         */
        Message message;

        /**
         * Method that initializes the Message instance.
         */
        public MessageBuilder()    {
            message = new Message();
        }


        /**
         * Method that calls the setter method of message that sets the sender of the message.
         * @param from sender of the message
         * @return the current MessageBuilder instance ie this
         */
        public MessageBuilder setFrom(User from)    {
            message.setFrom(from);
            return this;
        }

        /**
         * Method that calls the setter method of message that sets the receiver of the message.
         * @param to sender of the message
         * @return the current MessageBuilder instance ie this
         */
        public MessageBuilder setTo(User to)    {
            message.setTo(to);
            return this;
        }

        /**
         * Method that calls the setter method of message that sets the content of the message.
         * @param content sender of the message
         * @return the current MessageBuilder instance ie this
         */
        public MessageBuilder setMessageContent(String content)   {
            message.setContent(content);
            return this;
        }

        /**
         * Method that calls the setter method of message that sets the timestamp of the message.
         * @return the current MessageBuilder instance ie this
         */
        public MessageBuilder setMessageDateTime() {
            message.setDateTime();
            return this;
        }

        public boolean addReply(Message reply){
            return message.addReply(reply);
        }


        /**
         * Method that returns a message instance.
         * @return a message instance
         */
        public Message build()  {
            return message;
        }
    }
}
