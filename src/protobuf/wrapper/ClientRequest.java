package protobuf.wrapper;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientRequests.ChannelJoinRequest;
import protobuf.ClientRequests.ChannelLeaveRequest;
import protobuf.ClientRequests.ChannelMessageAnswerRequest;
import protobuf.ClientRequests.ChannelMessageDataRequest;
import protobuf.ClientRequests.ChannelMessageRequest;
import protobuf.ClientRequests.ClientFileDownloadRequest;
import protobuf.ClientRequests.ClientFileUploadRequest;
import protobuf.ClientRequests.ClientLoginRequest;
import protobuf.ClientRequests.ClientLogoutRequest;
import protobuf.ClientRequests.ClientPingMeasurementRequest;
import protobuf.ClientRequests.ClientProfileDataRequest;
import protobuf.ClientRequests.ClientRegistrationRequest;
import protobuf.ClientRequests.ClientRequestBase;
import protobuf.ClientRequests.PrivateMessageRequest;

public class ClientRequest {
	
	public static boolean isClientRequest(GeneratedMessageV3 message) {
		if(message == null)
			return false;
		switch(message.getClass().getSimpleName()) {
		case "ChannelJoinRequest":
		case "ChannelLeaveRequest":
		case "ChannelMessageDataRequest":
		case "ClientProfileDataRequest":
		case "ChannelMessageRequest":
		case "ChannelMessageAnswerRequest":
		case "PrivateMessageRequest":
		case "ClientLoginRequest":
		case "ClientLogoutRequest":
		case "ClientRegistrationRequest":
		case "ClientFileUploadRequest":
		case "ClientFileDownloadRequest":
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
												.setRequestorId(requestorId)
												.setRequestorUsername(requestorUsername)
												.setRequestorPassword(requestorPassword)
												.setRequestTimestamp(System.currentTimeMillis())
												.build();
	}
	
	public static ChannelJoinRequest newChannelJoinRequest(	int requestId, 
															int requestorId, 
															String requestorUsername, 
															String requestorPassword, 
															int requestedChannelId) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);														
		return ChannelJoinRequest.newBuilder()	.setRequestorBase(requestorBase)
												.setRequestedChannelId(requestedChannelId)
												.build();
	}
	
	public static ChannelLeaveRequest newChannelLeaveRequest(	int requestId, 
																int requestorId, 
																String requestorUsername, 
																String requestorPassword, 
																int requestedChannelId) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		return ChannelLeaveRequest.newBuilder()	.setRequestorBase(requestorBase)
												.setRequestedChannelId(requestedChannelId)
												.build();
	}
	
	public static ChannelMessageDataRequest newChannelMessageDataRequest(	int requestId, 
																			int requestorId, 
																			String requestorUsername, 
																			String requestorPassword,
																			int lastLoadedMessageId,
																			int messageCount) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		return ChannelMessageDataRequest.newBuilder()	.setRequestorBase(requestorBase)
														.setLastLoadedMessageId(lastLoadedMessageId)
														.setMessageCount(messageCount)
														.build();
	}
	
	public static ClientProfileDataRequest newProfileDataRequest(	int requestId, 
																	int requestorId, 
																	String requestorUsername, 
																	String requestorPassword,
																	int requestedId,
																	String requestedUsername) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		return ClientProfileDataRequest.newBuilder().setRequestorBase(requestorBase)
													.setRequestedBase(ClientBase.newBuilder()	
																				.setId(requestedId)
																				.setUsername(requestedUsername))
																				.build();												
	}
	
	public static ChannelMessageRequest newChannelMessageRequest(	int requestId, 
																	int requestorId, 
																	String requestorUsername, 
																	String requestorPassword) {
		
		ClientRequestBase requestorBase = newClientRequestBase(requestId, requestorId, requestorUsername, requestorPassword);
		return null;
	}
	
	public static ChannelMessageAnswerRequest newChannelMessageAnswerRequest() {
		return null;
	}
	
	public static PrivateMessageRequest newPrivateMessageRequest() {
		return null;
	}
	
	public static ClientLoginRequest newLoginRequest() {
		return null;
	}
	
	public static ClientLogoutRequest newLogoutRequest() {
		return null;
	}
	
	public static ClientRegistrationRequest newRegistrationRequest() {
		return null;
	}
	
	public static ClientFileUploadRequest newFileUploadRequest() {
		return null;
	}
	
	public static ClientFileDownloadRequest newFileDownloadRequest() {
		return null;
	}
	
	public static ClientPingMeasurementRequest newPingmeasurementRequest() {
		return null;
	}
}
