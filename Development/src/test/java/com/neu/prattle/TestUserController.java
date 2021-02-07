package com.neu.prattle;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.neu.prattle.controller.UserController;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.main.PrattleApplication;
import com.neu.prattle.model.Message;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.websocket.MessageDecoder;
import com.neu.prattle.websocket.MessageEncoder;
import org.junit.*;

import com.neu.prattle.model.User;
import org.junit.runners.MethodSorters;


import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Test class that tests the UserController class methods.
 */
public class TestUserController {

    private static UserController as;
    private static UserService userServiceImpl;

    @BeforeClass
    public static void setUp() {
        as = new UserController();
        userServiceImpl = UserServiceImpl.getInstance();
    }

    @After
    public void deleteUser() {
        UserService userServiceImpl = UserServiceImpl.getInstance();
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName("abc9"));
    }

    @Test(expected = UserAlreadyPresentException.class)
    public void a10testException() {
        User user = new User("abc9", "abc9@abc.com", "abc10", "al", "", "ABC");

        as.createUserAccount(user);
        as.createUserAccount(user);
    }




    @Test
    public void a12testAddUser() {
        User user = new User("bb", "bb@abc.com", "bb10", "bb", "", "BBB");
        as.createUserAccount(user);
        assertTrue(as.getAllUsers().contains("bb"));
        as.deleteUserById(as.getUserIdByUserName("bb"));
        assertFalse(as.getAllUsers().contains("bb"));
    }

    @Test
    public void a13testDeleteUser() {
        User user = new User("bb", "bb@abc.com", "bb10", "bb", "", "BBB");
        as.createUserAccount(user);
        String a = as.getUserByUsername("bb");
        String[] arr = a.split(",");
        assertEquals(user.getUsername(),arr[1].substring(12,arr[1].length()-1));
        as.deleteUserById(as.getUserIdByUserName("bb"));
    }

    @Test
    public void a14testUpdateUser() {
        User user1 = new User("Eden", "eden@rm.com", "eh10", "lw", "", "Eden");
        as.createUserAccount(user1);

        List<User> users = userServiceImpl.getAllUsers();
        User user = users.get(0);
        int id = user.getId();
        String username = user.getUsername();
        user.setEmail("yahoo@gamil.com");
        as.updateUserById(id,user);
        User newUser = userServiceImpl.getUserByUsername(username);
        assertEquals("yahoo@gamil.com", newUser.getEmail());
        userServiceImpl.deleteUserById(userServiceImpl.getUserIdByUserName("Eden"));
    }

    @Test
    public void a15message() {
        User a = new User("a", "a@rm.com", "eh10", "lw", "", "a");
        User b = new User("b", "b@rm.com", "eh10", "lw", "", "b");
        Message message = new Message();
        message.setFrom(a);
        message.setTo(b);
        message.setContent("ab");

        assertTrue(message.toString().contains("From: a To: b Content: ab At time:null"));
    }

    @Test
    public void a16testMessageGetTo(){
        Message message = new Message();
        User b = new User("b", "b@rm.com", "eh10", "lw", "", "b");
        message.setTo(b);
        assertEquals("b",message.getTo().getUsername());
    }

    @Test
    public void a17testPrattle() {
        PrattleApplication prattleApplication = new PrattleApplication();
        Set<Class<?>> set =  prattleApplication.getClasses();
        assertEquals(3,set.size());
    }

    @Test
    public void a18messageBuilder() {
        User b = new User("b", "b@rm.com", "eh10", "lw", "", "b");
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder n = messageBuilder.setTo(b);
        assertEquals("b",n.build().getTo().getUsername());
    }

    @Test
    public void a19testHashCode() {
        User user = new User("abc1", "abc1@abc", "abc10", "al", "", "ABC");
        int i = user.hashCode();
        assertEquals(64609,i);
    }

