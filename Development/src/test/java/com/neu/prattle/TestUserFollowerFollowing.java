package com.neu.prattle;

import java.util.List;

import com.neu.prattle.controller.UserController;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.neu.prattle.model.User;



import static org.junit.Assert.*;

/**
 * Test class that tests the Follow User feature in User services.
 */
public class TestUserFollowerFollowing {


    @Before
    public void a11testAddUser() {
        UserService userServiceImpl = UserServiceImpl.getInstance();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("Eden"));
        userService.deleteUserById(userService.getUserIdByUserName("abc5"));
        userService.deleteUserById(userService.getUserIdByUserName("Thorgan"));
        User user1 = new User("Eden", "eden@rm.com", "eh10", "lw", "", "Eden");
        User user2 = new User("abc5", "abc5@abc.com", "abc10", "al", "", "ABC");
        User user3 = new User("Thorgan", "thorgan@bvb.com", "th10", "lw", "", "Thorgan");
        userServiceImpl.addUser(user1);
        userServiceImpl.addUser(user2);
        userServiceImpl.addUser(user3);
        List<User> users = userServiceImpl.getAllUsers();
        assertEquals("Eden", userServiceImpl.getUserByUsername("Eden").getUsername());
        assertEquals("abc5", userServiceImpl.getUserByUsername("abc5").getUsername());
        assertEquals("Thorgan", userServiceImpl.getUserByUsername("Thorgan").getUsername());

    }

    @After
    public void a21deletePrevUsers() {
        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");
        userService.deleteUserById(userService.getUserIdByUserName(user1.getUsername()));
        userService.deleteUserById(userService.getUserIdByUserName(user2.getUsername()));
        userService.deleteUserById(userService.getUserIdByUserName(user3.getUsername()));
        UserController userController = new UserController();
        String users = userController.getAllUsers();
        assertFalse(users.contains("Eden"));
    }


    @Test
    public void a12addFollower() {
        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void a13alreadyAddedFollower() {

        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        userService.addFollower(user1,user2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a14alreadyAddedFollowing() {

        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        userService.addFollowing(user2,user1);
    }

    @Test
    public void a15testGetInformationOfFollowing() {

        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        String details = userService.getInformation(user1,user2);

        assertTrue(details.contains(user2.getUsername()));
        assertTrue(details.contains(user1.getUsername()));
    }

    @Test
    public void a17removeFollower() {

        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        user3 = userService.getUserByUsername("Thorgan");

        //user2 doesn't follow user1 anymore
        userService.removeFollower(user1,user2);
        userService.removeFollowing(user2,user1);

        assertFalse(user1.getFollowers().contains(user2.getUsername()));
        assertFalse(user2.getFollowings().contains(user1.getUsername()));

        //user1 doesn't follows user3 anymore

        userService.removeFollower(user3,user1);
        userService.removeFollowing(user1,user3);

        assertFalse(user3.getFollowers().contains(user1.getUsername()));
        assertFalse(user1.getFollowings().contains(user3.getUsername()));

        //user2 doesn't follows user3 anymore
        userService.removeFollower(user3,user2);
        userService.removeFollowing(user2,user3);

        assertFalse(user3.getFollowers().contains(user2.getUsername()));
        assertFalse(user2.getFollowings().contains(user3.getUsername()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void a18testGetInformationOfNotFollowing() {

        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        User user3 = userService.getUserByUsername("Thorgan");

        //user2 follows user1
        userService.addFollower(user1,user2);
        userService.addFollowing(user2,user1);

        assertTrue(user1.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user1.getUsername()));

        //user1 follows user3
        userService.addFollower(user3,user1);
        userService.addFollowing(user1,user3);

        assertTrue(user3.getFollowers().contains(user1.getUsername()));
        assertTrue(user1.getFollowings().contains(user3.getUsername()));

        //user2 follows user3
        userService.addFollower(user3,user2);
        userService.addFollowing(user2,user3);

        assertTrue(user3.getFollowers().contains(user2.getUsername()));
        assertTrue(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        user3 = userService.getUserByUsername("Thorgan");

        //user2 doesn't follow user1 anymore
        userService.removeFollower(user1,user2);
        userService.removeFollowing(user2,user1);

        assertFalse(user1.getFollowers().contains(user2.getUsername()));
        assertFalse(user2.getFollowings().contains(user1.getUsername()));

        //user1 doesn't follows user3 anymore

        userService.removeFollower(user3,user1);
        userService.removeFollowing(user1,user3);

        assertFalse(user3.getFollowers().contains(user1.getUsername()));
        assertFalse(user1.getFollowings().contains(user3.getUsername()));

        //user2 doesn't follows user3 anymore
        userService.removeFollower(user3,user2);
        userService.removeFollowing(user2,user3);

        assertFalse(user3.getFollowers().contains(user2.getUsername()));
        assertFalse(user2.getFollowings().contains(user3.getUsername()));

        //actual test
        userService = UserServiceImpl.getInstance();
        user1 = userService.getUserByUsername("Eden");
        user2 = userService.getUserByUsername("abc5");
        userService.getInformation(user1,user2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a19removeNotAFollower() {
        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        userService.removeFollower(user1,user2);

    }

    @Test(expected = IllegalArgumentException.class)
    public void a20removeNotAFollowing() {
        UserService userService = UserServiceImpl.getInstance();
        User user1 = userService.getUserByUsername("Eden");
        User user2 = userService.getUserByUsername("abc5");
        userService.removeFollowing(user1,user2);
    }

}
