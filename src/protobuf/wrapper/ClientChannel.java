package protobuf.wrapper;

import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientChannels.ChannelMembers;
import protobuf.ClientChannels.ChannelRestrictionType;
import protobuf.ClientChannels.TextChannel;
import protobuf.ClientChannels.VoiceChannel;

public class ClientChannel {
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {"TextChannel", "VoiceChannel"};
	}
	
	public static boolean isClientChannel(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static ChannelBase newChannelBase(int channelId, String channelName) {
		return ChannelBase.newBuilder().setChannelId(channelId).setChannelName(channelName).build();
	}
	
	public static ChannelMemberVerification newChannelMemberPasswordVerification(String password) {
		return ChannelMemberVerification.newBuilder().setChannelPassword(password).build();
	}
	
	public static ChannelMemberVerification newChannelMemberInvitationKeyVerification(Collection<String> invitationKeys) {
		return ChannelMemberVerification.newBuilder().addAllInvitationKey(invitationKeys).build();
	}
	
	public static TextChannel newPublicTextChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PUBLIC, 
								ChannelMemberVerification.getDefaultInstance(), 
								maxMembers, 
								memberIds);
	}
	
	public static TextChannel newPasswordSecuredTextChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password),
								maxMembers, 
								memberIds);
	}
	
	public static TextChannel newTextChannel(	int id,
												String name,
												ChannelRestrictionType type, 
												ChannelMemberVerification verification, 
												int maxMembers,
												Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		ChannelMembers members = ChannelMembers.newBuilder().setMaxMembers(maxMembers).addAllMemberId(memberIds).build();
		return TextChannel.newBuilder()	.setBase(base)
										.setMembers(members)
										.setRestrictionType(type)
										.setMemberVerification(verification)
										.build();
	}
	
	public static VoiceChannel newPublicVoiceChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(id, name, ChannelRestrictionType.PUBLIC, ChannelMemberVerification.getDefaultInstance(), maxMembers, memberIds);
	}
	
	public static VoiceChannel newPasswordSecuredVoiceChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password), 
								maxMembers, 
								memberIds);	
	}
	
	public static VoiceChannel newVoiceChannel(	int id,
												String name,
												ChannelRestrictionType type,
												ChannelMemberVerification verification,
												int maxMembers,
												Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		ChannelMembers members = ChannelMembers.newBuilder().setMaxMembers(maxMembers).addAllMemberId(memberIds).build();
		return VoiceChannel.newBuilder()	.setBase(base)
											.setMembers(members)
											.setRestrictionType(type)
											.setMemberVerification(verification)
											.build();
	}
}
