package network.ssl.server.manager;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.protobufDatabase.ClientAccountManager;
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
import protobuf.wrapper.ClientIdentity;

class ClientAccountManagerTest {
	protected ClientAccountManager database;
	
	protected static ClientAccount testAccount1;
	protected static ClientAccount testAccount2;
	protected static ClientAccount testAccount3;
	protected static ClientAccount testAccount4;
	protected static ClientAccount testAccount5;
	
	@BeforeAll
	public static void init() {
		ClientImages images = ClientImages.newBuilder().setProfileImageURI("/accounts/TestoRozach/pictures/profileImage.png").build();
		
		ClientBadge badge1 = ClientBadge.newBuilder().setBadgeId(1).setBadgeName("Badge 1").setBadgeDescription("Badge Description 1").build();
		ClientBadge badge2 = ClientBadge.newBuilder().setBadgeId(2).setBadgeName("Badge 2").setBadgeDescription("Badge Description 2").build();
		ClientBadge badge3 = ClientBadge.newBuilder().setBadgeId(3).setBadgeName("Badge 3").setBadgeDescription("Badge Description 3").build();
		ClientBadges badges = ClientBadges.newBuilder().addBadge(badge1).addBadge(badge2).addBadge(badge3).build();
		
		ClientFriend friend1 = ClientFriend.newBuilder().setClientId(10).setMarkedAsBuddy(true).setDateFriendsSince(ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 22, 30, 0).getTimeInMillis())).build();
		ClientFriend friend2 = ClientFriend.newBuilder().setClientId(11).setMarkedAsBuddy(false).setDateFriendsSince(ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 21, 30, 0).getTimeInMillis())).build();
		ClientFriend friend3 = ClientFriend.newBuilder().setClientId(12).setMarkedAsBuddy(false).setDateFriendsSince(ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 20, 30, 0).getTimeInMillis())).build();
		ClientFriends friends = ClientFriends.newBuilder().addFriend(friend1).addFriend(friend2).addFriend(friend3).build();
		
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(1).setGroupName("Moderator").setPermissionLevel(1).setDateMemberSince(ClientIdentity.newClientDate(new GregorianCalendar(2020, 1, 1).getTimeInMillis())).build();
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(1).setGroupName("Junior").setGroupLevel(2).setDateMemberSince(ClientIdentity.newClientDate(new GregorianCalendar(2020, 0, 30).getTimeInMillis())).build();
		ClientGroups groups = ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build();
		
		ClientProfile profile1 = ClientIdentity.newClientProfile(	1, 
																	"Rozach", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount1 = ClientIdentity.newClientAccount(profile1, ClientDevice.getDefaultInstance(), "rozachPass");
		
		ClientProfile profile2 = ClientIdentity.newClientProfile(	1, 
																	"Jenn", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount2 = ClientIdentity.newClientAccount(profile2, ClientDevice.getDefaultInstance(), "jennPass");

		ClientProfile profile3 = ClientIdentity.newClientProfile(	1, 
																	"Husan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount3 = ClientIdentity.newClientAccount(profile3, ClientDevice.getDefaultInstance(), "husanPass");
		
		ClientProfile profile4 = ClientIdentity.newClientProfile(	1, 
																	"Hendrizio", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount4 = ClientIdentity.newClientAccount(profile4, ClientDevice.getDefaultInstance(), "hendrizioPass");

		ClientProfile profile5 = ClientIdentity.newClientProfile(	1, 
																	"Farkan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups,  
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount5 = ClientIdentity.newClientAccount(profile5, ClientDevice.getDefaultInstance(), "farkanPass");
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

		assertTrue(database.getItems().indexOf(testAccount1) == 0);
		assertTrue(database.getItems().indexOf(testAccount2) == 1);
		assertTrue(database.getItems().indexOf(testAccount3) == 2);
		assertTrue(database.getItems().indexOf(testAccount4) == 3);
		assertTrue(database.getItems().indexOf(testAccount5) == 4);
	}
	
	@Test
	public void readItems_readsAllItemsFromDatabaseFile() throws IOException {
		database = new ClientAccountManager("test/testresources/clientAccountManager/writeItemTest.txt");
		List<ClientAccount> accounts = database.readItems();
		assertTrue(accounts.size() == 5);
		assertTrue(accounts.get(0).equals(testAccount1));
		assertTrue(accounts.get(1).equals(testAccount2));
		assertTrue(accounts.get(2).equals(testAccount3));
		assertTrue(accounts.get(3).equals(testAccount4));
		assertTrue(accounts.get(4).equals(testAccount5));
	}

}
