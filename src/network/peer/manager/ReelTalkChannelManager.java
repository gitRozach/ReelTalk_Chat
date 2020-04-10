package network.peer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import protobuf.ClientChannels.ClientChannel;
import protobuf.ClientIdentities.ClientProfile;

public class ReelTalkChannelManager {
	protected List<ClientChannel> channelList;
	protected Map<Integer, List<ClientProfile>> channelMap;

	public ReelTalkChannelManager() {
		channelList = Collections.synchronizedList(new LinkedList<ClientChannel>());
		channelMap = Collections.synchronizedMap(new HashMap<Integer, List<ClientProfile>>());
	}
	
	protected static final Comparator<ClientProfile> ClientProfileComparator = new Comparator<ClientProfile>() {
		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			return o1.getBase().getId() - o2.getBase().getId();
		}
	};
	
	protected static final Comparator<ClientChannel> ClientChannelComparator = new Comparator<ClientChannel>() {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return o1.getBase().getChannelId() - o2.getBase().getChannelId();
		}
	};
	
	public void clear() {
		channelList.clear();
		channelMap.clear();
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
	
	public void addClientsToChannel(int channelId, Collection<ClientProfile> clientProfiles) {
		if(clientProfiles != null)
			clientProfiles.forEach(a -> addClientToChannel(channelId, a));
	}
	
	public void addClientToChannel(int channelId, ClientProfile clientProfile) {
		if(clientProfile == null)
			return;
		if(!channelMap.containsKey(channelId)) {
			List<ClientProfile> memberList = new ArrayList<>();
			memberList.add(clientProfile);
			channelMap.put(channelId, memberList);
			return;
		}
		channelMap.get(channelId).add(0, clientProfile);
		channelMap.get(channelId).sort(ClientProfileComparator);
	}
	
	public boolean removeClientFromChannel(int clientId, int channelId) {
		return removeClientFromChannel(getClientProfileById(clientId), channelId);
	}
	
	public boolean removeClientFromChannel(ClientProfile clientProfile, int channelId) {
		if(clientProfile == null || !channelMap.containsKey(channelId))
			return false;
		return channelMap.get(channelId).remove(clientProfile);
	}
	
	public List<ClientProfile> getChannelMembersOf(int channelId) {
		if(!channelMap.containsKey(channelId))
			return null;
		return channelMap.get(channelId);
	}
	
	public List<ClientChannel> getChannels() {
		return channelList;
	}
	
	public ClientProfile getClientProfileById(int clientId)	{
		for(Entry<Integer, List<ClientProfile>> currentEntry : channelMap.entrySet()) {
			for(ClientProfile currentProfile : currentEntry.getValue())
				if(currentProfile.getBase().getId() == clientId)
					return currentProfile;
		}
		return null;
	}
	
	public int getChannelIdOfClient(int id) {
		return getChannelIdOfClient(getClientProfileById(id));
	}
	
	public int getChannelIdOfClient(ClientProfile clientProfile) {
		if(clientProfile == null)
			return -1;
		for(Entry<Integer, List<ClientProfile>> currentEntry : channelMap.entrySet())
			if(currentEntry.getValue().contains(clientProfile))
				return currentEntry.getKey();
		return -1;
	}
	
	public boolean isClientChannelMember(int id) {
		return isClientChannelMember(getClientProfileById(id));
	}
	
	public boolean isClientChannelMember(ClientProfile clientProfile) {
		return getChannelIdOfClient(clientProfile) != -1;
	}
	
	public boolean isClientChannelMemberOf(int clientId, int channelId) {
		return isClientChannelMemberOf(getClientProfileById(clientId), channelId);
	}
	
	public boolean isClientChannelMemberOf(ClientProfile clientProfile, int channelId) {
		return getChannelIdOfClient(clientProfile) == channelId;
	}
	
	public boolean channelContainsClient(int channelId, int clientId) {
		return channelContainsClient(channelId, getClientProfileById(clientId));
	}
	
	public boolean channelContainsClient(int channelId, ClientProfile clientProfile) {
		if(clientProfile == null)
			return false;
		return channelMap.containsKey(channelId) && channelMap.get(channelId).contains(clientProfile);
	}
}
