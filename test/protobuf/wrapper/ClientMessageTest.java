package protobuf.wrapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Test;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.ClientFileMessageBase;
import protobuf.ClientMessages.ClientMessageBase;
import protobuf.ClientMessages.ClientProfileComment;
import protobuf.ClientMessages.ClientProfileCommentAnswer;
import protobuf.ClientMessages.PrivateMessage;

class ClientMessageTest {
	@Test
	public void clientMessageBaseDefaultInstance_checkDefaultMessageValues() {
		ClientMessageBase defaultBase = ClientMessageBase.getDefaultInstance();
		assertEquals(defaultBase.getMessageId(), 0);
		assertEquals(defaultBase.getMessageText(), "");
		assertEquals(defaultBase.getSenderId(), 0);
		assertEquals(defaultBase.getSenderUsername(), "");
		assertEquals(defaultBase.getTimestampMillis(), 0L);
	}
	
	@Test
	public void newClientMessageBase_checkMessageValues() {
		ClientMessageBase base = ClientMessage.newClientMessageBase(1, "Hallo", 11, "Jann", new GregorianCalendar(2020, 3, 1, 11, 30, 0).getTimeInMillis());
		assertEquals(base.getMessageId(), 1);
		assertEquals(base.getMessageText(), "Hallo");
		assertEquals(base.getSenderId(), 11);
		assertEquals(base.getSenderUsername(), "Jann");
		assertEquals(base.getTimestampMillis(), new GregorianCalendar(2020, 3, 1, 11, 30, 0).getTimeInMillis());
	}
	
