package com.neu.prattle.model;


import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/***
 * A User object represents a basic account information for a user.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Entity
@Table(appliesTo = "User")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "image")
    private String image;

    private String followers = "";

    private String followings = "";

    private boolean find;

    @Column(name = "plan")
    private String plan="basic";

    /**
     * Getter method to get the subscription plan of the this user.
     * @return the subscription plan of this user
     */
    public String getPlan() {
        return plan;
    }

    /**
     * Setter method to set the subscription plan of this user.
     * @param plan the subscription plan of current user
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * Getter method to get the follower users of the this user.
     * @return the follower users of the this user
     */
    public String getFollowers() {
        return followers;
    }

    /**
     * Adds a user to the follower list of the user.
     * @param user the user to be added to the follower list of the user
     */
    public void addFollowers(User user) {
        if(followers.contains(user.getUsername()))
            throw new IllegalArgumentException("Other user is already a follower of current user");

        if(followers.equals(""))
            followers += user.getUsername();
        else
            followers += " " + user.getUsername();
    }

    /**
     * Removes a user to the follower list of the user.
     * @param user the user to be removed to the follower list of the user
     */
    public void removeFollowers(User user) {
        if(!followers.contains(user.getUsername()))
            throw new IllegalArgumentException("Other user is not a follower");

        String[] followerArray = followers.split(" ");
        StringBuilder newFollowers = new StringBuilder();

        for(String arrayElement : followerArray) {
            if(!arrayElement.equals(user.getUsername()))
                newFollowers.append(" ").append(arrayElement);
        }
        followers = newFollowers.toString().trim();
    }

    /**
     * Getter method to get the following users of the this user.
     * @return the followering users of the this user
     */
    public String getFollowings() {
        return followings;
    }

    /**
     * Adds a user to the following list of the user.
     * @param user the user to be added to the following list of the user
     */
    public void addFollowings(User user) {
        if(followings.contains(user.getUsername()))
            throw new IllegalArgumentException("Current user already follows the other user");

        if(followings.equals(""))
            followings += user.getUsername();
        else
            followings += " " + user.getUsername();
    }

    /**
     * Setter method to set the follower users of this user.
     * @param followers the follower users of this user
     */
    public void setFollowers(String followers) {
        this.followers = followers;
    }

    /**
     * Setter method to set the following users of this user.
     * @param followings the following users of this user
     */
    public void setFollowings(String followings) {
        this.followings = followings;
    }

    /**
     * Removes a user to the following list of the user.
     * @param user the user to be removed to the following list of the user
     */
    public void removeFollowings(User user) {

        if(!followings.contains(user.getUsername()))
            throw new IllegalArgumentException("Current user does not follow other user");

        StringBuilder newFollowings = new StringBuilder();
        String[] followingArray = followings.split(" ");

        for(String arrayElement : followingArray) {
            if(!arrayElement.equals(user.getUsername()))
                newFollowings.append(" ").append(arrayElement);
        }
        followings = newFollowings.toString().trim();
    }







    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "toUser")
    private List<Message> toMessage = new ArrayList<>();
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fromUser")
    private List<Message> fromMessage = new ArrayList<>();



    /**
     * Getter method that returns the id of User.
     * @return id of User
     */
    public int getId() {
        return id;
    }

    /**
     * Getter method that returns the list of messages received by User.
     * @return list of messages received by User
     */
    public List<Message> getToMessage() {
        return toMessage;
    }

    public void setToMessage(List<Message> messages){
        toMessage = messages;
    }

    /**
     * Getter method that returns the list of messages send by User.
     * @return list of messages send by User
     */
    public List<Message> getFromMessage() {
        return fromMessage;
    }

    public void setFromMessage(List<Message> messages){
        fromMessage = messages;
    }

    /**
     * The user constructor used to create a user object.
     * @param username username of the user.
     * @param email email of the user.
     * @param password password of the user.
     * @param status status of the user.
     * @param image image of the user.
     * @param name name of the user.
     */
    public User(String username, String email, String password, String status, String image, String name) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.image = image;
        this.name = name;
        this.find = true;
    }

    /**
     * The user constructor used to create a user object.
     * @param username username of the user.
     * @param email email of the user.
     * @param password password of the user.
     * @param status status of the user.
     * @param image image of the user.
     * @param name name of the user.
     */
    public User(String username, String email, String password, String status, String image, String name, boolean find) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.image = image;
        this.name = name;
        this.find = true;
        this.find = find;
    }

    /**
     * Getter method that returns username of User.
     * @return username of User
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method that sets the username of the user
     * @param username username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method that returns email of User.
     * @return email of User
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter method that sets the email of the user
     * @param email email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter method that returns username of User.
     * @return username of User
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter method that sets the password of the user
     * @param password password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter method that returns username of User.
     * @return username of User
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter method that sets the status of the user
     * @param status status of the user
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter method that returns image of User.
     * @return image of User
     */
    public String getImage() {
        return image;
    }

    public boolean isFind() {
        return find;
    }

    public void setFind(boolean find) {
        this.find = find;
    }

    /**
     * Setter method that sets the image of the user
     * @param image username of the user
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Getter method that returns name of User.
     * @return name of User
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method that sets the name of the user
     * @param name name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    private String name;

    /**
     * Non parameterized constructor used to create a user object.
     */
    public User() {

    }

    /**
     * Parameterized constructor with only name as parameter, used to create a user object.
     * @param name the name of the User
     */
    public User(String name) {
        this.name = name;
    }

    /***
     * Returns the hashCode of this object.
     *
     * As name can be treated as a sort of identifier for
     * this instance, we can use the hashCode of "name"
     * for the complete object.
     *
     *
     * @return hashCode of "this"
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /***
     * Makes comparison between two user accounts.
     *
     * Two user objects are equal if their name are equal ( names are case-sensitive )
     *
     * @param obj Object to compare
     * @return a predicate value for the comparison.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;

        User user = (User) obj;
        return user.username.equals(this.username);
    }

    /**
     * Returns the details of this user to a follower of the user.
     * @param user the user to whom the details of this user is to be returned
     * @return the details of this user to a follower of the user
     */
    public String getDetails(User user) {

        if(!(this.getFollowers().contains(user.getUsername())
                && user.getFollowings().contains(this.getUsername()))
        )
            throw new IllegalArgumentException("User is not a follower of current user");

        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", followers='" + followers + '\'' +
                ", followings='" + followings + '\'' +
                '}';
    }

    /**
     * Method that returns the properties of a user in String format.
     * @return the properties of a user in String format
     */
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", followers='" + followers + '\'' +
                ", followings='" + followings + '\'' +
                '}';
    }

    /***
     * A Builder helper class to create instances of {@link User}
     */
    public static class UserBuilder    {
        /***
         * Invoking the build method will return this message object.
         */
        User user;

        /**
         * Method that initializes the Message instance.
         */
        public UserBuilder()    {
            user = new User();
        }


        /**
         * Method that calls the setter method of user that sets the username of the user.
         * @param username username of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setUsername(String username)    {
            user.setUsername(username);
            return this;
        }

        /**
         * Method that calls the setter method of user that sets the email of the user.
         * @param email username of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setEmail(String email)    {
            user.setEmail(email);
            return this;
        }

        /**
         * Method that calls the setter method of user that sets the name of the user.
         * @param name username of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setName(String name)    {
            user.setName(name);
            return this;
        }

        /**
         * Method that calls the setter method of user that sets the password of the user.
         * @param password username of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setPassword(String password)    {
            user.setName(password);
            return this;
        }

        /**
         * Method that calls the setter method of user that sets the image of the user.
         * @param image username of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setImage(String image)    {
            user.setImage(image);
            return this;
        }

        /**
         * Method that calls the setter method of user that sets the privacy option of the user.
         * @param find privacy option of the user
         * @return the current UserBuilder instance ie this
         */
        public User.UserBuilder setFind(boolean find)    {
            user.setFind(find);
            return this;
        }


        /**
         * Method that returns a user instance.
         * @return a user instance
         */
        public User build()  {
            return user;
        }
    }
}
