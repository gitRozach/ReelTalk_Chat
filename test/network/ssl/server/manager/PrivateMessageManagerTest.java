package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.protobufDatabase.PrivateMessageManager;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.wrapper.ClientMessage;

class PrivateMessageManagerTest {
	protected PrivateMessageManager messageDatabase;
	
	@After
	public void afterEach() throws IOException {
		if(messageDatabase != null && !messageDatabase.isClosed())
			messageDatabase.close();
	}
	
	@Test
	public void addItem_addsProtobufMessagesAndWritesToFile() throws IOException {
		messageDatabase = new PrivateMessageManager("test/testresources/messageManager/messagesToWrite.txt");
		messageDatabase.clear();
		assertEquals(messageDatabase.initialize(), 0); //Init 0 items because database should be empty
		
		PrivateMessage m1 = ClientMessage.newPrivateMessage(1, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage m2 = ClientMessage.newPrivateMessage(2, "Hallo Rozach", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage m3 = ClientMessage.newPrivateMessage(3, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage m4 = ClientMessage.newPrivateMessage(4, "Ganz gut und selbst?", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage m5 = ClientMessage.newPrivateMessage(5, "Auch gut", 12, "Rozach", 11, 0L, Collections.emptyList());
		
		assertTrue(messageDatabase.addItem(m1));
		assertTrue(messageDatabase.addItem(m2));
		assertTrue(messageDatabase.addItem(m3));
		assertTrue(messageDatabase.addItem(m4));
		assertTrue(messageDatabase.addItem(m5));
	}
	
	@Test
	public void getItem_getsExpectedProtobufItems() throws IOException {
		messageDatabase = new PrivateMessageManager("test/testresources/messageManager/messagesToRead.txt");
		assertEquals(messageDatabase.initialize(), 5); //Init 5 items
		
		PrivateMessage m1 = messageDatabase.getItem(0);
		PrivateMessage m2 = messageDatabase.getItem(1);
		PrivateMessage m3 = messageDatabase.getItem(2);
		PrivateMessage m4 = messageDatabase.getItem(3);
		PrivateMessage m5 = messageDatabase.getItem(4);
				
		PrivateMessage expM1 = ClientMessage.newPrivateMessage(1, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage expM2 = ClientMessage.newPrivateMessage(2, "Hallo Rozach", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage expM3 = ClientMessage.newPrivateMessage(3, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage expM4 = ClientMessage.newPrivateMessage(4, "Ganz gut und selbst?", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage expM5 = ClientMessage.newPrivateMessage(5, "Auch gut", 12, "Rozach", 11, 0L, Collections.emptyList());
		
		assertEquals(m1, expM1);
		assertEquals(m2, expM2);
		assertEquals(m3, expM3);
		assertEquals(m4, expM4);
		assertEquals(m5, expM5);
	}
}
