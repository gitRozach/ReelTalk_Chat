package protobuf.wrapper;

import java.util.Arrays;
import java.util.Collection;

import com.google.protobuf.GeneratedMessageV3;

import protobuf.ClientChannels.Channel;
import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientChannels.ChannelCommunicationType;
import protobuf.ClientChannels.ChannelMemberVerification;
import protobuf.ClientChannels.ChannelRestrictionType;

public class ClientChannels {
	
	public static String[] getRegisteredTypeNames() {
		return new String[] {"Channel"};
	}
	
	public static boolean isChannel(Class<? extends GeneratedMessageV3> messageClass) {
		if(messageClass == null)
			return false;
		for(String registeredTypeName : getRegisteredTypeNames())
			if(registeredTypeName.equalsIgnoreCase(messageClass.getSimpleName()))
				return true;
		return false;
	}
	
	public static Channel addMemberIdToChannel(Channel channel, Integer id) {
		return addMemberIdsToChannel(channel, Arrays.asList(id));
	}
	
	public static Channel addMemberIdsToChannel(Channel channel, Collection<Integer> memberIds) {
		return channel.toBuilder().addAllMemberId(memberIds).build();
	}
	
	public static ChannelBase newChannelBase(int channelId, String channelName) {
		return ChannelBase.newBuilder().setId(channelId).setName(channelName).build();
	}
	
	public static ChannelMemberVerification newChannelMemberPasswordVerification(String password) {
		return ChannelMemberVerification.newBuilder().setPassword(password).build();
	}
	
	public static ChannelMemberVerification newChannelMemberInvitationKeyVerification(Collection<String> invitationKeys) {
		return ChannelMemberVerification.newBuilder().addAllInvitationKey(invitationKeys).build();
	}
	
	public static Channel newPublicTextChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PUBLIC, 
								ChannelMemberVerification.getDefaultInstance(), 
								maxMembers, 
								memberIds);
	}
	
	public static Channel newPasswordSecuredTextChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newTextChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password),
								maxMembers, 
								memberIds);
	}
	
	public static Channel newTextChannel(	int id,
											String name,
											ChannelRestrictionType type, 
											ChannelMemberVerification verification, 
											int maxMembers,
											Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		return Channel.newBuilder()	.setBase(base)
									.setMaxMembers(maxMembers)
									.addAllMemberId(memberIds)
									.setCommunicationType(ChannelCommunicationType.TEXT)
									.setRestrictionType(type)
									.setMemberVerification(verification)
									.build();
	}
	
	public static Channel newPublicVoiceChannel(int id, String name, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(id, name, ChannelRestrictionType.PUBLIC, ChannelMemberVerification.getDefaultInstance(), maxMembers, memberIds);
	}
	
	public static Channel newPasswordSecuredVoiceChannel(int id, String name, String password, int maxMembers, Collection<Integer> memberIds) {
		return newVoiceChannel(	id, 
								name, 
								ChannelRestrictionType.PASSWORD_REQUIRED, 
								newChannelMemberPasswordVerification(password), 
								maxMembers, 
								memberIds);	
	}
	
	public static Channel newVoiceChannel(	int id,
											String name,
											ChannelRestrictionType type,
											ChannelMemberVerification verification,
											int maxMembers,
											Collection<Integer> memberIds) {
		ChannelBase base = newChannelBase(id, name);
		return Channel.newBuilder()	.setBase(base)
									.setMaxMembers(maxMembers)
									.addAllMemberId(memberIds)
									.setCommunicationType(ChannelCommunicationType.VOICE)
									.setRestrictionType(type)
									.setMemberVerification(verification)
									.build();
	}
}
