package network.ssl.communication;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.TextChannel;
import protobuf.ClientChannels.VoiceChannel;
import protobuf.ClientEvents.ChannelFileDownloadEvent;
import protobuf.ClientEvents.ChannelFileUploadEvent;
import protobuf.ClientEvents.ChannelMessageAnswerGetEvent;
import protobuf.ClientEvents.ChannelMessageAnswerPostEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientChannelJoinEvent;
import protobuf.ClientEvents.ClientChannelLeaveEvent;
import protobuf.ClientEvents.ClientLoginEvent;
import protobuf.ClientEvents.ClientLogoutEvent;
import protobuf.ClientEvents.ClientProfileCommentAnswerGetEvent;
import protobuf.ClientEvents.ClientProfileCommentAnswerPostEvent;
import protobuf.ClientEvents.ClientProfileCommentGetEvent;
import protobuf.ClientEvents.ClientProfileCommentPostEvent;
import protobuf.ClientEvents.ClientProfileGetEvent;
import protobuf.ClientEvents.ClientRegistrationEvent;
import protobuf.ClientEvents.ClientRequestRejectedEvent;
import protobuf.ClientEvents.PingMeasurementEvent;
import protobuf.ClientEvents.PrivateFileDownloadEvent;
import protobuf.ClientEvents.PrivateFileUploadEvent;
import protobuf.ClientEvents.PrivateMessageGetEvent;
import protobuf.ClientEvents.PrivateMessagePostEvent;
import protobuf.ClientRequests.ChannelFileDownloadRequest;
import protobuf.ClientRequests.ChannelFileUploadRequest;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageAnswerGetRequest;
import protobuf.ClientRequests.ChannelMessageAnswerPostRequest;
import protobuf.ClientRequests.ChannelMessageGetRequest;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.ClientRequests.ClientChannelGetRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientProfileCommentAnswerGetRequest;
import protobuf.ClientRequests.ClientProfileCommentAnswerPostRequest;
import protobuf.ClientRequests.ClientProfileCommentGetRequest;
import protobuf.ClientRequests.ClientProfileCommentPostRequest;
import protobuf.ClientRequests.ClientProfileGetRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;

public class ProtobufMessage {
	protected SocketChannel socketChannel;
	protected GeneratedMessageV3 protobufMessage;
	
	public ProtobufMessage() {
		socketChannel = null;
		protobufMessage = null;
	}
	
	public ProtobufMessage(GeneratedMessageV3 message) {
		socketChannel = null;
		protobufMessage = message;
	}
	
	public ProtobufMessage(SelectionKey key, GeneratedMessageV3 message) {
		this((SocketChannel)key.channel(), message);
	}
	
	public ProtobufMessage(SocketChannel channel, GeneratedMessageV3 message) {
		socketChannel = channel;
		protobufMessage = message;
	}
	
