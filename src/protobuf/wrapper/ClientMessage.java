package protobuf.wrapper;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.ClientFileMessageBase;
import protobuf.ClientMessages.ClientMessageBase;
import protobuf.ClientMessages.ClientProfileComment;
import protobuf.ClientMessages.ClientProfileCommentAnswer;
import protobuf.ClientMessages.PrivateMessage;

public class ClientMessage {
	
	public static boolean isClientMessage(GeneratedMessageV3 message) {
		if(message == null)
			return false;
		switch(message.getClass().getSimpleName()) {
		case "ClientMessageBase":
		case "ClientFileMessageBase":
		case "ChannelMessage":
		case "ChannelMessageAnswer":
		case "PrivateMessage":
		case "ClientProfileComment":
		case "ClientProfileCommentAnswer":
			return true;
		default:
			return false;
		}
	}
	
	public static ClientMessageBase newClientMessageBase(	int messageId, 
															String messageText, 
															int senderId, 
															String senderUsername,
															long timestampMillis) {
		return ClientMessageBase.newBuilder()	.setMessageId(messageId)
												.setMessageText(messageText)
												.setSenderId(senderId)
												.setSenderUsername(senderUsername)
												.setTimestampMillis(timestampMillis)
												.build();
	}
	
	public static ChannelMessage newChannelMessage(	int messageId,
													String messageText,
													int senderId,
													String senderUsername,
													int channelId,
													long timestampMillis,
													Collection<ClientFileMessageBase> attachedFiles) {
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(channelId).build();
		ClientMessageBase messageBase = newClientMessageBase(messageId, messageText, senderId, senderUsername, timestampMillis);
		return ChannelMessage.newBuilder()	.setChannelBase(channelBase)
											.setMessageBase(messageBase)
											.addAllAttachedFileMessage(attachedFiles)
											//.addMessageAnswer(messageAnswer)
											.build();
											
	}
	
	public static ChannelMessageAnswer newChannelMessageAnswer(	int answerId,
																String answerText,
																int senderId,
																String senderUsername,
																int channelId,
																int messageToAnswerId,
																long timestampMillis) {
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(channelId).build();
		ClientMessageBase messageBase = newClientMessageBase(answerId, answerText, senderId, senderUsername, timestampMillis);
		return ChannelMessageAnswer.newBuilder().setChannelBase(channelBase)
												.setMessageBase(messageBase)
												.setMessageToAnswerId(messageToAnswerId)
												.build();
	}
	
	public static PrivateMessage newPrivateMessage(	int messageId,
													String messageText,
													int senderId,
													String senderUsername,
													int receiverId,
													long timestampMillis,
													Collection<ClientFileMessageBase> attachedFiles) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		ClientMessageBase messageBase = newClientMessageBase(messageId, messageText, senderId, senderUsername, timestampMillis);
		return PrivateMessage.newBuilder()	.setClientBase(clientBase)
											.setMessageBase(messageBase)
											.addAllAttachedFileMessage(attachedFiles)
											.build();
	}
	
	public static ClientProfileComment newClientProfileComment(	int commentId,
																String commentText,
																int senderId,
																String senderUsername,
																int receiverId,
																long timestampMillis,
																Collection<ClientProfileCommentAnswer> commentAnswers) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		ClientMessageBase messageBase = newClientMessageBase(commentId, commentText, senderId, senderUsername, timestampMillis);
		return ClientProfileComment.newBuilder().setClientBase(clientBase)
												.setMessageBase(messageBase)
												.addAllCommentAnswer(commentAnswers)
												.build();
	}
	
	public static ClientProfileCommentAnswer newClientProfileCommentAnswer(	int answerId,
																			String answerText,
																			int senderId,
																			String senderUsername,
																			int receiverId,
																			int commentToAnswerId,
																			long timestampMillis) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		ClientMessageBase messageBase = newClientMessageBase(answerId, answerText, senderId, senderUsername, timestampMillis);
		return ClientProfileCommentAnswer.newBuilder()	.setClientBase(clientBase)
														.setMessageBase(messageBase)
														.setCommentToAnswerId(commentToAnswerId)
														.build();
	}
}
