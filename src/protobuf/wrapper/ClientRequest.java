package protobuf.wrapper;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientDeviceAddress;
import protobuf.ClientIdentities.ClientDeviceBase;
import protobuf.ClientIdentities.ClientDeviceOs;
import protobuf.ClientIdentities.ClientDeviceOs.ClientDeviceOsType;
import protobuf.ClientIdentities.ClientDeviceType;
import protobuf.ClientMessages.ClientMessageBase;
import protobuf.ClientRequests.ChannelFileDownloadRequest;
import protobuf.ClientRequests.ChannelFileUploadRequest;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageAnswerRequest;
import protobuf.ClientRequests.ChannelMessageRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientPingMeasurementRequest;
import protobuf.ClientRequests.ClientProfileRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.ClientRequestBase;
import protobuf.ClientRequests.FileDownloadBase;
import protobuf.ClientRequests.FileUploadBase;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageRequest;
import utils.system.SystemUtils;

public class ClientRequest {
	
	public static boolean isClientRequest(GeneratedMessageV3 message) {
		if(message == null)
			return false;
		switch(message.getClass().getSimpleName()) {
		case "ChannelJoinRequest":
		case "ChannelLeaveRequest":
		case "ChannelMessageRequest":
		case "ChannelMessageAnswerRequest":
		case "PrivateMessageRequest":
		case "ClientProfileRequest":
		case "ClientLoginRequest":
		case "ClientLogoutRequest":
		case "ClientRegistrationRequest":
		case "ChannelFileUploadRequest":
		case "ChannelFileDownloadRequest":
		case "PrivateFileUploadRequest":
		case "PrivateFileDownloadRequest":
		case "ClientPingMeasurementRequest":
			return true;
		default:
			return false;
		}
	}
	
	public static ClientRequestBase newClientRequestBase(	int requestId,
															int requestorId,
															String requestorUsername,
															String requestorPassword) {
		return ClientRequestBase.newBuilder()	.setRequestId(requestId)
												.setRequestorClientId(requestorId)
												.setRequestorClientUsername(requestorUsername)
												.setRequestorClientPassword(requestorPassword)
												.setTimestampMillis(System.currentTimeMillis())
												.build();
	}
	