	public static Class<? extends GeneratedMessageV3> getMessageTypeOf(Any message){
		if(message.is(TextChannel.class))
			return TextChannel.class;
		else if(message.is(VoiceChannel.class))
			return VoiceChannel.class;
		
		else if(message.is(ClientRequestRejectedEvent.class))
			return ClientRequestRejectedEvent.class;
		else if(message.is(ClientProfileGetEvent.class))
			return ClientProfileGetEvent.class;
		else if(message.is(ChannelMessagePostEvent.class))
			return ChannelMessagePostEvent.class;
		else if(message.is(ChannelMessageGetEvent.class))
			return ChannelMessageGetEvent.class;
		else if(message.is(ChannelMessageAnswerPostEvent.class))
			return ChannelMessageAnswerPostEvent.class;
		else if(message.is(ChannelMessageAnswerGetEvent.class))
			return ChannelMessageAnswerGetEvent.class;
		else if(message.is(PrivateMessagePostEvent.class))
			return PrivateMessagePostEvent.class;
		else if(message.is(PrivateMessageGetEvent.class))
			return PrivateMessageGetEvent.class;
		else if(message.is(ClientProfileCommentPostEvent.class))
			return ClientProfileCommentPostEvent.class;
		else if(message.is(ClientProfileCommentGetEvent.class))
			return ClientProfileCommentGetEvent.class;
		else if(message.is(ClientProfileCommentAnswerPostEvent.class))
			return ClientProfileCommentAnswerPostEvent.class;
		else if(message.is(ClientProfileCommentAnswerGetEvent.class))
			return ClientProfileCommentAnswerGetEvent.class;
		else if(message.is(ClientChannelJoinEvent.class))
			return ClientChannelJoinEvent.class;
		else if(message.is(ClientChannelLeaveEvent.class))
			return ClientChannelLeaveEvent.class;
		else if(message.is(ClientLoginEvent.class))
			return ClientLoginEvent.class;
		else if(message.is(ClientLogoutEvent.class))
			return ClientLogoutEvent.class;
		else if(message.is(ClientRegistrationEvent.class))
			return ClientRegistrationEvent.class;
		else if(message.is(ChannelFileDownloadEvent.class))
			return ChannelFileDownloadEvent.class;
		else if(message.is(ChannelFileUploadEvent.class))
			return ChannelFileUploadEvent.class;
		else if(message.is(PrivateFileDownloadEvent.class))
			return PrivateFileDownloadEvent.class;
		else if(message.is(PrivateFileUploadEvent.class))
			return PrivateFileUploadEvent.class;
		else if(message.is(PingMeasurementEvent.class))
			return PingMeasurementEvent.class;
		
		else if(message.is(ChannelJoinRequest.class))
			return ChannelJoinRequest.class;
		else if(message.is(ChannelLeaveRequest.class))
			return ChannelLeaveRequest.class;
		else if(message.is(ClientProfileGetRequest.class))
			return ClientProfileGetRequest.class;
		else if(message.is(ChannelMessageGetRequest.class))
			return ChannelMessageGetRequest.class;
		else if(message.is(ChannelMessagePostRequest.class))
			return ChannelMessagePostRequest.class;
		else if(message.is(ChannelMessageAnswerGetRequest.class))
			return ChannelMessageAnswerGetRequest.class;
		else if(message.is(ChannelMessageAnswerPostRequest.class))
			return ChannelMessageAnswerPostRequest.class;
		else if(message.is(PrivateMessageGetRequest.class))
			return PrivateMessageGetRequest.class;
		else if(message.is(PrivateMessagePostRequest.class))
			return PrivateMessagePostRequest.class;
		else if(message.is(ClientProfileCommentGetRequest.class))
			return ClientProfileCommentGetRequest.class;
		else if(message.is(ClientProfileCommentPostRequest.class))
			return ClientProfileCommentPostRequest.class;
		else if(message.is(ClientProfileCommentAnswerGetRequest.class))
			return ClientProfileCommentAnswerGetRequest.class;
		else if(message.is(ClientProfileCommentAnswerPostRequest.class))
			return ClientProfileCommentAnswerPostRequest.class;
		else if(message.is(ClientChannelGetRequest.class))
			return ClientChannelGetRequest.class;
		else if(message.is(ChannelFileDownloadRequest.class))
			return ChannelFileDownloadRequest.class;
		else if(message.is(ChannelFileUploadRequest.class))
			return ChannelFileUploadRequest.class;
		else if(message.is(PrivateFileDownloadRequest.class))
			return PrivateFileDownloadRequest.class;
		else if(message.is(PrivateFileUploadRequest.class))
			return PrivateFileUploadRequest.class;
		else if(message.is(ClientLoginRequest.class))
			return ClientLoginRequest.class;
		else if(message.is(ClientLogoutRequest.class))
			return ClientLogoutRequest.class;
		else if(message.is(ClientRegistrationRequest.class))
			return ClientRegistrationRequest.class;
		else if(message.is(PingMeasurementRequest.class))
			return PingMeasurementRequest.class;
		
		System.out.println("NOT FOUND!");
		return null;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void putSocketChannel(SocketChannel channel) {
		socketChannel = channel;
	}

	public GeneratedMessageV3 getMessage() {
		return protobufMessage;
	}

	public void setProtobufMessage(GeneratedMessageV3 message) {
		protobufMessage = message;
	}
	
	public boolean hasSocketChannel() {
		return socketChannel != null;
	}
	
	public boolean hasMessage() {
		return protobufMessage != null;
	}
}
