package protobuf.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientIdentities.ClientBase;
import protobuf.ClientIdentities.ClientDevice;
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

class ClientRequestTest {
	@Test
	public void clientRequestBaseDefaultInstance_checkDefaultRequestBaseValues() {
		RequestBase base = RequestBase.getDefaultInstance();
		assertEquals(base.getRequestId(), 0);
		assertEquals(base.getUsername(), "");
		assertEquals(base.getPassword(), "");
		assertEquals(base.getTimestampMillis(), 0L);
	}
	
	@Test
	public void newClientRequestBase_checkRequestBaseValues() {
		RequestBase base = ClientRequests.newRequestBase(1, "Jann", "jannPassword", 0L);
		assertEquals(base.getRequestId(), 1);
		assertEquals(base.getUsername(), "Jann");
		assertEquals(base.getPassword(), "jannPassword");
		assertEquals(base.getTimestampMillis(), 0L);
	}
	
	@Test
	public void channelJoinRequestDefaultInstance_checkDefaultJoinRequestValues() {
		ChannelJoinRequest joinRequest = ChannelJoinRequest.getDefaultInstance();
		assertEquals(joinRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(joinRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(joinRequest.getMemberVerfication(), ChannelMemberVerification.getDefaultInstance());
	}
	
	@Test
	public void newChannelJoinRequest_checkJoinRequestValues() {
		ChannelJoinRequest joinRequest = ClientRequests.newChannelJoinRequest(1, "Rozach", "rozachPassword", 5);
		assertEquals(joinRequest.getRequestBase().getRequestId(), 1);
		assertEquals(joinRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(joinRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(joinRequest.getChannelBase().getId(), 5);
		assertEquals(joinRequest.getChannelBase().getName(), "");
		assertEquals(joinRequest.getMemberVerfication(), ChannelMemberVerification.getDefaultInstance());
	}
	
	@Test
	public void channelLeaveRequestDefaultInstance_checkDefaultLeaveRequestValues() {
		ChannelLeaveRequest leaveRequest = ChannelLeaveRequest.getDefaultInstance();
		assertEquals(leaveRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(leaveRequest.getChannelBase(), ChannelBase.getDefaultInstance());
	}
	
	@Test
	public void newChannelLeaveRequest_checkLeaveRequestValues() {
		ChannelLeaveRequest leaveRequest = ClientRequests.newChannelLeaveRequest(1, "Rozach", "rozachPassword", 5);
		assertEquals(leaveRequest.getRequestBase().getRequestId(), 1);
		assertEquals(leaveRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(leaveRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(leaveRequest.getChannelBase().getId(), 5);
		assertEquals(leaveRequest.getChannelBase().getName(), "");
	}
	
	@Test
	public void clientProfileGetRequestDefaultInstance_checkDefaultProfileRequestValues() {
		ProfileGetRequest profileGetRequest = ProfileGetRequest.getDefaultInstance();
		assertEquals(profileGetRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(profileGetRequest.getRequestBase(), RequestBase.getDefaultInstance());
	}
	
	@Test
	public void newClientProfileGetRequest_checkProfileRequestValues() {
		ProfileGetRequest profileGetRequest = ClientRequests.newProfileGetRequest(1, "Rozach", "rozachPassword", 11);
		assertEquals(profileGetRequest.getClientBase().getId(), 11);
		assertEquals(profileGetRequest.getClientBase().getUsername(), "");
		assertEquals(profileGetRequest.getRequestBase().getRequestId(), 1);
		assertEquals(profileGetRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(profileGetRequest.getRequestBase().getPassword(), "rozachPassword");
		assertTrue(profileGetRequest.getRequestBase().getTimestampMillis() > 0L);
	}
	
	@Test
	public void channelMessageGetRequestDefaultInstance_checkDefaultMessageRequestValues() {
		ChannelMessageGetRequest messageGetRequest = ChannelMessageGetRequest.getDefaultInstance();
		assertEquals(messageGetRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(messageGetRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(messageGetRequest.getMessageCount(), 0);
		assertEquals(messageGetRequest.getLastIndex(), 0);
	}
	
	@Test
	public void newChannelMessageGetRequest_checkMessageRequestValues() {
		ChannelMessageGetRequest messageGetRequest = ClientRequests.newChannelMessageGetRequest(1, "Rozach", "rozachPassword", 5, 0, 1);
		assertEquals(messageGetRequest.getChannelBase().getId(), 5);
		assertEquals(messageGetRequest.getChannelBase().getName(), "");
		assertEquals(messageGetRequest.getRequestBase().getRequestId(), 1);
		assertEquals(messageGetRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(messageGetRequest.getRequestBase().getPassword(), "rozachPassword");
		assertTrue(messageGetRequest.getRequestBase().getTimestampMillis() > 0L);
		assertEquals(messageGetRequest.getLastIndex(), 0);
		assertEquals(messageGetRequest.getMessageCount(), 1);
	}
	
	@Test
	public void channelMessagePostRequestDefaultInstance_checkDefaultMessageRequestValues() {
		ChannelMessagePostRequest messagePostRequest = ChannelMessagePostRequest.getDefaultInstance();
		assertEquals(messagePostRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(messagePostRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(messagePostRequest.getMessageText(), "");
	}
	
	@Test
	public void newChannelMessagePostRequest_checkMessageRequestValues() {
		ChannelMessagePostRequest messagePostRequest = ClientRequests.newChannelMessagePostRequest(1, "Rozach", "rozachPassword", 5, "Hallo!");
		assertEquals(messagePostRequest.getChannelBase().getId(), 5);
		assertEquals(messagePostRequest.getChannelBase().getName(), "");
		assertEquals(messagePostRequest.getRequestBase().getRequestId(), 1);
		assertEquals(messagePostRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(messagePostRequest.getRequestBase().getPassword(), "rozachPassword");
		assertTrue(messagePostRequest.getRequestBase().getTimestampMillis() > 0L);
		assertEquals(messagePostRequest.getMessageText(), "Hallo!");
	}
	
	@Test
	public void channelMessageAnswerGetRequestDefaultInstance_checkDefaultMessageAnswerRequestValues() {
		ChannelMessageAnswerGetRequest answerGetRequest = ChannelMessageAnswerGetRequest.getDefaultInstance();
		assertEquals(answerGetRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(answerGetRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(answerGetRequest.getMessageBase(), MessageBase.getDefaultInstance());
		assertEquals(answerGetRequest.getAnswerCount(), 0);
		assertEquals(answerGetRequest.getStartCountWithAnswerId(), 0);
	}
	
	@Test
	public void newChannelMessageAnswerGetRequest_checkMessageAnswerRequestValues() {
		ChannelMessageAnswerGetRequest answerGetRequest = ClientRequests.newChannelMessageAnswerGetRequest(1, "Rozach", "rozachPassword", 5, 1, 0, 3);
		assertEquals(answerGetRequest.getChannelBase().getId(), 5);
		assertEquals(answerGetRequest.getChannelBase().getName(), "");
		assertEquals(answerGetRequest.getRequestBase().getRequestId(), 1);
		assertEquals(answerGetRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(answerGetRequest.getRequestBase().getPassword(), "rozachPassword");
		assertTrue(answerGetRequest.getRequestBase().getTimestampMillis() > 0L);
		assertEquals(answerGetRequest.getStartCountWithAnswerId(), 0);
		assertEquals(answerGetRequest.getAnswerCount(), 3);
		assertEquals(answerGetRequest.getMessageBase().getMessageId(), 1);
	}
	
	@Test
	public void channelMessageAnswerPostRequestDefaultInstance_checkDefaultMessageAnswerRequestValues() {
		ChannelMessageAnswerPostRequest answerPostRequest = ChannelMessageAnswerPostRequest.getDefaultInstance();
		assertEquals(answerPostRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(answerPostRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(answerPostRequest.getMessageBase(), MessageBase.getDefaultInstance());
		assertEquals(answerPostRequest.getAnswerText(), "");
	}
	
	@Test
	public void newChannelMessageAnswerPostRequest_checkMessageAnswerRequestValues() {
		ChannelMessageAnswerPostRequest answerPostRequest = ClientRequests.newChannelMessageAnswerPostRequest(1, "Rozach", "rozachPassword", 5, 2, "Antwort");
		assertEquals(answerPostRequest.getChannelBase().getId(), 5);
		assertEquals(answerPostRequest.getChannelBase().getName(), "");
		assertEquals(answerPostRequest.getRequestBase().getRequestId(), 1);
		assertEquals(answerPostRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(answerPostRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(answerPostRequest.getMessageBase().getMessageId(), 2);
		assertEquals(answerPostRequest.getAnswerText(), "Antwort");
	}
	
	@Test
	public void privateMessageGetRequestDefaultInstance_checkDefaultMessageRequestValues() {
		PrivateMessageGetRequest messageGetRequest = PrivateMessageGetRequest.getDefaultInstance();
		assertEquals(messageGetRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(messageGetRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(messageGetRequest.getMessageCount(), 0);
		assertEquals(messageGetRequest.getStartCountWithMessageId(), 0);
	}
	
	@Test
	public void newPrivateMessageGetRequest_checkMessageRequestValues() {
		PrivateMessageGetRequest messageGetRequest = ClientRequests.newPrivateMessageGetRequest(1, "Rozach", "rozachPassword", 11, 0, 10);
		assertEquals(messageGetRequest.getClientBase().getId(), 11);
		assertEquals(messageGetRequest.getClientBase().getUsername(), "");
		assertEquals(messageGetRequest.getRequestBase().getRequestId(), 1);
		assertEquals(messageGetRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(messageGetRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(messageGetRequest.getMessageCount(), 10);
		assertEquals(messageGetRequest.getStartCountWithMessageId(), 0);
	}
	
	@Test
	public void privateMessagePostRequestDefaultInstance_checkDefaultMessageRequestValues() {
		PrivateMessagePostRequest messagePostRequest = PrivateMessagePostRequest.getDefaultInstance();
		assertEquals(messagePostRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(messagePostRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(messagePostRequest.getMessageText(), "");
	}
	
	@Test
	public void newPrivateMessagePostRequest_checkMessageRequestValues() {
		PrivateMessagePostRequest messagePostRequest = ClientRequests.newPrivateMessagePostRequest(1, "Jann", "jannPassword", 12, "Hallo Rozach");
		assertEquals(messagePostRequest.getClientBase().getId(), 12);
		assertEquals(messagePostRequest.getClientBase().getUsername(), "");
		assertEquals(messagePostRequest.getRequestBase().getRequestId(), 1);
		assertEquals(messagePostRequest.getRequestBase().getUsername(), "Jann");
		assertEquals(messagePostRequest.getRequestBase().getPassword(), "jannPassword");
		assertEquals(messagePostRequest.getMessageText(), "Hallo Rozach");
	}
	
	@Test
	public void clientLoginRequestDefaultInstance_checkDefaultLoginRequestValues() {
		LoginRequest loginRequest = LoginRequest.getDefaultInstance();
		assertEquals(loginRequest.getDevice(), ClientDevice.getDefaultInstance());
		assertEquals(loginRequest.getRequestBase(), RequestBase.getDefaultInstance());
	}
	
	@Test
	public void newClientLoginRequest_checkLoginRequestValues() {
		LoginRequest loginRequest = ClientRequests.newLoginRequest(1, "Jann", "jannPassword");
		assertEquals(loginRequest.getRequestBase().getRequestId(), 1);
		assertEquals(loginRequest.getRequestBase().getUsername(), "Jann");
		assertEquals(loginRequest.getRequestBase().getPassword(), "jannPassword");
		assertFalse(loginRequest.getDevice().equals(ClientDevice.getDefaultInstance()));
	}
	
	@Test
	public void clientLogoutRequestDefaultInstance_checkDefaultLogoutRequestValues() {
		LogoutRequest logoutRequest = LogoutRequest.getDefaultInstance();
		assertEquals(logoutRequest.getDevice(), ClientDevice.getDefaultInstance());
		assertEquals(logoutRequest.getRequestBase(), RequestBase.getDefaultInstance());
	}
	
	@Test
	public void newClientLogoutRequest_checkLogoutRequestValues() {
		LogoutRequest logoutRequest = ClientRequests.newLogoutRequest(1, "Jann", "jannPassword");
		assertEquals(logoutRequest.getRequestBase().getRequestId(), 1);
		assertEquals(logoutRequest.getRequestBase().getUsername(), "Jann");
		assertEquals(logoutRequest.getRequestBase().getPassword(), "jannPassword");
		assertFalse(logoutRequest.getDevice().equals(ClientDevice.getDefaultInstance()));
	}
	
	@Test
	public void clientRegistrationRequestDefaultInstance_checkDefaultRegistrationRequestValues() {
		RegistrationRequest registrationRequest = RegistrationRequest.getDefaultInstance();
		assertEquals(registrationRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(registrationRequest.getUsername(), "");
		assertEquals(registrationRequest.getPassword(), "");
		assertEquals(registrationRequest.getPasswordRepeat(), "");
		assertEquals(registrationRequest.getEmail(), "");
		assertEquals(registrationRequest.getInvitationKey(), "");
		assertEquals(registrationRequest.getDevice(), ClientDevice.getDefaultInstance());
	}
	
	@Test
	public void newClientRegistrationRequest_checkRegistrationRequestValues() {
		RegistrationRequest registrationRequest = ClientRequests.newRegistrationRequest(1, "Hasan", "husseini", "husseini", "egonecohasan@gmail.com");
		assertEquals(registrationRequest.getRequestBase().getRequestId(), 1);
		assertEquals(registrationRequest.getRequestBase().getUsername(), "Hasan");
		assertEquals(registrationRequest.getRequestBase().getPassword(), "husseini");
		assertEquals(registrationRequest.getUsername(), "Hasan");
		assertEquals(registrationRequest.getPassword(), "husseini");
		assertEquals(registrationRequest.getPasswordRepeat(), "husseini");
		assertEquals(registrationRequest.getEmail(), "egonecohasan@gmail.com");
		assertEquals(registrationRequest.getInvitationKey(), "");
		assertFalse(registrationRequest.getDevice().equals(ClientDevice.getDefaultInstance()));
	}
	
	@Test
	public void channelFileUploadRequestDefaultInstance_checkDefaultFileUploadRequestValues() {
		ChannelFileUploadRequest uploadRequest = ChannelFileUploadRequest.getDefaultInstance();
		assertEquals(uploadRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(uploadRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(uploadRequest.getFileBase(), FileUploadBase.getDefaultInstance());
	}
	
	@Test
	public void newChannelFileUploadRequest_checkFileUploadRequestValues() {
		ChannelFileUploadRequest uploadRequest = ClientRequests.newChannelFileUploadRequest(1, "Rozach", "rozachPassword", 5, "readme.txt", "C:/Users/Rozach/readme.txt", 1024L);
		assertEquals(uploadRequest.getRequestBase().getRequestId(), 1);
		assertEquals(uploadRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(uploadRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(uploadRequest.getChannelBase().getId(), 5);
		assertEquals(uploadRequest.getFileBase().getFileName(), "readme.txt");
		assertEquals(uploadRequest.getFileBase().getFilePath(), "C:/Users/Rozach/readme.txt");
		assertEquals(uploadRequest.getFileBase().getFileSize(), 1024L);
	}
	
	@Test
	public void channelFileDownloadRequestDefaultInstance_checkDefaultFileDownloadRequestValues() {
		ChannelFileDownloadRequest downloadRequest = ChannelFileDownloadRequest.getDefaultInstance();
		assertEquals(downloadRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(downloadRequest.getChannelBase(), ChannelBase.getDefaultInstance());
		assertEquals(downloadRequest.getFileBase(), FileDownloadBase.getDefaultInstance());
	}
	
	@Test
	public void newChannelFileDownloadRequest_checkFileDownloadRequestValues() {
		ChannelFileDownloadRequest downloadRequest = ClientRequests.newChannelFileDownloadRequest(1, "Jann", "jannPassword", 5, "readme.txt", "C:/Users/Jann/Downloads");
		assertEquals(downloadRequest.getRequestBase().getRequestId(), 1);
		assertEquals(downloadRequest.getRequestBase().getUsername(), "Jann");
		assertEquals(downloadRequest.getRequestBase().getPassword(), "jannPassword");
		assertEquals(downloadRequest.getChannelBase().getId(), 5);
		assertEquals(downloadRequest.getFileBase().getFileName(), "readme.txt");
		assertEquals(downloadRequest.getFileBase().getDownloadPath(), "C:/Users/Jann/Downloads");
	}
	
	@Test
	public void privateFileUploadRequestDefaultInstance_checkDefaultFileUploadRequestValues() {
		PrivateFileUploadRequest uploadRequest = PrivateFileUploadRequest.getDefaultInstance();
		assertEquals(uploadRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(uploadRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(uploadRequest.getFileBase(), FileUploadBase.getDefaultInstance());
	}
	
	@Test
	public void newPrivateFileUploadRequest_checkFileUploadRequestValues() {
		PrivateFileUploadRequest uploadRequest = ClientRequests.newPrivateFileUploadRequest(1, "Rozach", "rozachPassword", 11, "readme.txt", "C:/Users/Rozach/readme.txt", 1024L);
		assertEquals(uploadRequest.getRequestBase().getRequestId(), 1);
		assertEquals(uploadRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(uploadRequest.getRequestBase().getPassword(), "rozachPassword");
		assertEquals(uploadRequest.getClientBase().getId(), 11);
		assertEquals(uploadRequest.getFileBase().getFileName(), "readme.txt");
		assertEquals(uploadRequest.getFileBase().getFilePath(), "C:/Users/Rozach/readme.txt");
		assertEquals(uploadRequest.getFileBase().getFileSize(), 1024L);
	}
	
	@Test
	public void privateFileDownloadRequestDefaultInstance_checkDefaultFileDownloadRequestValues() {
		PrivateFileDownloadRequest downloadRequest = PrivateFileDownloadRequest.getDefaultInstance();
		assertEquals(downloadRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(downloadRequest.getClientBase(), ClientBase.getDefaultInstance());
		assertEquals(downloadRequest.getFileBase(), FileDownloadBase.getDefaultInstance());
	}
	
	@Test
	public void newPrivateFileDownloadRequest_checkFileDownloadRequestValues() {
		PrivateFileDownloadRequest downloadRequest = ClientRequests.newPrivateFileDownloadRequest(1, "Jann", "jannPassword", 12, "readme.txt", "C:/Users/Jann/Downloads");
		assertEquals(downloadRequest.getRequestBase().getRequestId(), 1);
		assertEquals(downloadRequest.getRequestBase().getUsername(), "Jann");
		assertEquals(downloadRequest.getRequestBase().getPassword(), "jannPassword");
		assertEquals(downloadRequest.getClientBase().getId(), 12);
		assertEquals(downloadRequest.getFileBase().getFileName(), "readme.txt");
		assertEquals(downloadRequest.getFileBase().getDownloadPath(), "C:/Users/Jann/Downloads");
	}
	
	@Test
	public void pingMeasurementRequestDefaultInstance_checkDefaultMeasurementRequestValues() {
		PingMeasurementRequest measurementRequest = PingMeasurementRequest.getDefaultInstance();
		assertEquals(measurementRequest.getRequestBase(), RequestBase.getDefaultInstance());
		assertEquals(measurementRequest.getDevice(), ClientDevice.getDefaultInstance());
	}
	
	@Test
	public void newPingMeasurementRequest_checkMeasurementRequestValues() {
		PingMeasurementRequest measurementRequest = ClientRequests.newPingMeasurementRequest(1, "Rozach", "rozachPassword");
		assertEquals(measurementRequest.getRequestBase().getRequestId(), 1);
		assertEquals(measurementRequest.getRequestBase().getUsername(), "Rozach");
		assertEquals(measurementRequest.getRequestBase().getPassword(), "rozachPassword");
		assertFalse(measurementRequest.getDevice().equals(ClientDevice.getDefaultInstance()));
	}
}
