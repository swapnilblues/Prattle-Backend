package com.neu.prattle;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.neu.prattle.model.User;

/**
 * Test class that tests the User class methods.
 */
public class TestSimpleExample {

	private static User user1;
	private static User user2;
	private static User user3;


	@BeforeClass
	public static void setUp() {
		user1 = new User("Eden", "eden@rm.com","eh10","lw","","Eden");
		user2 = new User("Eden", "eden@rm.com","eh10","lw","","Eden");
		user3 = new User("Thorgan", "thorgan@bvb.com","th10","lw","","Thorgan");
	}

	@Test
	public void checkUserGetterMethods() {
		User user = new User("lm10","leo@fcb.com","leo123","rw","","Lionel");
		assertEquals("lm10",user.getUsername());
		assertEquals("leo@fcb.com",user.getEmail());
		assertEquals("leo123",user.getPassword());
		assertEquals("rw",user.getStatus());
		assertEquals("",user.getImage());
		assertEquals("Lionel",user.getName());
	}

	@Test
	public void checkUserSetterMethods() {
		User user = new User();
		user.setUsername("cr7");
		user.setEmail("ron@juve.com");
		user.setPassword("siu");
		user.setStatus("st");
		user.setImage("Not found");
		user.setName("Cristiano");
		assertEquals("cr7",user.getUsername());
		assertEquals("ron@juve.com",user.getEmail());
		assertEquals("siu",user.getPassword());
		assertEquals("st",user.getStatus());
		assertEquals("Not found",user.getImage());
		assertEquals("Cristiano",user.getName());
	}

	@Test
	public void testEquals() {
		assertTrue(user1.equals(user2));
		assertFalse(user1.equals(user3));
		assertFalse(user1.equals(new ArrayList<>()));
	}

	@Test
	public void testNameConstructorOfUser() {
		User user = new User("Chettri");
		user.setUsername("chettri10");
		user.setEmail("chettri@india.com");
		assertEquals("Chettri",user.getName());
		assertEquals("chettri10",user.getUsername());
		assertEquals("chettri@india.com",user.getEmail());
	}
}
