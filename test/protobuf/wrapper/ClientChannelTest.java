package protobuf.wrapper;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientChannels.ChannelRestrictionType;
import protobuf.ClientChannels.TextChannel;
import protobuf.ClientChannels.VoiceChannel;

class ClientChannelTest {
	
	@Test
	public void newChannelBase_checkValues() {
		ChannelBase channelBase = ClientChannel.newChannelBase(99, "Channel99");
		assertEquals(channelBase.getChannelId(), 99);
		assertEquals(channelBase.getChannelName(), "Channel99");
	}
	
	@Test
	public void newChannelMemberPasswordVerification_checkPasswordValue() {
		ChannelMemberVerification verification = ClientChannel.newChannelMemberPasswordVerification("verification_password");
		assertEquals(verification.getChannelPassword(), "verification_password");
	}
	
	@Test
	public void newChannelMemberInvitationKeyVerification_checkInvitationKeyValue() {
		List<String> invitationKeys = new ArrayList<String>();
		invitationKeys.add("invitationKey1");
		invitationKeys.add("invitationKey2");
		invitationKeys.add("invitationKey3");
		ChannelMemberVerification verification = ClientChannel.newChannelMemberInvitationKeyVerification(invitationKeys);
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
		TextChannel channel = ClientChannel.newPublicTextChannel(11, "Channel 11", 100, memberIds);
		assertEquals(channel.getBase().getChannelId(), 11);
		assertEquals(channel.getBase().getChannelName(), "Channel 11");
		assertEquals(channel.getMembers().getMaxMembers(), 100);
		assertEquals(channel.getMembers().getMemberId(0), 8);
		assertEquals(channel.getMembers().getMemberId(1), 12);
		assertEquals(channel.getMembers().getMemberId(2), 16);
	}
	
	@Test
	public void newPasswordSecuredTextChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		TextChannel channel = ClientChannel.newPasswordSecuredTextChannel(11, "Channel 11", "Channel11Password", 10, memberIds);
		assertEquals(channel.getBase().getChannelId(), 11);
		assertEquals(channel.getBase().getChannelName(), "Channel 11");
		assertEquals(channel.getMemberVerification().getChannelPassword(), "Channel11Password");
		assertEquals(channel.getMembers().getMaxMembers(), 10);
		assertEquals(channel.getMembers().getMemberId(0), 8);
		assertEquals(channel.getMembers().getMemberId(1), 12);
		assertEquals(channel.getMembers().getMemberId(2), 16);
	}
	
	@Test
	public void newTextChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		TextChannel channel = ClientChannel.newTextChannel(	420, 
															"Channel 420", 
															ChannelRestrictionType.INVITE_ONLY, 
															ChannelMemberVerification.getDefaultInstance(), 
															50, 
															memberIds);
		assertEquals(channel.getBase().getChannelId(), 420);
		assertEquals(channel.getBase().getChannelName(), "Channel 420");
		assertEquals(channel.getRestrictionType(), ChannelRestrictionType.INVITE_ONLY);
		assertEquals(channel.getMemberVerification().getChannelPassword(), "");
		assertTrue(channel.getMemberVerification().getInvitationKeyList().isEmpty());
		assertEquals(channel.getMembers().getMaxMembers(), 50);
		assertEquals(channel.getMembers().getMemberId(0), 8);
		assertEquals(channel.getMembers().getMemberId(1), 12);
		assertEquals(channel.getMembers().getMemberId(2), 16);
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
		VoiceChannel channel = ClientChannel.newPublicVoiceChannel(101, "Public Voice Channel 101", 49, memberIds);
		assertEquals(channel.getBase().getChannelId(), 101);
		assertEquals(channel.getBase().getChannelName(), "Public Voice Channel 101");
		assertEquals(channel.getMembers().getMaxMembers(), 49);
		assertEquals(channel.getMembers().getMemberId(0), 1);
		assertEquals(channel.getMembers().getMemberId(1), 2);
		assertEquals(channel.getMembers().getMemberId(2), 4);
		assertEquals(channel.getMembers().getMemberId(3), 8);
		assertEquals(channel.getMembers().getMemberId(4), 12);
		assertEquals(channel.getMembers().getMemberId(5), 16);
	}
	
	@Test
	public void newPasswordSecuredVoiceChannel_checkValues() {
		List<Integer> memberIds = new ArrayList<Integer>();
		memberIds.add(8);
		memberIds.add(12);
		memberIds.add(16);
		VoiceChannel channel = ClientChannel.newPasswordSecuredVoiceChannel(11, "Voice 11", "Voice11Password", 63, memberIds);
		assertEquals(channel.getBase().getChannelId(), 11);
		assertEquals(channel.getBase().getChannelName(), "Voice 11");
		assertEquals(channel.getMemberVerification().getChannelPassword(), "Voice11Password");
		assertEquals(channel.getMembers().getMaxMembers(), 63);
		assertEquals(channel.getMembers().getMemberId(0), 8);
		assertEquals(channel.getMembers().getMemberId(1), 12);
		assertEquals(channel.getMembers().getMemberId(2), 16);
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
		VoiceChannel channel = ClientChannel.newVoiceChannel(	10, 
																"Public Voice Channel", 
																ChannelRestrictionType.PUBLIC, 
																ChannelMemberVerification.getDefaultInstance(), 
																100, 
																memberIds);
		assertEquals(channel.getBase().getChannelId(), 10);
		assertEquals(channel.getBase().getChannelName(), "Public Voice Channel");
		assertEquals(channel.getRestrictionType(), ChannelRestrictionType.PUBLIC);
		assertEquals(channel.getMemberVerification().getChannelPassword(), "");
		assertTrue(channel.getMemberVerification().getInvitationKeyList().isEmpty());
		assertEquals(channel.getMembers().getMaxMembers(), 100);
		assertEquals(channel.getMembers().getMemberId(0), 8);
		assertEquals(channel.getMembers().getMemberId(1), 12);
		assertEquals(channel.getMembers().getMemberId(2), 16);
		assertEquals(channel.getMembers().getMemberId(3), 20);
		assertEquals(channel.getMembers().getMemberId(4), 24);
		assertEquals(channel.getMembers().getMemberId(5), 28);
	}
}