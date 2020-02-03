package network.ssl.serverclient;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import network.ssl.client.SecuredChatClient;
import network.ssl.communication.ByteMessage;
import network.ssl.communication.MessagePacket;
import network.ssl.communication.events.ClientLoggedInEvent;
import network.ssl.communication.requests.ClientLoginRequest;
import network.ssl.communication.requests.PrivateMessageRequest;
import network.ssl.server.SecuredChatServer;

class SecuredMessageServerClientTest {

	private static SecuredChatServer server;
	
	private static final String TEST_PROTOCOL = "TLSv1.2";
	private static final String TEST_HOST_ADDRESS = "localhost";
	private static final int TEST_HOST_PORT = 2197;
	
	@BeforeAll
	public static void setUp() throws Exception {
		server = new SecuredChatServer(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
		server.enableMessageHandler(false);
		server.start();
		Thread.sleep(50L);
	}
	
	@Test
	void sendMessage_serverSendsMultipleMessagesAndClientReceivesAllMessages() throws Exception {
		try(SecuredChatClient client1 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client2 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client3 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client4 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client5 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client1.connect();
			client1.enableMessageHandler(false);
			client2.connect();
			client2.enableMessageHandler(false);
			client3.connect();
			client3.enableMessageHandler(false);
			client4.connect();
			client4.enableMessageHandler(false);
			client5.connect();
			client5.enableMessageHandler(false);
			
			for(int i = 0; i < 100; ++i) {
				server.sendMessage(server.getLocalSocketChannel(client1.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client2.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client3.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client4.getChannel()), new ClientLoggedInEvent());
				server.sendMessage(server.getLocalSocketChannel(client5.getChannel()), new ClientLoggedInEvent());
			}
			for(int a = 0; a < 100; ++a) {
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client1.hasReadableBytes());
				MessagePacket reception1 = client1.readMessage();
				assertTrue(reception1 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client2.hasReadableBytes());
				MessagePacket reception2 = client2.readMessage();
				assertTrue(reception2 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client3.hasReadableBytes());
				MessagePacket reception3 = client3.readMessage();
				assertTrue(reception3 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client4.hasReadableBytes());
				MessagePacket reception4 = client4.readMessage();
				assertTrue(reception4 instanceof ClientLoggedInEvent);
				
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client5.hasReadableBytes());
				MessagePacket reception5 = client5.readMessage();
				assertTrue(reception5 instanceof ClientLoggedInEvent);
			}
		}
	}
	
	@Test
	void readMessage_clientRetrievesFirstMessage() throws Exception {
		ClientLoggedInEvent messageToReceive = new ClientLoggedInEvent();
		try(SecuredChatClient client = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.enableMessageHandler(false);
			
			Thread.sleep(250L);
			
			server.sendMessage(server.getLocalSocketChannel(client.getChannel()), messageToReceive);
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.hasReadableBytes());
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
		try(SecuredChatClient client = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			assertTrue(client.connect());
			client.sendMessage(req);
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceptionMessage());
			ByteMessage byteMessage = server.pollReceptionMessage();
			MessagePacket message = MessagePacket.deserialize(byteMessage.getMessageBytes());
			assertTrue(message instanceof ClientLoginRequest);
		}
	}
	
	@Test
	void clientLogin_invalidLoginTest() throws Exception {
		ClientLoginRequest req = new ClientLoginRequest("Does_not_exist", "wrong_anyway");
		try(SecuredChatClient client = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			client.sendMessage(req);
			
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> server.hasReceptionMessage());
			ByteMessage byteMessage = server.pollReceptionMessage();
			MessagePacket message = MessagePacket.deserialize(byteMessage.getMessageBytes());
			assertTrue(message instanceof ClientLoginRequest);
		}
	}
	
	@Test
	void sendMessage_tenClientsSendMultipleMessagesAndServerReceivesAllMessages() throws Exception {
		PrivateMessageRequest requestMessage = new PrivateMessageRequest("rozach", "testpass", 12, "Hallo Client mit ID 12!");
		try(SecuredChatClient client1 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client2 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client3 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client4 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client5 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client6 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client7 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client8 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client9 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredChatClient client10 = new SecuredChatClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
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
				Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> server.hasReceptionMessage());
				MessagePacket currentMes = MessagePacket.deserialize(server.pollReceptionMessage().getMessageBytes());
				PrivateMessageRequest receptionMessage = (PrivateMessageRequest)currentMes;
				assertTrue(receptionMessage.getReceiverId() == (requestMessage.getReceiverId()));
				assertTrue(receptionMessage.getUsername().equals(requestMessage.getUsername()));
				assertTrue(receptionMessage.getMessage().equals(requestMessage.getMessage()));
			}
		}
	}

}
