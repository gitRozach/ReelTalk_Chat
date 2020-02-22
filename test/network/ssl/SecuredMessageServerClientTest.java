package network.ssl;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import network.ssl.client.SecuredMessageClient;
import network.ssl.communication.MessagePacket;
import network.ssl.communication.ProtobufMessage;
import network.ssl.server.SecuredMessageServer;
import protobuf.ClientEvents.ClientLoggedInEvent;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.PrivateMessageRequest;

class SecuredMessageServerClientTest {

	private static SecuredMessageServer server;
	
	private static final String TEST_PROTOCOL = "TLSv1.2";
	private static final String TEST_HOST_ADDRESS = "localhost";
	private static final int TEST_HOST_PORT = 2197;
	
	@BeforeAll
	public static void setUp() throws Exception {
		server = new SecuredMessageServer(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
		server.setBufferingReceivedBytes(true);
		server.start();
		Thread.sleep(50L);
	}
	
	@Test
	void sendMessage_serverSendsMultipleMessagesAndClientReceivesAllMessages() throws Exception {
		try(SecuredMessageClient client1 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client2 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client3 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client4 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client5 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client1.connect();
			client1.setBufferingReceivedBytes(true);
			client2.connect();
			client2.setBufferingReceivedBytes(true);
			client3.connect();
			client3.setBufferingReceivedBytes(true);
			client4.connect();
			client4.setBufferingReceivedBytes(true);
			client5.connect();
			client5.setBufferingReceivedBytes(true);
			
			for(int i = 0; i < 100; ++i) {
				server.sendMessage(server.getLocalSocketChannel(client1.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client2.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client3.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client4.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client5.getChannel()), new ClientLoggedInEvent());
			}
			for(int a = 0; a < 100; ++a) {
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client1.hasReceivableBytes());
				MessagePacket reception1 = client1.readMessage();
				assertTrue(reception1 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client2.hasReceivableBytes());
				MessagePacket reception2 = client2.readMessage();
				assertTrue(reception2 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client3.hasReceivableBytes());
				MessagePacket reception3 = client3.readMessage();
				assertTrue(reception3 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client4.hasReceivableBytes());
				MessagePacket reception4 = client4.readMessage();
				assertTrue(reception4 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client5.hasReceivableBytes());
				MessagePacket reception5 = client5.readMessage();
				assertTrue(reception5 instanceof ClientLoggedInEvent);
			}
		}
	}
	
	@Test
	void readMessage_clientRetrievesFirstMessage() throws Exception {
		ClientLoggedInEvent messageToReceive = new ClientLoggedInEvent();
		try(SecuredMessageClient client = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.setBufferingReceivedBytes(true);
			
			Thread.sleep(250L);
			
			server.sendMessage(server.getLocalSocketChannel(client.getChannel()), messageToReceive);
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.hasReceivableBytes());
			MessagePacket reception = client.readMessage();
			assertTrue(reception.getClass().equals(messageToReceive.getClass()));
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
		ClientLoginRequest req = new ClientLoginRequest("Rozach", "jajut");
		try(SecuredMessageClient client = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			assertTrue(client.connect());
			client.sendMessage(req);
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceivableBytes());
			ProtobufMessage byteMessage = server.pollReceptionBytes();
			MessagePacket message = MessagePacket.deserialize(byteMessage.getMessageBytes());
			assertTrue(message instanceof ClientLoginRequest);
		}
	}
	
	@Test
	void clientLogin_invalidLoginTest() throws Exception {
		ClientLoginRequest req = new ClientLoginRequest("Does_not_exist", "wrong_anyway");
		try(SecuredMessageClient client = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.sendMessage(req);
			
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceivableBytes());
			ProtobufMessage byteMessage = server.pollReceptionBytes();
			MessagePacket message = MessagePacket.deserialize(byteMessage.getMessageBytes());
			assertTrue(message instanceof ClientLoginRequest);
		}
	}
	
	@Test
	void sendMessage_tenClientsSendMultipleMessagesAndServerReceivesAllMessages() throws Exception {
		PrivateMessageRequest requestMessage = new PrivateMessageRequest("rozach", "testpass", 12, "Hallo Client mit ID 12!");
		try(SecuredMessageClient client1 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client2 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client3 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client4 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client5 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client6 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client7 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client8 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client9 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredMessageClient client10 = new SecuredMessageClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
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
				MessagePacket currentMes = MessagePacket.deserialize(server.pollReceptionBytes().getMessageBytes());
				PrivateMessageRequest receptionMessage = (PrivateMessageRequest)currentMes;
				assertTrue(receptionMessage.getReceiverId() == (requestMessage.getReceiverId()));
				assertTrue(receptionMessage.getUsername().equals(requestMessage.getUsername()));
				assertTrue(receptionMessage.getMessage().equals(requestMessage.getMessage()));
			}
		}
	}

}
