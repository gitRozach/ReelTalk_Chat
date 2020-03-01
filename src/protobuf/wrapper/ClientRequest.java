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
import protobuf.ClientRequests.ChannelMessageAnswerGetRequest;
import protobuf.ClientRequests.ChannelMessageAnswerPostRequest;
import protobuf.ClientRequests.ChannelMessageGetRequest;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientProfileGetRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.ClientRequestBase;
import protobuf.ClientRequests.FileDownloadBase;
import protobuf.ClientRequests.FileDownloadVerification;
import protobuf.ClientRequests.FileUploadBase;
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;
import utils.system.SystemUtils;

public class ClientRequest {
	
	public static boolean isClientRequest(GeneratedMessageV3 message) {
		if(message == null)
			return false;
		switch(message.getClass().getSimpleName()) {
		case "ClientRequestBase":
		case "FileDownloadVerification":
		case "FileDownloadBase":
		case "FileUploadBase":
		case "ChannelJoinRequest":
		case "ChannelLeaveRequest":
		case "ClientProfileGetRequest":
		case "ChannelMessageGetRequest":
		case "ChannelMessagePostRequest":
		case "ChannelMessageAnswerGetRequest":
		case "ChannelMessageAnswerPostRequest":
		case "PrivateMessageGetRequest":
		case "PrivateMessagePostRequest":
		case "ClientProfileCommentGetRequest":
		case "ClientProfileCommentPostRequest":
		case "ClientProfileCommentAnswerGetRequest":
		case "ClientProfileCommentAnswerPostRequest":
		case "ClientChannelGetRequest":
		case "ClientChannelPostRequest":
		case "ChannelFileUploadRequest":
		case "ChannelFileDownloadRequest":
		case "PrivateFileUploadRequest":
		case "PrivateFileDownloadRequest":
		case "ClientLoginRequest":
		case "ClientLogoutRequest":
		case "ClientRegistrationRequest":
		case "PingMeasurementRequest":
			return true;
		default:
			return false;
		}
	}
	
	public static ClientRequestBase newClientRequestBase(	int requestId,
															String requestorUsername,
															String requestorPassword) {
		return newClientRequestBase(requestId, requestorUsername, requestorPassword, System.currentTimeMillis());
	}
	
	public static ClientRequestBase newClientRequestBase(	int requestId,
															String requestorUsername,
															String requestorPassword,
															long timestampMillis) {
		return ClientRequestBase.newBuilder()	.setRequestId(requestId)
												.setUsername(requestorUsername)
												.setPassword(requestorPassword)
												.setTimestampMillis(timestampMillis)
												.build();
	}
	
