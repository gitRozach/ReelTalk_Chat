package network.ssl.server.protobufFileDatabase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.protobufDatabase.ClientAccountManager;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientPictures;

class ClientAccountManagerTest {
	protected ClientAccountManager database;
	
	protected static ClientAccount testAccount1;
	protected static ClientAccount testAccount2;
	protected static ClientAccount testAccount3;
	protected static ClientAccount testAccount4;
	protected static ClientAccount testAccount5;
	protected static ClientAccount testAccount6;
	protected static ClientAccount testAccount7;
	protected static ClientAccount testAccount8;
	protected static ClientAccount testAccount9;
	protected static ClientAccount testAccount10;
	
	@BeforeAll
	public static void init() {
		testAccount1 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("TestoRozach"))
		.setPassword("rozachPass")
		.setPictures(ClientPictures.getDefaultInstance())
		.setServerGroup(1)
		.setAdminGroup(5)
		.addFriend(8)
		.addFriend(17)
		.addFriend(28)
		.setDeleted(false)
		.setBanned(false)
		.build();
		
		testAccount2 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(3).setUsername("Felix"))
				.setPassword("superPass")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(0)
				.setAdminGroup(0)
				.addFriend(44)
				.addFriend(56)
				.addFriend(67)
				.setDeleted(true)
				.setBanned(false)
				.build();
		testAccount3 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(8).setUsername("Jenn"))
				.setPassword("dumbPass")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(5)
				.setAdminGroup(5)
				.addFriend(0)
				.addFriend(17)
				.addFriend(28)
				.setDeleted(false)
				.setBanned(true)
				.build();
		testAccount4 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_4"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount5 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_5"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount6 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_6"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount7 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_7"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount8 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_8"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount9 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_9"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
		testAccount10 = ClientAccount.newBuilder().setBase(ClientBase.newBuilder().setId(0).setUsername("Client_10"))
				.setPassword("password")
				.setPictures(ClientPictures.getDefaultInstance())
				.setServerGroup(3)
				.setAdminGroup(0)
				.addFriend(0)
				.addFriend(5)
				.addFriend(12)
				.setDeleted(false)
				.setBanned(false)
				.build();
	}
	
	@After
	public void tearDown() {
		if(database != null)
			database.close();
	}
	
	@Test
	void addItem_addsMultipleItemsToDatabaseFile() throws IOException {
		database = new ClientAccountManager("test/testresources/clientAccountManager/writeItemTest.txt");
		database.clear();
		
		assertTrue(database.addItem(testAccount1));
		assertTrue(database.addItem(testAccount2));
		assertTrue(database.addItem(testAccount3));
		assertTrue(database.addItem(testAccount4));
		assertTrue(database.addItem(testAccount5));
		assertTrue(database.addItem(testAccount6));
		assertTrue(database.addItem(testAccount7));
		assertTrue(database.addItem(testAccount8));
		assertTrue(database.addItem(testAccount9));
		assertTrue(database.addItem(testAccount10));
		assertTrue(database.getItems().indexOf(testAccount1) == 0);
		assertTrue(database.getItems().indexOf(testAccount2) == 1);
		assertTrue(database.getItems().indexOf(testAccount3) == 2);
		assertTrue(database.getItems().indexOf(testAccount4) == 3);
		assertTrue(database.getItems().indexOf(testAccount5) == 4);
		assertTrue(database.getItems().indexOf(testAccount6) == 5);
		assertTrue(database.getItems().indexOf(testAccount7) == 6);
		assertTrue(database.getItems().indexOf(testAccount8) == 7);
		assertTrue(database.getItems().indexOf(testAccount9) == 8);
		assertTrue(database.getItems().indexOf(testAccount10) == 9);
	}
	
	@Test
	public void readItems_readsAllItemsFromDatabaseFile() throws IOException {
		database = new ClientAccountManager("test/testresources/clientAccountManager/writeItemTest.txt");
		List<ClientAccount> accounts = database.readItems();
		assertTrue(accounts.size() == 10);
		assertTrue(accounts.get(0).equals(testAccount1));
		assertTrue(accounts.get(1).equals(testAccount2));
		assertTrue(accounts.get(2).equals(testAccount3));
		assertTrue(accounts.get(3).equals(testAccount4));
		assertTrue(accounts.get(4).equals(testAccount5));
		assertTrue(accounts.get(5).equals(testAccount6));
		assertTrue(accounts.get(6).equals(testAccount7));
		assertTrue(accounts.get(7).equals(testAccount8));
		assertTrue(accounts.get(8).equals(testAccount9));
		assertTrue(accounts.get(9).equals(testAccount10));
	}

}
