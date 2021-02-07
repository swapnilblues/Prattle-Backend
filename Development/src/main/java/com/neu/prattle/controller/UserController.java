package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;



import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

/***
 * A Resource class responsible for handling CRUD operations
 * on User objects.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */

@Path(value = "/user")
public class UserController {

    // Usually Dependency injection will be used to inject the service at run-time
    private UserService accountService = UserServiceImpl.getInstance();


    /***
     * Handles a HTTP POST request for user creation
     *
     * @param user -> The User object decoded from the payload of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserAccount(User user) {
        accountService.addUser(user);
        return Response.ok().build();
    }

    /***
     * Handles a HTTP POST request for user authentication
     *
     * @param user -> The User object decoded from the payload of POST request.
     * @return -> A Response indicating the outcome of the requested operation.
     */
    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)

    public String verifyUserAccount(User user) {
        User foundUser;
        try {
            foundUser = accountService.getUserByUsername(user.getUsername());
        }
        catch (Exception e) {
            return String.valueOf(Response.serverError().entity(e.getMessage()).build().getStatus());
        }


        if(user.getPassword().equals(foundUser.getPassword())&&
                foundUser.getEmail().equals(user.getEmail())){
            Gson gson = new Gson();
            return gson.toJson(foundUser);
        }
        return null;
    }

    /**
     * The controller handles HTTP get to getALlUsers
     * @return a json representation of all users.
     */

    @GET
    @Path("/getAllUsers")
    public String getAllUsers() {
        List<User> result = accountService.getAllUsers();
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    /**
     * The controller handles http delete to delete a user by id.
     * @param userId to be deleted.
     * @return status
     */

    @DELETE
    @Path("/deleteUserById/{userId}")
    public int deleteUserById(@PathParam("userId") int userId){
        int res = accountService.deleteUserById(userId);
        if(res<=0){
            throw  new IllegalArgumentException("User with userId: "+userId+ " doesn't exist");
        }
        else{
            return res;
        }
    }

    /**
     * The controller handles http get to get userID by the username
     * @param userName whose is is needed.
     * @return userId of userName
     */

    @GET
    @Path("/getUserIdByUsername/{userName}")
    public int getUserIdByUserName(@PathParam("userName") String userName){
        int res = accountService.getUserIdByUserName(userName);
        if(res==-1){
            throw new IllegalArgumentException("No user with username"+userName+" exists");
        }
        else{
            return res;
        }
    }

    /**
     * The controller handles http get to get user by username.
     * @param userName for the user needed to fetch.
     * @return the user whose username is userName.
     */

    @GET
    @Path("/getUserByUsername/{userName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getUserByUsername(@PathParam("userName") String userName) {
        User user = accountService.getUserByUsername(userName);
        Gson gson = new Gson();
        if(user==null){
            throw new IllegalArgumentException("No user with username:"+userName+" exists");
        }
        return gson.toJson(user);

    }

    /**
     * The controller handles http PUT to update a user by id.
     * @param id of the user who is updated.
     * @param user with updated user properties.
     * @return the update status.
     */

    @PUT
    @Path("/updateUserById/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public int updateUserById(@PathParam("id") int id, User user) {
        int res = accountService.updateUserById(id, user);
        if(res==-1){
            throw new IllegalArgumentException("User with userId: "+id+" doesn't exist");
        }
        else return res;
    }

    /**
     * Adds a follower to a user
     * @param userName userName of user to whom follower has to be added
     * @param followerUserName userName follower to be added to user
     * @return response status
     */
    @PUT
    @Path("/addFollower/user/{userName}/follower/{followerUserName}")
    public Response addFollower(@PathParam("userName") String userName, @PathParam("followerUserName") String followerUserName) {

        try {
            User user = accountService.getUserByUsername(userName);
            User follower = accountService.getUserByUsername(followerUserName);
            accountService.addFollower(user,follower);
            return Response.ok().build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Removes a follower from a user.
     * @param userName userName of user to whom follower has from be removed
     * @param followerUserName userName of follower to be removed from user
     * @return response status code
     */
    @PUT
    @Path("/removeFollower/user/{userName}/follower/{followerUserName}")
    public Response removeFollower(@PathParam("userName") String userName, @PathParam("followerUserName") String followerUserName) {

        try {
            User user = accountService.getUserByUsername(userName);
            User follower = accountService.getUserByUsername(followerUserName);
            accountService.removeFollower(user,follower);
            return Response.ok().build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Adds a following to a user.
     * @param userName userName of user whose following has to be added
     * @param followingUserName userName of following to be added to user
     * @return response status code
     */
    @PUT
    @Path("/addFollowing/user/{userName}/following/{followingUserName}")
    public Response addFollowing(@PathParam("userName") String userName, @PathParam("followingUserName") String followingUserName) {

        try {
            User user = accountService.getUserByUsername(userName);
            User following = accountService.getUserByUsername(followingUserName);
            accountService.addFollowing(user,following);
            return Response.ok().build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Removes a following from a user.
     * @param userName userName of user whose following has to be removed
     * @param followingUserName userName of following to be removed to user
     * @return response status code
     */
    @PUT
    @Path("/removeFollowing/user/{userName}/following/{followingUserName}")
    public Response removeFollowing(@PathParam("userName") String userName, @PathParam("followingUserName") String followingUserName) {

        try {
            User user = accountService.getUserByUsername(userName);
            User following = accountService.getUserByUsername(followingUserName);
            accountService.removeFollowing(user,following);
            return Response.ok().build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Returns information about a user to its follower.
     * @param userName user whose information needs to be returned
     * @param followingUserName follower to whom information needs to be returned
     * @return information about a user to its follower or false error code
     */
    @GET
    @Path("/getInformation/user/{userName}/follower/{followingUserName}")
    public String getInformation(@PathParam("userName") String userName, @PathParam("followingUserName") String followingUserName) {

        try {
            User user = accountService.getUserByUsername(userName);
            User following = accountService.getUserByUsername(followingUserName);
            return accountService.getInformation(user,following);

        }catch (Exception e) {
            return String.valueOf(Response.serverError().build().getStatus());
        }
    }

    /**
     * Returns all the other users visible by a user.
     * @param userName the username of the user who will view the other users
     * @return all the other users visible by a user
     */
    @GET
    @Path("/seeAllOtherUsers/user/{userName}")
    public String seeAllOtherUsers(@PathParam("userName") String userName) {

        try {
            User currentUser = accountService.getUserByUsername(userName);
            List<User> users = accountService.seeAllOtherUsers(currentUser);
            StringBuilder sb = new StringBuilder("[");
            for(User user: users) {
                if(!user.equals(currentUser))
                    sb.append(user.toString()).append(",");
            }
            sb.append("]");
            return sb.toString();

        }catch (Exception e) {
            return "err";
        }
    }




}