	public static ChannelJoinRequest newChannelJoinRequest(	int requestId, 
															String requestorUsername, 
															String requestorPassword, 
															int requestedChannelId) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		return ChannelJoinRequest.newBuilder().setRequestBase(requestorBase).setChannelBase(channelBase).build();
	}
	
	public static ChannelLeaveRequest newChannelLeaveRequest(	int requestId, 
																String requestorUsername, 
																String requestorPassword, 
																int requestedChannelId) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		return ChannelLeaveRequest.newBuilder().setRequestBase(requestorBase).setChannelBase(channelBase).build();
	}
	
	public static ClientProfileGetRequest newProfileGetRequest(	int requestId, 
																String requestorUsername, 
																String requestorPassword,
																int requestedId) {

		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ClientBase clientBase = ClientBase.newBuilder().setId(requestedId).setUsername(requestorUsername).build();
		return ClientProfileGetRequest.newBuilder().setRequestBase(requestorBase).setClientBase(clientBase).build();
	}
	
	public static ChannelMessageGetRequest newChannelMessageGetRequest(	int requestId, 
																	String requestorUsername, 
																	String requestorPassword,
																	int requestedChannelId,
																	int startFromMessageId,
																	int messageCount) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		return ChannelMessageGetRequest.newBuilder()	.setRequestBase(requestorBase)
														.setChannelBase(channelBase)
														.setStartCountWithMessageId(startFromMessageId)
														.setMessageCount(messageCount)
														.build();
	}
	
	public static ChannelMessagePostRequest newChannelMessagePostRequest(	int requestId, 
																			String requestorUsername, 
																			String requestorPassword,
																			int requestedChannelId,
																			String messageText) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		return ChannelMessagePostRequest.newBuilder()	.setRequestBase(requestorBase)
														.setChannelBase(channelBase)
														.setMessageText(messageText)
														.build();
	}

	public static ChannelMessageAnswerGetRequest newChannelMessageAnswerGetRequest(	int requestId, 
																					String requestorUsername, 
																					String requestorPassword,
																					int requestedChannelId,
																					int requestedMessageId,
																					int startCountWithAnswerId,
																					int answerCount) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		ClientMessageBase messageBase = ClientMessageBase.newBuilder().setMessageId(requestedMessageId).build();
		return ChannelMessageAnswerGetRequest.newBuilder()	.setRequestBase(requestorBase)
															.setChannelBase(channelBase)
															.setMessageBase(messageBase)
															.setStartCountWithAnswerId(startCountWithAnswerId)
															.setAnswerCount(answerCount)
															.build();
	}
	
	public static ChannelMessageAnswerPostRequest newChannelMessageAnswerPostRequest(	int requestId, 
																					String requestorUsername, 
																					String requestorPassword,
																					int requestedChannelId,
																					int requestedMessageId,
																					String answerText) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		ClientMessageBase messageBase = ClientMessageBase.newBuilder().setMessageId(requestedMessageId).build();
		return ChannelMessageAnswerPostRequest.newBuilder()	.setRequestBase(requestorBase)
															.setChannelBase(channelBase)
															.setMessageBase(messageBase)
															.setAnswerText(answerText)
															.build();
	}
	
	public static PrivateMessageGetRequest newPrivateMessageGetRequest(int requestId, 
																		String requestorUsername, 
																		String requestorPassword,
																		int requestedClientId,
																		int startCountWithMessageId,
																		int count) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		return PrivateMessageGetRequest.newBuilder().setRequestBase(requestorBase)
													.setClientBase(requestedBase)
													.setStartCountWithMessageId(startCountWithMessageId)
													.setMessageCount(count)
													.build();
	}
	
	public static PrivateMessagePostRequest newPrivateMessagePostRequest(	int requestId, 
																		String requestorUsername, 
																		String requestorPassword,
																		int requestedClientId,
																		String messageText) {
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorUsername, requestorPassword);
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		return PrivateMessagePostRequest.newBuilder()	.setRequestBase(requestorBase)
														.setClientBase(requestedBase)
														.setMessageText(messageText)
														.build();
	}
	
	public static ClientLoginRequest newLoginRequest(	int requestId,
														String username,
														String password) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setUsername(username)
																		.setPassword(password)
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getPublicIPv4()).build();
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
		return ClientLoginRequest.newBuilder().setRequestBase(requestorBase).setDevice(requestorDevice).build();
	}
	
	public static ClientLogoutRequest newLogoutRequest(	int requestId,
														String username,
														String password) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setUsername(username)
																		.setPassword(password)
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getPublicIPv4()).build();
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
		return ClientLogoutRequest.newBuilder().setRequestBase(requestorBase).setDevice(requestorDevice).build();
}
	
	public static ClientRegistrationRequest newRegistrationRequest(	int requestId,
																	String username,
																	String password,
																	String passwordRepeat,
																	String email) {
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceId(0).setDeviceName("").build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getPublicIPv4()).build();
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
																		.setUsername(requestorUsername)
																		.setPassword(requestorPassword)
																		.build();
		ChannelBase channelBase = ChannelBase.newBuilder().setChannelId(requestedChannelId).build();
		FileUploadBase fileBase = FileUploadBase.newBuilder()	.setFileName(requestedFileName)
																.setFilePath(requestedFilePath)
																.setFileSize(requestedFileSize)
																.build();
		return ChannelFileUploadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setChannelBase(channelBase)
														.setFileBase(fileBase)
														.build();
	}
	
	public static ChannelFileDownloadRequest newChannelFileDownloadRequest(	int requestId,
																			String requestorUsername,
																			String requestorPassword,
																			int requestedChannelId,
																			String requestedFileName,
																			String downloadPath) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setRequestId(requestId)
																		.setUsername(requestorUsername)
																		.setPassword(requestorPassword)
																		.build();
		ChannelBase channelBase = ChannelBase.newBuilder()	.setChannelId(requestedChannelId).build();
		FileDownloadBase fileBase = FileDownloadBase.newBuilder()	.setFileName(requestedFileName)
																	.setDownloadPath(downloadPath)
																	.build();
		return ChannelFileDownloadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setChannelBase(channelBase)
														.setFileBase(fileBase)
														.build();
	}
	
	public static PrivateFileUploadRequest newPrivateFileUploadRequest(	int requestId,
																		String requestorUsername,
																		String requestorPassword,
																		int requestedClientId,
																		String requestedFileName,
																		String requestedFilePath,
																		long requestedFileSize) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setUsername(requestorUsername)
																		.setPassword(requestorPassword)
																		.build();
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		FileUploadBase fileBase = FileUploadBase.newBuilder()	.setFileName(requestedFileName)
																.setFilePath(requestedFilePath)
																.setFileSize(requestedFileSize)
																.build();
		return PrivateFileUploadRequest.newBuilder().setRequestBase(requestorBase)
													.setClientBase(requestedBase)
													.setFileBase(fileBase)
													.build();
			
	}
	
	public static PrivateFileDownloadRequest newPrivateFileDownloadRequest(	int requestId,
																			String requestorUsername,
																			String requestorPassword,
																			int requestedClientId,
																			String requestedFileName,
																			String downloadPath) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setUsername(requestorUsername)
																		.setPassword(requestorPassword)
																		.build();
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		FileDownloadBase fileBase = FileDownloadBase.newBuilder()	.setFileName(requestedFileName)
																	.setDownloadPath(downloadPath)
																	.setDownloadVerification(FileDownloadVerification.newBuilder().setDownloadKey(""))
																	.build();
		return PrivateFileDownloadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setClientBase(requestedBase)
														.setFileBase(fileBase)
														.build();
	}
	
	public static PingMeasurementRequest newPingMeasurementRequest(	int requestId,
																	String requestorUsername,
																	String requestorPassword) {
		ClientRequestBase requestorBase = ClientRequestBase.newBuilder().setRequestId(requestId)
																		.setUsername(requestorUsername)
																		.setPassword(requestorPassword)
																		.build();
		ClientDeviceBase deviceBase = ClientDeviceBase.newBuilder().setDeviceName(SystemUtils.getUserName()).build();
		ClientDeviceAddress deviceAddress = ClientDeviceAddress.newBuilder().addDeviceIpV4(SystemUtils.getPublicIPv4()).build();
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
		return PingMeasurementRequest.newBuilder().setRequestBase(requestorBase)
														.setDevice(device)
														.build();
	}
}
