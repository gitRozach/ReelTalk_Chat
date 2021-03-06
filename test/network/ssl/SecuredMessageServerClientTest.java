package network.ssl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.GregorianCalendar;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Message;

import network.messages.ProtobufMessage;
import network.peer.client.ReelTalkClient;
import network.peer.server.ReelTalkServer;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientIdentities.AdminGroup;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadge;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientFriend;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroup;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.ClientRequests.LoginRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;
import protobuf.wrapper.ClientEvents;
import protobuf.wrapper.ClientIdentities;
import protobuf.wrapper.ClientRequests;

class SecuredMessageServerClientTest {

	protected static ReelTalkServer server;
	
	protected static final String TEST_PROTOCOL = "TLSv1.2";
	protected static final String TEST_HOST_ADDRESS = "localhost";
	protected static final int TEST_HOST_PORT = 2197;
		
	public static ClientAccount createSampleAccount() {
		ClientImages images = ClientImages.newBuilder().setProfileImageURI("/accounts/TestoRozach/pictures/profileImage.png").build();
		ClientBadge badge1 = ClientBadge.newBuilder().setBadgeId(1).setBadgeName("Badge 1").setBadgeDescription("Badge Description 1").build();
		ClientBadge badge2 = ClientBadge.newBuilder().setBadgeId(2).setBadgeName("Badge 2").setBadgeDescription("Badge Description 2").build();
		ClientBadge badge3 = ClientBadge.newBuilder().setBadgeId(3).setBadgeName("Badge 3").setBadgeDescription("Badge Description 3").build();
		ClientBadges badges = ClientBadges.newBuilder().addBadge(badge1).addBadge(badge2).addBadge(badge3).build();
		ClientFriend friend1 = ClientFriend.newBuilder().setClientId(10).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 22, 30, 0).getTimeInMillis())).build();
		ClientFriend friend2 = ClientFriend.newBuilder().setClientId(11).setMarkedAsBuddy(false).setDateFriendsSince(ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 21, 30, 0).getTimeInMillis())).build();
		ClientFriend friend3 = ClientFriend.newBuilder().setClientId(12).setMarkedAsBuddy(false).setDateFriendsSince(ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 20, 30, 0).getTimeInMillis())).build();
		ClientFriends friends = ClientFriends.newBuilder().addFriend(friend1).addFriend(friend2).addFriend(friend3).build();
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(1).setGroupName("Moderator").setPermissionLevel(1).setDateMemberSince(ClientIdentities.newClientDate(new GregorianCalendar(2020, 1, 1).getTimeInMillis())).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(1).setGroupName("Junior").setGroupLevel(2).setDateMemberSince(ClientIdentities.newClientDate(new GregorianCalendar(2020, 0, 30).getTimeInMillis())).build();
		ClientGroups groups = ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build();
		ClientProfile profile =  ClientIdentities.newClientProfile(	1, 
																	"Rozach", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		return ClientIdentities.newClientAccount(profile, "rozachPass");
	}
	
	@BeforeAll
	public static void setUp() throws Exception {
		server = new ReelTalkServer(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
		server.setBufferingReceivedMessages(true);
		server.start();
		Thread.sleep(50L);
	}
	
	@Test
	void sendMessage_serverSendsMultipleMessagesAndClientReceivesAllMessages() throws Exception {
		try(ReelTalkClient client1 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client2 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client3 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client4 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client5 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client1.connect();
			client1.setBufferingReceivedMessages(true);
			client2.connect();
			client2.setBufferingReceivedMessages(true);
			client3.connect();
			client3.setBufferingReceivedMessages(true);
			client4.connect();
			client4.setBufferingReceivedMessages(true);
			client5.connect();
			client5.setBufferingReceivedMessages(true);
			
			LoginEvent eventMessage = ClientEvents.newLoginEvent(1, createSampleAccount());
			
			for(int i = 0; i < 100; ++i) {
				server.sendMessage(client1.getChannel(), eventMessage);
				server.sendMessage(client2.getChannel(), eventMessage);
				server.sendMessage(client3.getChannel(), eventMessage);
				server.sendMessage(client4.getChannel(), eventMessage);
				server.sendMessage(client5.getChannel(), eventMessage);
			}
			for(int a = 0; a < 100; ++a) {
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client1.hasReceivableBytes());
				Message reception1 = client1.readMessage();
				assertTrue(reception1 instanceof LoginEvent);
				
//				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client2.hasReceivableBytes());
//				Message reception2 = client2.readMessage();
//				assertTrue(reception2 instanceof LoginEvent);
//				
//				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client3.hasReceivableBytes());
//				Message reception3 = client3.readMessage();
//				assertTrue(reception3 instanceof LoginEvent);
//				
//				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client4.hasReceivableBytes());
//				Message reception4 = client4.readMessage();
//				assertTrue(reception4 instanceof LoginEvent);
//				
//				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client5.hasReceivableBytes());
//				Message reception5 = client5.readMessage();
//				assertTrue(reception5 instanceof LoginEvent);
			}
		}
	}
	
	@Test
	void readMessage_clientRetrievesFirstMessage() throws Exception {
		LoginEvent eventMessage = ClientEvents.newLoginEvent(1, createSampleAccount());
		try(ReelTalkClient client = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.setBufferingReceivedMessages(true);
			
			Thread.sleep(250L);
			
			server.sendMessage(client.getChannel(), eventMessage);
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.hasReceivableBytes());
			Message reception = client.readMessage();
			assertTrue(reception.getClass().equals(eventMessage.getClass()));
		}
	}
	
