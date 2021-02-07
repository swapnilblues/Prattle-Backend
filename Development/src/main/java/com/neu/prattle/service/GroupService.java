package com.neu.prattle.service;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.User;

import java.util.List;

/**
 * Interface that contains methods performing operations on the group.
 */
public interface GroupService {

    /**
     * Creates a group.
     * @param group the group to be created
     */
    void createGroup(GroupMessage group);

    /**
     * Updates a group
     * @param id the id of the old group
     * @param groupMessage the new GroupMessage instance to which the old group is to be updated to
     * @param admin the admin of the group
     * @return status of the operation
     */
    int updateGroupById(int id, GroupMessage groupMessage,User admin);

    /**
     * Adds a member to a group.
     * @param group the group to which a user is to be added
     * @param user the user to be added to the group
     * @param admin the admin of the group
     * @return status of the operation
     */
    int addMemberToGroup(GroupMessage group, User user,User admin);

    /**
     * Removes a member to a group.
     * @param group the group to which a user is to be removed
     * @param user the user to be removed to the group
     * @param admin the admin of the group
     * @return status of the operation
     */
    int removeMemberFromGroup(GroupMessage group, User user,User admin);

    /**
     * Deletes a group
     * @param group the group to be deleted
     * @param admin the admin of the group
     */
    void deleteGroupByAdmin(GroupMessage group, User admin);

    /**
     * Checks if a user is the admin of the group.
     * @param group the group whose admin is to be checked
     * @param admin the user who is checked as the admin of the group
     * @return true if the user passed in the argument is the admin otherwise not
     */
    boolean isAdmin(GroupMessage group, User admin);

    /**
     * Returns all the groups a user can see.
     * @param user user who will view the groups
     * @return all the groups a user can see
     */
    public List<GroupMessage> seeAllGroups(User user);

    /**
     * Returns a group by id.
     * @param id the id of the group to be returned
     * @return a group by id
     */
    GroupMessage getGroupById(int id);

    /**
     * Returns a group by name.
     * @param name the name of the group to be returned
     * @return a group by name
     */
    GroupMessage getGroupByName(String name);
}
