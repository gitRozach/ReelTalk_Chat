package protobuf.wrapper;

import java.util.Collection;
import java.util.Collections;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ClientChannel;
import protobuf.ClientEvents.ChannelFileDownloadEvent;
import protobuf.ClientEvents.ChannelFileUploadEvent;
import protobuf.ClientEvents.ChannelMessageAnswerGetEvent;
import protobuf.ClientEvents.ChannelMessageAnswerPostEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.ClientChannelGetEvent;
import protobuf.ClientEvents.ClientChannelJoinEvent;
import protobuf.ClientEvents.ClientChannelLeaveEvent;
import protobuf.ClientEvents.ClientEventBase;
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
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.ClientProfileComment;
import protobuf.ClientMessages.ClientProfileCommentAnswer;
import protobuf.ClientMessages.PrivateMessage;

public class ClientEvents {
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {	"ClientRequestRejectedEvent", 
								"ClientProfileGetEvent",
								"ChannelMessagePostEvent", 
								"ChannelMessageGetEvent",
								"ChannelMessageAnswerPostEvent",
								"ChannelMessageAnswerGetEvent",
								"PrivateMessagePostEvent",
								"PrivateMessageGetEvent",
								"ClientProfileCommentPostEvent",
								"ClientProfileCommentGetEvent",
								"ClientProfileCommentAnswerPostEvent",
								"ClientProfileCommentAnswerGetEvent",
								"ClientChannelJoinEvent",
								"ClientChannelLeaveEvent",
								"ClientLoginEvent",
								"ClientLogoutEvent",
								"ClientRegistrationEvent",
								"ChannelFileDownloadEvent",
								"ChannelFileUploadEvent",
								"PrivateFileDownloadEvent",
								"PrivateFileUploadEvent",
								"ClientChannelGetEvent",
								"PingMeasurementEvent"};
	}
	
	public static boolean isClientEvent(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static ClientEventBase newClientEventBase(int eventId, int clientId) {
		ClientBase requestorBase = ClientBase.newBuilder().setId(clientId).build();
		return ClientEventBase.newBuilder()	.setRequestorClientBase(requestorBase)
											.setEventId(eventId)
											.setEventTimestamp(System.currentTimeMillis())
											.build();
	}
	
	public static ClientRequestRejectedEvent newClientRequestRejectedEvent(int eventId, String rejectionMessage) {
		ClientEventBase eventBase = ClientEventBase.newBuilder().setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientRequestRejectedEvent.newBuilder()	.setEventBase(eventBase)
														.setRejectionMessage(rejectionMessage)
														.build();
	}
	
	public static ClientProfileGetEvent newClientProfileGetEvent(int eventId, int clientId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientProfileGetEvent.newBuilder()	.setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static ChannelMessagePostEvent newChannelMessagePostEvent(int eventId, int clientId, Collection<ChannelMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelMessagePostEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static ChannelMessageGetEvent newChannelMessageGetEvent(int eventId, int clientId, Collection<ChannelMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelMessageGetEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}

	public static ChannelMessageAnswerPostEvent newChannelMessageAnswerPostEvent(int eventId, int clientId, Collection<ChannelMessageAnswer> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelMessageAnswerPostEvent.newBuilder()	.setEventBase(eventBase)
															.addAllMessageAnswer(messages)
															.build();
	}
	
	public static ChannelMessageAnswerGetEvent newChannelMessageAnswerGetEvent(int eventId, int clientId, Collection<ChannelMessageAnswer> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelMessageAnswerGetEvent.newBuilder().setEventBase(eventBase)
														.addAllMessageAnswer(messages)
														.build();
	}
	
	public static PrivateMessagePostEvent newPrivateMessagePostEvent(int eventId, int clientId, Collection<PrivateMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return PrivateMessagePostEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static PrivateMessageGetEvent newPrivateMessageGetEvent(int eventId, int clientId, Collection<PrivateMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return PrivateMessageGetEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static ClientProfileCommentPostEvent newClientProfileCommentPostEvent(int eventId, int clientId, Collection<ClientProfileComment> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientProfileCommentPostEvent.newBuilder()	.setEventBase(eventBase)
															.addAllProfileComment(comments)
															.build();
	}
	
	public static ClientProfileCommentGetEvent newClientProfileCommentGetEvent(int eventId, int clientId, Collection<ClientProfileComment> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientProfileCommentGetEvent.newBuilder().setEventBase(eventBase)
														.addAllProfileComment(comments)
														.build();
	}
	
	public static ClientProfileCommentAnswerPostEvent newClientProfileCommentAnswerPostEvent(	int eventId,
																								int clientId,
																								Collection<ClientProfileCommentAnswer> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientProfileCommentAnswerPostEvent.newBuilder()	.setEventBase(eventBase)
																.addAllProfileCommentAnswer(comments)
																.build();
	}
	
	public static ClientProfileCommentAnswerGetEvent newClientProfileCommentAnswerGetEvent( int eventId,
																							int clientId,
																							Collection<ClientProfileCommentAnswer> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientProfileCommentAnswerGetEvent.newBuilder()	.setEventBase(eventBase)
																.addAllProfileCommentAnswer(comments)
																.build();
	}
	
	public static ClientChannelJoinEvent newClientChannelJoinEvent(int eventId, int channelId, Collection<ChannelMessage> channelMessages) {
		ClientBase clientBase = ClientBase.newBuilder().build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientChannelJoinEvent.newBuilder()	.setEventBase(eventBase)
													.setChannelBase(ChannelBase.newBuilder().setChannelId(channelId))
													.addAllChannelMessage(channelMessages)
													.build();
	}
	
	public static ClientChannelLeaveEvent newClientChannelLeaveEvent(int eventId, int channelId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientChannelLeaveEvent.newBuilder()	.setEventBase(eventBase)
													.setChannelBase(ChannelBase.newBuilder().setChannelId(channelId))
													.setProfile(clientProfile)
													.build();
	}
	
	public static ClientLoginEvent newClientLoginEvent(int eventId, ClientAccount clientAccount) {
		return newClientLoginEvent(eventId, clientAccount, Collections.emptyList(), Collections.emptyList());
	}
	
	public static ClientLoginEvent newClientLoginEvent(int eventId, ClientAccount clientAccount, Collection<ClientChannel> channels, Collection<ClientProfile> members) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientAccount.getProfile().getBase().getId())
														.setUsername(clientAccount.getProfile().getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientLoginEvent.newBuilder().setEventBase(eventBase)
											.setAccount(clientAccount)
											.addAllServerChannel(channels)
											.addAllMemberProfile(members)
											.build();
	}
	
	public static ClientLogoutEvent newClientLogoutEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientLogoutEvent.newBuilder()	.setEventBase(eventBase)
												.setProfile(clientProfile)
												.build();
	}
	
	public static ClientRegistrationEvent newClientRegistrationEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientRegistrationEvent.newBuilder()	.setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static ChannelFileDownloadEvent newChannelFileDownloadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelFileDownloadEvent.newBuilder().setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static ChannelFileUploadEvent newChannelFileUploadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelFileUploadEvent.newBuilder()	.setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static PrivateFileDownloadEvent newPrivateFileDownloadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return PrivateFileDownloadEvent.newBuilder().setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static PrivateFileUploadEvent newPrivateFileUploadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return PrivateFileUploadEvent.newBuilder()	.setEventBase(eventBase)
													.setProfile(clientProfile)
													.build();
	}
	
	public static ClientChannelGetEvent newClientChannelGetEvent(int eventId, int clientId, Collection<ClientChannel> channels) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ClientChannelGetEvent.newBuilder()	.setEventBase(eventBase)
													.setClientBase(clientBase)
													.addAllChannel(channels)
													.build();
	}
	
	public static PingMeasurementEvent newPingMeasurementEvent(int eventId, int clientId, long measurementMillis) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientId).build();
		ClientEventBase eventBase = ClientEventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return PingMeasurementEvent.newBuilder().setEventBase(eventBase)
												.setMeasuredMillis(measurementMillis)
												.build();
	}
}
