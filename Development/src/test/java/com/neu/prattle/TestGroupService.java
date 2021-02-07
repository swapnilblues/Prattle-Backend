package com.neu.prattle;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;
import org.junit.*;

import static org.junit.Assert.*;
import java.util.List;

/**
 * Test class that tests the MessageService interface methods.
 */
public class TestGroupService {

    private static GroupService groupService;
    private static UserService userService;
    User zola;
    User terry;
    User lampard;
    User drogba;
    User hazard;
    User willian;

    @BeforeClass
    public static void setUpServices(){
        groupService = GroupServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
    }

    @Before
    public void setUp() {
        zola = new User("zola25", "zola@blues.com", "zola1", "lw", "", "zola",true);
        terry = new User("terry26", "terry@blues.com", "terry1", "cd", "", "terry",false);
        lampard = new User("lampard8", "lampard@blues.com", "lampard1", "cm", "", "lampard",false);
        drogba = new User("drogba11", "drogba@blues.com", "drogba1", "st", "", "drogba",true);
        hazard = new User("hazard10", "hazard@blues.com", "eden1", "lw", "", "eden",true);
        willian = new User("hazard22", "willian@blues.com", "willian1", "rw", "", "willian",true);


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

        GroupMessage group = new GroupMessage(lampard,"demo name");

        groupService.createGroup(group);

        groupService.addMemberToGroup(group,hazard,lampard);

        groupService.addMemberToGroup(group,willian,lampard);

        List<User> memberList = group.getMembers();

        assertTrue(memberList.contains(lampard));
        assertTrue(memberList.contains(hazard));
        assertTrue(memberList.contains(willian));

        groupService.removeMemberFromGroup(group,hazard,lampard);

        GroupMessage group1 = new GroupMessage(lampard,"demo name1");

        groupService.createGroup(group1);


        assertTrue(memberList.contains(lampard));
        assertFalse(memberList.contains(hazard));
        assertTrue(memberList.contains(willian));

        groupService.deleteGroupByAdmin(group,lampard);
        groupService.deleteGroupByAdmin(group1,lampard);
    }


    @Test
    public void a12updateGroupById() {

        GroupMessage group = new GroupMessage(hazard,"demo name");

        groupService.createGroup(group);

        groupService.addMemberToGroup(group,lampard,hazard);

        group.setName("group demo");
        groupService.updateGroupById(group.getId(),group,hazard);

        group = groupService.getGroupById(group.getId());

        assertEquals("group demo",group.getName());

        groupService.deleteGroupByAdmin(group,hazard);


    }

    @Test
    public void a13testIsAdmin() {

        GroupMessage group = new GroupMessage(lampard,"demo name");

        groupService.createGroup(group);

        assertTrue(groupService.isAdmin(group,lampard));

        assertFalse(groupService.isAdmin(group,hazard));

        groupService.deleteGroupByAdmin(group,lampard);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a14addAdminToGroup() {

        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");

        GroupMessage group = new GroupMessage(admin,"demo name");

        groupService.addMemberToGroup(group, admin,admin);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a15removeAdminFromGroup() {

        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");

        GroupMessage group = new GroupMessage(admin,"demo name");

        groupService.removeMemberFromGroup(group, admin,admin);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a16removeNotPresentGroup() {

        GroupMessage group = new GroupMessage(hazard,"demo name");

        groupService.deleteGroupByAdmin(group,hazard);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a17getGroupByIdNegative() {

        groupService.getGroupById(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a17getGroupByIdNotPresent() {

        groupService.getGroupById(Integer.MAX_VALUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a18testNullGroup() {

        groupService.addMemberToGroup(null,new User(),new User());

    }

    @Test(expected = IllegalArgumentException.class)
    public void a18testNullAdmin() {

        groupService.addMemberToGroup(new GroupMessage(),null,new User());

    }

    @Test(expected = IllegalArgumentException.class)
    public void a18testNullUser() {

        groupService.addMemberToGroup(new GroupMessage(),new User(),null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByNotAdmin() {

        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User user1 = new User("abc1", "abc1@abc.com", "abc10", "al", "", "ABC");

        GroupMessage group = new GroupMessage(admin,"demo name");

        groupService.removeMemberFromGroup(group,user1,user1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a20testNullGroupName() {

        User admin = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User user1 = new User("abc1", "abc1@abc.com", "abc10", "al", "", "ABC");

        GroupMessage group = new GroupMessage(admin,null);

        groupService.removeMemberFromGroup(group,admin,user1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a20AddUserToGroupTwice() {

        GroupMessage group = new GroupMessage(lampard,"Chelsea");

        group.addUsers(hazard);
        group.addUsers(hazard);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a20removeUserNotPartOfGroup() {

        GroupMessage group = new GroupMessage(terry,"ABCD");

        group.removeUsers(hazard);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a21testNullAdmin() {

        GroupMessage group = new GroupMessage(null,"Dummy name");

        groupService.createGroup(group);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a22NullAdmin() {
        userService.updateUserById(5,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a23NullAdminInUpdateGroup() {
        groupService.updateGroupById(5,new GroupMessage(terry,"ABCD"),null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a24AdminNotAdminOfGroup() {
        groupService.updateGroupById(5,new GroupMessage(terry,"ABCD"),lampard);
    }

}
