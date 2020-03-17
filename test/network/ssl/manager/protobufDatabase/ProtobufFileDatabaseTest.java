package network.ssl.manager.protobufDatabase;

import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.peer.server.database.protobuf.ProtobufFileDatabase;
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
import protobuf.wrapper.java.ClientIdentity;

class ProtobufFileDatabaseTest {
	protected ProtobufFileDatabase<ClientAccount> database;
	protected static ClientAccount testAccount1;
	protected static ClientAccount testAccount2;
	protected static ClientAccount testAccount3;
	protected static ClientAccount testAccount4;
	protected static ClientAccount testAccount5;
	
	@After
	public void tearDown() {
		if(database != null && !database.isClosed())
			database.close();
	}
	
	@Test
	void generateUniqueId_addItemsAndCheckIdValues() throws Exception {
		database = new ProtobufFileDatabase<ClientAccount>(ClientAccount.class);
		database.loadFileItems("test/testresources/protobufFileDatabase/generateId.txt");
		database.clear();
		
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
		assertTrue(database.addItem(testAccount1));
		
		ClientProfile profile2 = ClientIdentity.newClientProfile(	2, 
																	"Jenn", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount2 = ClientIdentity.newClientAccount(profile2, ClientDevice.getDefaultInstance(), "jennPass");
		assertTrue(database.addItem(testAccount2));
		
		ClientProfile profile3 = ClientIdentity.newClientProfile(	3, 
																	"Husan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount3 = ClientIdentity.newClientAccount(profile3, ClientDevice.getDefaultInstance(), "husanPass");
		assertTrue(database.addItem(testAccount3));
		
		ClientProfile profile4 = ClientIdentity.newClientProfile(	4, 
																	"Hendrizio", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount4 = ClientIdentity.newClientAccount(profile4, ClientDevice.getDefaultInstance(), "hendrizioPass");
		assertTrue(database.addItem(testAccount4));
		
		ClientProfile profile5 = ClientIdentity.newClientProfile(	5, 
																	"Farkan", 
																	ClientStatus.ONLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups,  
																	ClientIdentity.newClientDate(new GregorianCalendar(2020, 2, 2, 19, 34, 32).getTimeInMillis()), 
																	ClientIdentity.newClientDate(new GregorianCalendar(2019, 11, 12, 18, 25, 10).getTimeInMillis()));
		testAccount5 = ClientIdentity.newClientAccount(profile5, ClientDevice.getDefaultInstance(), "farkanPass");
		assertTrue(database.addItem(testAccount5));
		
		assertTrue(testAccount1.getProfile().getBase().getId() == 1);
		assertTrue(testAccount2.getProfile().getBase().getId() == 2);
		assertTrue(testAccount3.getProfile().getBase().getId() == 3);
		assertTrue(testAccount4.getProfile().getBase().getId() == 4);
		assertTrue(testAccount5.getProfile().getBase().getId() == 5);
	}

}
