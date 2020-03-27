package network.ssl.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.peer.server.database.protobuf.ClientAccountDatabase;
import protobuf.ClientIdentities.AdminGroup;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadge;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientFriend;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroup;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;
import protobuf.wrapper.ClientIdentities;

class ClientAccountManagerTest {
	protected ClientAccountDatabase database;
	
	protected static ClientAccount testAccount1;
	protected static ClientAccount testAccount2;
	protected static ClientAccount testAccount3;
	protected static ClientAccount testAccount4;
	protected static ClientAccount testAccount5;
	
	@After
	public void tearDown() {
		if(database != null)
			database.close();
	}
	
	@Test
	void addItem_addsMultipleItemsToDatabaseFile() throws IOException {
		database = new ClientAccountDatabase();
		database.loadFileItems("test/testresources/clientAccountManager/writeItemTest.txt");
		database.clear();
		
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
		
		ClientProfile profile1 = ClientIdentities.newClientProfile(	database.generateUniqueBaseId(), 
																	"Rozach", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount1 = ClientIdentities.newClientAccount(profile1, ClientDevice.getDefaultInstance(), "rozachPass");
		assertTrue(database.addItem(testAccount1));
		
		ClientProfile profile2 = ClientIdentities.newClientProfile(	database.generateUniqueBaseId(1), 
																	"Jenn", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount2 = ClientIdentities.newClientAccount(profile2, ClientDevice.getDefaultInstance(), "jennPass");
		assertTrue(database.addItem(testAccount2));
		
		ClientProfile profile3 = ClientIdentities.newClientProfile(	database.generateUniqueBaseId(1, 10), 
																	"Husan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount3 = ClientIdentities.newClientAccount(profile3, ClientDevice.getDefaultInstance(), "husanPass");
		assertTrue(database.addItem(testAccount3));
		
		ClientProfile profile4 = ClientIdentities.newClientProfile(	database.generateUniqueBaseId(1), 
																	"Hendrizio", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount4 = ClientIdentities.newClientAccount(profile4, ClientDevice.getDefaultInstance(), "hendrizioPass");
		assertTrue(database.addItem(testAccount4));
		
		ClientProfile profile5 = ClientIdentities.newClientProfile(	database.generateUniqueBaseId(), 
																	"Farkan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups,  
																	ClientIdentities.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentities.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount5 = ClientIdentities.newClientAccount(profile5, ClientDevice.getDefaultInstance(), "farkanPass");
		assertTrue(database.addItem(testAccount5));
		
		assertTrue(testAccount1.getProfile().getBase().getId() == 1);
		assertTrue(testAccount2.getProfile().getBase().getId() == 2);
		assertTrue(testAccount3.getProfile().getBase().getId() == 3);
		assertTrue(testAccount4.getProfile().getBase().getId() == 4);
		assertTrue(testAccount5.getProfile().getBase().getId() == 5);
		
		assertTrue(database.getLoadedItems().indexOf(testAccount1) == 0);
		assertTrue(database.getLoadedItems().indexOf(testAccount2) == 1);
		assertTrue(database.getLoadedItems().indexOf(testAccount3) == 2);
		assertTrue(database.getLoadedItems().indexOf(testAccount4) == 3);
		assertTrue(database.getLoadedItems().indexOf(testAccount5) == 4);
	}
	
	@Test
	public void readItems_readsAllItemsFromDatabaseFile() throws IOException {
		database = new ClientAccountDatabase();
		assertEquals(database.loadFileItems("test/testresources/clientAccountManager/writeItemTest.txt"), 5);
		
		List<ClientAccount> accounts = database.readItems();
		assertTrue(accounts.get(0).equals(testAccount1));
		assertTrue(accounts.get(1).equals(testAccount2));
		assertTrue(accounts.get(2).equals(testAccount3));
		assertTrue(accounts.get(3).equals(testAccount4));
		assertTrue(accounts.get(4).equals(testAccount5));
	}

}
