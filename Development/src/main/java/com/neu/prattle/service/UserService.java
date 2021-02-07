package com.neu.prattle.service;

import com.neu.prattle.model.User;

import java.util.List;

/***
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on user accounts.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 *
 */
public interface UserService {

    /***
     * Tries to add a user in the system
     * @param user User object
     *
     */
    void addUser(User user);

    /**
     * Returns a list of all users.
     * @return list of users.
     */

    List<User> getAllUsers();

    /**
     * Updates a user by id.
     * @param id the id of user who has to be updated.
     * @param user user object with updated fields.
     * @return update status.
     */

    int updateUserById(int id, User user);

    /**
     * Return a user by username.
     * @param userName the username of the user being searched in db.
     * @return the user with username userName.
     */

    User getUserByUsername(String userName);

    /**
     * Delete a user by id.
     * @param userId the userId who has to be deleted.
     * @return the delete status.
     */

    int deleteUserById(int userId);

    /**
     * Return the id of the user whose username is userName.
     * @param userName the username of the user whose id is desired.
     * @return the userId.
     */
    int getUserIdByUserName(String userName);

    /**
     * Adds a follower to a user.
     * @param user user to whom follower has to be added
     * @param follower follower to be added to user
     */
    void addFollower(User user, User follower);

    /**
     * Adds a following to a user.
     * @param user user whose following has to be added
     * @param following following to be added to user
     */
    void addFollowing(User user, User following);

    /**
     * Removes a follower from a user.
     * @param user user to whom follower has from be removed
     * @param follower follower to be removed from user
     */
    void removeFollower(User user, User follower);

    /**
     * Removes a following from a user.
     * @param user user whose following has to be removed
     * @param following following to be removed to user
     */
    void removeFollowing(User user, User following);

    /**
     * Returns information about a user to its follower.
     * @param user user whose information needs to be returned
     * @param follower follower to whom information needs to be returned
     * @return information about a user to its follower
     */
    String getInformation(User user, User follower);

    /**
     * Lists all the other users a user can see.
     * @param currentUser the current user
     * @return all the other users a user can see
     */
    List<User> seeAllOtherUsers(User currentUser);

}
