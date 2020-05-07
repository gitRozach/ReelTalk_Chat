package protobuf.wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

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
import protobuf.ClientIdentities.AdminGroup;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientFriend;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroup;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ChannelMessageAnswer;
import protobuf.ClientMessages.PrivateMessage;
import protobuf.ClientMessages.ProfileComment;
import protobuf.ClientMessages.ProfileCommentAnswer;

class ClientEventTest {
	@Test
	public void newClientEventBase_checkValues() {
		EventBase eventBase = ClientEvents.newEventBase(0, 1);
		assertEquals(eventBase.getEventId(), 0);
		assertEquals(eventBase.getRequestorClientBase().getId(), 1);
	}
	
	@Test
	public void newClientRequestRejectedEvent_checkValues() {
		RequestRejectedEvent rejectedEvent = ClientEvents.newRequestRejectedEvent(0, "Request rejected!");
		assertEquals(rejectedEvent.getEventBase().getEventId(), 0);
		assertEquals(rejectedEvent.getRejectionMessage(), "Request rejected!");
	}
	
	@Test
	public void newClientProfileGetEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(11, 
																"Rozach", 
																ClientStatus.ONLINE, 
																ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
																ClientBadges.getDefaultInstance(), 
																ClientFriends.newBuilder().addFriend(friend).build(), 
																ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
																ClientIdentities.newClientDate(0L), 
																ClientIdentities.newClientDate(0L));
		
		ProfileGetEvent profileGetEvent = ClientEvents.newProfileGetEvent(0, 1, profile);
		assertEquals(profileGetEvent.getEventBase().getEventId(), 0);
		assertEquals(profileGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(profileGetEvent.getProfile(), profile);
	}
	
