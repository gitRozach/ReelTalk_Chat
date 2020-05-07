package protobuf.wrapper;

import java.util.Collection;
import java.util.Collections;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.Channel;
import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientEvents.ChannelGetEvent;
import protobuf.ClientEvents.ChannelJoinEvent;
import protobuf.ClientEvents.ChannelLeaveEvent;
import protobuf.ClientEvents.ChannelMessageAnswerGetEvent;
import protobuf.ClientEvents.ChannelMessageAnswerPostEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
import protobuf.ClientEvents.EventBase;
import protobuf.ClientEvents.FileDownloadEvent;
import protobuf.ClientEvents.FileUploadEvent;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientEvents.LogoutEvent;
import protobuf.ClientEvents.PingMeasurementEvent;
import protobuf.ClientEvents.PrivateMessageGetEvent;
import protobuf.ClientEvents.PrivateMessagePostEvent;
import protobuf.ClientEvents.ProfileCommentAnswerGetEvent;
import protobuf.ClientEvents.ProfileCommentAnswerPostEvent;
import protobuf.ClientEvents.ProfileCommentGetEvent;
import protobuf.ClientEvents.ProfileCommentPostEvent;
import protobuf.ClientEvents.ProfileGetEvent;
import protobuf.ClientEvents.RegistrationEvent;
import protobuf.ClientEvents.RequestRejectedEvent;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.ClientMessages.ProfileComment;
import protobuf.ClientMessages.ProfileCommentAnswer;

