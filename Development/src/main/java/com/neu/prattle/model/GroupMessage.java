package com.neu.prattle.model;

import javax.persistence.*;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Table;


/**
 * A basic POJO for Groups
 */
@Entity
@Table(appliesTo = "GroupMessage")
public class GroupMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public GroupMessage() {
        members = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "password")
    private String password;


    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    private List<User> members;

    @OneToOne
    private User admin;

    private boolean find;

    /**
     * Returns the admin of the group.
     * @return the admin of the group
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * Sets the admin of the group
     * @param admin the admin of the group
     */
    public void setAdmin(User admin) {
        this.admin = admin;
    }

    /**
     * Constructor to initialize class variables.
     * @param admin admin of the group
     * @param name name of the group
     */
    public GroupMessage(User admin, String name) {
        this.admin = admin;
        this.name = name;
        this.find = true;
        members = new ArrayList<>();
        addUsers(admin);
    }

    /**
     * Constructor to initialize class variables.
     * @param admin admin of the group
     * @param name name of the group
     * @param find privacy option of the group
     */
    public GroupMessage(User admin, String name, boolean find) {
        this.admin = admin;
        this.name = name;
        this.find = find;
        members = new ArrayList<>();
        addUsers(admin);
    }

    /**
     * Returns the id of the group.
     * @return id of the group
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the group.
     * @return name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the group.
     * @param name name of the group
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets all the members of the group.
     * @return all the members of the group
     */
    public List<User> getMembers() {
        return members;
    }

    /**
     * Sets all the members of the group.
     * @param users all the members of the group
     */
    public void setMembers(List<User> users) {
        members = users;
    }

    /**
     * Adds a user to group.
     * @param user the user to be added to the group
     */
    public void addUsers(User user) {
        if(members.contains(user))
            throw new IllegalArgumentException("User already part of group");
        members.add(user);
    }

    /**
     * Removes an user from the group.
     * @param user user to be removed from the group
     */
    public void removeUsers(User user) {
        if(!members.contains(user))
            throw new IllegalArgumentException("User not part of group");
        this.members.remove(user);
    }

    /**
     * Checks whether the privacy option of the group is true or false.
     * @return whether the privacy option of the group is true or false
     */
    public boolean isFind() {
        return find;
    }

    /**
     * Sets the privacy of the group.
     * @param find privacy option of the group
     */
    public void setFind(boolean find) {
        this.find = find;
    }

}
