package protobuf.wrapper;

import com.google.protobuf.Message;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDevice;
import protobuf.ClientIdentities.ClientDeviceAddress;
import protobuf.ClientIdentities.ClientDeviceBase;
import protobuf.ClientIdentities.ClientDeviceOs;
import protobuf.ClientIdentities.ClientDeviceOs.ClientDeviceOsType;
import protobuf.ClientIdentities.ClientDeviceType;
import protobuf.ClientMessages.MessageBase;
import protobuf.ClientRequests.ChannelFileDownloadRequest;
import protobuf.ClientRequests.ChannelFileUploadRequest;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageAnswerGetRequest;
import protobuf.ClientRequests.ChannelMessageAnswerPostRequest;
import protobuf.ClientRequests.ChannelMessageGetRequest;
import protobuf.ClientRequests.ChannelMessagePostRequest;
import protobuf.ClientRequests.FileDownloadBase;
import protobuf.ClientRequests.FileUploadBase;
import protobuf.ClientRequests.LoginRequest;
import protobuf.ClientRequests.LogoutRequest;
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;
import protobuf.ClientRequests.ProfileGetRequest;
import protobuf.ClientRequests.RegistrationRequest;
import protobuf.ClientRequests.RequestBase;
import utils.SystemUtils;

public class ClientRequests {	
	public static String[] getRegisteredTypeNames() {
		return new String[] {	"RequestBase",
								"FileDownloadVerification",
								"FileDownloadBase",
								"FileUploadBase",
								"ChannelJoinRequest",
								"ChannelLeaveRequest",
								"ProfileGetRequest",
								"ChannelMessageGetRequest",
								"ChannelMessagePostRequest",
								"ChannelMessageAnswerGetRequest",
								"ChannelMessageAnswerPostRequest",
								"PrivateMessageGetRequest",
								"PrivateMessagePostRequest",
								"ProfileCommentGetRequest",
								"ProfileCommentPostRequest",
								"ProfileCommentAnswerGetRequest",
								"ProfileCommentAnswerPostRequest",
								"ChannelGetRequest",
								"ChannelPostRequest",
								"ChannelFileUploadRequest",
								"ChannelFileDownloadRequest",
								"PrivateFileUploadRequest",
								"PrivateFileDownloadRequest",
								"LoginRequest",
								"LogoutRequest",
								"RegistrationRequest",
								"PingMeasurementRequest"};
	}
	
	public static boolean isRequest(Class<? extends Message> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static RequestBase newRequestBase(	int requestId,
												String requestorUsername,
												String requestorPassword) {
		return newRequestBase(requestId, requestorUsername, requestorPassword, System.currentTimeMillis());
	}
	
	public static RequestBase newRequestBase(	int requestId,
												String requestorUsername,
												String requestorPassword,
												long timestampMillis) {
		return RequestBase.newBuilder()	.setRequestId(requestId)
										.setUsername(requestorUsername)
										.setPassword(requestorPassword)
										.setTimestampMillis(timestampMillis)
										.build();
	}
	
	public static ChannelJoinRequest newChannelJoinRequest(	int requestId, 
															String requestorUsername, 
															String requestorPassword, 
															int requestedChannelId) {
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		return ChannelJoinRequest.newBuilder().setRequestBase(requestorBase).setChannelBase(channelBase).build();
	}
	
	public static ChannelLeaveRequest newChannelLeaveRequest(	int requestId, 
																String requestorUsername, 
																String requestorPassword, 
																int requestedChannelId) {
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		return ChannelLeaveRequest.newBuilder().setRequestBase(requestorBase).setChannelBase(channelBase).build();
	}
	
	public static ProfileGetRequest newProfileGetRequest(	int requestId, 
															String requestorUsername, 
															String requestorPassword,
															int requestedId) {
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ClientBase clientBase = ClientBase.newBuilder().setId(requestedId).build();
		return ProfileGetRequest.newBuilder().setRequestBase(requestorBase).setClientBase(clientBase).build();
	}
	
	public static ChannelMessageGetRequest newChannelMessageGetRequest(	int requestId, 
																		String requestorUsername, 
																		String requestorPassword,
																		int requestedChannelId,
																		int lastIndex,
																		int messageCount) {
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		return ChannelMessageGetRequest.newBuilder()	.setRequestBase(requestorBase)
														.setChannelBase(channelBase)
														.setLastIndex(lastIndex)
														.setMessageCount(messageCount)
														.build();
	}
	
	public static ChannelMessagePostRequest newChannelMessagePostRequest(	int requestId, 
																			String requestorUsername, 
																			String requestorPassword,
																			int requestedChannelId,
																			String messageText) {
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
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
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		MessageBase messageBase = MessageBase.newBuilder().setMessageId(requestedMessageId).build();
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
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		MessageBase messageBase = MessageBase.newBuilder().setMessageId(requestedMessageId).build();
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
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
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
		RequestBase requestorBase = newRequestBase(requestId, requestorUsername, requestorPassword);
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		return PrivateMessagePostRequest.newBuilder()	.setRequestBase(requestorBase)
														.setClientBase(requestedBase)
														.setMessageText(messageText)
														.build();
	}
	
	public static LoginRequest newLoginRequest(	int requestId,
														String username,
														String password) {
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
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
		return LoginRequest.newBuilder().setRequestBase(requestorBase).setDevice(requestorDevice).build();
	}
	
	public static LogoutRequest newLogoutRequest(	int requestId,
													String username,
													String password) {
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
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
		return LogoutRequest.newBuilder().setRequestBase(requestorBase).setDevice(requestorDevice).build();
}
	
	public static RegistrationRequest newRegistrationRequest(	int requestId,
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
		return RegistrationRequest.newBuilder()	.setRequestBase(ClientRequests.newRequestBase(requestId, username, password))
												.setUsername(username)
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
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
															.setUsername(requestorUsername)
															.setPassword(requestorPassword)
															.build();
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
		FileUploadBase fileBase = FileUploadBase.newBuilder()	.setFileName(requestedFileName)
																.setFilePath(requestedFilePath)
																.setFileSize(requestedFileSize)
																.build();
		return ChannelFileUploadRequest.newBuilder().setRequestBase(requestorBase)
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
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
															.setRequestId(requestId)
															.setUsername(requestorUsername)
															.setPassword(requestorPassword)
															.build();
		ChannelBase channelBase = ChannelBase.newBuilder().setId(requestedChannelId).build();
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
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
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
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
															.setUsername(requestorUsername)
															.setPassword(requestorPassword)
															.build();
		ClientBase requestedBase = ClientBase.newBuilder().setId(requestedClientId).build();
		FileDownloadBase fileBase = FileDownloadBase.newBuilder()	.setFileName(requestedFileName)
																	.setDownloadPath(downloadPath)
																	.setDownloadKey("")
																	.build();
		return PrivateFileDownloadRequest.newBuilder()	.setRequestBase(requestorBase)
														.setClientBase(requestedBase)
														.setFileBase(fileBase)
														.build();
	}
	
	public static PingMeasurementRequest newPingMeasurementRequest(	int requestId,
																	String requestorUsername,
																	String requestorPassword) {
		RequestBase requestorBase = RequestBase.newBuilder().setRequestId(requestId)
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
