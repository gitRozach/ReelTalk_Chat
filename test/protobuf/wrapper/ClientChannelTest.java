package protobuf.wrapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import protobuf.ClientChannels.Channel;
import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientChannels.ChannelRestrictionType;
import protobuf.ClientEvents.LoginEvent;
import protobuf.ClientIdentities.ClientAccount;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientRequests.LoginRequest;

class ClientChannelTest {
	
	@Test
	public void isClientChannel_validClientChannelsReturnTrue() {
		Channel c1 = ClientChannels.newPublicTextChannel(1, "Text 1", 25, Collections.emptyList());
		Channel c2 = ClientChannels.newPublicTextChannel(2, "Text 2", 25, Collections.emptyList());
		Channel v1 = ClientChannels.newPublicVoiceChannel(3, "Voice 1", 10, Collections.emptyList());
		Channel v2 = ClientChannels.newPublicVoiceChannel(4, "Voice 2", 10, Collections.emptyList());
		assertTrue(ClientChannels.isChannel(c1.getClass()));
		assertTrue(ClientChannels.isChannel(c2.getClass()));
		assertTrue(ClientChannels.isChannel(v1.getClass()));
		assertTrue(ClientChannels.isChannel(v2.getClass()));
	}
	
	@Test
	public void isClientChannel_invalidClientChannelsReturnFalse() {
		ClientAccount account = ClientAccount.getDefaultInstance();
		ChannelMessage channelMessage = ChannelMessage.getDefaultInstance();
		LoginEvent loginEvent = LoginEvent.getDefaultInstance();
		LoginRequest loginRequest = LoginRequest.getDefaultInstance();
		assertFalse(ClientChannels.isChannel(account.getClass()));
		assertFalse(ClientChannels.isChannel(channelMessage.getClass()));
		assertFalse(ClientChannels.isChannel(loginEvent.getClass()));
		assertFalse(ClientChannels.isChannel(loginRequest.getClass()));
	}
	
	@Test
	public void addMemberIdsToChannel_addTextChannelMemberIdsAndCheckMemberList() {
		Channel channel = ClientChannels.newPublicTextChannel(1, "Channel 1", 100, Arrays.asList(1, 2, 3, 4, 5));
		channel = ClientChannels.addMemberIdsToChannel(channel, Arrays.asList(6, 7, 8, 9, 10));
		assertTrue(channel != null);
		assertEquals(channel.getMemberIdCount(), 10);
		assertEquals(channel.getMemberId(5), 6);
		assertEquals(channel.getMemberId(6), 7);
		assertEquals(channel.getMemberId(7), 8);
		assertEquals(channel.getMemberId(8), 9);
		assertEquals(channel.getMemberId(9), 10);
	}
	
	@Test
	public void addMemberIdToChannel_addTextChannelMemberIdsAndCheckMemberList() {
		Channel channel = ClientChannels.newPublicTextChannel(1, "Channel 1", 100, Arrays.asList(1, 2, 3, 4, 5));
		channel = ClientChannels.addMemberIdToChannel(channel, 6);
		channel = ClientChannels.addMemberIdToChannel(channel, 7);
		channel = ClientChannels.addMemberIdToChannel(channel, 8);
		channel = ClientChannels.addMemberIdToChannel(channel, 9);
		channel = ClientChannels.addMemberIdToChannel(channel, 10);
		assertTrue(channel != null);
		assertEquals(channel.getMemberIdCount(), 10);
		assertEquals(channel.getMemberId(5), 6);
		assertEquals(channel.getMemberId(6), 7);
		assertEquals(channel.getMemberId(7), 8);
		assertEquals(channel.getMemberId(8), 9);
		assertEquals(channel.getMemberId(9), 10);
	}
	
	@Test
	public void addMemberIdsToChannel_addVoiceChannelMemberIdsAndCheckMemberList() {
		Channel channel = ClientChannels.newPublicVoiceChannel(1, "Channel 1", 100, Arrays.<Integer>asList(1, 2, 3, 4, 5));
		channel = ClientChannels.addMemberIdsToChannel(channel, Arrays.asList(6, 7, 8, 9, 10));
		assertTrue(channel != null);
		assertEquals(channel.getMemberIdCount(), 10);
		assertEquals(channel.getMemberId(5), 6);
		assertEquals(channel.getMemberId(6), 7);
		assertEquals(channel.getMemberId(7), 8);
		assertEquals(channel.getMemberId(8), 9);
		assertEquals(channel.getMemberId(9), 10);
	}
	
	@Test
	public void addMemberIdToChannel_addVoiceChannelMemberIdsAndCheckMemberList() {
		Channel channel = ClientChannels.newPublicVoiceChannel(1, "Channel 1", 100, Arrays.<Integer>asList(1, 2, 3, 4, 5));
		channel = ClientChannels.addMemberIdToChannel(channel, 6);
		channel = ClientChannels.addMemberIdToChannel(channel, 7);
		channel = ClientChannels.addMemberIdToChannel(channel, 8);
		channel = ClientChannels.addMemberIdToChannel(channel, 9);
		channel = ClientChannels.addMemberIdToChannel(channel, 10);
		assertTrue(channel != null);
		assertEquals(channel.getMemberIdCount(), 10);
		assertEquals(channel.getMemberId(5), 6);
		assertEquals(channel.getMemberId(6), 7);
		assertEquals(channel.getMemberId(7), 8);
		assertEquals(channel.getMemberId(8), 9);
		assertEquals(channel.getMemberId(9), 10);
	}
	
