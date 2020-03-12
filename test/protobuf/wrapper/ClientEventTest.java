package protobuf.wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import protobuf.ClientEvents.ChannelFileDownloadEvent;
import protobuf.ClientEvents.ChannelFileUploadEvent;
import protobuf.ClientEvents.ChannelMessageAnswerGetEvent;
import protobuf.ClientEvents.ChannelMessageAnswerPostEvent;
import protobuf.ClientEvents.ChannelMessageGetEvent;
import protobuf.ClientEvents.ChannelMessagePostEvent;
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
import protobuf.ClientIdentities.AdminGroup;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientFriend;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroup;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.ClientProfileComment;
import protobuf.ClientMessages.ClientProfileCommentAnswer;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.wrapper.java.ClientEvent;
import protobuf.wrapper.java.ClientIdentity;
import protobuf.wrapper.java.ClientMessage;

class ClientEventTest {
	@Test
	public void newClientEventBase_checkValues() {
		ClientEventBase eventBase = ClientEvent.newClientEventBase(0, 1);
		assertEquals(eventBase.getEventId(), 0);
		assertEquals(eventBase.getRequestorClientBase().getId(), 1);
	}
	
	@Test
	public void newClientRequestRejectedEvent_checkValues() {
		ClientRequestRejectedEvent rejectedEvent = ClientEvent.newClientRequestRejectedEvent(0, 1, "Request rejected!");
		assertEquals(rejectedEvent.getEventBase().getEventId(), 0);
		assertEquals(rejectedEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(rejectedEvent.getRejectionMessage(), "Request rejected!");
	}
	
	@Test
	public void newClientProfileGetEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(11, 
																"Rozach", 
																ClientStatus.ONLINE, 
																ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
																ClientBadges.getDefaultInstance(), 
																ClientFriends.newBuilder().addFriend(friend).build(), 
																ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
																ClientIdentity.newClientDate(0L), 
																ClientIdentity.newClientDate(0L));
		
		ClientProfileGetEvent profileGetEvent = ClientEvent.newClientProfileGetEvent(0, 1, profile);
		assertEquals(profileGetEvent.getEventBase().getEventId(), 0);
		assertEquals(profileGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(profileGetEvent.getProfile(), profile);
	}
	
	@Test
	public void newChannelMessagePostEvent_checkValues() {
		ChannelMessage message1 = ClientMessage.newChannelMessage(0, "Wer", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message2 = ClientMessage.newChannelMessage(1, "das", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message3 = ClientMessage.newChannelMessage(2, "liest", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message4 = ClientMessage.newChannelMessage(3, "ist", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message5 = ClientMessage.newChannelMessage(4, "Hasan", 1, "Rozach", 2, 0L, Collections.emptyList());
		List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		
		ChannelMessagePostEvent messagePostEvent = ClientEvent.newChannelMessagePostEvent(0, 1, messages);
		assertEquals(messagePostEvent.getEventBase().getEventId(), 0);
		assertEquals(messagePostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messagePostEvent.getMessage(0), message1);
		assertEquals(messagePostEvent.getMessage(1), message2);
		assertEquals(messagePostEvent.getMessage(2), message3);
		assertEquals(messagePostEvent.getMessage(3), message4);
		assertEquals(messagePostEvent.getMessage(4), message5);
	}
	
	@Test
	public void newChannelMessageGetEvent_checkValues() {
		ChannelMessage message1 = ClientMessage.newChannelMessage(0, "Wer", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message2 = ClientMessage.newChannelMessage(1, "das", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message3 = ClientMessage.newChannelMessage(2, "liest", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message4 = ClientMessage.newChannelMessage(3, "ist", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message5 = ClientMessage.newChannelMessage(4, "Huasahn", 1, "Rozach", 2, 0L, Collections.emptyList());
		List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		ChannelMessageGetEvent messageGetEvent = ClientEvent.newChannelMessageGetEvent(0, 1, messages);
		assertEquals(messageGetEvent.getEventBase().getEventId(), 0);
		assertEquals(messageGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageGetEvent.getMessage(0), message1);
		assertEquals(messageGetEvent.getMessage(1), message2);
		assertEquals(messageGetEvent.getMessage(2), message3);
		assertEquals(messageGetEvent.getMessage(3), message4);
		assertEquals(messageGetEvent.getMessage(4), message5);
	}
	
	@Test
	public void newChannelMessageAnswerPostEvent_checkValues() {
		ChannelMessageAnswer messageAnswer = ClientMessage.newChannelMessageAnswer(0, "Antwort auf Nachricht.", 1, "Rozach", 2, 4, 0L);
		List<ChannelMessageAnswer> messages = new ArrayList<ChannelMessageAnswer>();
		messages.add(messageAnswer);
		ChannelMessageAnswerPostEvent messageAnswerPostEvent = ClientEvent.newChannelMessageAnswerPostEvent(0, 1, messages);
		assertEquals(messageAnswerPostEvent.getEventBase().getEventId(), 0);
		assertEquals(messageAnswerPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageAnswerPostEvent.getMessageAnswer(0), messageAnswer);
	}
	
	@Test
	public void newChannelMessageAnswerGetEvent_checkValues() {
		ChannelMessageAnswer message1 = ClientMessage.newChannelMessageAnswer(0, "Wer", 1, "Rozach", 2, 4,  0L);
		ChannelMessageAnswer message2 = ClientMessage.newChannelMessageAnswer(1, "das", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message3 = ClientMessage.newChannelMessageAnswer(2, "liest", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message4 = ClientMessage.newChannelMessageAnswer(3, "ist", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message5 = ClientMessage.newChannelMessageAnswer(4, "Huasahn", 1, "Rozach", 2, 4, 0L);
		List<ChannelMessageAnswer> messages = new ArrayList<ChannelMessageAnswer>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		ChannelMessageAnswerGetEvent messageAnswerGetEvent = ClientEvent.newChannelMessageAnswerGetEvent(0, 1, messages);
		assertEquals(messageAnswerGetEvent.getEventBase().getEventId(), 0);
		assertEquals(messageAnswerGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(0), message1);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(1), message2);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(2), message3);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(3), message4);
	}
	
	@Test
	public void newPrivateMessagePostEvent_checkValues() {
		PrivateMessage message = ClientMessage.newPrivateMessage(0, "Hallo Hasan", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<PrivateMessage> messages = new ArrayList<PrivateMessage>();
		messages.add(message);
		PrivateMessagePostEvent messagePostEvent = ClientEvent.newPrivateMessagePostEvent(0, 1, messages);
		assertEquals(messagePostEvent.getEventBase().getEventId(), 0);
		assertEquals(messagePostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messagePostEvent.getMessage(0), message);
	}
	
	@Test
	public void newPrivateMessageGetEvent_checkValues() {
		PrivateMessage message1 = ClientMessage.newPrivateMessage(0, "Hallo Hasan", 1, "Rozach", 9, 0L, Collections.emptyList());
		PrivateMessage message2 = ClientMessage.newPrivateMessage(1, "Du Schacken!", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<PrivateMessage> messages = new ArrayList<PrivateMessage>();
		messages.add(message1);
		messages.add(message2);
		PrivateMessageGetEvent messageGetEvent = ClientEvent.newPrivateMessageGetEvent(0, 1, messages);
		assertEquals(messageGetEvent.getEventBase().getEventId(), 0);
		assertEquals(messageGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageGetEvent.getMessage(0), message1);
		assertEquals(messageGetEvent.getMessage(1), message2);
	}
	
	@Test
	public void newClientProfileCommentPostEvent_checkValues() {
		ClientProfileComment comment = ClientMessage.newClientProfileComment(0, "Kommentar", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<ClientProfileComment> comments = new ArrayList<ClientProfileComment>();
		comments.add(comment);
		ClientProfileCommentPostEvent commentPostEvent = ClientEvent.newClientProfileCommentPostEvent(0, 1, comments);
		assertEquals(commentPostEvent.getEventBase().getEventId(), 0);
		assertEquals(commentPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentPostEvent.getProfileComment(0), comment);
	}
	
	@Test
	public void newClientProfileCommentGetEvent_checkValues() {
		ClientProfileComment comment1 = ClientMessage.newClientProfileComment(0, "Kommentar 1", 1, "Rozach", 9, 0L, Collections.emptyList());
		ClientProfileComment comment2 = ClientMessage.newClientProfileComment(1, "Kommentar 2", 1, "Rozach", 9, 0L, Collections.emptyList());
		ClientProfileComment comment3 = ClientMessage.newClientProfileComment(2, "Kommentar 3", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<ClientProfileComment> comments = new ArrayList<ClientProfileComment>();
		comments.add(comment1);
		comments.add(comment2);
		comments.add(comment3);
		ClientProfileCommentGetEvent commentGetEvent = ClientEvent.newClientProfileCommentGetEvent(0, 1, comments);
		assertEquals(commentGetEvent.getEventBase().getEventId(), 0);
		assertEquals(commentGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentGetEvent.getProfileComment(0), comment1);
		assertEquals(commentGetEvent.getProfileComment(1), comment2);
		assertEquals(commentGetEvent.getProfileComment(2), comment3);
	}
	
	@Test
	public void newClientProfileCommentAnswerPostEvent_checkValues() {
		ClientProfileCommentAnswer answer = ClientMessage.newClientProfileCommentAnswer(0, "Kommentar", 1, "Rozach", 9, 1, 0L);
		List<ClientProfileCommentAnswer> answers = new ArrayList<ClientProfileCommentAnswer>();
		answers.add(answer);
		ClientProfileCommentAnswerPostEvent commentPostEvent = ClientEvent.newClientProfileCommentAnswerPostEvent(0, 1, answers);
		assertEquals(commentPostEvent.getEventBase().getEventId(), 0);
		assertEquals(commentPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentPostEvent.getProfileCommentAnswer(0), answer);
	}
	
	@Test
	public void newClientProfileCommentAnswerGetEvent_checkValues() {
		ClientProfileCommentAnswer answer1 = ClientMessage.newClientProfileCommentAnswer(0, "Kommentar 1", 1, "Rozach", 9, 1, 0L);
		ClientProfileCommentAnswer answer2 = ClientMessage.newClientProfileCommentAnswer(1, "Kommentar 2", 1, "Rozach", 9, 1, 0L);
		ClientProfileCommentAnswer answer3 = ClientMessage.newClientProfileCommentAnswer(2, "Kommentar 3", 1, "Rozach", 9, 1, 0L);
		List<ClientProfileCommentAnswer> answers = new ArrayList<ClientProfileCommentAnswer>();
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		ClientProfileCommentAnswerGetEvent commentGetEvent = ClientEvent.newClientProfileCommentAnswerGetEvent(0, 1, answers);
		assertEquals(commentGetEvent.getEventBase().getEventId(), 0);
		assertEquals(commentGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentGetEvent.getProfileCommentAnswer(0), answer1);
		assertEquals(commentGetEvent.getProfileCommentAnswer(1), answer2);
		assertEquals(commentGetEvent.getProfileCommentAnswer(2), answer3);
	}
	
	@Test
	public void newClientChannelJoinEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ClientChannelJoinEvent joinEvent = ClientEvent.newClientChannelJoinEvent(0, profile);
		assertEquals(joinEvent.getEventBase().getEventId(), 0);
		assertEquals(joinEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(joinEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientChannelLeaveEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ClientChannelLeaveEvent leaveEvent = ClientEvent.newClientChannelLeaveEvent(0, profile);
		assertEquals(leaveEvent.getEventBase().getEventId(), 0);
		assertEquals(leaveEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(leaveEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientLoginEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ClientLoginEvent loginEvent = ClientEvent.newClientLoginEvent(0, profile);
		assertEquals(loginEvent.getEventBase().getEventId(), 0);
		assertEquals(loginEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(loginEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientLogoutEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ClientLogoutEvent logoutEvent = ClientEvent.newClientLogoutEvent(0, profile);
		assertEquals(logoutEvent.getEventBase().getEventId(), 0);
		assertEquals(logoutEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(logoutEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientRegistrationEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ClientRegistrationEvent registrationEvent = ClientEvent.newClientRegistrationEvent(0, profile);
		assertEquals(registrationEvent.getEventBase().getEventId(), 0);
		assertEquals(registrationEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(registrationEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientChannelFileDownloadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ChannelFileDownloadEvent downloadEvent = ClientEvent.newChannelFileDownloadEvent(0, profile);
		assertEquals(downloadEvent.getEventBase().getEventId(), 0);
		assertEquals(downloadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(downloadEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientChannelFileUploadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		ChannelFileUploadEvent uploadEvent = ClientEvent.newChannelFileUploadEvent(0, profile);
		assertEquals(uploadEvent.getEventBase().getEventId(), 0);
		assertEquals(uploadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(uploadEvent.getProfile(), profile);
	}
	
	@Test
	public void newPrivateFileDownloadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		PrivateFileDownloadEvent downloadEvent = ClientEvent.newPrivateFileDownloadEvent(0, profile);
		assertEquals(downloadEvent.getEventBase().getEventId(), 0);
		assertEquals(downloadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(downloadEvent.getProfile(), profile);
	}
	
	@Test
	public void newPrivateFileUploadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentity.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentity.newClientDate(0L), 
				ClientIdentity.newClientDate(0L));
		PrivateFileUploadEvent uploadEvent = ClientEvent.newPrivateFileUploadEvent(0, profile);
		assertEquals(uploadEvent.getEventBase().getEventId(), 0);
		assertEquals(uploadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(uploadEvent.getProfile(), profile);
	}
	
	@Test
	public void newPingMeasurementEvent_checkValues() {
		PingMeasurementEvent measurementEvent = ClientEvent.newPingMeasurementEvent(0, 1, 25L);
		assertEquals(measurementEvent.getEventBase().getEventId(), 0);
		assertEquals(measurementEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(measurementEvent.getMeasuredMillis(), 25L);
	}
}