//	@Test
//	void readMessage_clientRetrievesSpecificMessage() throws Exception {
//		PrivateMessageEvent fillMessage1 = new PrivateMessageEvent();
//		PrivateMessageEvent fillMessage2 = new PrivateMessageEvent();
//		PrivateMessageEvent fillMessage3 = new PrivateMessageEvent();
//		ClientLoggedInEvent messageToReceive = new ClientLoggedInEvent();
//		
//		SecuredChatClient client = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
//		//client.enableMessageHandler(false);
//		client.connect();
//		
//		server.sendMessage(server.getLocalSelectionKey(server.getLocalSocketChannel(client.getChannel())), fillMessage1);
//		System.out.println("Sent 1");
//		server.sendMessage(server.getLocalSelectionKey(server.getLocalSocketChannel(client.getChannel())), fillMessage2);
//		System.out.println("Sent 2");
//		server.sendMessage(server.getLocalSelectionKey(server.getLocalSocketChannel(client.getChannel())), fillMessage3);
//		System.out.println("Sent 3");
//		server.sendMessage(server.getLocalSelectionKey(server.getLocalSocketChannel(client.getChannel())), messageToReceive);
//		System.out.println("Sent 4");
//
//		Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.readMessage(ClientLoggedInEvent.class) != null);
//	}
	
	@Test
	void clientLogin_validLoginTest() throws Exception {
		LoginRequest req = ClientRequests.newLoginRequest(1, "TestoRozach", "rozachPass");
		try(ReelTalkClient client = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			assertTrue(client.connect());
			client.sendMessage(req);
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceivableBytes());
			ProtobufMessage message = server.pollReceptionBytes();
			assertTrue(message.getMessage() instanceof LoginRequest);
		}
	}
	
	@Test
	void clientLogin_invalidLoginTest() throws Exception {
		LoginRequest req = ClientRequests.newLoginRequest(1, "Rozach", "rozachPass");
		try(ReelTalkClient client = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.sendMessage(req);
			
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceivableBytes());
			ProtobufMessage message = server.pollReceptionBytes();
			assertTrue(message.getMessage() instanceof LoginRequest);
		}
	}
	
	@Test
	void sendMessage_tenClientsSendMultipleMessagesAndServerReceivesAllMessages() throws Exception {
		PrivateMessagePostRequest requestMessage = ClientRequests.newPrivateMessagePostRequest(1, "Rozach", "rozachPass", 12, "Hallo Jann!");
		try(ReelTalkClient client1 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client2 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client3 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client4 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client5 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client6 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client7 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client8 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client9 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			ReelTalkClient client10 = new ReelTalkClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client1.connect();
			client2.connect();
			client3.connect();
			client4.connect();
			client5.connect();
			client6.connect();
			client7.connect();
			client8.connect();
			client9.connect();
			client10.connect();
			for(int i = 0; i < 10; ++i) {
				client1.sendMessage(requestMessage);
				client2.sendMessage(requestMessage);
				client3.sendMessage(requestMessage);
				client4.sendMessage(requestMessage);
				client5.sendMessage(requestMessage);
				client6.sendMessage(requestMessage);
				client7.sendMessage(requestMessage);
				client8.sendMessage(requestMessage);
				client9.sendMessage(requestMessage);
				client10.sendMessage(requestMessage);
			}
			for(int y = 0; y < 100; ++y) {
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> server.hasReceivableBytes());
				ProtobufMessage message = server.pollReceptionBytes();
				PrivateMessagePostRequest receptionMessage = (PrivateMessagePostRequest)message.getMessage();
				assertEquals(requestMessage, receptionMessage);
			}
		}
	}

}