	@Test
	public void channelMessageDefaultInstance_checkDefaultChannelMessageValues() {
		ChannelMessage defaultMessage = ChannelMessage.getDefaultInstance();
		assertEquals(defaultMessage.getMessageBase(), ClientMessageBase.getDefaultInstance());
		assertEquals(defaultMessage.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(defaultMessage.getAttachedFileMessageCount(), 0);
		assertEquals(defaultMessage.getMessageAnswerCount(), 0);
	}
	
	@Test
	public void newChannelMessage_checkChannelMessageValues() {
		ClientFileMessageBase fileToAttach = ClientFileMessageBase.newBuilder()	.setMessageId(1)
																				.setFileName("readme.txt")
																				.setFilePath("/clients/files/Channel 5/readme.txt")
																				.setIsDownloadMessage(true)
																				.build();
		List<ClientFileMessageBase> attachedFiles = new ArrayList<ClientFileMessageBase>();
		attachedFiles.add(fileToAttach);
		ChannelMessage defaultMessage = ClientMessage.newChannelMessage(1, "Hallo", 11, "Jann", 5, new GregorianCalendar(2020, 3, 1, 11, 30, 0).getTimeInMillis(), attachedFiles);																		
		assertEquals(defaultMessage.getMessageBase().getMessageId(), 1);
		assertEquals(defaultMessage.getMessageBase().getMessageText(), "Hallo");
		assertEquals(defaultMessage.getMessageBase().getSenderId(), 11);
		assertEquals(defaultMessage.getMessageBase().getSenderUsername(), "Jann");
		assertEquals(defaultMessage.getChannelBase().getChannelId(), 5);
		assertEquals(defaultMessage.getMessageBase().getTimestampMillis(), new GregorianCalendar(2020, 3, 1, 11, 30, 0).getTimeInMillis());
		assertEquals(defaultMessage.getAttachedFileMessage(0), fileToAttach);
		assertEquals(defaultMessage.getMessageAnswerCount(), 0);
	}
	
	@Test
	public void channelMessageAnswerDefaultInstance_checkDefaultChannelMessageAnswerValues() {
		ChannelMessageAnswer defaultAnswer = ChannelMessageAnswer.getDefaultInstance();
		assertEquals(defaultAnswer.getMessageBase().getMessageId(), 0);
		assertEquals(defaultAnswer.getMessageBase().getMessageText(), "");
		assertEquals(defaultAnswer.getMessageBase().getSenderId(), 0);
		assertEquals(defaultAnswer.getMessageBase().getSenderUsername(), "");
		assertEquals(defaultAnswer.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(defaultAnswer.getChannelBase().getChannelId(), 0);
		assertEquals(defaultAnswer.getChannelBase().getChannelName(), "");
		assertEquals(defaultAnswer.getMessageToAnswerId(), 0);
	}
	
	@Test
	public void newChannelMessageAnswer_checkChannelMessageAnswerValues() {
		ChannelMessageAnswer answer = ClientMessage.newChannelMessageAnswer(1, "Antwort", 11, "Jann", 5, 1, 0L);
		assertEquals(answer.getMessageBase().getMessageId(), 1);
		assertEquals(answer.getMessageBase().getMessageText(), "Antwort");
		assertEquals(answer.getMessageBase().getSenderId(), 11);
		assertEquals(answer.getMessageBase().getSenderUsername(), "Jann");
		assertEquals(answer.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(answer.getChannelBase().getChannelId(), 5);
		assertEquals(answer.getChannelBase().getChannelName(), "");
		assertEquals(answer.getMessageToAnswerId(), 1);
	}
	
	@Test
	public void privateMessageDefaultInstance_checkDefaultPrivateMessageValues() {
		PrivateMessage defaultMessage = PrivateMessage.getDefaultInstance();
		assertEquals(defaultMessage.getMessageBase().getMessageId(), 0);
		assertEquals(defaultMessage.getMessageBase().getMessageText(), "");
		assertEquals(defaultMessage.getMessageBase().getSenderId(), 0);
		assertEquals(defaultMessage.getMessageBase().getSenderUsername(), "");
		assertEquals(defaultMessage.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(defaultMessage.getClientBase().getId(), 0);
		assertEquals(defaultMessage.getClientBase().getUsername(), "");
		assertEquals(defaultMessage.getAttachedFileMessageCount(), 0);
	}
	
	@Test
	public void newPrivateMessage_checkPrivateMessageValues() {
		PrivateMessage message = ClientMessage.newPrivateMessage(1, "Hallo", 12, "Rozach", 11, 0L, Collections.emptyList());
		assertEquals(message.getMessageBase().getMessageId(), 1);
		assertEquals(message.getMessageBase().getMessageText(), "Hallo");
		assertEquals(message.getMessageBase().getSenderId(), 12);
		assertEquals(message.getMessageBase().getSenderUsername(), "Rozach");
		assertEquals(message.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(message.getClientBase().getId(), 11);
		assertEquals(message.getClientBase().getUsername(), "");
		assertEquals(message.getAttachedFileMessageCount(), 0);
	}
	
	@Test
	public void clientProfileCommentDefaultInstance_checkDefaultProfileCommentValues() {
		ClientProfileComment defaultComment = ClientProfileComment.getDefaultInstance();
		assertEquals(defaultComment.getMessageBase().getMessageId(), 0);
		assertEquals(defaultComment.getMessageBase().getMessageText(), "");
		assertEquals(defaultComment.getMessageBase().getSenderId(), 0);
		assertEquals(defaultComment.getMessageBase().getSenderUsername(), "");
		assertEquals(defaultComment.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(defaultComment.getClientBase().getId(), 0);
		assertEquals(defaultComment.getClientBase().getUsername(), "");
		assertEquals(defaultComment.getCommentAnswerCount(), 0);
	}
	
	@Test
	public void newClientProfileComment_checkProfileCommentValues() {
		ClientProfileComment comment = ClientMessage.newClientProfileComment(1, "Kommentar", 12, "Rozach", 11, 0L, Collections.emptyList());
		assertEquals(comment.getMessageBase().getMessageId(), 1);
		assertEquals(comment.getMessageBase().getMessageText(), "Kommentar");
		assertEquals(comment.getMessageBase().getSenderId(), 12);
		assertEquals(comment.getMessageBase().getSenderUsername(), "Rozach");
		assertEquals(comment.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(comment.getClientBase().getId(), 11);
		assertEquals(comment.getClientBase().getUsername(), "");
		assertEquals(comment.getCommentAnswerCount(), 0);
	}
	
	@Test
	public void clientProfileCommentAnswerDefaultInstance_checkDefaultProfileCommentAnswerValues() {
		ClientProfileCommentAnswer defaultAnswer = ClientProfileCommentAnswer.getDefaultInstance();
		assertEquals(defaultAnswer.getMessageBase().getMessageId(), 0);
		assertEquals(defaultAnswer.getMessageBase().getMessageText(), "");
		assertEquals(defaultAnswer.getMessageBase().getSenderId(), 0);
		assertEquals(defaultAnswer.getMessageBase().getSenderUsername(), "");
		assertEquals(defaultAnswer.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(defaultAnswer.getClientBase().getId(), 0);
		assertEquals(defaultAnswer.getClientBase().getUsername(), "");
		assertEquals(defaultAnswer.getCommentToAnswerId(), 0);
	}

	@Test
	public void newClientProfileCommentAnswer_checkProfileCommentAnswerValues() {
		ClientProfileCommentAnswer answer = ClientMessage.newClientProfileCommentAnswer(1, "Antwort", 11, "Jann", 11, 1, 0L);
		assertEquals(answer.getMessageBase().getMessageId(), 1);
		assertEquals(answer.getMessageBase().getMessageText(), "Antwort");
		assertEquals(answer.getMessageBase().getSenderId(), 11);
		assertEquals(answer.getMessageBase().getSenderUsername(), "Jann");
		assertEquals(answer.getMessageBase().getTimestampMillis(), 0L);
		assertEquals(answer.getClientBase().getId(), 11);
		assertEquals(answer.getClientBase().getUsername(), "");
		assertEquals(answer.getCommentToAnswerId(), 1);
	}
}
