package protobuf.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

import protobuf.ClientIdentities.AdminGroup;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadge;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDate;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientDeviceOs.ClientDeviceOsType;
import protobuf.wrapper.ClientIdentities;
import protobuf.ClientIdentities.ClientDeviceType;
import protobuf.ClientIdentities.ClientFriend;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroup;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;

class ClientIdentityTest {
	@Test
	public void clientBaseDefaultInstance_checkDefaultBaseValues() {
		ClientBase defaultBase = ClientBase.getDefaultInstance();
		assertEquals(defaultBase.getId(), 0);
		assertEquals(defaultBase.getUsername(), "");
	}
	
	@Test
	public void newClientBase_checkBaseValues() {
		ClientBase base = ClientIdentities.newClientBase(1, "Rozett");
		assertEquals(base.getId(), 1);
		assertEquals(base.getUsername(), "Rozett");
	}
	
	@Test
	public void clientDateDefaultInstance_checkDefaultDateValues() {
		ClientDate defaultDate = ClientDate.getDefaultInstance();
		assertEquals(defaultDate.getYear(), 0);
		assertEquals(defaultDate.getMonth(), 0);
		assertEquals(defaultDate.getDay(), 0);
		assertEquals(defaultDate.getHour(), 0);
		assertEquals(defaultDate.getMinutes(), 0);
		assertEquals(defaultDate.getSeconds(), 0);
	}
	
	@Test
	public void newClientDate_checkDateValues() {
		Calendar customDate = new GregorianCalendar(2020, 1, 28, 4, 20, 0); //Month = 1 -> February
		ClientDate clientDate = ClientIdentities.newClientDate(customDate.getTimeInMillis());
		assertEquals(clientDate.getYear(), 2020);
		assertEquals(clientDate.getMonth(), 2);
		assertEquals(clientDate.getDay(), 28);
		assertEquals(clientDate.getHour(), 4);
		assertEquals(clientDate.getMinutes(), 20);
		assertEquals(clientDate.getSeconds(), 0);
	}
	
	@Test
	public void clientDeviceDefaultInstance_checkDefaultDeviceValues() {
		ClientDevice defaultDevice = ClientDevice.getDefaultInstance();
		assertEquals(defaultDevice.getDeviceBase().getDeviceId(), 0);
		assertEquals(defaultDevice.getDeviceBase().getDeviceName(), "");
		assertEquals(defaultDevice.getDeviceType(), ClientDeviceType.UNKNOWN_DEVICE_TYPE);
		assertEquals(defaultDevice.getDeviceOs().getDeviceOsName(), "");
		assertEquals(defaultDevice.getDeviceOs().getDeviceOsVersion(), "");
		assertEquals(defaultDevice.getDeviceOs().getDeviceOsType(), ClientDeviceOsType.UNKNOWN_DEVICE_OS_TYPE);
		assertTrue(defaultDevice.getDeviceAddress().getDeviceIpV4List().isEmpty());
		assertTrue(defaultDevice.getDeviceAddress().getDeviceIpV6List().isEmpty());
	}
	
	@Test
	public void newClientDevice_checkDeviceValues() {
		ClientDevice device = ClientIdentities.newClientDevice(1, 
															"Gaming-PC", 
															ClientDeviceType.DESKTOP, 
															"Windows 10 Pro", 
															"9.12",
															ClientDeviceOsType.WINDOWS,
															"104.103.2.11");
		assertEquals(device.getDeviceBase().getDeviceId(), 1);
		assertEquals(device.getDeviceBase().getDeviceName(), "Gaming-PC");
		assertEquals(device.getDeviceType(), ClientDeviceType.DESKTOP);
		assertEquals(device.getDeviceOs().getDeviceOsName(), "Windows 10 Pro");
		assertEquals(device.getDeviceOs().getDeviceOsVersion(), "9.12");
		assertEquals(device.getDeviceOs().getDeviceOsType(), ClientDeviceOsType.WINDOWS);
		assertTrue(device.getDeviceAddress().getDeviceIpV4(0).equals("104.103.2.11"));
		assertTrue(device.getDeviceAddress().getDeviceIpV6List().isEmpty());
	}
	
	@Test
	public void clientProfileDefaultInstance_checkDefaultProfileValues() {
		ClientProfile defaultProfile = ClientProfile.getDefaultInstance();
		
		assertEquals(defaultProfile.getBase().getId(), 0);
		assertEquals(defaultProfile.getBase().getUsername(), "");
		assertEquals(defaultProfile.getStatus(), ClientStatus.UNKNOWN_STATUS);
		assertEquals(defaultProfile.getImages().getBackgroundImageURI(), "");
		assertEquals(defaultProfile.getImages().getProfileImageURI(), "");
		assertEquals(defaultProfile.getImages().getTitleImageURI(), "");
		assertTrue(defaultProfile.getBadges().getBadgeList().isEmpty());
		assertEquals(defaultProfile.getGroups().getAdminGroup(), AdminGroup.getDefaultInstance());
		assertTrue(defaultProfile.getGroups().getClientGroupList().isEmpty());
		assertEquals(defaultProfile.getDateOfRegistration(), ClientDate.getDefaultInstance());
		assertEquals(defaultProfile.getDateLastOnline(), ClientDate.getDefaultInstance());
	}
	
