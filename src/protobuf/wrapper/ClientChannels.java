package protobuf.wrapper;

import java.util.Arrays;
import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientChannels.ChannelMembers;
import protobuf.ClientChannels.ChannelRestrictionType;
import protobuf.ClientChannels.ClientChannel;

public class ClientChannels {
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {"ClientChannel"};
	}
	
	public static boolean isClientChannel(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static ClientChannel addMemberIdToChannel(ClientChannel channel, Integer id) {
		return addMemberIdsToChannel(channel, Arrays.asList(id));
	}
	
	public static ClientChannel addMemberIdsToChannel(ClientChannel channel, Collection<Integer> memberIds) {
		ChannelMembers members = channel.getMembers().toBuilder().addAllMemberId(memberIds).build();
		return channel.toBuilder().setMembers(members).build();
	}
	
	public static ChannelBase newChannelBase(int channelId, String channelName) {
		return ChannelBase.newBuilder().setChannelId(channelId).setChannelName(channelName).build();
	}
	
	public static ChannelMemberVerification newChannelMemberPasswordVerification(String password) {
		return ChannelMemberVerification.newBuilder().setPassword(password).build();
	}
	
	public static ChannelMemberVerification newChannelMemberInvitationKeyVerification(Collection<String> invitationKeys) {
		return ChannelMemberVerification.newBuilder().addAllInvitationKey(invitationKeys).build();
	}
	
	public static ClientChannel newPublicTextChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PUBLIC, 
								ChannelMemberVerification.getDefaultInstance(), 
								maxMembers, 
								memberIds);
	}
	
	public static ClientChannel newPasswordSecuredTextChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password),
								maxMembers, 
								memberIds);
	}
	
	public static ClientChannel newTextChannel(	int id,
												String name,
												ChannelRestrictionType type, 
												ChannelMemberVerification verification, 
												int maxMembers,
												Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		ChannelMembers members = ChannelMembers.newBuilder().setMaxMembers(maxMembers).addAllMemberId(memberIds).build();
		return ClientChannel.newBuilder()	.setBase(base)
										.setMembers(members)
										.setRestrictionType(type)
										.setMemberVerification(verification)
										.build();
	}
	
	public static ClientChannel newPublicVoiceChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(id, name, ChannelRestrictionType.PUBLIC, ChannelMemberVerification.getDefaultInstance(), maxMembers, memberIds);
	}
	
	public static ClientChannel newPasswordSecuredVoiceChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password), 
								maxMembers, 
								memberIds);	
	}
	
	public static ClientChannel newVoiceChannel(int id,
												String name,
												ChannelRestrictionType type,
												ChannelMemberVerification verification,
												int maxMembers,
												Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		ChannelMembers members = ChannelMembers.newBuilder().setMaxMembers(maxMembers).addAllMemberId(memberIds).build();
		return ClientChannel.newBuilder()	.setBase(base)
											.setMembers(members)
											.setRestrictionType(type)
											.setMemberVerification(verification)
											.build();
	}
}
