package com.neu.prattle;

import java.util.List;

import com.neu.prattle.controller.UserController;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.Message;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.After;
import org.junit.Test;

import com.neu.prattle.model.User;
import org.mindrot.jbcrypt.BCrypt;


import static org.junit.Assert.*;

/**
 * Test class that tests the UserService interface methods.
 */

public class TestUserService {


    @After
    public void deleteUser() {
        UserService userServiceImpl = UserServiceImpl.getInstance();
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName("abc1"));

    }
    @Test(expected = UserAlreadyPresentException.class)
    public void a11testException() {
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("abc1"));
        User user = new User("abc1", "abc1@abc.com", "abc10", "al", "", "ABC");
        UserService userServiceImpl = UserServiceImpl.getInstance();
        userServiceImpl.addUser(user);
        userServiceImpl.addUser(user);
    }

    @Test
    public void a13testAddUser() {
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("bb"));
        User user = new User("bb", "bb@abc.com", "bb10", "bb", "", "BBB");
        UserService userServiceImpl = UserServiceImpl.getInstance();
        userServiceImpl.addUser(user);
        assertEquals("bb@abc.com", user.getEmail());
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName(user.getUsername()));
        userService.deleteUserById(userService.getUserIdByUserName("bb"));
    }

    @Test
    public void a14testAddUser() {
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
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName(user1.getUsername()));
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName(user2.getUsername()));
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName(user3.getUsername()));
        userService.deleteUserById(userService.getUserIdByUserName("Eden"));
        userService.deleteUserById(userService.getUserIdByUserName("abc5"));
        userService.deleteUserById(userService.getUserIdByUserName("Thorgan"));
    }

    @Test
    public void a15updateUser() {
        UserService userServiceImpl = UserServiceImpl.getInstance();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("wayne"));
        User user1 = new User("wayne", "wayne@db.com", "wr10", "st", "", "Wayne");
        userServiceImpl.addUser(user1);
        List<User> users = userServiceImpl.getAllUsers();
        User user = users.get(0);
        int id = user.getId();
        String username = user.getUsername();
        user.setEmail("yahoo@gamil.com");
        userServiceImpl.updateUserById(id, user);
        User newUser = userServiceImpl.getUserByUsername(username);
        assertEquals("yahoo@gamil.com", newUser.getEmail());
        userService.deleteUserById(userService.getUserIdByUserName("wayne"));
    }

    @Test
    public void a16updateUserWithNullValues() {
        UserService userServiceImpl = UserServiceImpl.getInstance();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("abc3"));
        userServiceImpl.addUser(new User("abc3", "abc3@abc.com", "abc10", "al", "", "ABC"));
        List<User> users = userServiceImpl.getAllUsers();
        User user = users.get(0);
        int id = user.getId();
        String username = user.getUsername();
        user.setEmail("yahoo@gamil.com");
        User newUser = new User(null, null, null, null, null, null);
        userServiceImpl.updateUserById(id, newUser);
        assertNull(newUser.getEmail());
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName("abc3"));
        userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("abc3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a17testEmptyAddUser(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        User newUser = new User(null, null, null, null, null, null);
        userServiceImpl.addUser(newUser);
        UserService userService = UserServiceImpl.getInstance();
    }

    @Test
    public void a18testUpdateUserWrongId(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        User newUser = new User(null, null, null, null, null, null);
        assertEquals(-1,userServiceImpl.updateUserById(1,newUser));
        UserService userService = UserServiceImpl.getInstance();
    }

    @Test(expected = IllegalArgumentException.class)
    public void a19getUserByUserName(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertNull(userServiceImpl.getUserByUsername("jake"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a20getUserByUserNameNull(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertNull(userServiceImpl.getUserByUsername(null));
    }

    @Test
    public void a21getUserIdByUserName(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(-1,userServiceImpl.getUserIdByUserName("jake"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a21getUserIdByUserNameNull(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        userServiceImpl.getUserIdByUserName(null);
        assertEquals(0,userServiceImpl.getUserIdByUserName(null));
    }

    @Test
    public void a22delUserById(){
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(-1,userServiceImpl.deleteUserById(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a24testControllerDeleteUserById(){
        UserController userController = new UserController();
        userController.deleteUserById(0);
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(0,userServiceImpl.deleteUserById(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a25testControllerGetUserIdByUsername(){
        UserController userController = new UserController();
        userController.getUserIdByUserName("jake");
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(0,userServiceImpl.deleteUserById(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a26testControllerGetUserByUsername(){
        UserController userController = new UserController();
        userController.getUserByUsername("jake");
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(0,userServiceImpl.deleteUserById(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a27testControllerUpdateUserById(){
        UserController userController = new UserController();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("marcus"));
        User user1 = new User("marcus", "marcus@mu.com", "mr10", "lw", "", "Marcus");
        userController.updateUserById(1,user1);
        UserService userServiceImpl = UserServiceImpl.getInstance();
        assertEquals(0,userServiceImpl.deleteUserById(-1));
        userService.deleteUserById(userService.getUserIdByUserName("marcus"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void a24testAddUserPasswordNull(){
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
        User user = new User();
        user.setPassword(null);
        user.setEmail("saj@gom.com");
        user.setUsername("sajag12");

        userService.addUser(user);
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a25testAddUserEmailNull(){
        User user = new User();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
        user.setPassword("sajag12");
        user.setEmail(null);
        user.setUsername("sajag12");

        userService.addUser(user);
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a26testAddUserUsernameNull(){
        User user = new User();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
        user.setPassword("sajag12");
        user.setEmail("saj@gom.com");
        user.setUsername(null);

        userService.addUser(user);
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
    }

    @Test(expected = IllegalStateException.class)
    public void a27testAddEmailInvalid(){
        UserService userService = UserServiceImpl.getInstance();
        int response = userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
        assertEquals(-1,response);
        User user = new User();
        user.setPassword("sajag121");
        user.setEmail("saj@gom");
        user.setUsername("sajag12");
        userService.addUser(user);
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
    }

    @Test
    public void a28testUserHashedPassword(){
        User user = new User();
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
        user.setPassword("sajag12");
        user.setEmail("saj@gom.com");
        user.setUsername("sajag12");

        userService.addUser(user);
        User savedUser = userService.getUserByUsername("sajag12");
        assertEquals( "sajag12",savedUser.getPassword());
        userService.deleteUserById(userService.getUserIdByUserName("sajag12"));
    }

    @Test
    public void testUserBuilder() {

        User.UserBuilder userBuilder = new User.UserBuilder();
        User user = userBuilder.build();
        userBuilder = userBuilder.setName("Hakim Ziyech");
        userBuilder = userBuilder.setPassword("ziyech123");
        userBuilder = userBuilder.setEmail("ziyech@chelsea.com");
        userBuilder = userBuilder.setImage("");
        userBuilder = userBuilder.setUsername("ziyech");
        userBuilder.setFind(true);
        assertTrue(user.toString().contains("ziyech"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getInformationNotByFollower() {

        User user = new User("bb", "bb@abc.com", "bb10", "bb", "", "BBB");
        User user1 = new User("bb1", "bb1@abc.com", "bb10", "bb", "", "BBB");
        user.getDetails(user1);
    }
}
