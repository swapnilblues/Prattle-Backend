/*
 * Copyright (c) 2020. Manan Patel
 * All rights reserved
 */

package com.neu.prattle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.neu.prattle.controller.GroupController;
import com.neu.prattle.controller.MessageController;

import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.*;
import com.neu.prattle.websocket.ChatEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import com.neu.prattle.websocket.MessageDecoder;
import com.neu.prattle.websocket.MessageEncoder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.junit.*;

/**
 * Test class that tests the methods in the classes of websocket package.
 */
public class SimpleChatEndpointTestExample {

    private static User testUser1;
    private static User testUser2;
    private static User testUser3;
    private Message message;
    // Mocking Session to connect with websocket
    @Mock
    private Session session1;
    @Mock
    private Session session2;
    @Mock
    private Session session3;
    // Mocking basic which is used by session to send message
    @Mock
    private Basic basic;
    // To capture messages sent by Websockets
    private ArgumentCaptor<Object> valueCapture;
    // ChatEndpoints to test
    private ChatEndpoint chatEndpoint1;
    private ChatEndpoint chatEndpoint2;
    private ChatEndpoint chatEndpoint3;

    @Before
    public void setup() throws IOException, EncodeException {
        testUser1 = new User("ABC", "abc2@abc.com", "abc10", "al", "", "ABC");
        testUser2 = new User("BBB", "bb2@abc.com", "bb10", "bb", "", "BBB");
        testUser3 = new User("CCC", "cc2@abc.com", "cc10", "cc", "", "CCC");

        // Adding users
        UserService userService = UserServiceImpl.getInstance();
        userService.addUser(testUser1);
        userService.addUser(testUser2);
        userService.addUser(testUser3);
        // Creating session mocks
        session1 = mock(Session.class);
        session2 = mock(Session.class);
        session3 = mock(Session.class);

        basic = mock(Basic.class);

        message = Message.messageBuilder().build();

        chatEndpoint1 = new ChatEndpoint();
        chatEndpoint2 = new ChatEndpoint();
        chatEndpoint3 = new ChatEndpoint();

        // Capturing method calls using when and then
        when(session1.getBasicRemote()).thenReturn(basic);
        when(session2.getBasicRemote()).thenReturn(basic);
        when(session3.getBasicRemote()).thenReturn(basic);

        // Setting up argument captor to capture any Objects
        valueCapture = ArgumentCaptor.forClass(Object.class);
        // Defining argument captor to capture messages emitted by websockets
        doNothing().when(basic).sendObject(valueCapture.capture());
        // Capturing method calls to session.getId() using when and then
        when(session1.getId()).thenReturn("id1");
        when(session2.getId()).thenReturn("id2");
        when(session3.getId()).thenReturn("id3");
    }

    @Test
    public void testOnOpen() throws IOException, EncodeException {
        chatEndpoint1.onOpen(session1, testUser1.getName());

        // Finding the message with content 'Connected!'
        Optional<Message> m = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.getContent().equals("Connected!")).findAny();

