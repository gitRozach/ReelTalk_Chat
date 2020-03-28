package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.peer.server.database.protobuf.PrivateMessageDatabase;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.wrapper.ClientMessages;

class PrivateMessageManagerTest {
	protected PrivateMessageDatabase messageDatabase;
	
	@After
	public void afterEach() throws IOException {
		if(messageDatabase != null && !messageDatabase.isClosed())
			messageDatabase.close();
	}
	
	@Test
	public void addItem_addsProtobufMessagesAndWritesToFile() throws IOException {
		messageDatabase = new PrivateMessageDatabase();
		messageDatabase.loadFileItems("test/testresources/privateMessageManager/messagesToWrite.txt");
		messageDatabase.clear();
		
		PrivateMessage m1 = ClientMessages.newPrivateMessage(5, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage m2 = ClientMessages.newPrivateMessage(1, "Hallo Rozach", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage m3 = ClientMessages.newPrivateMessage(4, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage m4 = ClientMessages.newPrivateMessage(2, "Ganz gut und selbst?", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage m5 = ClientMessages.newPrivateMessage(3, "Auch gut", 12, "Rozach", 11, 0L, Collections.emptyList());
		
		assertTrue(messageDatabase.addItem(m1));
		assertTrue(messageDatabase.addItem(m2));
		assertTrue(messageDatabase.addItem(m3));
		assertTrue(messageDatabase.addItem(m4));
		assertTrue(messageDatabase.addItem(m5));
		
		assertEquals(messageDatabase.getItem(0), m2);
		assertEquals(messageDatabase.getItem(1), m4);
		assertEquals(messageDatabase.getItem(2), m5);
		assertEquals(messageDatabase.getItem(3), m3);
		assertEquals(messageDatabase.getItem(4), m1);
	}
	
	@Test
	public void getItem_getsExpectedProtobufItems() throws IOException {
		messageDatabase = new PrivateMessageDatabase();
		assertEquals(messageDatabase.loadFileItems("test/testresources/privateMessageManager/messagesToRead.txt"), 5); //Init 5 items
		
		PrivateMessage m1 = messageDatabase.getItem(0);
		PrivateMessage m2 = messageDatabase.getItem(1);
		PrivateMessage m3 = messageDatabase.getItem(2);
		PrivateMessage m4 = messageDatabase.getItem(3);
		PrivateMessage m5 = messageDatabase.getItem(4);
				
		PrivateMessage expM1 = ClientMessages.newPrivateMessage(1, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage expM2 = ClientMessages.newPrivateMessage(2, "Hallo Rozach", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage expM3 = ClientMessages.newPrivateMessage(3, "Hallo Jann", 12, "Rozach", 11, 0L, Collections.emptyList());
		PrivateMessage expM4 = ClientMessages.newPrivateMessage(4, "Ganz gut und selbst?", 11, "Jann", 12, 0L, Collections.emptyList());
		PrivateMessage expM5 = ClientMessages.newPrivateMessage(5, "Auch gut", 12, "Rozach", 11, 0L, Collections.emptyList());
		
		assertEquals(m1, expM1);
		assertEquals(m2, expM2);
		assertEquals(m3, expM3);
		assertEquals(m4, expM4);
		assertEquals(m5, expM5);
	}
}
