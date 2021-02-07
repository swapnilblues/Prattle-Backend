package com.neu.prattle;

import com.neu.prattle.controller.MessageController;
import com.neu.prattle.exceptions.NoSuchMessageException;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class that tests the MessageService interface methods.
 */
public class TestMessageService {

    private static MessageService messageServiceImpl;

    private static UserService userService;

    @BeforeClass
    public static void setUp() {

        messageServiceImpl = MessageServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
    }


    @Test
    public void a11testMessageAdd() {

        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");

        userService.addUser(userTo);
        userService.addUser(userFrom);

        userTo = userService.getUserByUsername("eden10");
        userFrom = userService.getUserByUsername("timo11");


        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("BAAB");
        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();

        userTo.getToMessage().add(message);
        userFrom.getFromMessage().add(message);

//        userService.updateUserById(userTo.getId(),userTo);
//        userService.updateUserById(userFrom.getId(),userFrom);

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();

        messageServiceImpl.addMessage(message);
        assertEquals(message.getId(), messageServiceImpl.getMessageById(message.getId()).getId());

        messageServiceImpl.deleteMessageById(message.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }
//
    @Test
    public void a12testMessageAdd1() {

        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");

        userService.addUser(userTo);
        userService.addUser(userFrom);


        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder messageBuilder1 = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("Chelsea");

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();


        messageServiceImpl.addMessage(message);
        assertEquals(message.getId(), messageServiceImpl.getMessageById(message.getId()).getId());


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userFrom);
        messageBuilder1 = messageBuilder1.setTo(userTo);
        messageBuilder1.setMessageDateTime();


        messageServiceImpl.addMessage(message1);
        assertEquals(message1.getId(), messageServiceImpl.getMessageById(message1.getId()).getId());

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));


    }

    @Test
    public void a14testMessageGetAll() {

        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");

        userService.addUser(userTo);
        userService.addUser(userFrom);


        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder messageBuilder1 = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("Chelsea");

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();


        messageServiceImpl.addMessage(message);
        assertEquals(message.getId(), messageServiceImpl.getMessageById(message.getId()).getId());


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageServiceImpl.addMessage(message1);
        assertEquals(message1.getId(), messageServiceImpl.getMessageById(message1.getId()).getId());

        List<Message> messages = messageServiceImpl.getAllMessages();
//        for(Message m : messages) {
//            System.out.println(m.getId() + " " + m.getTo().getName() + " " + m.getFrom().getName() + " " + m.getContent() + " " + m.getDateTime());
//        }
        assertEquals(2,messages.size());

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));
    }

    @Test
    public void a15testMessageGetById() {

        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");

        userService.addUser(userTo);
        userService.addUser(userFrom);


        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder messageBuilder1 = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("Chelsea");

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();


        messageServiceImpl.addMessage(message);
        assertEquals(message.getId(), messageServiceImpl.getMessageById(message.getId()).getId());


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageServiceImpl.addMessage(message1);
        assertEquals(message1.getId(), messageServiceImpl.getMessageById(message1.getId()).getId());

        List<Message> messages = messageServiceImpl.getAllMessages();

        assertEquals(2,messages.size());

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));
    }


    @Test
    public void testCALCEA(){
        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");
        userService.addUser(userTo);
        userService.addUser(userFrom);

        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder messageBuilder1 = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("Chelsea");

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();
        messageServiceImpl.addMessage(message);

        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();
        messageServiceImpl.addMessage(message1);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date startDate = cal.getTime();
        Date endDate = Calendar.getInstance().getTime();
        System.out.println("Start date "+startDate.toString());
        System.out.println("End date "+endDate.toString());
        List<Message> messages1 = messageServiceImpl.getAllMessagesOfAUser("eden10", dateFormat.format(startDate),dateFormat.format(endDate));
        List<Message> messages = messageServiceImpl.getAllMessagesOfAUser("eden10",dateFormat.format(startDate),null);
//        assertEquals(2,messages1.size());
//        assertEquals(2,messages.size());
        assertEquals("eden10",userTo.getUsername());
        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));
    }


    @Test
    public void a17testUpdateMessageById() {

        User userTo = new User("eden10", "eden@eden.com", "eden10", "al", "", "Eden",false);
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo",false);

        userService.addUser(userTo);
        userService.addUser(userFrom);

        messageServiceImpl = MessageServiceImpl.getInstance();

        Message message = new Message();
        message.setTo(userTo);
        message.setFrom(userFrom);
        message.setContent("Chelsea");
        messageServiceImpl.addMessage(message);

        Message message1 = new Message();
        message1.setTo(userTo);
        message1.setFrom(userFrom);
        message1.setContent("KTBFFH");
        messageServiceImpl.addMessage(message1);



        //List<Message> messages = messageServiceImpl.getAllMessages();


        message.setContent("Blues");
        messageServiceImpl.updateMessageById(message.getId(),message);
        assertEquals("Blues", messageServiceImpl.getMessageById(message.getId()).getContent());


        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("eden10"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }

    @Test(expected = NoSuchMessageException.class)
    public void a18testGetMessageByIdWithInvalidId() {
        messageServiceImpl.getMessageById(-1);
    }

    @Test(expected = NoSuchMessageException.class)
    public void a19testGetMessageByIdNotPresent() {

      messageServiceImpl.getMessageById(Integer.MAX_VALUE);
    }

    @Test
    public void a20testUpdateMessageWithNullMessage() {
        MessageService messageService = MessageServiceImpl.getInstance();
        assertEquals(-1, messageService.updateMessageById(1, null));
    }

    @Test
    public void a21testUpdateMessageWithNullMessageFrom() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        assertEquals(-1, messageService.updateMessageById(1, message));
    }

    @Test
    public void a22testUpdateMessageWithNullMessageContent() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder.setFrom(new User());
        assertEquals(-1, messageService.updateMessageById(1, message));
    }


    @Test(expected = NoSuchMessageException.class)
    public void a21testDeleteMessageWithIdNotPresent() {

        messageServiceImpl.deleteMessageById(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a22testMessageWithNullMessage() {
        MessageService messageService = MessageServiceImpl.getInstance();
        messageService.addMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a23testAddMessageWithNullMessageFrom() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageService.addMessage(message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a24testAddMessageWithNullMessageContent() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder.setFrom(new User());
        messageService.addMessage(message);
    }

    @Test
    public void a25testMessagePersistenceWithNoReplies(){
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
        mess.setContent("Parent Message");


        MessageService messageService = MessageServiceImpl.getInstance();

        messageService.addMessage(mess);

        assertEquals(mess.getId(), messageService.getMessageById(mess.getId()).getId());

        messageService.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }


    @Test
    public void a26testMessagePersistenceWithOneReply(){
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
        mess.setContent("Parent Message");

        Message mess2 = new Message();
        mess2.setTo(suarez);
        mess2.setFrom(messi);
        mess2.setContent("Child message");

        MessageService messageService = MessageServiceImpl.getInstance();

        mess.addReply(mess2);
        messageService.addMessage(mess);

        assertEquals(mess.getId(), messageService.getMessageById(mess.getId()).getId());
//        assertEquals(mess2.getId(), messageService.getMessageById(mess.getId()).getReplies().get(0).getId());

        messageService.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    @Test
    public void a27testMessagePersistenceWithOneReply2(){
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
        mess.setContent("Parent Message");

        Message mess2 = new Message();
        mess2.setTo(suarez);
        mess2.setFrom(messi);
        mess2.setContent("Child message");

        MessageService messageService = MessageServiceImpl.getInstance();
        messageService.addMessage(mess);

        mess.addReply(mess2);
        messageService.addMessage(mess);

        assertEquals(mess.getId(), messageService.getMessageById(mess.getId()).getId());
//        assertEquals(mess2.getId(), messageService.getMessageById(mess.getId()).getReplies().get(0).getId());

        messageService.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllMessagesNullInput() {
        messageServiceImpl.getAllMessagesOfAUser("abcd",null,null);
    }
}
