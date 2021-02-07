package com.neu.prattle;

import com.neu.prattle.controller.MessageController;
import com.neu.prattle.exceptions.NoSuchMessageException;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class that tests the MessageService interface methods.
 */
public class TestZMessageController {

    private static MessageService messageServiceImpl;
    private static MessageController messageController;
    private static UserService userService;

    @BeforeClass
    public static void setUp() {
        messageController = new MessageController();
        messageServiceImpl = MessageServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
    }

    @Test
    public void a11testMessageAdd() {


        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");
        userService.addUser(userTo);
        userService.addUser(userFrom);

        userTo = userService.getUserByUsername("billy");
        userFrom = userService.getUserByUsername("timo11");


        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message.MessageBuilder messageBuilder1 = new Message.MessageBuilder();

        //insert first message
        Message message = messageBuilder.build();
        messageBuilder = messageBuilder.setMessageContent("BAAB");
        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();

        userTo.getToMessage().add(message);
        userFrom.getFromMessage().add(message);

        messageBuilder = messageBuilder.setFrom(userFrom);
        messageBuilder = messageBuilder.setTo(userTo);
        messageBuilder.setMessageDateTime();
        messageController.addMessage(message);

        //insert second message
        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("BAAB1");
        messageBuilder1 = messageBuilder1.setFrom(userFrom);
        messageBuilder1 = messageBuilder1.setTo(userTo);
        messageBuilder1.setMessageDateTime();

        userTo.getToMessage().add(message1);
        userFrom.getFromMessage().add(message1);


        messageController.addMessage(message1);


        String messages = messageController.getAllMessages();

        assertTrue(messages.contains("BAAB"));

        messageController.deleteMessageById(message.getId());
        messageController.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }



