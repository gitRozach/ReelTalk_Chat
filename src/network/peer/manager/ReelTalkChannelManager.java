package network.peer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import protobuf.ClientChannels.Channel;

public class ReelTalkChannelManager {
	protected List<Channel> channelList;

	public ReelTalkChannelManager() {
		channelList = Collections.synchronizedList(new LinkedList<Channel>());
	}
	
	protected static final Comparator<Channel> ClientChannelComparator = new Comparator<Channel>() {
		@Override
		public int compare(Channel o1, Channel o2) {
			return o1.getBase().getId() - o2.getBase().getId();
		}
	};
	
	public void clear() {
		channelList.clear();
	}
	
	public void addChannels(Collection<Channel> channels) {
		if(channels != null)
			for(Channel channel : channels)
				addChannel(channel);
	}
	
	public void addChannel(Channel newChannel) {
		if(newChannel == null || channelList.contains(newChannel))
			return;
		channelList.add(newChannel);
		channelList.sort(ClientChannelComparator);
	}
	
	public boolean removeChannelById(int id) {
		return removeChannel(getChannelById(id));
	}
	
	public boolean removeChannel(Channel channel) {
		return removeChannel(channelList.indexOf(channel)) != null;
	}
	
	public Channel removeChannel(int index) {
		if(index < 0 || index >= channelList.size())
			return null;
		Channel channelToRemove = channelList.get(index);
		channelList.remove(index);
		return channelToRemove;
	}
	
	public Channel getChannel(int index) {
		if(index < 0 || index >= channelList.size())
			return null;
		return channelList.get(index);
	}
	
	public Channel getChannelById(int channelId) {
		for(Channel currentChannel : channelList)
			if(currentChannel.getBase().getId() == channelId)
				return currentChannel;
		return null;
	}
	
	public int getIndexOfChannel(Channel channel) {
		if(channel == null)
			return -1;
		return channelList.indexOf(channel);
	}
	
	/*
	 * 
	 */
	
	public void addClientsToChannel(int channelId, Collection<Integer> clientIds) {
		if(clientIds != null)
			for(int clientId : clientIds)
				addClientToChannel(channelId, clientId);
	}
	
	public void addClientToChannel(int channelId, int clientId) {
		Channel channel = getChannelById(channelId);
		if(channel == null)
			return;
		int channelIndex = channelList.indexOf(channel);
		Channel newChannel = channel.toBuilder().addAllMemberId(List.of(clientId)).build();
		channelList.set(channelIndex, newChannel);
	}
	
	public boolean removeClientFromChannel(int clientId, int channelId) {
		Channel channel = getChannelById(channelId);
		if(channel == null || !channelContainsClient(channelId, clientId))
			return false;
		int channelIndex = channelList.indexOf(channel);

		List<Integer> memberList = new ArrayList<Integer>(channel.getMemberIdList());
		memberList.remove(memberList.indexOf(clientId));
				
		Channel newChannel = channel.toBuilder().addAllMemberId(memberList).build();
		channelList.set(channelIndex, newChannel);
		return true;
	}
	
	public List<Integer> getMemberIdsOf(int channelId) {
		Channel channel = getChannelById(channelId);
		return channel == null ? null : channel.getMemberIdList();
	}
	
	public List<Channel> getChannels() {
		return channelList;
	}
	
	public int getChannelIdOfClient(int id) {
		int resultId = -1;
		for(Channel currentChannel : channelList)
			if(currentChannel.getMemberIdList().contains(id))
				return currentChannel.getBase().getId();
		return resultId;
	}
	
	public boolean isClientChannelMember(int id) {
		return getChannelIdOfClient(id) != -1;
	}
	
	public boolean isClientChannelMemberOf(int clientId, int channelId) {
		return getChannelIdOfClient(clientId) == channelId;
	}
	
	public boolean channelContainsClient(int channelId, int clientId) {
		Channel channel = getChannelById(channelId);
		return channel != null && channel.getMemberIdList().contains(clientId);
	}
	
	public boolean containsClient(int id) {
		return isClientChannelMember(id);
	}
}