        if (m.isPresent()) {
            assertEquals("Connected!", m.get().getContent());
            assertEquals(testUser1, m.get().getFrom());
        } else {
            fail();
        }
    }

    @Test
    public void testOnOpen2() throws IOException, EncodeException {
        chatEndpoint1.onOpen(session1, "dummy");
        // Finding the message with content 'Connected!'
        Optional<Message> m = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.getContent().equals("User dummy could not be found")).findAny();

        if (m.isPresent()) {
            assertEquals("User dummy could not be found", m.get().getContent());
        } else {
            fail();
        }


    }

    @Test
    public void testOnClose() throws IOException, EncodeException {
        chatEndpoint1.onOpen(session1, testUser1.getName());
        chatEndpoint2.onOpen(session2, testUser2.getName());

        chatEndpoint1.onClose(session1);

        // Finding the message with content 'Disconnected!'
        Optional<Message> m = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.getContent().equals("Disconnected!")).findAny();

        if (m.isPresent()) {
            assertEquals("Disconnected!", m.get().getContent());
            assertEquals(testUser1, m.get().getFrom());
        } else {
            fail();
        }
    }

    @After
    public void deleteDB() {
        UserService userService = UserServiceImpl.getInstance();
        userService.deleteUserById(testUser1.getId());
        userService.deleteUserById(testUser2.getId());
        userService.deleteUserById(testUser3.getId());
    }

    @Test
    public void testOnMessage() throws IOException, EncodeException {

        chatEndpoint1.onOpen(session1, testUser1.getName());
        chatEndpoint2.onOpen(session2, testUser2.getName());
        chatEndpoint3.onOpen(session3, testUser3.getName());

        message.setTo(testUser2);
        message.setContent("Hey");
        // Sending a message using onMessage method
        chatEndpoint1.onMessage(session1, message);

        // Finding messages with content hey
        Optional<Message> m = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.getContent().equals("Hey")).findAny();

        if (m.isPresent()) {
            assertEquals("Hey", m.get().getContent());
            assertEquals(testUser1.getName(), m.get().getFrom().getName());
            MessageService messageService = MessageServiceImpl.getInstance();

            messageService.deleteMessageById(m.get().getId());
        } else {
            fail();
        }
    }

    @Test
    public void testEncDec() {
        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        Message mess = new Message();
        mess.setTo(messi);
        mess.setFrom(suarez);
        mess.setContent("Test content");

        MessageEncoder encoder = new MessageEncoder();
        System.out.println(encoder.encode(mess));



        userService.getUserByUsername(messi.getUsername());

        assertTrue(encoder.encode(mess).contains("from"));
        assertTrue(encoder.encode(mess).contains("to"));
        assertTrue(encoder.encode(mess).contains("content"));
        assertTrue(encoder.encode(mess).contains("\"username\":\"messi10\""));
        assertTrue(encoder.encode(mess).contains("\"email\":\"messi@barca.com\""));
        assertTrue(encoder.encode(mess).contains("\"username\":\"suarez9\""));
        assertTrue(encoder.encode(mess).contains("\"email\":\"suarez@barca.com\""));
        assertTrue(encoder.encode(mess).contains("\"content\":\"Test content\""));
        MessageDecoder dec = new MessageDecoder();

        Message decMessage = dec.decode("{\"from\":{\"id\":1964,\"username\":\"BBB\",\"email\":\"bb2@abc.com\",\"password\":\"bb10\",\"status\":\"bb\",\"image\":\"\",\"name\":\"BBB\"},\"to\":{\"username\":\"ABC\"},\"content\":\"Test content\"}\n");

        assertEquals("From: BBB To: ABC Content: Test content At time:null Replies: []", decMessage.toString());


        assertTrue(decMessage.getFrom().equals(testUser2));
        assertTrue(decMessage.getTo().equals(testUser1));

        Message decMessage2 = dec.decode("{\"from\":{\"id\":1964,\"username\":\"BBB\",\"email\":\"bb2@abc.com\",\"password\":\"bb10\",\"status\":\"bb\",\"image\":\"\",\"name\":\"BBB\"},\"to\":{},\"content\":\"Test content\"}\n");
        assertEquals("From: BBB To: null Content: Test content At time:null Replies: []", decMessage2.toString());

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());
    }

    @Test
    public void test1() throws IOException, EncodeException {
        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        Message mess = new Message();
        mess.setTo(messi);
        mess.setFrom(suarez);
        mess.setContent("Test content");

        MessageEncoder encoder = new MessageEncoder();
        System.out.println(encoder.encode(mess));


        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");

        Stream<Message> m = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.getContent().equals("Connected!"));

        ArrayList<Message> lst = (ArrayList<Message>) m.collect(Collectors.toList());


        for (Message msg : lst) {
            assertTrue(msg.toString().contains("Content: Connected!"));
        }

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    @Test
    public void testOneToOneMsg() throws IOException, EncodeException {
        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        String encodedMessage = "{\"content\":\"Test content\",\"replies\":[],\"from\":{\"username\":\"messi10\"},\"to\":{\"username\":\"suarez9\"},\"dateTime\":null}\n";

        MessageDecoder decoder = new MessageDecoder();

        Message mess2 = decoder.decode(encodedMessage);

        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint1.onMessage(session1,mess2);


        Stream<Message> valCap = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.toString().contains("To"));

        ArrayList<Message> msgs = (ArrayList<Message>) valCap.collect(Collectors.toList());

        asserts(messi, suarez, msgs.get(3));

        MessageService messageService = MessageServiceImpl.getInstance();

        messageService.deleteMessageById(msgs.get(3).getId());

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    private void asserts(User fromUser, User toUser, Message msg){
        assertEquals(toUser.getName(), msg.getTo().getName());
        assertEquals(fromUser.getName(), msg.getFrom().getName());
        assertEquals("", msg.getTo().getPassword());
        assertEquals("", msg.getFrom().getPassword());
        assertEquals(toUser.getImage(), msg.getTo().getImage());
        assertEquals(fromUser.getImage(), msg.getFrom().getImage());
        assertEquals(toUser.getStatus(), msg.getTo().getStatus());
        assertEquals(fromUser.getStatus(), msg.getFrom().getStatus());
        assertEquals(toUser.getEmail(), msg.getTo().getEmail());
        assertEquals(fromUser.getEmail(), msg.getFrom().getEmail());
        assertEquals(toUser.getUsername(), msg.getTo().getUsername());
        assertEquals(fromUser.getUsername(), msg.getFrom().getUsername());
    }

    private void assertsGroup(User fromUser, GroupMessage toGroup, Message msg){
        assertTrue(msg.isContainsGroup());
        assertEquals(toGroup.getName(), msg.getChatGroup().getName());
        assertEquals(fromUser.getName(), msg.getFrom().getName());
        assertEquals("", msg.getFrom().getPassword());
        assertEquals(fromUser.getImage(), msg.getFrom().getImage());
        assertEquals(fromUser.getStatus(), msg.getFrom().getStatus());
        assertEquals(fromUser.getEmail(), msg.getFrom().getEmail());
        assertEquals(fromUser.getUsername(), msg.getFrom().getUsername());
        assertEquals(toGroup.getAdmin(), msg.getChatGroup().getAdmin());
        assertEquals(toGroup.getId(), msg.getChatGroup().getId());

        for(User user: msg.getChatGroup().getMembers()){
            assertTrue(toGroup.getMembers().contains(user));
        }
    }
    @Test
    public void testReply() throws IOException, EncodeException {
        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        String encodedMessage = "{\"content\":\"Parent msg\",\"replies\":[{\"content\":\"Child Msg 1\",\"replies\":[],\"from\":{\"username\":\"messi10\"},\"to\":{\"username\":\"suarez9\"},\"dateTime\":null}, {\"content\":\"Child Msg 1\",\"replies\":[],\"from\":{\"username\":\"suarez9\"},\"to\":{\"username\":\"messi10\"},\"dateTime\":null}],\"from\":{\"username\":\"messi10\"},\"to\":{\"username\":\"suarez9\"},\"dateTime\":null}\n";

        MessageDecoder decoder = new MessageDecoder();

        Message mess = decoder.decode(encodedMessage);


        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint1.onMessage(session1,mess);


        Stream<Message> valCap = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.toString().contains("To"));

        ArrayList<Message> msgs = (ArrayList<Message>) valCap.collect(Collectors.toList());



        asserts(messi, suarez, msgs.get(3));
        asserts(messi, suarez, msgs.get(3).getReplies().get(0));
        asserts(suarez, messi, msgs.get(3).getReplies().get(1));

        MessageService messageService = MessageServiceImpl.getInstance();

        messageService.deleteMessageById(msgs.get(3).getId());

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    @Test
    public void a25testMessageToGroupDec() throws IOException, EncodeException {

        MessageDecoder decoder = new MessageDecoder();
        MessageEncoder encoder = new MessageEncoder();

        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        GroupMessage group = new GroupMessage(messi,"Barca");
        GroupService groupService = GroupServiceImpl.getInstance();
        groupService.createGroup(group);
        groupService.addMemberToGroup(group, neymar, messi);
        groupService.addMemberToGroup(group, suarez, messi);


        String messageString = "{\"content\":\"Test content\",\"chatGroup\":{\"name\":\"Barca\"},\"replies\":[{\"content\":\"Child Msg 1\",\"from\":{\"username\":\"messi10\"}}, {\"content\":\"Child Msg 2\",\"from\":{\"username\":\"suarez9\"}}]}\n";
        Message mess = decoder.decode(messageString);

        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint3.onOpen(session3, "neymar11");

        chatEndpoint1.onMessage(session1,mess);


        Stream<Message> valCap = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.toString().contains("To"));

        ArrayList<Message> msgs = (ArrayList<Message>) valCap.collect(Collectors.toList());

        MessageService messageService = MessageServiceImpl.getInstance();

        assertsGroup(messi, group, msgs.get(6));
        assertsGroup(messi, group, msgs.get(6).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(6).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(7));
        assertsGroup(messi, group, msgs.get(7).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(7).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(8));
        assertsGroup(messi, group, msgs.get(8).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(8).getReplies().get(1));


        messageService.deleteMessageById(mess.getId());

        groupService.deleteGroupByAdmin(group, messi);

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());


    }


    @Test
    public void a25testMessageToGroupExchanges() throws IOException, EncodeException {

        MessageDecoder decoder = new MessageDecoder();

        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        GroupMessage group = new GroupMessage(messi,"Barca");
        GroupService groupService = GroupServiceImpl.getInstance();
        groupService.createGroup(group);
        groupService.addMemberToGroup(group, neymar, messi);
        groupService.addMemberToGroup(group, suarez, messi);


        String messageString = "{\"content\":\"Test content\",\"chatGroup\":{\"name\":\"Barca\"},\"replies\":[{\"content\":\"Child Msg 1\",\"from\":{\"username\":\"messi10\"}}, {\"content\":\"Child Msg 2\",\"from\":{\"username\":\"suarez9\"}}]}\n";
        Message mess = decoder.decode(messageString);

        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint3.onOpen(session3, "neymar11");

        chatEndpoint1.onMessage(session1,mess);


        Stream<Message> valCap = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.toString().contains("To"));

        ArrayList<Message> msgs = (ArrayList<Message>) valCap.collect(Collectors.toList());

        MessageService messageService = MessageServiceImpl.getInstance();

        assertsGroup(messi, group, msgs.get(6));
        assertsGroup(messi, group, msgs.get(6).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(6).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(7));
        assertsGroup(messi, group, msgs.get(7).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(7).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(8));
        assertsGroup(messi, group, msgs.get(8).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(8).getReplies().get(1));

        MessageController messageController = new MessageController();
        String messageList = messageController.getExchangedMessagesInGroup("Barca");

        assertTrue(messageList.contains("Test content"));
        assertTrue(messageList.contains("Child Msg 1"));
        assertTrue(messageList.contains("Child Msg 2"));

        messageService.deleteMessageById(mess.getId());

        groupService.deleteGroupByAdmin(group, messi);

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());


    }

    @Test
    public void a25testMessageToGroupWithPassword() throws IOException, EncodeException {

        MessageDecoder decoder = new MessageDecoder();

        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        GroupMessage group = new GroupMessage(messi,"Barca");
        GroupService groupService = GroupServiceImpl.getInstance();
        groupService.createGroup(group);
        GroupController gc = new GroupController();
        gc.addPasswordToGroup("Barca", "MesQueUnClub", group.getAdmin().getUsername());
        group = groupService.getGroupByName("Barca");
        groupService.addMemberToGroup(group, neymar, messi);
        groupService.addMemberToGroup(group, suarez, messi);


        String messageString = "{\"content\":\"Test content\",\"chatGroup\":{\"name\":\"Barca\",\"password\":\"MesQueUnClub\"},\"replies\":[{\"content\":\"Child Msg 1\",\"from\":{\"username\":\"messi10\"}}, {\"content\":\"Child Msg 2\",\"from\":{\"username\":\"suarez9\"}}]}\n";
        Message mess = decoder.decode(messageString);

        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint3.onOpen(session3, "neymar11");

        chatEndpoint1.onMessage(session1,mess);


        Stream<Message> valCap = valueCapture.getAllValues().stream()
                .map(val -> (Message) val)
                .filter(msg -> msg.toString().contains("To"));

        ArrayList<Message> msgs = (ArrayList<Message>) valCap.collect(Collectors.toList());

        MessageService messageService = MessageServiceImpl.getInstance();

        assertsGroup(messi, group, msgs.get(6));
        assertsGroup(messi, group, msgs.get(6).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(6).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(7));
        assertsGroup(messi, group, msgs.get(7).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(7).getReplies().get(1));
        assertsGroup(messi, group, msgs.get(8));
        assertsGroup(messi, group, msgs.get(8).getReplies().get(0));
        assertsGroup(suarez, group, msgs.get(8).getReplies().get(1));

        MessageController messageController = new MessageController();
        String messageList = messageController.getExchangedMessagesInGroup("Barca");

        assertTrue(messageList.contains("Test content"));
        assertTrue(messageList.contains("Child Msg 1"));
        assertTrue(messageList.contains("Child Msg 2"));

        messageService.deleteMessageById(mess.getId());

        groupService.deleteGroupByAdmin(group, messi);

        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());


    }

    @Test
    public void a25testMessageToGroupWithWrongPassword() throws IOException, EncodeException {

        MessageDecoder decoder = new MessageDecoder();

        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi");
        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez");
        User neymar = new User("neymar11", "neymar@psg.com", "njr11", "", "", "Neymar Jr");
        UserService userService = UserServiceImpl.getInstance();

        userService.addUser(messi);
        userService.addUser(neymar);
        userService.addUser(suarez);

        GroupMessage group = new GroupMessage(messi,"Barca");
        GroupService groupService = GroupServiceImpl.getInstance();
        groupService.createGroup(group);
        GroupController gc = new GroupController();
        gc.addPasswordToGroup("Barca", "MesQueUnClub", group.getAdmin().getUsername());
        group = groupService.getGroupByName("Barca");
        groupService.addMemberToGroup(group, neymar, messi);
        groupService.addMemberToGroup(group, suarez, messi);


        String messageString = "{\"content\":\"Test content\",\"chatGroup\":{\"name\":\"Barca\",\"password\":\"MesQueUnClub1\"},\"replies\":[{\"content\":\"Child Msg 1\",\"from\":{\"username\":\"messi10\"}}, {\"content\":\"Child Msg 2\",\"from\":{\"username\":\"suarez9\"}}]}\n";
        Message mess = decoder.decode(messageString);

        chatEndpoint1.onOpen(session1, "messi10");
        chatEndpoint2.onOpen(session2, "suarez9");
        chatEndpoint3.onOpen(session3, "neymar11");

        try {
            chatEndpoint1.onMessage(session1,mess);
        }
        catch (Exception e){
            assertEquals("Enter the correct password for Group Barca", e.getLocalizedMessage());
        }
        finally {
            groupService.deleteGroupByAdmin(group, messi);
            userService.deleteUserById(messi.getId());
            userService.deleteUserById(suarez.getId());
            userService.deleteUserById(neymar.getId());

        }



    }



}