    @Test
    public void a14testMessageGetAll() {

        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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
        assertEquals(message.getId(),messageServiceImpl.getMessageById(message.getId()).getId());


        Message message1 = messageBuilder1.build();
        messageBuilder = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageServiceImpl.addMessage(message1);
        assertEquals(message1.getId(),messageServiceImpl.getMessageById(message1.getId()).getId());


        String messages = messageController.getAllMessages();

        assertTrue(messages.contains("billy"));
        assertTrue(messages.contains("timo11"));

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));
    }

    @Test
    public void a15testMessageGetById() {

        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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


        messageController.addMessage(message);


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();

        messageController.addMessage(message1);

        List<Message> messages = messageServiceImpl.getAllMessages();

        int id = messages.get(0).getId();

        String result = messageController.getMessageById(id);

        assertTrue(result.contains("timo11"));
        assertTrue(result.contains("billy"));
        assertTrue(result.contains("Chelsea"));

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));
    }

    @Test
    public void a17testUpdateMessageById() {

        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
        User userFrom = new User("timo11", "timo11@eden.com", "timo11", "al", "", "Timo");

        userService.addUser(userTo);
        userService.addUser(userFrom);


        Message message = new Message();
        message.setTo(userTo);
        message.setFrom(userFrom);
        message.setContent("Chelsea");
        messageController.addMessage(message);

        Message message1 = new Message();
        message1.setTo(userTo);
        message1.setFrom(userFrom);
        message1.setContent("KTBFFH");
        messageController.addMessage(message1);


        messageServiceImpl = MessageServiceImpl.getInstance();
        //List<Message> messages = messageServiceImpl.getAllMessages();


        message.setContent("Blues");
        messageController.updateMessageById(message.getId(),message);
        assertEquals("Blues", messageServiceImpl.getMessageById(message.getId()).getContent());

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void a20testUpdateMessageWithNullMessage() {
        messageController.updateMessageById(1,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a21testUpdateMessageWithNullMessageFrom() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageController.updateMessageById(1,message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void a22testUpdateMessageWithNullMessageContent() {
        MessageService messageService = MessageServiceImpl.getInstance();
        Message.MessageBuilder messageBuilder = new Message.MessageBuilder();
        Message message = messageBuilder.build();
        messageBuilder.setFrom(new User());
        messageController.updateMessageById(1,message);
    }

    @Test
    public void a22testUpdateMessageWithInvalidId() {
        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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


        messageController.addMessage(message);


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageController.addMessage(message1);

        List<Message> messages = messageServiceImpl.getAllMessages();
        assertEquals(2,messages.size());

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());

        messages = messageServiceImpl.getAllMessages();
        assertEquals(0,messages.size());

        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }

    @Test()
    public void a23testGetMessageByInvalidId() {
        MessageController messageController = new MessageController();
        assertEquals("500",messageController.getMessageById(Integer.MAX_VALUE));
    }

    @Test

    public void testGetReplyForAMessage() {
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

        Message reply = new Message();
        reply.setTo(messi);
        reply.setFrom(suarez);
        reply.setContent("Reply");

        mess.addReply(reply);
        messageServiceImpl.addMessage(mess);
        String replies = messageController.getRepliesForMessage(mess.getId());
        assertTrue(replies.contains("Reply"));

        messageServiceImpl.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }


@Test
    public void testMessageControllerGetReplies() {

        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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
        messageController.addMessage(message);


        Message reply = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("Reply");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();

        messageController.addMessage(reply);

        List<Message> replies = new ArrayList<>();
        replies.add(reply);
        message.setReplies(replies);

        messageServiceImpl.updateMessageById(message.getId(),message);

        String string = messageController.getRepliesForMessage(message.getId());

        assertTrue(string.contains("Reply"));

        messageServiceImpl.deleteMessageById(reply.getId());
        messageServiceImpl.deleteMessageById(message.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }

//    @Test
//    public void a26testMessageControllerForSubscriptionPlan(){
//        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi","basic");
//        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez","basic");
//        UserService userService = UserServiceImpl.getInstance();
//
//        userService.addUser(messi);
//        userService.addUser(suarez);
//
//        Message mess = new Message();
//        mess.setTo(messi);
//        mess.setFrom(suarez);
//        mess.setContent("aaaa");
//
//        Message mess2 = new Message();
//        mess2.setTo(suarez);
//        mess2.setFrom(messi);
//        mess2.setContent("bbbb");
//
//        Message mess3 = new Message();
//        mess3.setTo(suarez);
//        mess3.setFrom(messi);
//        mess3.setContent("bbbb");
//
//        Message mess4 = new Message();
//        mess4.setTo(suarez);
//        mess4.setFrom(messi);
//        mess4.setContent("bbbb");
//
//        Message mess5 = new Message();
//        mess5.setTo(suarez);
//        mess5.setFrom(messi);
//        mess5.setContent("bbbb");
//
//        Message mess6 = new Message();
//        mess6.setTo(suarez);
//        mess6.setFrom(messi);
//        mess6.setContent("sfafdd");
//
//        MessageService messageService = MessageServiceImpl.getInstance();
//        messageService.addMessage(mess);
//        messageService.addMessage(mess2);
//        messageService.addMessage(mess3);
//        messageService.addMessage(mess4);
//        messageService.addMessage(mess5);
//        messageService.addMessage(mess6);
//
//        String messages = messageController.getExchangedMessages("messi10","suarez9");
//
//        System.out.println(messages);
//
//        int count = 0;
//
//        while(messages.contains("content")){
//            messages = messages.replaceFirst("content","");
//            count++;
//        }
//
//        assertEquals(5,count);
//
//        messageService.deleteMessageById(mess.getId());
//        messageService.deleteMessageById(mess2.getId());
//        messageService.deleteMessageById(mess3.getId());
//        messageService.deleteMessageById(mess4.getId());
//        messageService.deleteMessageById(mess5.getId());
//        messageService.deleteMessageById(mess6.getId());
//
//        userService.deleteUserById(messi.getId());
//        userService.deleteUserById(suarez.getId());
//
//    }
//
//    @Test
//    public void a27testMessageControllerForSubscriptionPlanMixed1(){
//        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi","basic");
//        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez","premium");
//        UserService userService = UserServiceImpl.getInstance();
//
//        userService.addUser(messi);
//        userService.addUser(suarez);
//
//        Message mess = new Message();
//        mess.setTo(messi);
//        mess.setFrom(suarez);
//        mess.setContent("aaaa");
//
//        Message mess2 = new Message();
//        mess2.setTo(suarez);
//        mess2.setFrom(messi);
//        mess2.setContent("bbbb");
//
//        Message mess3 = new Message();
//        mess3.setTo(suarez);
//        mess3.setFrom(messi);
//        mess3.setContent("bbbb");
//
//        Message mess4 = new Message();
//        mess4.setTo(suarez);
//        mess4.setFrom(messi);
//        mess4.setContent("bbbb");
//
//        Message mess5 = new Message();
//        mess5.setTo(suarez);
//        mess5.setFrom(messi);
//        mess5.setContent("bbbb");
//
//        Message mess6 = new Message();
//        mess6.setTo(suarez);
//        mess6.setFrom(messi);
//        mess6.setContent("sfafdd");
//
//        MessageService messageService = MessageServiceImpl.getInstance();
//        messageService.addMessage(mess);
//        messageService.addMessage(mess2);
//        messageService.addMessage(mess3);
//        messageService.addMessage(mess4);
//        messageService.addMessage(mess5);
//        messageService.addMessage(mess6);
//
//        String messages = messageController.getExchangedMessages("messi10","suarez9");
//
//        System.out.println(messages);
//
//        int count = 0;
//
//        while(messages.contains("content")){
//            messages = messages.replaceFirst("content","");
//            count++;
//        }
//
//        assertEquals(5,count);
//
//        messageService.deleteMessageById(mess.getId());
//        messageService.deleteMessageById(mess2.getId());
//        messageService.deleteMessageById(mess3.getId());
//        messageService.deleteMessageById(mess4.getId());
//        messageService.deleteMessageById(mess5.getId());
//        messageService.deleteMessageById(mess6.getId());
//
//        userService.deleteUserById(messi.getId());
//        userService.deleteUserById(suarez.getId());
//
//    }
//
//    @Test
//    public void a28testMessageControllerForSubscriptionPlanMixed2(){
//        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi","premium");
//        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez","basic");
//        UserService userService = UserServiceImpl.getInstance();
//
//        userService.addUser(messi);
//        userService.addUser(suarez);
//
//        Message mess = new Message();
//        mess.setTo(messi);
//        mess.setFrom(suarez);
//        mess.setContent("aaaa");
//
//        Message mess2 = new Message();
//        mess2.setTo(suarez);
//        mess2.setFrom(messi);
//        mess2.setContent("bbbb");
//
//        Message mess3 = new Message();
//        mess3.setTo(suarez);
//        mess3.setFrom(messi);
//        mess3.setContent("bbbb");
//
//        Message mess4 = new Message();
//        mess4.setTo(suarez);
//        mess4.setFrom(messi);
//        mess4.setContent("bbbb");
//
//        Message mess5 = new Message();
//        mess5.setTo(suarez);
//        mess5.setFrom(messi);
//        mess5.setContent("bbbb");
//
//        Message mess6 = new Message();
//        mess6.setTo(suarez);
//        mess6.setFrom(messi);
//        mess6.setContent("sfafdd");
//
//        MessageService messageService = MessageServiceImpl.getInstance();
//        messageService.addMessage(mess);
//        messageService.addMessage(mess2);
//        messageService.addMessage(mess3);
//        messageService.addMessage(mess4);
//        messageService.addMessage(mess5);
//        messageService.addMessage(mess6);
//
//        String messages = messageController.getExchangedMessages("messi10","suarez9");
//
//        System.out.println(messages);
//
//        int count = 0;
//
//        while(messages.contains("content")){
//            messages = messages.replaceFirst("content","");
//            count++;
//        }
//
//        assertEquals(5,count);
//
//        messageService.deleteMessageById(mess.getId());
//        messageService.deleteMessageById(mess2.getId());
//        messageService.deleteMessageById(mess3.getId());
//        messageService.deleteMessageById(mess4.getId());
//        messageService.deleteMessageById(mess5.getId());
//        messageService.deleteMessageById(mess6.getId());
//
//        userService.deleteUserById(messi.getId());
//        userService.deleteUserById(suarez.getId());
//
//    }
//
//    @Test
//    public void a29testMessageControllerForSubscriptionPlanPremiumUsers(){
//        User messi = new User("messi10", "messi@barca.com", "lm10", "", "", "Lionel Messi","premium");
//        User suarez = new User("suarez9", "suarez@barca.com", "ls9", "", "", "Luis Suarez","premium");
//        UserService userService = UserServiceImpl.getInstance();
//
//        userService.addUser(messi);
//        userService.addUser(suarez);
//
//        Message mess = new Message();
//        mess.setTo(messi);
//        mess.setFrom(suarez);
//        mess.setContent("aaaa");
//
//        Message mess2 = new Message();
//        mess2.setTo(suarez);
//        mess2.setFrom(messi);
//        mess2.setContent("bbbb");
//
//        Message mess3 = new Message();
//        mess3.setTo(suarez);
//        mess3.setFrom(messi);
//        mess3.setContent("bbbb");
//
//        Message mess4 = new Message();
//        mess4.setTo(suarez);
//        mess4.setFrom(messi);
//        mess4.setContent("bbbb");
//
//        Message mess5 = new Message();
//        mess5.setTo(suarez);
//        mess5.setFrom(messi);
//        mess5.setContent("bbbb");
//
//        Message mess6 = new Message();
//        mess6.setTo(suarez);
//        mess6.setFrom(messi);
//        mess6.setContent("sfafdd");
//
//        MessageService messageService = MessageServiceImpl.getInstance();
//        messageService.addMessage(mess);
//        messageService.addMessage(mess2);
//        messageService.addMessage(mess3);
//        messageService.addMessage(mess4);
//        messageService.addMessage(mess5);
//        messageService.addMessage(mess6);
//
//        String messages = messageController.getExchangedMessages("messi10","suarez9");
//
//        System.out.println(messages);
//
//        int count = 0;
//
//        while(messages.contains("content")){
//            messages = messages.replaceFirst("content","");
//            count++;
//        }
//
//        assertEquals(6,count);
//
//        messageService.deleteMessageById(mess.getId());
//        messageService.deleteMessageById(mess2.getId());
//        messageService.deleteMessageById(mess3.getId());
//        messageService.deleteMessageById(mess4.getId());
//        messageService.deleteMessageById(mess5.getId());
//        messageService.deleteMessageById(mess6.getId());
//
//        userService.deleteUserById(messi.getId());
//        userService.deleteUserById(suarez.getId());
//}

    @Test
    public void testGetMessageByStartName() {
        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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


        messageController.addMessage(message);


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageController.addMessage(message1);

        String messages = messageController.getMessagesCalceaOnlyStartDate(userTo.getUsername(),"2020-06-2300:00:00");

        assertTrue(messages.contains(userTo.getUsername()));
        assertTrue(messages.contains(userFrom.getUsername()));
        assertTrue(messages.contains("Chelsea"));
        assertTrue(messages.contains("KTBFFH"));

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }

    @Test
    public void testGetMessagesCalceaStartAndEndDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDateAndTime = formatter.format(date);
        String newCurrentDateAndTime = currentDateAndTime.substring(0,10) + currentDateAndTime.substring(11);

        User userTo = new User("billy","b@b.com","billy8","al","","Billy");
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


        messageController.addMessage(message);


        Message message1 = messageBuilder1.build();
        messageBuilder1 = messageBuilder1.setMessageContent("KTBFFH");

        messageBuilder1 = messageBuilder1.setFrom(userTo);
        messageBuilder1 = messageBuilder1.setTo(userFrom);
        messageBuilder1.setMessageDateTime();


        messageController.addMessage(message1);

        String messages = messageController.getMessagesCalceaStartAndEndDate(userTo.getUsername(),"2020-06-2300:00:00",newCurrentDateAndTime);

        assertTrue(messages.length()>0);

        messageServiceImpl.deleteMessageById(message.getId());
        messageServiceImpl.deleteMessageById(message1.getId());
        userService.deleteUserById(userService.getUserIdByUserName("billy"));
        userService.deleteUserById(userService.getUserIdByUserName("timo11"));

    }


}
