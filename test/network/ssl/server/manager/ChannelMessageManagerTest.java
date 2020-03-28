package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import network.peer.server.database.protobuf.ChannelMessageDatabase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.ClientFileMessageBase;
import protobuf.wrapper.ClientMessages;

class ChannelMessageManagerTest {

	protected ChannelMessageDatabase database;
	
	@AfterEach
	public void tearDown() {
		if(database != null)
			database.close();
	}

	@Test
	public void addMessage_addItemsAndCheckIfListIsSorted() throws IOException {
		database = new ChannelMessageDatabase();
		database.loadFileItems("test/testresources/channelMessageManager/addChannelMessages.txt");
		database.clear();
		
		ClientFileMessageBase file1 = ClientFileMessageBase.newBuilder().setFileName("file1").setIsDownloadMessage(true).build();
		ClientFileMessageBase file2 = ClientFileMessageBase.newBuilder().setFileName("file2").setIsDownloadMessage(true).build();
		ClientFileMessageBase file3 = ClientFileMessageBase.newBuilder().setFileName("file3").setIsDownloadMessage(true).build();
		ClientFileMessageBase file4 = ClientFileMessageBase.newBuilder().setFileName("file4").setIsDownloadMessage(true).build();
		ClientFileMessageBase file5 = ClientFileMessageBase.newBuilder().setFileName("file5").setIsDownloadMessage(true).build();
		List<ClientFileMessageBase> files = new ArrayList<>();
		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		
		ChannelMessage message5 = ClientMessages.newChannelMessage(5, "Jungs haltet mal die Fressen.", 30, "Thuraeh", 1, new GregorianCalendar(2020, 1, 1, 10, 4, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message5));
		
		ChannelMessage message4 = ClientMessages.newChannelMessage(4, "AH! Rozach!", 10, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 3, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message4));
		
		ChannelMessage message3 = ClientMessages.newChannelMessage(3, "Ah, Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 2, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message3));
		
		ChannelMessage message2 = ClientMessages.newChannelMessage(2, "Hallo Rozach!", 10, "Jenn", 1, new GregorianCalendar(2020, 1, 1, 10, 1, 0).getTimeInMillis(), files);
		assertTrue(database.addItem(message2));
		
		ChannelMessage message1 = ClientMessages.newChannelMessage(1, "Hallo Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 0, 0).getTimeInMillis(), files);
		assertTrue(database.addItem(message1));
		
		assertEquals(database.getItem(0), message1);
		assertEquals(database.getItem(1), message2);
		assertEquals(database.getItem(2), message3);
		assertEquals(database.getItem(3), message4);
		assertEquals(database.getItem(4), message5);
		
		assertEquals(database.getItem(0).getMessageBase().getMessageId(), 1);
		assertEquals(database.getItem(1).getMessageBase().getMessageId(), 2);
		assertEquals(database.getItem(2).getMessageBase().getMessageId(), 3);
		assertEquals(database.getItem(3).getMessageBase().getMessageId(), 4);
		assertEquals(database.getItem(4).getMessageBase().getMessageId(), 5);
	}
	
	@Test
	public void removeMessage_removeAndAddItemsCheckGeneratedIdValues() throws IOException {
		database = new ChannelMessageDatabase();
		database.loadFileItems("test/testresources/channelMessageManager/addAndRemoveChannelMessages.txt");
		database.clear();
		
		ChannelMessage message1 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Hallo Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 0, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message1));

		ChannelMessage message2 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Hallo Rozach!", 10, "Jenn", 1, new GregorianCalendar(2020, 1, 1, 10, 1, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message2));