	@Test
	public void newChannelBase_checkValues() {
		ChannelBase channelBase = ClientChannels.newChannelBase(99, "Channel99");
		assertEquals(channelBase.getId(), 99);
		assertEquals(channelBase.getName(), "Channel99");
	}
	
	@Test
	public void newChannelMemberPasswordVerification_checkPasswordValue() {
		ChannelMemberVerification verification = ClientChannels.newChannelMemberPasswordVerification("verification_password");
		assertEquals(verification.getPassword(), "verification_password");
	}
	
	@Test
	public void newChannelMemberInvitationKeyVerification_checkInvitationKeyValue() {
		List<String> invitationKeys = new ArrayList<String>();
		invitationKeys.add("invitationKey1");
		invitationKeys.add("invitationKey2");
		invitationKeys.add("invitationKey3");
		ChannelMemberVerification verification = ClientChannels.newChannelMemberInvitationKeyVerification(invitationKeys);
		assertEquals(verification.getInvitationKey(0), "invitationKey1");
		assertEquals(verification.getInvitationKey(1), "invitationKey2");
		assertEquals(verification.getInvitationKey(2), "invitationKey3");
	}
	
	@Test
	public void newPublicTextChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		Channel channel = ClientChannels.newPublicTextChannel(11, "Channel 11", 100, memberIds);
		assertEquals(channel.getBase().getId(), 11);
		assertEquals(channel.getBase().getName(), "Channel 11");
		assertEquals(channel.getMaxMembers(), 100);
		assertEquals(channel.getMemberId(0), 8);
		assertEquals(channel.getMemberId(1), 12);
		assertEquals(channel.getMemberId(2), 16);
	}
	
	@Test
	public void newPasswordSecuredTextChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		Channel channel = ClientChannels.newPasswordSecuredTextChannel(11, "Channel 11", "Channel11Password", 10, memberIds);
		assertEquals(channel.getBase().getId(), 11);
		assertEquals(channel.getBase().getName(), "Channel 11");
		assertEquals(channel.getMemberVerification().getPassword(), "Channel11Password");
		assertEquals(channel.getMaxMembers(), 10);
		assertEquals(channel.getMemberId(0), 8);
		assertEquals(channel.getMemberId(1), 12);
		assertEquals(channel.getMemberId(2), 16);
	}
	
	@Test
	public void newTextChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		Channel channel = ClientChannels.newTextChannel(420, 
														"Channel 420", 
														ChannelRestrictionType.INVITE_ONLY, 
														ChannelMemberVerification.getDefaultInstance(), 
														50, 
														memberIds);
		assertEquals(channel.getBase().getId(), 420);
		assertEquals(channel.getBase().getName(), "Channel 420");
		assertEquals(channel.getRestrictionType(), ChannelRestrictionType.INVITE_ONLY);
		assertEquals(channel.getMemberVerification().getPassword(), "");
		assertTrue(channel.getMemberVerification().getInvitationKeyList().isEmpty());
		assertEquals(channel.getMaxMembers(), 50);
		assertEquals(channel.getMemberId(0), 8);
		assertEquals(channel.getMemberId(1), 12);
		assertEquals(channel.getMemberId(2), 16);
	}
	
	@Test
	public void newPublicVoiceChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(1);
		memberIds.add(2);
		memberIds.add(4);
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		Channel channel = ClientChannels.newPublicVoiceChannel(101, "Public Voice Channel 101", 49, memberIds);
		assertEquals(channel.getBase().getId(), 101);
		assertEquals(channel.getBase().getName(), "Public Voice Channel 101");
		assertEquals(channel.getMaxMembers(), 49);
		assertEquals(channel.getMemberId(0), 1);
		assertEquals(channel.getMemberId(1), 2);
		assertEquals(channel.getMemberId(2), 4);
		assertEquals(channel.getMemberId(3), 8);
		assertEquals(channel.getMemberId(4), 12);
		assertEquals(channel.getMemberId(5), 16);
	}
	
	@Test
	public void newPasswordSecuredVoiceChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		Channel channel = ClientChannels.newPasswordSecuredVoiceChannel(11, "Voice 11", "Voice11Password", 63, memberIds);
		assertEquals(channel.getBase().getId(), 11);
		assertEquals(channel.getBase().getName(), "Voice 11");
		assertEquals(channel.getMemberVerification().getPassword(), "Voice11Password");
		assertEquals(channel.getMaxMembers(), 63);
		assertEquals(channel.getMemberId(0), 8);
		assertEquals(channel.getMemberId(1), 12);
		assertEquals(channel.getMemberId(2), 16);
	}
	
	@Test
	public void newVoiceChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		memberIds.add(20);
		memberIds.add(24);
		memberIds.add(28);
		Channel channel = ClientChannels.newVoiceChannel(	10, 
															"Public Voice Channel", 
															ChannelRestrictionType.PUBLIC, 
															ChannelMemberVerification.getDefaultInstance(), 
															100, 
															memberIds);
		assertEquals(channel.getBase().getId(), 10);
		assertEquals(channel.getBase().getName(), "Public Voice Channel");
		assertEquals(channel.getRestrictionType(), ChannelRestrictionType.PUBLIC);
		assertEquals(channel.getMemberVerification().getPassword(), "");
		assertTrue(channel.getMemberVerification().getInvitationKeyList().isEmpty());
		assertEquals(channel.getMaxMembers(), 100);
		assertEquals(channel.getMemberId(0), 8);
		assertEquals(channel.getMemberId(1), 12);
		assertEquals(channel.getMemberId(2), 16);
		assertEquals(channel.getMemberId(3), 20);
		assertEquals(channel.getMemberId(4), 24);
		assertEquals(channel.getMemberId(5), 28);
	}
}