	public static ChannelJoinRequest newChannelJoinRequest(	int requestId, 
															int requestorId, 
															String requestorUsername, 
															String requestorPassword, 
															int requestedChannelId,
															String requestedChannelName) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).setChannelName(requestedChannelName).build();
		return ChannelJoinRequest.newBuilder()	.setRequestBase(requestorBase).setRequestedChannelBase(channelBase).build();
	}
	
	public static ChannelLeaveRequest newChannelLeaveRequest(	int requestId, 
																int requestorId, 
																String requestorUsername, 
																String requestorPassword, 
																int requestedChannelId,
																String requestedChannelName) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).setChannelName(requestedChannelName).build();
		return ChannelLeaveRequest.newBuilder()	.setRequestBase(requestorBase).setRequestedChannelBase(channelBase).build();
	}
	
	public static ClientProfileRequest newProfileRequest(	int requestId, 
															int requestorId, 
															String requestorUsername, 
															String requestorPassword,
															int requestedId,
															String requestedUsername) {

		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ClientBase clientBase = ClientBase.newBuilder().setId(requestedId).setUsername(requestedUsername).build();
		return ClientProfileRequest.newBuilder().setRequestBase(requestorBase).setRequestedClientBase(clientBase).build();
	}
	
	public static ChannelMessageRequest newChannelMessageRequest(	int requestId, 
																	int requestorId, 
																	String requestorUsername, 
																	String requestorPassword,
																	int requestedChannelId,
																	String requestedChannelName,
																	int startFromMessageId,
																	int messageCount) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).setChannelName(requestedChannelName).build();
		return ChannelMessageRequest.newBuilder()	.setRequestBase(requestorBase)
													.setRequestedChannelBase(channelBase)
													.setStartMessageId(startFromMessageId)
													.setMessageCount(messageCount)
													.build();
	}
	
	public static ChannelMessageAnswerRequest newChannelMessageAnswerRequest(	int requestId, 
																				int requestorId, 
																				String requestorUsername, 
																				String requestorPassword,
																				int requestedChannelId,
																				String requestedChannelName,
																				int requestedMessageId,
																				String answerText) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).setChannelName(requestedChannelName).build();
		ClientMessageBase messageBase = ClientMessageBase.newBuilder().setMessageId(requestedMessageId).build();
		return ChannelMessageAnswerRequest.newBuilder()	.setRequestBase(requestorBase)
														.setRequestedChannelBase(channelBase)
														.setRequestedMessageBase(messageBase)
														.setAnswerText(answerText)
														.build();
	}
	
	public static PrivateMessageRequest newPrivateMessageRequest(	int requestId, 
																	int requestorId, 
																	String requestorUsername, 
																	String requestorPassword,
																	int requestedClientId,
																	String requestedClientUsername,
																	String requestedMessageText) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).setUsername(requestedClientUsername).build();
		return PrivateMessageRequest.newBuilder()	.setRequestBase(requestorBase)
													.setReceiverClientBase(requestedBase)
													.setMessageText(requestedMessageText)
													.build();
	}
	
	public static ClientLoginRequest newLoginRequest(	int requestId,
														String username,
														String password) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setRequestorClientUsername(username)
																		.setRequestorClientPassword(password)
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getLocalInetAddress().getHostAddress())
																			.build();
		System.out.println(SystemUtils.getLocalInetAddress().getHostAddress());
		ClientDeviceOs deviceOs = ClientDeviceOs.newBuilder()	.setDeviceOsName(SystemUtils.getOsName())
																.setDeviceOsType(ClientDeviceOsType.WINDOWS)
																.setDeviceOsVersion(SystemUtils.getOsVersion())
																.build();
		ClientDeviceType deviceType = ClientDeviceType.DESKTOP;
		ClientDevice requestorDevice = ClientDevice.newBuilder()	.setDeviceBase(deviceBase)
														.setDeviceAddress(deviceAddress)
														.setDeviceOs(deviceOs)
														.setDeviceType(deviceType)
														.build();
		return ClientLoginRequest.newBuilder().setRequestBase(requestorBase).setRequestedDevice(requestorDevice).build();
	}
	
	public static ClientLogoutRequest newLogoutRequest(	int requestId,
														String username,
														String password) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setRequestorClientUsername(username)
																		.setRequestorClientPassword(password)
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getLocalInetAddress().getHostAddress())
																			.build();
		System.out.println(SystemUtils.getLocalInetAddress().getHostAddress());
		ClientDeviceOs deviceOs = ClientDeviceOs.newBuilder()	.setDeviceOsName(SystemUtils.getOsName())
																.setDeviceOsType(ClientDeviceOsType.WINDOWS)
																.setDeviceOsVersion(SystemUtils.getOsVersion())
																.build();
		ClientDeviceType deviceType = ClientDeviceType.DESKTOP;
		ClientDevice requestorDevice = ClientDevice.newBuilder()	.setDeviceBase(deviceBase)
														.setDeviceAddress(deviceAddress)
														.setDeviceOs(deviceOs)
														.setDeviceType(deviceType)
														.build();
		return ClientLogoutRequest.newBuilder().setRequestBase(requestorBase).setRequestedDevice(requestorDevice).build();
}
	
	public static ClientRegistrationRequest newRegistrationRequest(	int requestId,
																	String username,
																	String password,
																	String passwordRepeat,
																	String email) {
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getLocalInetAddress().getHostAddress())
																			.build();
		System.out.println(SystemUtils.getLocalInetAddress().getHostAddress());
		ClientDeviceOs deviceOs = ClientDeviceOs.newBuilder()	.setDeviceOsName(SystemUtils.getOsName())
																.setDeviceOsType(ClientDeviceOsType.WINDOWS)
																.setDeviceOsVersion(SystemUtils.getOsVersion())
																.build();
		ClientDeviceType deviceType = ClientDeviceType.DESKTOP;
		ClientDevice device = ClientDevice.newBuilder()	.setDeviceBase(deviceBase)
														.setDeviceAddress(deviceAddress)
														.setDeviceOs(deviceOs)
														.setDeviceType(deviceType)
														.build();
		return ClientRegistrationRequest.newBuilder()	.setUsername(username)
														.setPassword(password)
														.setPasswordRepeat(passwordRepeat)
														.setEmail(email)
														.setDevice(device)
														.build();
	}
	
	public static ChannelFileUploadRequest newChannelFileUploadRequest(	int requestId,
																		String requestorUsername,
																		String requestorPassword,
																		int requestedChannelId,
																		String requestedFileName,
																		String requestedFilePath,
																		long requestedFileSize
																		) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setRequestId(requestId)
																		.setRequestorClientUsername(requestorUsername)
																		.setRequestorClientPassword(requestorPassword)
																		.build();
		ChannelBase channelBase = ChannelBase.newBuilder()	.setChannelId(requestedChannelId)
															.setChannelName("")
															.build();
		FileUploadBase fileBase = FileUploadBase.newBuilder()	.setUploadFileName(requestedFileName)
																.setUploadFilePath(requestedFilePath)
																.setUploadFileSize(requestedFileSize)
																.build();
		return ChannelFileUploadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setRequestedChannelBase(channelBase)
														.setRequestedFileBase(fileBase)
														.build();
	}
	
	public static ChannelFileDownloadRequest newChannelFileDownloadRequest(	int requestId,
																			String requestorUsername,
																			String requestorPassword,
																			int requestedChannelId,
																			int requestedFileId,
																			String requestedFileName) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setRequestId(requestId)
																		.setRequestorClientUsername(requestorUsername)
																		.setRequestorClientPassword(requestorPassword)
																		.build();
		ChannelBase channelBase = ChannelBase.newBuilder()	.setChannelId(requestedChannelId)
															.setChannelName("")
															.build();
		FileDownloadBase fileBase = FileDownloadBase.newBuilder()	.setDownloadFileId(requestedFileId)
																	.setDownloadFileName(requestedFileName)
																	.setDownloadFilePath("")
																	.build();
		return ChannelFileDownloadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setRequestedChannelBase(channelBase)
														.setRequestedFileBase(fileBase)
														.build();
	}
	
	public static PrivateFileUploadRequest newPrivateFileUploadRequest(	int requestId,
																		String requestorUsername,
																		String requestorPassword,
																		int requestedClientId,
																		String requestedClientUsername,
																		String requestedFileName,
																		String requestedFilePath,
																		long requestedFileSize) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
				.setRequestId(requestId)
				.setRequestorClientUsername(requestorUsername)
				.setRequestorClientPassword(requestorPassword)
				.build();
		ClientBase requestedBase = ClientBase.newBuilder()	.setId(requestedClientId)
															.setUsername(requestedClientUsername)
															.build();
		FileUploadBase fileBase = FileUploadBase.newBuilder()	.setUploadFileName(requestedFileName)
																.setUploadFilePath(requestedFilePath)
																.setUploadFileSize(requestedFileSize)
																.build();
		return PrivateFileUploadRequest.newBuilder().setRequestBase(requestorBase)
													.setRequestedClientBase(requestedBase)
													.setRequestedFileBase(fileBase)
													.build();
			
	}
	
	public static PrivateFileDownloadRequest newPrivateFileDownloadRequest(	int requestId,
																			String requestorUsername,
																			String requestorPassword,
																			int requestedClientId,
																			String requestedClientUsername,
																			int requestedFileId,
																			String requestedFileName) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
				.setRequestId(requestId)
				.setRequestorClientUsername(requestorUsername)
				.setRequestorClientPassword(requestorPassword)
				.build();
		ClientBase requestedBase = ClientBase.newBuilder()	.setId(requestedClientId)
															.setUsername(requestedClientUsername)
															.build();
		FileDownloadBase fileBase = FileDownloadBase.newBuilder()	.setDownloadFileId(requestedFileId)
																	.setDownloadFileName(requestedFileName)
																	.setDownloadFilePath("")
																	.setDownloadKey("")
																	.build();
		return PrivateFileDownloadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setRequestedClientBase(requestedBase)
														.setRequestedFileBase(fileBase)
														.build();
	}
	
	public static ClientPingMeasurementRequest newPingmeasurementRequest() {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(0)
																		.setRequestorClientId(0)
																		.setRequestorClientUsername("")
																		.setRequestorClientPassword("")
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder()	.setDeviceId(0)
																	.setDeviceName(SystemUtils.getUserName())
																	.build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getLocalInetAddress().getHostAddress())
																			.build();
		System.out.println(SystemUtils.getLocalInetAddress().getHostAddress());
		ClientDeviceOs deviceOs = ClientDeviceOs.newBuilder()	.setDeviceOsName(SystemUtils.getOsName())
																.setDeviceOsType(ClientDeviceOsType.WINDOWS)
																.setDeviceOsVersion(SystemUtils.getOsVersion())
																.build();
		ClientDeviceType deviceType = ClientDeviceType.DESKTOP;
		ClientDevice device = ClientDevice.newBuilder()	.setDeviceBase(deviceBase)
														.setDeviceAddress(deviceAddress)
														.setDeviceOs(deviceOs)
														.setDeviceType(deviceType)
														.build();
		return ClientPingMeasurementRequest.newBuilder().setRequestorBase(requestorBase)
														.setRequestedDevice(device)
														.build();
	}
}