	@Test
	public void newClientProfile_checkProfileValues() {
		ClientImages images = ClientImages.newBuilder()	.setBackgroundImageURI("/data/accounts/Rozach/images/background.png")
														.setProfileImageURI("/data/accounts/Rozach/images/profileImage.png")
														.setTitleImageURI("/data/accounts/Rozach/images/titleImage.png")
														.build();
		ClientBadge badge1 = ClientBadge.newBuilder()	.setBadgeId(1)
														.setBadgeName("Badge 1")
														.setBadgeDescription("Description for Badge 1")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2017, 12, 10, 9, 15).getTimeInMillis()))
														.build();
		ClientBadge badge2 = ClientBadge.newBuilder()	.setBadgeId(2)
														.setBadgeName("Badge 2")
														.setBadgeDescription("Description for Badge 2")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2018, 11, 11, 8, 30).getTimeInMillis()))
														.build();
		ClientBadge badge3 = ClientBadge.newBuilder()	.setBadgeId(3)
														.setBadgeName("Badge 3")
														.setBadgeDescription("Description for Badge 3")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2019, 10, 12, 7, 45).getTimeInMillis()))
														.build();
		ClientBadges badges = ClientBadges.newBuilder().addBadge(badge1).addBadge(badge2).addBadge(badge3).build();
		
		ClientDate dateFriend1 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 3, 1, 12, 30).getTimeInMillis());
		ClientFriend friend1 = ClientFriend.newBuilder().setClientId(44).setMarkedAsBuddy(false).setDateFriendsSince(dateFriend1).build();
		ClientDate dateFriend2 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 4, 1, 12, 30).getTimeInMillis());
		ClientFriend friend2 = ClientFriend.newBuilder().setClientId(33).setMarkedAsBuddy(false).setDateFriendsSince(dateFriend2).build();
		ClientDate dateFriend3 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 5, 1, 12, 30).getTimeInMillis());
		ClientFriend friend3 = ClientFriend.newBuilder().setClientId(22).setMarkedAsBuddy(true).setDateFriendsSince(dateFriend3).build();
		ClientFriends friends = ClientFriends.newBuilder().addFriend(friend1).addFriend(friend2).addFriend(friend3).build();
		
		ClientDate dateClientGroup = ClientIdentities.newClientDate(new GregorianCalendar(2020, 1, 15, 20, 15).getTimeInMillis());
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(1).setGroupLevel(1).setGroupName("Junior").setDateMemberSince(dateClientGroup).build();
		ClientDate dateAdminGroup = ClientIdentities.newClientDate(new GregorianCalendar(2020, 1, 20, 14, 20).getTimeInMillis());
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(1).setGroupName("Moderator").setPermissionLevel(3).setDateMemberSince(dateAdminGroup).build();
		ClientGroups groups = ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build();
		
		ClientDate lastOnlineDate = ClientIdentities.newClientDate(new GregorianCalendar(2020, 3, 1, 12, 0).getTimeInMillis());
		ClientDate registrationDate = ClientIdentities.newClientDate(new GregorianCalendar(2019, 1, 1, 4, 20).getTimeInMillis());
		
		ClientProfile profile = ClientIdentities.newClientProfile(	11, 
																	"Rozach", 
																	ClientStatus.OFFLINE, 
																	images, 
																	badges, 
																	friends, 
																	groups, 
																	lastOnlineDate, 
																	registrationDate);
		assertEquals(profile.getBase().getId(), 11);
		assertEquals(profile.getBase().getUsername(), "Rozach");
		assertEquals(profile.getStatus(), ClientStatus.OFFLINE);
		assertEquals(profile.getImages().getBackgroundImageURI(), "/data/accounts/Rozach/images/background.png");
		assertEquals(profile.getImages().getProfileImageURI(), "/data/accounts/Rozach/images/profileImage.png");
		assertEquals(profile.getImages().getTitleImageURI(), "/data/accounts/Rozach/images/titleImage.png");
		assertEquals(profile.getBadges().getBadgeList().get(0), badge1);
		assertEquals(profile.getBadges().getBadgeList().get(1), badge2);
		assertEquals(profile.getBadges().getBadgeList().get(2), badge3);
		assertEquals(profile.getGroups().getAdminGroup(), adminGroup);
		assertEquals(profile.getGroups().getClientGroup(0), clientGroup);
		assertEquals(profile.getDateOfRegistration(), registrationDate);
		assertEquals(profile.getDateLastOnline(), lastOnlineDate);
	}
	
	@Test
	public void clientAccountDefaultInstance_checkDefaultAccountValues() {
		ClientAccount defaultAccount = ClientAccount.getDefaultInstance();
		assertEquals(defaultAccount.getProfile(), ClientProfile.getDefaultInstance());
		assertEquals(defaultAccount.getRegisteredDeviceCount(), 0);
		assertEquals(defaultAccount.getPassword(), "");
	}
	
	@Test
	public void newClientAccount_checkAccountValues() {
		ClientImages images = ClientImages.newBuilder()	.setBackgroundImageURI("/data/accounts/Rozach/images/background.png")
														.setProfileImageURI("/data/accounts/Rozach/images/profileImage.png")
														.setTitleImageURI("/data/accounts/Rozach/images/titleImage.png")
														.build();
		ClientBadge badge1 = ClientBadge.newBuilder()	.setBadgeId(1)
														.setBadgeName("Badge 1")
														.setBadgeDescription("Description for Badge 1")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2017, 12, 10, 9, 15).getTimeInMillis()))
														.build();
		ClientBadge badge2 = ClientBadge.newBuilder()	.setBadgeId(2)
														.setBadgeName("Badge 2")
														.setBadgeDescription("Description for Badge 2")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2018, 11, 11, 8, 30).getTimeInMillis()))
														.build();
		ClientBadge badge3 = ClientBadge.newBuilder()	.setBadgeId(3)
														.setBadgeName("Badge 3")
														.setBadgeDescription("Description for Badge 3")
														.setDateOfReception(ClientIdentities.newClientDate(new GregorianCalendar(2019, 10, 12, 7, 45).getTimeInMillis()))
														.build();
		ClientBadges badges = ClientBadges.newBuilder().addBadge(badge1).addBadge(badge2).addBadge(badge3).build();
		
		ClientDate dateFriend1 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 3, 1, 12, 30).getTimeInMillis());
		ClientFriend friend1 = ClientFriend.newBuilder().setClientId(44).setMarkedAsBuddy(false).setDateFriendsSince(dateFriend1).build();
		ClientDate dateFriend2 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 4, 1, 12, 30).getTimeInMillis());
		ClientFriend friend2 = ClientFriend.newBuilder().setClientId(33).setMarkedAsBuddy(false).setDateFriendsSince(dateFriend2).build();
		ClientDate dateFriend3 = ClientIdentities.newClientDate(new GregorianCalendar(2019, 5, 1, 12, 30).getTimeInMillis());
		ClientFriend friend3 = ClientFriend.newBuilder().setClientId(22).setMarkedAsBuddy(true).setDateFriendsSince(dateFriend3).build();
		ClientFriends friends = ClientFriends.newBuilder().addFriend(friend1).addFriend(friend2).addFriend(friend3).build();
		
		ClientDate dateClientGroup = ClientIdentities.newClientDate(new GregorianCalendar(2020, 1, 15, 20, 15).getTimeInMillis());
		ClientGroup clientGroup = ClientGroup.newBuilder().setGroupId(1).setGroupLevel(1).setGroupName("Junior").setDateMemberSince(dateClientGroup).build();
		ClientDate dateAdminGroup = ClientIdentities.newClientDate(new GregorianCalendar(2020, 1, 20, 14, 20).getTimeInMillis());
		AdminGroup adminGroup = AdminGroup.newBuilder().setGroupId(1).setGroupName("Moderator").setPermissionLevel(3).setDateMemberSince(dateAdminGroup).build();
		ClientGroups groups = ClientGroups.newBuilder().setAdminGroup(adminGroup).addClientGroup(clientGroup).build();
		
		ClientDate lastOnlineDate = ClientIdentities.newClientDate(new GregorianCalendar(2020, 3, 1, 12, 0).getTimeInMillis());
		ClientDate registrationDate = ClientIdentities.newClientDate(new GregorianCalendar(2019, 1, 1, 4, 20).getTimeInMillis());
		
		ClientProfile profile = ClientIdentities.newClientProfile(11, 
																"Rozach", 
																ClientStatus.OFFLINE, 
																images, 
																badges, 
																friends, 
																groups, 
																lastOnlineDate, 
																registrationDate);
		
		ClientDevice device = ClientIdentities.newClientDevice(	1, 
																"Gaming-PC", 
																ClientDeviceType.DESKTOP, 
																"Windows 10 Pro", 
																"9.12",
																ClientDeviceOsType.WINDOWS,
																"104.103.2.11");
		ClientAccount account = ClientIdentities.newClientAccount(profile, device, "rozachPassword");
		assertEquals(account.getProfile(), profile);
		assertEquals(account.getRegisteredDevice(0), device);
		assertEquals(account.getPassword(), "rozachPassword");
	}
}
