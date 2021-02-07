package com.neu.prattle.controller;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GroupService;
import com.neu.prattle.service.GroupServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

/***
 * Class that exposes APIs for CRUD operations on GroupMessage.
 */
@Path(value = "/group")
public class GroupController {

    private GroupService accountService = GroupServiceImpl.getInstance();
    private UserService userService = UserServiceImpl.getInstance();

    /**
     * Create a group.
     * @param name name of the group to be created
     * @param adminUserName username of admin
     * @return Response of the operation
     */
    @POST
    @Path("/create/{name}/{adminUserName}")
    public Response createGroup(@PathParam("name") String name, @PathParam("adminUserName") String adminUserName) {
        try {
            User admin = userService.getUserByUsername(adminUserName);
            GroupMessage groupMessage = new GroupMessage(admin, name);
            accountService.createGroup(groupMessage);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Adds password to a group.
     * @param groupName name of the group
     * @param groupPassword password of the group
     * @param admin username of the admin
     * @return Response of the operation
     */
    @POST
    @Path("/{groupName}/admin/{admin}/password/{password}")
    public Response addPasswordToGroup(@PathParam("groupName") String groupName,
                                       @PathParam("password") String groupPassword, @PathParam("admin") String admin){
        try{
            GroupMessage groupMessage = accountService.getGroupByName(groupName);
            if(admin.equals(groupMessage.getAdmin().getUsername())){
                groupMessage.setPassword(groupPassword);
                accountService.updateGroupById(groupMessage.getId(),groupMessage,groupMessage.getAdmin());
                return Response.ok().build();
            }
            else{
             throw new IllegalArgumentException("Wrong admin");
            }
        }
        catch (Exception e){
            return Response.serverError().build();
        }
    }


    /**
     * Returns a group by name.
     * @param name name of the group
     * @return a group by name
     */
    @GET
    @Path("/getByName/{name}")
    public String getGroupByName(@PathParam("name") String name) {
        try {
            GroupMessage groupMessage = accountService.getGroupByName(name);
            List<User> members = groupMessage.getMembers();
            StringBuilder sb = new StringBuilder("");
            for (User member : members)
                sb.append(member.getUsername()).append(" ");
            return "{" +
                    "id=" + groupMessage.getId() +
                    ", admin='" + groupMessage.getAdmin() + '\'' +
                    ", name='" + groupMessage.getName() + '\'' +
                    ", members='" + sb.toString().trim() + '\'' +
                    '}';
        } catch (Exception e) {
            return "err";
        }
    }

    /**
     * Adds a member to the group.
     * @param groupName name of the group
     * @param userName username of the member
     * @return Response status
     */
    @PUT
    @Path("/addMembers/{group}/user/{userName}")
    public Response addMember(@PathParam("group") String groupName, @PathParam("userName") String userName) {
        try {
            GroupMessage groupMessage = accountService.getGroupByName(groupName);
            User admin = groupMessage.getAdmin();
            User user = userService.getUserByUsername(userName);
            accountService.addMemberToGroup(groupMessage, user, admin);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    /**
     * Removes a member to the group.
     * @param groupName name of the group
     * @param userName username of the member
     * @return Response status
     */
    @PUT
    @Path("/removeMember/{group}/user/{userName}")
    public Response removeMember(@PathParam("group") String groupName, @PathParam("userName") String userName) {
        try {
            GroupMessage groupMessage = accountService.getGroupByName(groupName);
            User admin = groupMessage.getAdmin();
            User user = userService.getUserByUsername(userName);
            accountService.removeMemberFromGroup(groupMessage, user, admin);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    /**
     * Updates a group.
     * @param oldName old name of the group
     * @param newName new name of the group
     * @param privacy privacy option of the group
     * @return Response status
     */
    @PUT
    @Path("/changeGroupName/{oldName}/newName/{newName}/privacy/{privacy}")
    public Response updateGroup(@PathParam("oldName") String oldName, @PathParam("newName") String newName, @PathParam("privacy") String privacy) {
        try {
            GroupMessage groupMessage = accountService.getGroupByName(oldName);
            groupMessage.setName(newName);
            if (!privacy.equals("true")
                    && !privacy.equals("false")
            )
                throw new IllegalArgumentException("Wrong input for privacy");
            groupMessage.setFind(Boolean.parseBoolean(privacy));
            User admin = groupMessage.getAdmin();
            accountService.updateGroupById(groupMessage.getId(), groupMessage, admin);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    /**
     * Delete a group.
     * @param groupName name of the group to be deleted
     * @param userName username of admin
     * @return Response status
     */
    @DELETE
    @Path("/deleteGroup/{group}/admin/{userName}")
    public Response deleteGroup(@PathParam("group") String groupName, @PathParam("userName") String userName) {
        try {
            GroupMessage groupMessage = accountService.getGroupByName(groupName);
            User admin = userService.getUserByUsername(userName);
            accountService.deleteGroupByAdmin(groupMessage, admin);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    /**
     * Returns all the groups that can be viewed by a user.
     * @param userName username of the user viewing all the groups
     * @return all the groups that can be viewed by a user
     */
    @GET
    @Path("/seeAllGroups/{userName}")
    public String seeAllGroups(@PathParam("userName") String userName) {
        try {
            User user = userService.getUserByUsername(userName);
            List<GroupMessage> groups = accountService.seeAllGroups(user);
            StringBuilder sb = new StringBuilder("[");
            for (GroupMessage group : groups) {
                sb.append(getGroupByName(group.getName())).append(",");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            return "err";
        }
    }


}
