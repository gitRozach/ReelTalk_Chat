package protobuf.wrapper;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDevice;
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
import protobuf.ClientRequests.PingMeasurementRequest;
import protobuf.ClientRequests.PrivateFileDownloadRequest;
import protobuf.ClientRequests.PrivateFileUploadRequest;
import protobuf.ClientRequests.PrivateMessageGetRequest;
import protobuf.ClientRequests.PrivateMessagePostRequest;

class ClientRequestTest {
	@Test
	public void clientRequestBaseDefaultInstance_checkDefaultRequestBaseValues() {
		ClientRequestBase base = ClientRequestBase.getDefaultInstance();
		assertEquals(base.getRequestId(), 0);
		assertEquals(base.getUsername(), "");
		assertEquals(base.getPassword(), "");
		assertEquals(base.getTimestampMillis(), 0L);
	}
	
	@Test
	public void newClientRequestBase_checkRequestBaseValues() {
		ClientRequestBase base = ClientRequest.newClientRequestBase(1, "Jann", "jannPassword", 0L);
		assertEquals(base.getRequestId(), 1);
		assertEquals(base.getUsername(), "Jann");
		assertEquals(base.getPassword(), "jannPassword");
		assertEquals(base.getTimestampMillis(), 0L);
	}
	
	@Test
	public void channelJoinRequestDefaultInstance_checkDefaultJoinRequestValues() {
		ChannelJoinRequest joinRequest = ChannelJoinRequest.getDefaultInstance();
		assertEquals(joinRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(joinRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(joinRequest.getMemberVerfication(), ChannelMemberVerification.getDefaultInstance());
	}
	
	@Test
	public void newChannelJoinRequest_checkJoinRequestValues() {
		ChannelJoinRequest joinRequest = ClientRequest.newChannelJoinRequest(1, "Rozach", "rozachPassword", 5);
		assertEquals(joinRequest.getRequestBase().getRequestId(), 1);
		assertEquals(joinRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(joinRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(joinRequest.getChannelBase().getChannelId(), 5);
		assertEquals(joinRequest.getChannelBase().getChannelName(), "");
		assertEquals(joinRequest.getMemberVerfication(), ChannelMemberVerification.getDefaultInstance());
	}
	
	@Test
	public void channelLeaveRequestDefaultInstance_checkDefaultLeaveRequestValues() {
		ChannelLeaveRequest leaveRequest = ChannelLeaveRequest.getDefaultInstance();
		assertEquals(leaveRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(leaveRequest.getChannelBase(), ChannelBase.getDefaultInstance());
	}
	
	@Test
	public void newChannelLeaveRequest_checkLeaveRequestValues() {
		ChannelLeaveRequest leaveRequest = ClientRequest.newChannelLeaveRequest(1, "Rozach", "rozachPassword", 5);
		assertEquals(leaveRequest.getRequestBase().getRequestId(), 1);
		assertEquals(leaveRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(leaveRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(leaveRequest.getChannelBase().getChannelId(), 5);
		assertEquals(leaveRequest.getChannelBase().getChannelName(), "");
	}
	
	@Test
	public void clientProfileGetRequestDefaultInstance_checkDefaultProfileRequestValues() {
		ClientProfileGetRequest profileGetRequest = ClientProfileGetRequest.getDefaultInstance();
		assertEquals(profileGetRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(profileGetRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
	}
	
	@Test
	public void newClientProfileGetRequest_checkProfileRequestValues() {
		ClientProfileGetRequest profileGetRequest = ClientRequest.newProfileGetRequest(1, "Rozach", "rozachPassword", 11);
	}
	
	@Test
	public void channelMessageGetRequestDefaultInstance_checkDefaultMessageRequestValues() {
		ChannelMessageGetRequest messageGetRequest = ChannelMessageGetRequest.getDefaultInstance();
		assertEquals(messageGetRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(messageGetRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(messageGetRequest.getMessageCount(), 0);
		assertEquals(messageGetRequest.getStartCountWithMessageId(), 0);
	}
	
	@Test
	public void newChannelMessageGetRequest_checkMessageRequestValues() {
		ChannelMessageGetRequest messageGetRequest = ClientRequest.newChannelMessageGetRequest(1, "Rozach", "rozachPassword", 5, 0, 1);
	}
	
	@Test
	public void channelMessagePostRequestDefaultInstance_checkDefaultMessageRequestValues() {
		ChannelMessagePostRequest messagePostRequest = ChannelMessagePostRequest.getDefaultInstance();
		assertEquals(messagePostRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(messagePostRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(messagePostRequest.getMessageText(), "");
	}
	
	@Test
	public void newChannelMessagePostRequest_checkMessageRequestValues() {
		ChannelMessagePostRequest messagePostRequest = ClientRequest.newChannelMessagePostRequest(1, "Rozach", "rozachPassword", 5, "Hallo!");
	}
	
	@Test
	public void channelMessageAnswerGetRequestDefaultInstance_checkDefaultMessageAnswerRequestValues() {
		ChannelMessageAnswerGetRequest answerGetRequest = ChannelMessageAnswerGetRequest.getDefaultInstance();
		assertEquals(answerGetRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(answerGetRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(answerGetRequest.getMessageBase(), ClientMessageBase.getDefaultInstance());
		assertEquals(answerGetRequest.getAnswerCount(), 0);
		assertEquals(answerGetRequest.getStartCountWithAnswerId(), 0);
	}
	
	@Test
	public void newChannelMessageAnswerGetRequest_checkMessageAnswerRequestValues() {
		ChannelMessageAnswerGetRequest answerGetRequest = ClientRequest.newChannelMessageAnswerGetRequest(1, "Rozach", "rozachPassword", 5, 1, 0, 3);
	}
	
	@Test
	public void channelMessageAnswerPostRequestDefaultInstance_checkDefaultMessageAnswerRequestValues() {
		ChannelMessageAnswerPostRequest answerPostRequest = ChannelMessageAnswerPostRequest.getDefaultInstance();
		assertEquals(answerPostRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(answerPostRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(answerPostRequest.getMessageBase(), ClientMessageBase.getDefaultInstance());
		assertEquals(answerPostRequest.getAnswerText(), "");
	}
	
	@Test
	public void newChannelMessageAnswerPostRequest_checkMessageAnswerRequestValues() {
		ChannelMessageAnswerPostRequest answerPostRequest = ClientRequest.newChannelMessageAnswerPostRequest(1, "Rozach", "rozachPassword", 5, 2, "Antwort");
	}
	
	@Test
	public void privateMessageGetRequestDefaultInstance_checkDefaultMessageRequestValues() {
		PrivateMessageGetRequest messageGetRequest = PrivateMessageGetRequest.getDefaultInstance();
		assertEquals(messageGetRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(messageGetRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(messageGetRequest.getMessageCount(), 0);
		assertEquals(messageGetRequest.getStartCountWithMessageId(), 0);
	}
	
	@Test
	public void newPrivateMessageGetRequest_checkMessageRequestValues() {
		PrivateMessageGetRequest messageGetRequest = ClientRequest.newPrivateMessageGetRequest(1, "Rozach", "rozachPassword", 11, 0, 10);
	}
	
	@Test
	public void privateMessagePostRequestDefaultInstance_checkDefaultMessageRequestValues() {
		PrivateMessagePostRequest messagePostRequest = PrivateMessagePostRequest.getDefaultInstance();
		assertEquals(messagePostRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(messagePostRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
		assertEquals(messagePostRequest.getMessageText(), "");
	}
	
	@Test
	public void newPrivateMessagePostRequest_checkMessageRequestValues() {
		PrivateMessagePostRequest messagePostRequest = ClientRequest.newPrivateMessagePostRequest(1, "Jann", "jannPassword", 12, "Hallo Rozach");
	}
	
	@Test
	public void clientLoginRequestDefaultInstance_checkDefaultLoginRequestValues() {
		ClientLoginRequest loginRequest = ClientLoginRequest.getDefaultInstance();
		assertEquals(loginRequest.getDevice(), ClientDevice.getDefaultInstance());
		assertEquals(loginRequest.getRequestBase(), ClientRequestBase.getDefaultInstance());
	}
	
	@Test
	public void newClientLoginRequest_checkLoginRequestValues() {
		ClientLoginRequest loginRequest = ClientRequest.newLoginRequest(1, "Jann", "jannPassword");
	}
	
	@Test
	public void clientLogoutRequestDefaultInstance_checkDefaultLogoutRequestValues() {
		ClientLogoutRequest logoutRequest = ClientLogoutRequest.getDefaultInstance();
	}
	
	@Test
	public void newClientLogoutRequest_checkLogoutRequestValues() {
		ClientLogoutRequest logoutRequest = ClientRequest.newLogoutRequest(1, "Jann", "jannPassword");
	}
	
	@Test
	public void clientRegistrationRequestDefaultInstance_checkDefaultRegistrationRequestValues() {
		ClientRegistrationRequest registrationRequest = ClientRegistrationRequest.getDefaultInstance();
	}
	
	@Test
	public void newClientRegistrationRequest_checkRegistrationRequestValues() {
		ClientRegistrationRequest registrationRequest = ClientRequest.newRegistrationRequest(1, "Hasan", "husseini", "husseini", "egonecohasan@gmail.com");
	}
	
	@Test
	public void channelFileUploadRequestDefaultInstance_checkDefaultFileUploadRequestValues() {
		ChannelFileUploadRequest uploadRequest = ChannelFileUploadRequest.getDefaultInstance();
	}
	
	@Test
	public void newChannelFileUploadRequest_checkFileUploadRequestValues() {
		ChannelFileUploadRequest uploadRequest = ClientRequest.newChannelFileUploadRequest(1, "Rozach", "rozachPassword", 5, "readme.txt", "C:/Users/Rozach/readme.txt", 1024L);
	}
	
	@Test
	public void channelFileDownloadRequestDefaultInstance_checkDefaultFileDownloadRequestValues() {
		ChannelFileDownloadRequest downloadRequest = ChannelFileDownloadRequest.getDefaultInstance();
	}
	
	@Test
	public void newChannelFileDownloadRequest_checkFileDownloadRequestValues() {
		ChannelFileDownloadRequest downloadRequest = ClientRequest.newChannelFileDownloadRequest(1, "Jann", "jannPassword", 5, "readme.txt", "C:/Users/Jann/Downloads/readme.txt");
	}
	
	@Test
	public void privateFileUploadRequestDefaultInstance_checkDefaultFileUploadRequestValues() {
		PrivateFileUploadRequest uploadRequest = PrivateFileUploadRequest.getDefaultInstance();
	}
	
	@Test
	public void newPrivateFileUploadRequest_checkFileUploadRequestValues() {
		PrivateFileUploadRequest uploadRequest = ClientRequest.newPrivateFileUploadRequest(1, "Rozach", "rozachPassword", 5, "readme.txt", "C:/Users/Rozach/readme.txt", 1024L);
	}
	
	@Test
	public void privateFileDownloadRequestDefaultInstance_checkDefaultFileDownloadRequestValues() {
		PrivateFileDownloadRequest downloadRequest = PrivateFileDownloadRequest.getDefaultInstance();
	}
	
	@Test
	public void newPrivateFileDownloadRequest_checkFileDownloadRequestValues() {
		PrivateFileDownloadRequest downloadRequest = ClientRequest.newPrivateFileDownloadRequest(1, "Jann", "jannPassword", 5, "readme.txt", "C:/Users/Jann/Downloads/readme.txt");
	}
	
	@Test
	public void pingMeasurementRequestDefaultInstance_checkDefaultMeasurementRequestValues() {
		PingMeasurementRequest measurementRequest = PingMeasurementRequest.getDefaultInstance();
	}
	
	@Test
	public void newPingMeasurementRequest_checkMeasurementRequestValues() {
		PingMeasurementRequest measurementRequest = ClientRequest.newPingMeasurementRequest(1, "Rozach", "rozachPassword");
	}
}