		ChannelMessage message3 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Ah, Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 2, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message3));

		ChannelMessage message4 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "AH! Rozach!", 10, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 3, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message4));

		ChannelMessage message5 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Jungs haltet mal die Fressen.", 30, "Thuraeh", 1, new GregorianCalendar(2020, 1, 1, 10, 4, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message5));
		
		assertEquals(database.getItem(0), message1);
		assertEquals(database.getItem(1), message2);
		assertEquals(database.getItem(2), message3);
		assertEquals(database.getItem(3), message4);
		assertEquals(database.getItem(4), message5);
		
		assertEquals(database.getItem(0).getMessageBase().getMessageId(), 1);
		assertEquals(database.getItem(1).getMessageBase().getMessageId(), 2);
		assertEquals(database.getItem(2).getMessageBase().getMessageId(), 3);
		assertEquals(database.getItem(3).getMessageBase().getMessageId(), 4);
		assertEquals(database.getItem(4).getMessageBase().getMessageId(), 5);
		
		assertTrue(database.removeItem(4));
		assertTrue(database.removeItem(3));
		assertTrue(database.removeItem(2));
		assertTrue(database.size() == 2);
		
		ChannelMessage newMessage1 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Ah, Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 11, 2, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(newMessage1));

		ChannelMessage newMessage2 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "AH! Rozach!", 10, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 11, 3, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(newMessage2));

		ChannelMessage newMessage3 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Jungs ...", 30, "Thuraeh", 1, new GregorianCalendar(2020, 1, 1, 12, 4, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(newMessage3));
		
		assertTrue(database.size() == 5);
		assertEquals(database.getItem(2).getMessageBase().getMessageId(), 3);
		assertEquals(database.getItem(3).getMessageBase().getMessageId(), 4);
		assertEquals(database.getItem(4).getMessageBase().getMessageId(), 5);
	}
	
	@Test
	public void addMessage_attachMessageAnswersAndCheckGeneratedAnswerIdValues() throws IOException {
		database = new ChannelMessageDatabase();
		database.loadFileItems("test/testresources/channelMessageManager/addChannelMessageAnswers.txt");
		database.clear();
		
		ChannelMessage message1 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Hallo Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 0, 0).getTimeInMillis(), Collections.emptyList());
		ChannelMessageAnswer answer1 = ClientMessages.newChannelMessageAnswer(ChannelMessageDatabase.generateAnswerIdFor(message1), "Antwort 1", 10, "Jann", 1, 1, 0L);
		message1 = message1.toBuilder().addMessageAnswer(answer1).build();
		
		ChannelMessageAnswer answer2 = ClientMessages.newChannelMessageAnswer(ChannelMessageDatabase.generateAnswerIdFor(message1), "Antwort 2", 1, "Rozach", 1, 1, 0L);
		message1 = message1.toBuilder().addMessageAnswer(answer2).build();
		
		ChannelMessageAnswer answer3 = ClientMessages.newChannelMessageAnswer(ChannelMessageDatabase.generateAnswerIdFor(message1), "Antwort 3", 10, "Jann", 1, 1, 0L);
		message1 = message1.toBuilder().addMessageAnswer(answer3).build();
		
		ChannelMessageAnswer answer4 = ClientMessages.newChannelMessageAnswer(ChannelMessageDatabase.generateAnswerIdFor(message1), "Antwort 4", 1, "Rozach", 1, 1, 0L);
		message1 = message1.toBuilder().addMessageAnswer(answer4).build();
		
		ChannelMessageAnswer answer5 = ClientMessages.newChannelMessageAnswer(ChannelMessageDatabase.generateAnswerIdFor(message1), "Antwort 5", 1, "Jann", 1, 1, 0L);
		message1 = message1.toBuilder().addMessageAnswer(answer5).build();
		
		assertTrue(database.addItem(message1));

		ChannelMessage message2 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Hallo Rozach!", 10, "Jenn", 1, new GregorianCalendar(2020, 1, 1, 10, 1, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message2));

		ChannelMessage message3 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Ah, Jann!", 1, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 2, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message3));

		ChannelMessage message4 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "AH! Rozach!", 10, "TestoRozach", 1, new GregorianCalendar(2020, 1, 1, 10, 3, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message4));

		ChannelMessage message5 = ClientMessages.newChannelMessage(database.generateUniqueMessageId(), "Jungs haltet mal die Fressen.", 30, "Thuraeh", 1, new GregorianCalendar(2020, 1, 1, 10, 4, 0).getTimeInMillis(), Collections.emptyList());
		assertTrue(database.addItem(message5));
		
		assertEquals(database.getItem(0), message1);
		assertEquals(database.getItem(1), message2);
		assertEquals(database.getItem(2), message3);
		assertEquals(database.getItem(3), message4);
		assertEquals(database.getItem(4), message5);
		
		assertEquals(database.getItem(0).getMessageBase().getMessageId(), 1);
		assertEquals(database.getItem(0).getMessageAnswer(0).getMessageBase().getMessageId(), 1);
		assertEquals(database.getItem(0).getMessageAnswer(1).getMessageBase().getMessageId(), 2);
		assertEquals(database.getItem(0).getMessageAnswer(2).getMessageBase().getMessageId(), 3);
		assertEquals(database.getItem(0).getMessageAnswer(3).getMessageBase().getMessageId(), 4);
		assertEquals(database.getItem(0).getMessageAnswer(4).getMessageBase().getMessageId(), 5);
		
		assertEquals(database.getItem(1).getMessageBase().getMessageId(), 2);
		assertEquals(database.getItem(2).getMessageBase().getMessageId(), 3);
		assertEquals(database.getItem(3).getMessageBase().getMessageId(), 4);
		assertEquals(database.getItem(4).getMessageBase().getMessageId(), 5);
	}
}
