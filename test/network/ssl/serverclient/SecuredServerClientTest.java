package network.ssl.serverclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import network.ssl.client.SecuredClient;
import network.ssl.server.SecuredServer;

class SecuredServerClientTest {

	private static final String TEST_PROTOCOL = "TLSv1.2";
	private static final String TEST_HOST_ADDRESS = "localhost";
	private static final int TEST_HOST_PORT = 2197;
	private static SecuredServer testHost;
	
	@BeforeAll
	public static void setUp() throws Exception {
		testHost = new SecuredServer(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
		testHost.start();
		Thread.sleep(50L);
	}
	
	@Test 
	void sendBytes_serverSendsStringAndClientReceivesSameString() throws Exception{
		String message = "Hallo Client!";
		try(SecuredClient c1  = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);){
			c1.setBufferingReceivedBytes(true);
			c1.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> c1.isConnected());
			
			testHost.sendBytes(testHost.getLocalSocketChannel(c1.getChannel()), message.getBytes(StandardCharsets.UTF_8));
				
			Awaitility.await().atMost(Duration.ofSeconds(3L)).until(() -> c1.hasReceivableBytes());
			assertTrue(message.equals(new String(c1.pollReceptionBytes().getMessageBytes(), StandardCharsets.UTF_8)));
		}
	}
	
	@Test
	void connect_clientIsConnected() throws Exception {
		try(SecuredClient client = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			assertTrue(!client.isConnected());
			client.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.isConnected());
			client.disconnect();
		}
	}
	
	@Test 
	void disconnect_clientIsNotConnected() throws Exception {
		try(SecuredClient client = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.isConnected());
			client.disconnect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> !client.isConnected());
		}
	}
	
	@Test 
	void connect_clientGetsKickedNotConnectedAnymore() throws Exception {		
		try(SecuredClient client = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.isConnected());
			testHost.kick(client.getChannel());
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> !client.isConnected());
		}
	}
	
	@Test
	void sendBytes_clientSendsMultipleStringsAndServerReceivesAllStrings() throws Exception {
		testHost.setBufferingReceivedBytes(true);
		String testMessage = new String("HeLlo_SerVER! This iS a tEsT.");
		try(SecuredClient client = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client.isConnected());
			
			for(int a = 0; a < 200; ++a)
				client.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
			for(int b = 0; b < 200; ++b) {
				Awaitility.await().atMost(Duration.ofSeconds(10L)).until(() -> testHost.hasReceivableBytes());
				System.out.println("Received No. " + (b+1));
				assertEquals(new String(testHost.pollReceptionBytes().getMessageBytes(), StandardCharsets.UTF_8), testMessage);
			}
		}
	}
	
	@Test
	void sendBytes_twoClientsSendMultipleStringsAndServerReceivesAllStrings() throws Exception {
		testHost.setBufferingReceivedBytes(true);
		String testMessage = new String("HeLlo_SerVER! This iS a tEsT.");
		try(SecuredClient client1 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client2 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
			client1.connect();
			client2.connect();
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client2.isConnected());
			
			for(int a = 0; a < 100; ++a) {
				client1.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client2.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
			}
			for(int b = 0; b < 100; ++b) {
				Awaitility.await().atMost(Duration.ofSeconds(10L)).until(() -> testHost.hasReceivableBytes());
				System.out.println("Received No. " + (b+1));
				assertEquals(new String(testHost.pollReceptionBytes().getMessageBytes(), StandardCharsets.UTF_8), testMessage);
			}
		}
	}
	
	@Test
	void sendBytes_tenClientsSendMultipleStringsAndServerReceivesAllStrings() throws Exception {
		testHost.setBufferingReceivedBytes(true);
		String testMessage = new String("HeLlo_SerVER! This iS a tEsT.");
		try(SecuredClient client1 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client2 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client3 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client4 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client5 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client6 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client7 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client8 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client9 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client10 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
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
			Awaitility.await().atMost(Duration.ofSeconds(5L)).until(() -> client10.isConnected());
			
			for(int a = 0; a < 10; ++a) {
				client1.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client2.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client3.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client4.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client5.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client6.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client7.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client8.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client9.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client10.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
			}
			for(int b = 0; b < 100; ++b) {
				Awaitility.await().atMost(Duration.ofSeconds(10L)).until(() -> testHost.hasReceivableBytes());
				System.out.println("Received No. " + (b+1));
				assertEquals(new String(testHost.pollReceptionBytes().getMessageBytes(), StandardCharsets.UTF_8), testMessage);
			}
		}
	}
	
	@Test
	void sendBytes_thirtyClientsSendMultipleStringsAndServerReceivesAllStrings() throws Exception {
		testHost.setBufferingReceivedBytes(true);
		String testMessage = new String("HeLlo_SerVER! This iS a tEsT.");
		try(SecuredClient client1 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client2 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client3 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client4 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client5 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client6 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client7 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client8 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client9 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client10 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client11 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client12 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client13 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client14 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client15 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client16 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client17 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client18 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client19 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client20 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client21 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client22 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client23 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client24 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client25 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client26 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client27 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client28 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client29 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT);
			SecuredClient client30 = new SecuredClient(TEST_PROTOCOL, TEST_HOST_ADDRESS, TEST_HOST_PORT)) {
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
			client11.connect();
			client12.connect();
			client13.connect();
			client14.connect();
			client15.connect();
			client16.connect();
			client17.connect();
			client18.connect();
			client19.connect();
			client20.connect();
			client21.connect();
			client22.connect();
			client23.connect();
			client24.connect();
			client25.connect();
			client26.connect();
			client27.connect();
			client28.connect();
			client29.connect();
			client30.connect();
			Awaitility.await().atMost(Duration.ofSeconds(10L)).until(() -> client30.isConnected());
			
			for(int a = 0; a < 10; ++a) {
				client1.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client2.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client3.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client4.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client5.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client6.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client7.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client8.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client9.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client10.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client11.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client12.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client13.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client14.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client15.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client16.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client17.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client18.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client19.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client20.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client21.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client22.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client23.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client24.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client25.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client26.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client27.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client28.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client29.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
				client30.sendBytes(testMessage.getBytes(StandardCharsets.UTF_8));
			}
			for(int b = 0; b < 300; ++b) {
				Awaitility.await().atMost(Duration.ofSeconds(10L)).until(() -> testHost.hasReceivableBytes());
				System.out.println("Received No. " + (b+1));
				assertEquals(new String(testHost.pollReceptionBytes().getMessageBytes(), StandardCharsets.UTF_8), testMessage);
			}
		}
	}
	
}
