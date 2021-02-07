package com.neu.prattle;

import com.neu.prattle.controller.GroupController;
import com.neu.prattle.controller.UserController;
import com.neu.prattle.hibernate.util.HibernateUtil;
import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import java.util.List;

/**
 * Test class that tests the MessageService interface methods.
 */
public class TestGroupController {

    private static GroupController groupController;

    GroupService groupService;
    private UserService userService;
    private UserController userController;
    User zola;
    User terry;
    User lampard;
    User drogba;
    User hazard;
    User willian;

    @Before
    public void setUp() {
        zola = new User("zola25", "zola@blues.com", "zola1", "lw", "", "zola",true);
        terry = new User("terry26", "terry@blues.com", "terry1", "cd", "", "terry",false);
        lampard = new User("lampard8", "lampard@blues.com", "lampard1", "cm", "", "lampard",false);
        drogba = new User("drogba11", "drogba@blues.com", "drogba1", "st", "", "drogba",true);
        hazard = new User("hazard10", "hazard@blues.com", "eden1", "lw", "", "eden",true);
        willian = new User("willian22", "willian@blues.com", "willian1", "rw", "", "willian",true);

        groupController = new GroupController();
        groupService = GroupServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        userController = new UserController();

        userService.addUser(zola);
        userService.addUser(terry);
        userService.addUser(lampard);
        userService.addUser(drogba);
        userService.addUser(hazard);
        userService.addUser(willian);
    }

    @After
    public void delete() {
        userService.deleteUserById(zola.getId());
        userService.deleteUserById(terry.getId());
        userService.deleteUserById(lampard.getId());
        userService.deleteUserById(drogba.getId());
        userService.deleteUserById(hazard.getId());
        userService.deleteUserById(willian.getId());
    }


    @Test
    public void a11testCreateGroup() {
        Response response = groupController.createGroup("demo name",lampard.getUsername());
        assertEquals(200,response.getStatus());


        groupController.addMember("demo name",hazard.getUsername());
        groupController.addMember("demo name",willian.getUsername());

        GroupMessage group = groupService.getGroupByName("demo name");

        List<User> memberList = group.getMembers();

        assertTrue(memberList.contains(lampard));
        assertTrue(memberList.contains(hazard));
        assertTrue(memberList.contains(willian));

        groupController.removeMember("demo name",hazard.getUsername());

        group = groupService.getGroupByName("demo name");

        memberList = group.getMembers();

        assertTrue(memberList.contains(lampard));
        assertFalse(memberList.contains(hazard));
        assertTrue(memberList.contains(willian));

        groupController.createGroup("demo name1",lampard.getUsername());

        groupController.addMember("demo name",willian.getUsername());

        GroupMessage group1 = groupService.getGroupByName("demo name");

        memberList = group1.getMembers();

        assertTrue(memberList.contains(lampard));
        assertFalse(memberList.contains(hazard));
        assertTrue(memberList.contains(willian));

        response = groupController.updateGroup("demo name1","demo name2","true");
        assertEquals(200,response.getStatus());
        group = groupService.getGroupByName("demo name2");
        assertTrue(group.isFind());

        response = groupController.updateGroup("demo name2","demo name1","false");
        assertEquals(200,response.getStatus());
        group = groupService.getGroupByName("demo name1");
        assertFalse(group.isFind());

        response = groupController.updateGroup("demo name1","demo name2","abcd");
        assertEquals(500,response.getStatus());

        groupController.deleteGroup("demo name",lampard.getUsername());
        groupController.deleteGroup("demo name1",lampard.getUsername());

    }

    @Test
    public void testGetGroupByName() {
        Response response = groupController.createGroup("demo name",lampard.getUsername());
        assertEquals(200,response.getStatus());

        groupController.addMember("demo name",hazard.getUsername());
        groupController.addMember("demo name",willian.getUsername());

        String group = groupController.getGroupByName("demo name");

        assertTrue(group.contains(lampard.getUsername()));
        assertTrue(group.contains(hazard.getUsername()));
        assertTrue(group.contains(willian.getUsername()));

        groupController.deleteGroup("demo name",lampard.getUsername());

    }

    @Test
    public void testCreateGroupAdminNotFound() {
        Response response = groupController.createGroup("demo name","x");
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGetGroupNotFound() {
        assertEquals("err",groupController.getGroupByName("demo name"));
    }

    @Test
    public void testRemoveMemberNotFound() {
        Response response = groupController.removeMember("demo name","x");
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testAddMemberNotFound() {
        Response response = groupController.addMember("demo name","x");
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testDeleteGroupNotFound() {
        Response response = groupController.deleteGroup("demo name","x");
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testChangeGroupNameNotFound() {
        Response response = groupController.updateGroup("demo name","x","true");
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGroupAddPasswordController(){
        Response response = groupController.createGroup("demo name",lampard.getUsername());
        assertEquals(200,response.getStatus());
        GroupMessage groupMessage = groupService.getGroupByName("demo name");
        Response newResponse = groupController.addPasswordToGroup("demo name","password",groupMessage.getAdmin().getUsername());
        assertEquals(200,newResponse.getStatus());
        groupController.deleteGroup("demo name",lampard.getUsername());
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void a15removeAdminFromGroup() {
//
//        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
//
//        GroupMessage group = new GroupMessage(admin,"demo name");
//
//        groupService.removeMemberFromGroup(group, admin,admin);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a16removeNotPresentGroup() {
//
//        GroupMessage group = new GroupMessage(hazard,"demo name");
//
//        groupService.deleteGroupByAdmin(group,hazard);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a17getGroupByIdNegative() {
//
//        groupService.getGroupById(-1);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a17getGroupByIdNotPresent() {
//
//        groupService.getGroupById(Integer.MAX_VALUE);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a18testNullGroup() {
//
//        groupService.addMemberToGroup(null,new User(),new User());
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a18testNullAdmin() {
//
//        groupService.addMemberToGroup(new GroupMessage(),null,new User());
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a18testNullUser() {
//
//        groupService.addMemberToGroup(new GroupMessage(),new User(),null);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testDeleteByNotAdmin() {
//
//        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
//        User user1 = new User("abc1", "abc1@abc.com", "abc10", "al", "", "ABC");
//
//        GroupMessage group = new GroupMessage(admin,"demo name");
//
//        groupService.removeMemberFromGroup(group,user1,user1);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a20testNullGroupName() {
//
//        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
//        User user1 = new User("abc1", "abc1@abc.com", "abc10", "al", "", "ABC");
//
//        GroupMessage group = new GroupMessage(admin,null);
//
//        groupService.removeMemberFromGroup(group,admin,user1);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a20AddUserToGroupTwice() {
//
//        GroupMessage group = new GroupMessage(lampard,"Chelsea");
//
//        group.addUsers(hazard);
//        group.addUsers(hazard);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a20removeUserNotPartOfGroup() {
//
//        GroupMessage group = new GroupMessage(terry,"ABCD");
//
//        group.removeUsers(hazard);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a21testNullAdmin() {
//
//        GroupMessage group = new GroupMessage(null,"Dummy name");
//
//        groupService.createGroup(group);
//
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a22NullAdmin() {
//        userService.updateUserById(5,null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a23NullAdminInUpdateGroup() {
//        groupService.updateGroupById(5,new GroupMessage(terry,"ABCD"),null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void a24AdminNotAdminOfGroup() {
//        groupService.updateGroupById(5,new GroupMessage(terry,"ABCD"),lampard);
//    }


}
