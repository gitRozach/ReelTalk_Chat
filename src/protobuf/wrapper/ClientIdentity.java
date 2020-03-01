package protobuf.wrapper;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientIdentities.ClientBadges;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDate;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientDeviceAddress;
import protobuf.ClientIdentities.ClientDeviceBase;
import protobuf.ClientIdentities.ClientDeviceOs;
import protobuf.ClientIdentities.ClientDeviceOs.ClientDeviceOsType;
import protobuf.ClientIdentities.ClientDeviceType;
import protobuf.ClientIdentities.ClientFriends;
import protobuf.ClientIdentities.ClientGroups;
import protobuf.ClientIdentities.ClientImages;
import protobuf.ClientIdentities.ClientProfile;
import protobuf.ClientIdentities.ClientStatus;

public class ClientIdentity {
	
	public static boolean isClientIdentity(GeneratedMessageV3 message) {
		if(message == null)
			return false;
		switch(message.getClass().getSimpleName()) {
		case "ClientBase":
		case "ClientProfile":
		case "ClientAccount":
		case "ClientStatus":
		case "ClientImages":
		case "AdminGroup":
		case "ClientGroup":
		case "ClientGroups":
		case "ClientBadge":
		case "ClientBadges":
		case "ClientFriend":
		case "ClientFriends":
		case "ClientDate":
		case "ClientDeviceBase":
		case "ClientDeviceAddress":
		case "ClientDeviceOs":
		case "ClientDeviceOsType":
		case "ClientDeviceType":
		case "ClientDevice":
			return true;
		default:
			return false;
		}
	}

	public static ClientBase newClientBase(int clientId, String clientUsername) {
		return ClientBase.newBuilder().setId(clientId).setUsername(clientUsername).build();
	}
	
	public static ClientDate newClientDate() {
		return newClientDate(System.currentTimeMillis());
	}
	
	public static ClientDate newClientDate(long timeMillis) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeMillis);
		return ClientDate.newBuilder()	.setYear(calendar.get(Calendar.YEAR))
										.setMonth(calendar.get(Calendar.MONTH) + 1)
										.setDay(calendar.get(Calendar.DAY_OF_MONTH))
										.setHour(calendar.get(Calendar.HOUR_OF_DAY))
										.setMinutes(calendar.get(Calendar.MINUTE))
										.setSeconds(calendar.get(Calendar.SECOND))
										.build();
	}
	
	public static ClientDevice newClientDevice(	int deviceId, 
												String deviceName,
												ClientDeviceType deviceType, 
												String deviceOsName,
												String deviceOsVersion,
												ClientDeviceOsType deviceOsType, 
												String ipv4) {
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(deviceId).setDeviceName(deviceName).build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(ipv4).build();
		ClientDeviceOs deviceOs = ClientDeviceOs.newBuilder()	.setDeviceOsName(deviceOsName)
																.setDeviceOsVersion(deviceOsVersion)
																.setDeviceOsType(deviceOsType)
																.build();
		return ClientDevice.newBuilder().setDeviceBase(deviceBase)
										.setDeviceAddress(deviceAddress)
										.setDeviceOs(deviceOs)
										.setDeviceType(deviceType)
										.build();
	}
	
	public static ClientProfile newClientProfile(	int clientId,
													String clientUsername,
													ClientStatus clientStatus,
													ClientImages clientImages,
													ClientBadges clientBadges,
													ClientFriends clientFriends,
													ClientGroups clientGroups,
													ClientDate lastOnlineDate,
													ClientDate registrationDate) {
		ClientBase clientBase = ClientBase.newBuilder().setId(clientId).setUsername(clientUsername).build();
		return ClientProfile.newBuilder()	.setBase(clientBase)
											.setStatus(clientStatus)
											.setImages(clientImages)
											.setBadges(clientBadges)
											.setFriends(clientFriends)
											.setGroups(clientGroups)
											.setDateLastOnline(lastOnlineDate)
											.setDateOfRegistration(registrationDate)
											.build();
	}
	
	public static ClientAccount newClientAccount(ClientProfile clientProfile, ClientDevice clientDevice, String clientPassword) {
		return ClientAccount.newBuilder()	.setProfile(clientProfile)
											.addRegisteredDevice(clientDevice)
											.setPassword(clientPassword)
											.build();
	}
}
