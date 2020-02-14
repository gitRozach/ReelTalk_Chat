package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.messageManager.Message;
import network.ssl.server.manager.messageManager.MessageManager;
import network.ssl.server.manager.messageManager.items.ChannelMessage;
import network.ssl.server.manager.messageManager.items.PrivateMessage;

class MessageManagerTest {
	protected MessageManager messageDatabase;
	
	@After
	public void afterEach() throws IOException {
		if(messageDatabase != null && !messageDatabase.isClosed())
			messageDatabase.close();
	}
	
	@Test
	public void addItem_addsMessagesAndWritesToFileWithPropertyValuePattern() throws IOException {
		messageDatabase = new MessageManager("test/testresources/messageManager/messagesToWrite.txt");
		messageDatabase.clear();
		assertEquals(messageDatabase.initialize(), 0); //Init 0 items because database should be empty
		
		Message m1 = new PrivateMessage(1, "Rozach", 10, "Hallo Jann!", 11, "Jann");
		Message m2 = new PrivateMessage(2, "Rozach", 10, "Hallo Hendrik!", 12, "Hendrik");
		Message m3 = new PrivateMessage(3, "Rozach", 10, "Hallo Hasan!", 13, "Hasan");
		Message m4 = new PrivateMessage(4, "Rozach", 10, "Hallo Joel!", 14, "Joel");
		Message m5 = new PrivateMessage(5, "Rozach", 10, "Hallo Jaal!", 15, "Jaal");
		Message n1 = new ChannelMessage(6, "Rozach", 10, "Hallo Channel 2!", 2, "Channel 2");
		Message n2 = new ChannelMessage(7, "Rozach", 10, "Hallo Channel 3!", 3, "Channel 3");
		Message n3 = new ChannelMessage(8, "Rozach", 10, "Hallo Channel 4!", 4, "Channel 4");
		Message n4 = new ChannelMessage(9, "Rozach", 10, "Hallo Channel 5!", 5, "Channel 5");
		Message n5 = new ChannelMessage(10, "Rozach", 10, "Hallo Channel 6!", 6, "Channel 6");
		
		assertTrue(messageDatabase.addItem(m1));
		assertTrue(messageDatabase.addItem(m2));
		assertTrue(messageDatabase.addItem(m3));
		assertTrue(messageDatabase.addItem(m4));
		assertTrue(messageDatabase.addItem(m5));
		assertTrue(messageDatabase.addItem(n1));
		assertTrue(messageDatabase.addItem(n2));
		assertTrue(messageDatabase.addItem(n3));
		assertTrue(messageDatabase.addItem(n4));
		assertTrue(messageDatabase.addItem(n5));
	}
	
	@Test
	public void getItem_getsExpectedItems() throws IOException {
		messageDatabase = new MessageManager("test/testresources/messageManager/messagesToRead.txt");
		assertEquals(messageDatabase.initialize(), 10); //Init 10 items
		
		Message m1 = new PrivateMessage();
		m1.initFromDatabaseString(messageDatabase.getItem(0));
		Message m2 = new PrivateMessage();
		m2.initFromDatabaseString(messageDatabase.getItem(1));
		Message m3 = new PrivateMessage();
		m3.initFromDatabaseString(messageDatabase.getItem(2));
		Message m4 = new PrivateMessage();
		m4.initFromDatabaseString(messageDatabase.getItem(3));
		Message m5 = new PrivateMessage();
		m5.initFromDatabaseString(messageDatabase.getItem(4));
		Message m6 = new ChannelMessage();
		m6.initFromDatabaseString(messageDatabase.getItem(5));
		Message m7 = new ChannelMessage();
		m7.initFromDatabaseString(messageDatabase.getItem(6));
		Message m8 = new ChannelMessage();
		m8.initFromDatabaseString(messageDatabase.getItem(7));
		Message m9 = new ChannelMessage();
		m9.initFromDatabaseString(messageDatabase.getItem(8));
		Message m10 = new ChannelMessage();
		m10.initFromDatabaseString(messageDatabase.getItem(9));
				
		Message expM1 = new PrivateMessage(1, "Rozach", 10, "Hallo Jann!", 11, "Jann");
		Message expM2 = new PrivateMessage(2, "Rozach", 10, "Hallo Hendrik!", 12, "Hendrik");
		Message expM3 = new PrivateMessage(3, "Rozach", 10, "Hallo Hasan!", 13, "Hasan");
		Message expM4 = new PrivateMessage(4, "Rozach", 10, "Hallo Joel!", 14, "Joel");
		Message expM5 = new PrivateMessage(5, "Rozach", 10, "Hallo Jaal!", 15, "Jaal");
		Message expM6 = new ChannelMessage(6, "Rozach", 10, "Hallo Channel 2!", 2, "Channel 2");
		Message expM7 = new ChannelMessage(7, "Rozach", 10, "Hallo Channel 3!", 3, "Channel 3");
		Message expM8 = new ChannelMessage(8, "Rozach", 10, "Hallo Channel 4!", 4, "Channel 4");
		Message expM9 = new ChannelMessage(9, "Rozach", 10, "Hallo Channel 5!", 5, "Channel 5");
		Message expM10 = new ChannelMessage(10, "Rozach", 10, "Hallo Channel 6!", 6, "Channel 6");
		
		assertEquals(m1, expM1);
		assertEquals(m2, expM2);
		assertEquals(m3, expM3);
		assertEquals(m4, expM4);
		assertEquals(m5, expM5);
		assertEquals(m6, expM6);
		assertEquals(m7, expM7);
		assertEquals(m8, expM8);
		assertEquals(m9, expM9);
		assertEquals(m10, expM10);
	}
}
