package com.neu.prattle;

import com.neu.prattle.controller.GroupController;
import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import java.util.List;

public class TestFindGroups {

    private GroupService groupService;
    private GroupController groupController;
    private UserService userService;
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
        willian = new User("hazard22", "willian@blues.com", "willian1", "rw", "", "willian",true);

        groupService = GroupServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        groupController = new GroupController();

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
    public void testFindGroup() {

        GroupMessage group = new GroupMessage(lampard,"group-lampard-admin");
        GroupMessage group1 = new GroupMessage(zola,"group-zola-admin",false);
        GroupMessage group2 = new GroupMessage(terry,"group-terry-admin",false);
        GroupMessage group3 = new GroupMessage(drogba,"group-drogba-admin",true);

        group.setFind(true);
        groupService.updateGroupById(group.getId(),group,group.getAdmin());

        groupService.createGroup(group);

        groupService.createGroup(group1);

        groupService.createGroup(group2);

        groupService.createGroup(group3);


        groupService.addMemberToGroup(group2,hazard,terry);

        groupService.addMemberToGroup(group3,hazard,drogba);

        groupService.addMemberToGroup(group3,willian,drogba);

        assertEquals(3,groupService.seeAllGroups(zola).size());
        assertEquals(3,groupService.seeAllGroups(terry).size());
        assertEquals(2,groupService.seeAllGroups(lampard).size());
        assertEquals(3,groupService.seeAllGroups(hazard).size());
        assertEquals(2,groupService.seeAllGroups(willian).size());

        String stringZola = groupController.seeAllGroups(zola.getUsername());
        String stringTerry = groupController.seeAllGroups(terry.getUsername());
        String stringLampard = groupController.seeAllGroups(lampard.getUsername());
        String stringHazard = groupController.seeAllGroups(hazard.getUsername());
        String stringWillian = groupController.seeAllGroups(willian.getUsername());

        System.out.println(stringZola);
        System.out.println(stringTerry);
        System.out.println(stringLampard);
        System.out.println(stringHazard);
        System.out.println(stringWillian);

        groupService.deleteGroupByAdmin(group,lampard);
        groupService.deleteGroupByAdmin(group1,zola);
        groupService.deleteGroupByAdmin(group2,terry);
        groupService.deleteGroupByAdmin(group3,drogba);

    }

    @Test
    public void testSeeAllMethodError() {

        assertEquals("err",groupController.seeAllGroups("abcd"));
    }
}