//    @Test
//    public void a20testMessageEncoder() throws EncodeException {
//        MessageEncoder messageEncoder = new MessageEncoder();
//        messageEncoder.destroy();
//        assertEquals("{\"id\":0,\"content\":null,\"from\":null,\"to\":null,\"dateTime\":null}",messageEncoder.encode(new Message()));
//    }

    @Test
    public void a21testMessageDecoder() throws EncodeException {
        MessageDecoder messageDecoder = new MessageDecoder();
        messageDecoder.decode("");
        messageDecoder.destroy();
        messageDecoder.willDecode("");

        assertFalse(messageDecoder.willDecode(null));
        messageDecoder.init(new EndpointConfig() {
            @Override
            public List<Class<? extends Encoder>> getEncoders() {
                return null;
            }

            @Override
            public List<Class<? extends Decoder>> getDecoders() {
                return null;
            }

            @Override
            public Map<String, Object> getUserProperties() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void a222testNull(){

        assertNull(userServiceImpl.getUserByUsername(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void a23testNull(){

        assertEquals(-1,userServiceImpl.updateUserById(1,null));
    }

    @Test
    public void a24testNullMessage(){
        MessageEncoder messageEncoder = new MessageEncoder();
        assertEquals("{}",messageEncoder.encode(null));
    }

//    @Test
//    public void a25testNullMessage(){
//        MessageEncoder messageEncoder = new MessageEncoder();
//        Message message = new Message();
//        assertEquals("{\"id\":0,\"content\":null,\"from\":null,\"to\":null,\"dateTime\":null}",messageEncoder.encode(message));
//    }

    @Test
    public void a26testNullMessageDec(){
        MessageDecoder messageDecoder = new MessageDecoder();
        assertNull(messageDecoder.decode(null));
    }

    @Test
    public void a28testUserAuthenticationVerifyUserNotPresent(){
        User user = new User();
        user.setPassword("sajag12");
        user.setEmail("saj@gom.com");
        user.setUsername("s");

        UserController userController = new UserController();
        String string = userController.verifyUserAccount(user);
        assertEquals("500",string);
    }

    @Test
    public void a29testUserAuthenticationVerifyUserWrongEmail(){
        User user = new User();
        user.setPassword("sajag56");
        user.setEmail("saj56@gom.com");
        user.setUsername("sajag56");

        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(user);

        User checkUser = new User();
        checkUser.setUsername("sajag56");
        checkUser.setPassword("sajag56");
        checkUser.setEmail("saj5@gom.com");
        UserController userController = new UserController();
        String string = userController.verifyUserAccount(checkUser);

        User deleteUser = userService.getUserByUsername("sajag56");
        userService.deleteUserById(deleteUser.getId());
        assertNull(string);
    }

    @Test
    public void a30testUserAuthenticationVerifyUserWrongPassword(){
        User user = new User();
        user.setPassword("sajag56");
        user.setEmail("saj56@gom.com");
        user.setUsername("sajag56");

        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(user);

        User checkUser = new User();
        checkUser.setUsername("sajag56");
        checkUser.setPassword("sajag5");
        checkUser.setEmail("saj56@gom.com");
        UserController userController = new UserController();
        String string = userController.verifyUserAccount(checkUser);

        User deleteUser = userService.getUserByUsername("sajag56");
        userService.deleteUserById(deleteUser.getId());
        assertNull(string);
    }

    @Test
    public void a31testUserAuthenticationVerifyUserWrongEmailRightPassword(){
        User user = new User();
        user.setPassword("sajag56");
        user.setEmail("saj56@gom.com");
        user.setUsername("sajag56");

        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(user);

        User checkUser = new User();
        checkUser.setUsername("sajag56");
        checkUser.setPassword("sajag56");
        checkUser.setEmail("saj5@gom.com");
        UserController userController = new UserController();
        String string = userController.verifyUserAccount(checkUser);

        User deleteUser = userService.getUserByUsername("sajag56");
        userService.deleteUserById(deleteUser.getId());
        assertNull(string);
    }

    @Test
    public void a32testUserAuthenticationVerifyUserRightEmailWrongPassword(){
        User user = new User();
        user.setPassword("sajag56");
        user.setEmail("saj56@gom.com");
        user.setUsername("sajag56");

        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(user);

        User checkUser = new User();
        checkUser.setUsername("sajag56");
        checkUser.setPassword("sajag5");
        checkUser.setEmail("saj56@gom.com");
        UserController userController = new UserController();
        String string = userController.verifyUserAccount(checkUser);
        assertNull(string);
        User deleteUser = userService.getUserByUsername("sajag56");
        userService.deleteUserById(deleteUser.getId());
    }

    @Test
    public void a33testUserAuthenticationVerifyUserCorrectlyVerifyUser(){
        User user = new User();
        user.setPassword("sajag56");
        user.setEmail("saj56@gom.com");
        user.setUsername("sajag56");

        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(user);

        User checkUser = new User();
        checkUser.setUsername("sajag56");
        checkUser.setPassword("sajag56");
        checkUser.setEmail("saj56@gom.com");
        UserController userController = new UserController();
        String string = userController.verifyUserAccount(checkUser);

        User deleteUser = userService.getUserByUsername("sajag56");
        userService.deleteUserById(deleteUser.getId());
        assertTrue(string.contains("sajag56"));
    }

    @Test
    public void testGetAllUsers() {
        User zola = new User("zola25", "zola@blues.com", "zola1", "lw", "", "zola",true);
        User terry = new User("terry26", "terry@blues.com", "terry1", "cd", "", "terry",false);
        User lampard = new User("lampard8", "lampard@blues.com", "lampard1", "cm", "", "lampard",false);
        User drogba = new User("drogba11", "drogba@blues.com", "drogba1", "st", "", "lampard",true);
        User hazard = new User("hazard10", "hazard@blues.com", "eden1", "lw", "", "eden",true);

        userServiceImpl.addUser(zola);
        userServiceImpl.addUser(terry);
        userServiceImpl.addUser(lampard);
        userServiceImpl.addUser(drogba);
        userServiceImpl.addUser(hazard);

        userServiceImpl.addFollower(lampard,hazard);
        userServiceImpl.addFollowing(hazard,lampard);


        List<User> users = userServiceImpl.seeAllOtherUsers(hazard);

        String string = as.seeAllOtherUsers(hazard.getUsername());
        System.out.println(string);

        userServiceImpl.deleteUserById(zola.getId());
        userServiceImpl.deleteUserById(terry.getId());
        userServiceImpl.deleteUserById(lampard.getId());
        userServiceImpl.deleteUserById(drogba.getId());
        userServiceImpl.deleteUserById(hazard.getId());

        assertTrue(users.contains(zola));
        assertFalse(users.contains(terry));
        assertTrue(users.contains(lampard));
        assertTrue(users.contains(drogba));
    }
}
