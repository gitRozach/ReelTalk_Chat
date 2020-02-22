package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.messageManager.items.PrivateMessage;
import network.ssl.server.manager.protobufDatabase.MessageManager;

class MessageManagerTest {
	protected MessageManager<PrivateMessage> messageDatabase;
	
	@After
	public void afterEach() throws IOException {
		if(messageDatabase != null && !messageDatabase.isClosed())
			messageDatabase.close();
	}
	
	@Test
	public void addItem_addsMessagesAndWritesToFileWithPropertyValuePattern() throws IOException {
		messageDatabase = new MessageManager<PrivateMessage>(PrivateMessage.class, "test/testresources/messageManager/messagesToWrite.txt");
		messageDatabase.clear();
		assertEquals(messageDatabase.initialize(), 0); //Init 0 items because database should be empty
		
		PrivateMessage m1 = new PrivateMessage(1, "Rozach", 10, "Hallo Jann!", 11, "Jann");
		PrivateMessage m2 = new PrivateMessage(2, "Rozach", 10, "Hallo Hendrik!", 12, "Hendrik");
		PrivateMessage m3 = new PrivateMessage(3, "Rozach", 10, "Hallo Hasan!", 13, "Hasan");
		PrivateMessage m4 = new PrivateMessage(4, "Rozach", 10, "Hallo Joel!", 14, "Joel");
		PrivateMessage m5 = new PrivateMessage(5, "Rozach", 10, "Hallo Jaal!", 15, "Jaal");
		PrivateMessage n1 = new PrivateMessage(6, "Rozach", 10, "Hallo Channel 2!", 2, "Channel 2");
		PrivateMessage n2 = new PrivateMessage(7, "Rozach", 10, "Hallo Channel 3!", 3, "Channel 3");
		PrivateMessage n3 = new PrivateMessage(8, "Rozach", 10, "Hallo Channel 4!", 4, "Channel 4");
		PrivateMessage n4 = new PrivateMessage(9, "Rozach", 10, "Hallo Channel 5!", 5, "Channel 5");
		PrivateMessage n5 = new PrivateMessage(10, "Rozach", 10, "Hallo Channel 6!", 6, "Channel 6");
		
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
		messageDatabase = new MessageManager<PrivateMessage>(PrivateMessage.class, "test/testresources/messageManager/messagesToRead.txt");
		assertEquals(messageDatabase.initialize(), 10); //Init 10 items
		
		PrivateMessage m1 = new PrivateMessage();
		m1.initFromDatabaseString(messageDatabase.getItem(0));
		PrivateMessage m2 = new PrivateMessage();
		m2.initFromDatabaseString(messageDatabase.getItem(1));
		PrivateMessage m3 = new PrivateMessage();
		m3.initFromDatabaseString(messageDatabase.getItem(2));
		PrivateMessage m4 = new PrivateMessage();
		m4.initFromDatabaseString(messageDatabase.getItem(3));
		PrivateMessage m5 = new PrivateMessage();
		m5.initFromDatabaseString(messageDatabase.getItem(4));
		PrivateMessage m6 = new PrivateMessage();
		m6.initFromDatabaseString(messageDatabase.getItem(5));
		PrivateMessage m7 = new PrivateMessage();
		m7.initFromDatabaseString(messageDatabase.getItem(6));
		PrivateMessage m8 = new PrivateMessage();
		m8.initFromDatabaseString(messageDatabase.getItem(7));
		PrivateMessage m9 = new PrivateMessage();
		m9.initFromDatabaseString(messageDatabase.getItem(8));
		PrivateMessage m10 = new PrivateMessage();
		m10.initFromDatabaseString(messageDatabase.getItem(9));
				
		PrivateMessage expM1 = new PrivateMessage(1, "Rozach", 10, "Hallo Jann!", 11, "Jann");
		PrivateMessage expM2 = new PrivateMessage(2, "Rozach", 10, "Hallo Hendrik!", 12, "Hendrik");
		PrivateMessage expM3 = new PrivateMessage(3, "Rozach", 10, "Hallo Hasan!", 13, "Hasan");
		PrivateMessage expM4 = new PrivateMessage(4, "Rozach", 10, "Hallo Joel!", 14, "Joel");
		PrivateMessage expM5 = new PrivateMessage(5, "Rozach", 10, "Hallo Jaal!", 15, "Jaal");
		PrivateMessage expM6 = new PrivateMessage(6, "Rozach", 10, "Hallo Channel 2!", 2, "Channel 2");
		PrivateMessage expM7 = new PrivateMessage(7, "Rozach", 10, "Hallo Channel 3!", 3, "Channel 3");
		PrivateMessage expM8 = new PrivateMessage(8, "Rozach", 10, "Hallo Channel 4!", 4, "Channel 4");
		PrivateMessage expM9 = new PrivateMessage(9, "Rozach", 10, "Hallo Channel 5!", 5, "Channel 5");
		PrivateMessage expM10 = new PrivateMessage(10, "Rozach", 10, "Hallo Channel 6!", 6, "Channel 6");
		
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
