package com.neu.prattle;

import com.neu.prattle.controller.MessageController;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServiceImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAMessageReply {

    @Test
    public void a28testMessagePersistenceWithOneReply3(){
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
        messageService.addMessage(mess2);

        mess.addReply(mess2);
        messageService.addMessage(mess);

        assertEquals(mess.getId(), messageService.getMessageById(mess.getId()).getId());
        assertEquals(mess2.getId(), messageService.getMessageById(mess.getId()).getReplies().get(0).getId());

        messageService.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

    @Test
    public void a28testExchangeMessage(){
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
        messageService.addMessage(mess2);

        mess.addReply(mess2);
        messageService.addMessage(mess);

        MessageController messageController = new MessageController();

        String s = messageController.getExchangedMessages("messi10","suarez9");

        assertTrue(s.contains("suarez9"));

        System.out.println("S: "+s);

        assertEquals(mess.getId(), messageService.getMessageById(mess.getId()).getId());
        assertEquals(mess2.getId(), messageService.getMessageById(mess.getId()).getReplies().get(0).getId());

        messageService.deleteMessageById(mess.getId());
        userService.deleteUserById(messi.getId());
        userService.deleteUserById(suarez.getId());
        userService.deleteUserById(neymar.getId());

    }

}
