package protobuf.wrapper;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.FileMessageBase;
import protobuf.ClientMessages.MessageBase;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.ClientMessages.ProfileComment;
import protobuf.ClientMessages.ProfileCommentAnswer;

public class ClientMessages {	
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {	"MessageBase",
								"FileMessageBase",
								"ChannelMessage",
								"ChannelMessageAnswer",
								"PrivateMessage",
								"ProfileComment",
								"ProfileCommentAnswer"};
	}
	
	public static boolean isMessage(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static MessageBase newMessageBase(	int messageId, 
												String messageText, 
												int senderId, 
												String senderUsername,
												long timestampMillis) {
		return MessageBase.newBuilder()	.setMessageId(messageId)
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
													Collection<FileMessageBase> attachedFiles) {
		ChannelBase channelBase = ChannelBase.newBuilder().setId(channelId).build();
		MessageBase messageBase = newMessageBase(messageId, messageText, senderId, senderUsername, timestampMillis);
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
		ChannelBase channelBase = ChannelBase.newBuilder().setId(channelId).build();
		MessageBase messageBase = newMessageBase(answerId, answerText, senderId, senderUsername, timestampMillis);
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
													Collection<FileMessageBase> attachedFiles) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		MessageBase messageBase = newMessageBase(messageId, messageText, senderId, senderUsername, timestampMillis);
		return PrivateMessage.newBuilder()	.setClientBase(clientBase)
											.setMessageBase(messageBase)
											.addAllAttachedFileMessage(attachedFiles)
											.build();
	}
	
	public static ProfileComment newProfileComment(	int commentId,
													String commentText,
													int senderId,
													String senderUsername,
													int receiverId,
													long timestampMillis,
													Collection<ProfileCommentAnswer> commentAnswers) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		MessageBase messageBase = newMessageBase(commentId, commentText, senderId, senderUsername, timestampMillis);
		return ProfileComment.newBuilder()	.setClientBase(clientBase)
											.setMessageBase(messageBase)
											.addAllCommentAnswer(commentAnswers)
											.build();
	}
	
	public static ProfileCommentAnswer newProfileCommentAnswer(	int answerId,
																String answerText,
																int senderId,
																String senderUsername,
																int receiverId,
																int commentToAnswerId,
																long timestampMillis) {
		ClientBase clientBase = ClientBase.newBuilder().setId(receiverId).build();
		MessageBase messageBase = newMessageBase(answerId, answerText, senderId, senderUsername, timestampMillis);
		return ProfileCommentAnswer.newBuilder()	.setClientBase(clientBase)
													.setMessageBase(messageBase)
													.setCommentToAnswerId(commentToAnswerId)
													.build();
	}
}
