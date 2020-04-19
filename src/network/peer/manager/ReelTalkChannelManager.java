package network.peer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import protobuf.ClientChannels.ChannelMembers;
import protobuf.ClientChannels.ClientChannel;

public class ReelTalkChannelManager {
	protected List<ClientChannel> channelList;

	public ReelTalkChannelManager() {
		channelList = Collections.synchronizedList(new LinkedList<ClientChannel>());
	}
	
	protected static final Comparator<ClientChannel> ClientChannelComparator = new Comparator<ClientChannel>() {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return o1.getBase().getChannelId() - o2.getBase().getChannelId();
		}
	};
	
	public void clear() {
		channelList.clear();
	}
	
	public void addChannels(Collection<ClientChannel> channels) {
		if(channels != null)
			channels.forEach(a -> addChannel(a));
	}
	
	public void addChannel(ClientChannel newChannel) {
		if(newChannel == null || channelList.contains(newChannel))
			return;
		channelList.add(newChannel);
		channelList.sort(ClientChannelComparator);
	}
	
	public boolean removeChannelById(int id) {
		return removeChannel(getChannelById(id));
	}
	
	public boolean removeChannel(ClientChannel channel) {
		return removeChannel(channelList.indexOf(channel)) != null;
	}
	
	public ClientChannel removeChannel(int index) {
		if(index < 0 || index >= channelList.size())
			return null;
		return channelList.get(index);
	}
	
	public ClientChannel getChannel(int index) {
		if(index < 0 || index >= channelList.size())
			return null;
		return channelList.get(index);
	}
	
	public ClientChannel getChannelById(int channelId) {
		for(ClientChannel currentChannel : channelList)
			if(currentChannel.getBase().getChannelId() == channelId)
				return currentChannel;
		return null;
	}
	
	public int getIndexOfChannel(ClientChannel channel) {
		if(channel == null)
			return -1;
		return channelList.indexOf(channel);
	}
	
	/*
	 * 
	 */
	
	public void addClientsToChannel(int channelId, Collection<Integer> clientProfiles) {
		if(clientProfiles != null)
			clientProfiles.forEach(a -> addClientToChannel(channelId, a));
	}
	
	public void addClientToChannel(int channelId, int clientId) {
		ClientChannel channel = getChannelById(channelId);
		if(channel == null)
			return;
		int channelIndex = channelList.indexOf(channel);
		ChannelMembers newMembers = channel.getMembers().toBuilder().addAllMemberId(List.of(clientId)).build();
		ClientChannel newChannel = channel.toBuilder().setMembers(newMembers).build();
		channelList.set(channelIndex, newChannel);
	}
	
	public boolean removeClientFromChannel(int clientId, int channelId) {
		ClientChannel channel = getChannelById(channelId);
		if(channel == null || !channelContainsClient(channelId, clientId))
			return false;
		int channelIndex = channelList.indexOf(channel);

		List<Integer> memberList = new ArrayList<Integer>(channel.getMembers().getMemberIdList());
		memberList.remove(memberList.indexOf(clientId));
		ChannelMembers newMembers = ChannelMembers.newBuilder().addAllMemberId(memberList).build();
				
		ClientChannel newChannel = channel.toBuilder().setMembers(newMembers).build();
		channelList.set(channelIndex, newChannel);
		return true;
	}
	
	public List<Integer> getChannelMembersOf(int channelId) {
		ClientChannel channel = getChannelById(channelId);
		if(channel == null)
			return null;
		return List.copyOf(channel.getMembers().getMemberIdList());
	}
	
	public List<ClientChannel> getChannels() {
		return channelList;
	}
	
	public int getChannelIdOfClient(int id) {
		int resultId = -1;
		for(ClientChannel currentChannel : channelList)
			if(currentChannel.getMembers().getMemberIdList().contains(id))
				return currentChannel.getBase().getChannelId();
		return resultId;
	}
	
	public boolean isClientChannelMember(int id) {
		return getChannelIdOfClient(id) != -1;
	}
	
	public boolean isClientChannelMemberOf(int clientId, int channelId) {
		return getChannelIdOfClient(clientId) == channelId;
	}
	
	public boolean channelContainsClient(int channelId, int clientId) {
		ClientChannel channel = getChannelById(channelId);
		if(channel == null)
			return false;
		return channel.getMembers().getMemberIdList().contains(clientId);
	}
}