	@Test
	public void newChannelMessagePostEvent_checkValues() {
		ChannelMessage message1 = ClientMessages.newChannelMessage(0, "Wer", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message2 = ClientMessages.newChannelMessage(1, "das", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message3 = ClientMessages.newChannelMessage(2, "liest", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message4 = ClientMessages.newChannelMessage(3, "ist", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message5 = ClientMessages.newChannelMessage(4, "Hasan", 1, "Rozach", 2, 0L, Collections.emptyList());
		List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		
		ChannelMessagePostEvent messagePostEvent = ClientEvents.newChannelMessagePostEvent(0, 1, messages);
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
		ChannelMessage message1 = ClientMessages.newChannelMessage(0, "Wer", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message2 = ClientMessages.newChannelMessage(1, "das", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message3 = ClientMessages.newChannelMessage(2, "liest", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message4 = ClientMessages.newChannelMessage(3, "ist", 1, "Rozach", 2, 0L, Collections.emptyList());
		ChannelMessage message5 = ClientMessages.newChannelMessage(4, "Huasahn", 1, "Rozach", 2, 0L, Collections.emptyList());
		List<ChannelMessage> messages = new ArrayList<ChannelMessage>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		ChannelMessageGetEvent messageGetEvent = ClientEvents.newChannelMessageGetEvent(0, 1, messages);
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
		ChannelMessageAnswer messageAnswer = ClientMessages.newChannelMessageAnswer(0, "Antwort auf Nachricht.", 1, "Rozach", 2, 4, 0L);
		List<ChannelMessageAnswer> messages = new ArrayList<ChannelMessageAnswer>();
		messages.add(messageAnswer);
		ChannelMessageAnswerPostEvent messageAnswerPostEvent = ClientEvents.newChannelMessageAnswerPostEvent(0, 1, messages);
		assertEquals(messageAnswerPostEvent.getEventBase().getEventId(), 0);
		assertEquals(messageAnswerPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageAnswerPostEvent.getMessageAnswer(0), messageAnswer);
	}
	
	@Test
	public void newChannelMessageAnswerGetEvent_checkValues() {
		ChannelMessageAnswer message1 = ClientMessages.newChannelMessageAnswer(0, "Wer", 1, "Rozach", 2, 4,  0L);
		ChannelMessageAnswer message2 = ClientMessages.newChannelMessageAnswer(1, "das", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message3 = ClientMessages.newChannelMessageAnswer(2, "liest", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message4 = ClientMessages.newChannelMessageAnswer(3, "ist", 1, "Rozach", 2, 4, 0L);
		ChannelMessageAnswer message5 = ClientMessages.newChannelMessageAnswer(4, "Huasahn", 1, "Rozach", 2, 4, 0L);
		List<ChannelMessageAnswer> messages = new ArrayList<ChannelMessageAnswer>();
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);
		messages.add(message4);
		messages.add(message5);
		ChannelMessageAnswerGetEvent messageAnswerGetEvent = ClientEvents.newChannelMessageAnswerGetEvent(0, 1, messages);
		assertEquals(messageAnswerGetEvent.getEventBase().getEventId(), 0);
		assertEquals(messageAnswerGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(0), message1);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(1), message2);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(2), message3);
		assertEquals(messageAnswerGetEvent.getMessageAnswer(3), message4);
	}
	
	@Test
	public void newPrivateMessagePostEvent_checkValues() {
		PrivateMessage message = ClientMessages.newPrivateMessage(0, "Hallo Hasan", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<PrivateMessage> messages = new ArrayList<PrivateMessage>();
		messages.add(message);
		PrivateMessagePostEvent messagePostEvent = ClientEvents.newPrivateMessagePostEvent(0, 1, messages);
		assertEquals(messagePostEvent.getEventBase().getEventId(), 0);
		assertEquals(messagePostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messagePostEvent.getMessage(0), message);
	}
	
	@Test
	public void newPrivateMessageGetEvent_checkValues() {
		PrivateMessage message1 = ClientMessages.newPrivateMessage(0, "Hallo Hasan", 1, "Rozach", 9, 0L, Collections.emptyList());
		PrivateMessage message2 = ClientMessages.newPrivateMessage(1, "Du Schacken!", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<PrivateMessage> messages = new ArrayList<PrivateMessage>();
		messages.add(message1);
		messages.add(message2);
		PrivateMessageGetEvent messageGetEvent = ClientEvents.newPrivateMessageGetEvent(0, 1, messages);
		assertEquals(messageGetEvent.getEventBase().getEventId(), 0);
		assertEquals(messageGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(messageGetEvent.getMessage(0), message1);
		assertEquals(messageGetEvent.getMessage(1), message2);
	}
	
	@Test
	public void newClientProfileCommentPostEvent_checkValues() {
		ProfileComment comment = ClientMessages.newProfileComment(0, "Kommentar", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<ProfileComment> comments = new ArrayList<ProfileComment>();
		comments.add(comment);
		ProfileCommentPostEvent commentPostEvent = ClientEvents.newProfileCommentPostEvent(0, 1, comments);
		assertEquals(commentPostEvent.getEventBase().getEventId(), 0);
		assertEquals(commentPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentPostEvent.getProfileComment(0), comment);
	}
	
	@Test
	public void newClientProfileCommentGetEvent_checkValues() {
		ProfileComment comment1 = ClientMessages.newProfileComment(0, "Kommentar 1", 1, "Rozach", 9, 0L, Collections.emptyList());
		ProfileComment comment2 = ClientMessages.newProfileComment(1, "Kommentar 2", 1, "Rozach", 9, 0L, Collections.emptyList());
		ProfileComment comment3 = ClientMessages.newProfileComment(2, "Kommentar 3", 1, "Rozach", 9, 0L, Collections.emptyList());
		List<ProfileComment> comments = new ArrayList<ProfileComment>();
		comments.add(comment1);
		comments.add(comment2);
		comments.add(comment3);
		ProfileCommentGetEvent commentGetEvent = ClientEvents.newProfileCommentGetEvent(0, 1, comments);
		assertEquals(commentGetEvent.getEventBase().getEventId(), 0);
		assertEquals(commentGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentGetEvent.getProfileComment(0), comment1);
		assertEquals(commentGetEvent.getProfileComment(1), comment2);
		assertEquals(commentGetEvent.getProfileComment(2), comment3);
	}
	
	@Test
	public void newClientProfileCommentAnswerPostEvent_checkValues() {
		ProfileCommentAnswer answer = ClientMessages.newProfileCommentAnswer(0, "Kommentar", 1, "Rozach", 9, 1, 0L);
		List<ProfileCommentAnswer> answers = new ArrayList<ProfileCommentAnswer>();
		answers.add(answer);
		ProfileCommentAnswerPostEvent commentPostEvent = ClientEvents.newProfileCommentAnswerPostEvent(0, 1, answers);
		assertEquals(commentPostEvent.getEventBase().getEventId(), 0);
		assertEquals(commentPostEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentPostEvent.getProfileCommentAnswer(0), answer);
	}
	
	@Test
	public void newClientProfileCommentAnswerGetEvent_checkValues() {
		ProfileCommentAnswer answer1 = ClientMessages.newProfileCommentAnswer(0, "Kommentar 1", 1, "Rozach", 9, 1, 0L);
		ProfileCommentAnswer answer2 = ClientMessages.newProfileCommentAnswer(1, "Kommentar 2", 1, "Rozach", 9, 1, 0L);
		ProfileCommentAnswer answer3 = ClientMessages.newProfileCommentAnswer(2, "Kommentar 3", 1, "Rozach", 9, 1, 0L);
		List<ProfileCommentAnswer> answers = new ArrayList<ProfileCommentAnswer>();
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		ProfileCommentAnswerGetEvent commentGetEvent = ClientEvents.newProfileCommentAnswerGetEvent(0, 1, answers);
		assertEquals(commentGetEvent.getEventBase().getEventId(), 0);
		assertEquals(commentGetEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(commentGetEvent.getProfileCommentAnswer(0), answer1);
		assertEquals(commentGetEvent.getProfileCommentAnswer(1), answer2);
		assertEquals(commentGetEvent.getProfileCommentAnswer(2), answer3);
	}
	
//	@Test
//	public void newClientChannelJoinEvent_checkValues() {
//		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
//		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
//		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
//		ClientProfile profile = ClientIdentities.newClientProfile(1, 
//				"Rozach", 
//				ClientStatus.ONLINE, 
//				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
//				ClientBadges.getDefaultInstance(), 
//				ClientFriends.newBuilder().addFriend(friend).build(), 
//				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
//				ClientIdentities.newClientDate(0L), 
//				ClientIdentities.newClientDate(0L));
//		ChannelJoinEvent joinEvent = ClientEvents.newChannelJoinEvent(0, 1, profile);
//		assertEquals(joinEvent.getEventBase().getEventId(), 0);
//		assertEquals(joinEvent.getEventBase().getRequestorClientBase().getId(), 1);
//		assertEquals(joinEvent.getChannelBase().getId(), 1);
//		assertEquals(joinEvent.getProfile(), profile);
//	}
	
	@Test
	public void newClientChannelLeaveEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		ChannelLeaveEvent leaveEvent = ClientEvents.newChannelLeaveEvent(0, 1, profile);
		assertEquals(leaveEvent.getEventBase().getEventId(), 0);
		assertEquals(leaveEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(leaveEvent.getChannelBase().getId(), 1);
		assertEquals(leaveEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientLoginEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		ClientAccount account = ClientIdentities.newClientAccount(profile, "rozachPass");
		LoginEvent loginEvent = ClientEvents.newLoginEvent(0, account);
		assertEquals(loginEvent.getEventBase().getEventId(), 0);
		assertEquals(loginEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(loginEvent.getAccount().getRegisteredDevice(0), ClientDevice.getDefaultInstance());
		assertEquals(loginEvent.getAccount(), account);
	}
	
	@Test
	public void newClientLogoutEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		LogoutEvent logoutEvent = ClientEvents.newLogoutEvent(0, profile);
		assertEquals(logoutEvent.getEventBase().getEventId(), 0);
		assertEquals(logoutEvent.getEventBase().getRequestorClientBase().getId(), 1);
//		assertEquals(logoutEvent.getProfile(), profile);
	}
	
	@Test
	public void newClientRegistrationEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		RegistrationEvent registrationEvent = ClientEvents.newRegistrationEvent(0, profile);
		assertEquals(registrationEvent.getEventBase().getEventId(), 0);
		assertEquals(registrationEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(registrationEvent.getProfile(), profile);
	}
	
	@Test
	public void newFileDownloadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		FileDownloadEvent downloadEvent = ClientEvents.newFileDownloadEvent(0, profile);
		assertEquals(downloadEvent.getEventBase().getEventId(), 0);
		assertEquals(downloadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(downloadEvent.getProfile(), profile);
	}
	
	@Test
	public void newFileUploadEvent_checkValues() {
		ClientFriend friend = ClientFriend.newBuilder().setClientId(19).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(0L)).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(9).setGroupName("Host").setPermissionLevel(10).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(4).setGroupName("Senior Member").setGroupLevel(24).build();
		ClientProfile profile = ClientIdentities.newClientProfile(1, 
				"Rozach", 
				ClientStatus.ONLINE, 
				ClientImages.newBuilder().setProfileImageURI("image.png").build(), 
				ClientBadges.getDefaultInstance(), 
				ClientFriends.newBuilder().addFriend(friend).build(), 
				ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build(),
				ClientIdentities.newClientDate(0L), 
				ClientIdentities.newClientDate(0L));
		FileUploadEvent uploadEvent = ClientEvents.newFileUploadEvent(0, profile);
		assertEquals(uploadEvent.getEventBase().getEventId(), 0);
		assertEquals(uploadEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(uploadEvent.getProfile(), profile);
	}
	
	@Test
	public void newPingMeasurementEvent_checkValues() {
		PingMeasurementEvent measurementEvent = ClientEvents.newPingMeasurementEvent(0, 1, 25L);
		assertEquals(measurementEvent.getEventBase().getEventId(), 0);
		assertEquals(measurementEvent.getEventBase().getRequestorClientBase().getId(), 1);
		assertEquals(measurementEvent.getMeasuredMillis(), 25L);
	}
}