public class ClientEvents {
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {	"RequestRejectedEvent", 
								"ProfileGetEvent",
								"ChannelMessagePostEvent", 
								"ChannelMessageGetEvent",
								"ChannelMessageAnswerPostEvent",
								"ChannelMessageAnswerGetEvent",
								"PrivateMessagePostEvent",
								"PrivateMessageGetEvent",
								"ProfileCommentPostEvent",
								"ProfileCommentGetEvent",
								"ProfileCommentAnswerPostEvent",
								"ProfileCommentAnswerGetEvent",
								"ChannelJoinEvent",
								"ChannelLeaveEvent",
								"ClientJoinedChannelEvent",
								"ClientLeftChannelEvent",
								"LoginEvent",
								"LogoutEvent",
								"ClientLoggedInEvent",
								"ClientLoggedOutEvent",
								"RegistrationEvent",
								"FileDownloadEvent",
								"FileUploadEvent",
								"ChannelGetEvent",
								"PingMeasurementEvent"};
	}
	
	public static boolean isEvent(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		System.out.println(messageClass.getSimpleName());
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static EventBase newEventBase(int eventId, int clientId) {
		ClientBase requestorBase = ClientBase.newBuilder().setId(clientId).build();
		return EventBase.newBuilder()	.setRequestorClientBase(requestorBase)
											.setEventId(eventId)
											.setEventTimestamp(System.currentTimeMillis())
											.build();
	}
	
	public static RequestRejectedEvent newRequestRejectedEvent(int eventId, String rejectionMessage) {
		EventBase eventBase = EventBase.newBuilder().setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return RequestRejectedEvent.newBuilder().setEventBase(eventBase)
												.setRejectionMessage(rejectionMessage)
												.build();
	}
	
	public static ProfileGetEvent newProfileGetEvent(int eventId, int clientId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ProfileGetEvent.newBuilder()	.setEventBase(eventBase)
											.setProfile(clientProfile)
											.build();
	}
	
	public static ChannelMessagePostEvent newChannelMessagePostEvent(int eventId, int clientId, Collection<ChannelMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelMessagePostEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static ChannelMessageGetEvent newChannelMessageGetEvent(int eventId, int clientId, Collection<ChannelMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelMessageGetEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}

	public static ChannelMessageAnswerPostEvent newChannelMessageAnswerPostEvent(int eventId, int clientId, Collection<ChannelMessageAnswer> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
																.setEventId(eventId)
																.setEventTimestamp(System.currentTimeMillis())
																.build();
		return ChannelMessageAnswerPostEvent.newBuilder()	.setEventBase(eventBase)
															.addAllMessageAnswer(messages)
															.build();
	}
	
	public static ChannelMessageAnswerGetEvent newChannelMessageAnswerGetEvent(int eventId, int clientId, Collection<ChannelMessageAnswer> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelMessageAnswerGetEvent.newBuilder().setEventBase(eventBase)
														.addAllMessageAnswer(messages)
														.build();
	}
	
	public static PrivateMessagePostEvent newPrivateMessagePostEvent(int eventId, int clientId, Collection<PrivateMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return PrivateMessagePostEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static PrivateMessageGetEvent newPrivateMessageGetEvent(int eventId, int clientId, Collection<PrivateMessage> messages) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return PrivateMessageGetEvent.newBuilder()	.setEventBase(eventBase)
													.addAllMessage(messages)
													.build();
	}
	
	public static ProfileCommentPostEvent newProfileCommentPostEvent(int eventId, int clientId, Collection<ProfileComment> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ProfileCommentPostEvent.newBuilder()	.setEventBase(eventBase)
													.addAllProfileComment(comments)
													.build();
	}
	
	public static ProfileCommentGetEvent newProfileCommentGetEvent(int eventId, int clientId, Collection<ProfileComment> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ProfileCommentGetEvent.newBuilder()	.setEventBase(eventBase)
													.addAllProfileComment(comments)
													.build();
	}
	
	public static ProfileCommentAnswerPostEvent newProfileCommentAnswerPostEvent(	int eventId,
																					int clientId,
																					Collection<ProfileCommentAnswer> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ProfileCommentAnswerPostEvent.newBuilder()	.setEventBase(eventBase)
															.addAllProfileCommentAnswer(comments)
															.build();
	}
	
	public static ProfileCommentAnswerGetEvent newProfileCommentAnswerGetEvent( int eventId,
																				int clientId,
																				Collection<ProfileCommentAnswer> comments) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ProfileCommentAnswerGetEvent.newBuilder().setEventBase(eventBase)
														.addAllProfileCommentAnswer(comments)
														.build();
	}
	
	public static ChannelJoinEvent newChannelJoinEvent(int eventId, int channelId, Collection<ChannelMessage> channelMessages) {
		ClientBase clientBase = ClientBase.newBuilder().build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelJoinEvent.newBuilder().setEventBase(eventBase)
											.setChannelBase(ChannelBase.newBuilder().setId(channelId))
											.addAllChannelMessage(channelMessages)
											.build();
	}
	
	public static ChannelLeaveEvent newChannelLeaveEvent(int eventId, int channelId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelLeaveEvent.newBuilder()	.setEventBase(eventBase)
												.setChannelBase(ChannelBase.newBuilder().setId(channelId))
												.setProfile(clientProfile)
												.build();
	}
	
	public static LoginEvent newLoginEvent(int eventId, ClientAccount clientAccount) {
		return newLoginEvent(eventId, clientAccount, Collections.emptyList(), Collections.emptyList());
	}
	
	public static LoginEvent newLoginEvent(int eventId, ClientAccount clientAccount, Collection<Channel> channels, Collection<ClientProfile> members) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientAccount.getProfile().getBase().getId())
														.setUsername(clientAccount.getProfile().getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return LoginEvent.newBuilder()	.setEventBase(eventBase)
										.setAccount(clientAccount)
										.addAllServerChannel(channels)
										.addAllMemberProfile(members)
										.build();
	}
	
	public static LogoutEvent newLogoutEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return LogoutEvent.newBuilder().setEventBase(eventBase).build();
	}
	
	public static RegistrationEvent newRegistrationEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return RegistrationEvent.newBuilder()	.setEventBase(eventBase)
												.setProfile(clientProfile)
												.build();
	}
	
	public static FileDownloadEvent newFileDownloadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return FileDownloadEvent.newBuilder()	.setEventBase(eventBase)
												.setProfile(clientProfile)
												.build();
	}
	
	public static FileUploadEvent newFileUploadEvent(int eventId, ClientProfile clientProfile) {
		ClientBase clientBase = ClientBase.newBuilder()	.setId(clientProfile.getBase().getId())
														.setUsername(clientProfile.getBase().getUsername())
														.build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return FileUploadEvent.newBuilder()	.setEventBase(eventBase)
											.setProfile(clientProfile)
											.build();
	}
	
	public static ChannelGetEvent newChannelGetEvent(int eventId, int clientId, Collection<Channel> channels) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return ChannelGetEvent.newBuilder()	.setEventBase(eventBase)
											.setClientBase(clientBase)
											.addAllChannel(channels)
											.build();
	}
	
	public static PingMeasurementEvent newPingMeasurementEvent(int eventId, int clientId, long measurementMillis) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).build();
		EventBase eventBase = EventBase.newBuilder().setRequestorClientBase(clientBase)
													.setEventId(eventId)
													.setEventTimestamp(System.currentTimeMillis())
													.build();
		return PingMeasurementEvent.newBuilder().setEventBase(eventBase)
												.setMeasuredMillis(measurementMillis)
												.build();
	}
}
