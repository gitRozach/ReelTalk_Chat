package network.peer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import protobuf.ClientIdentities.ClientProfile;
import protobuf.wrapper.ClientIdentities;

public class ReelTalkClientProfileManager {
	protected List<ClientProfile> onlineList;
	protected List<ClientProfile> offlineList;
	
	public ReelTalkClientProfileManager() {
		onlineList = Collections.synchronizedList(new LinkedList<ClientProfile>());
		offlineList = Collections.synchronizedList(new LinkedList<ClientProfile>());
	}
	
	protected static final Comparator<ClientProfile> ClientProfileComparator = new Comparator<ClientProfile>() {
		@Override
		public int compare(ClientProfile o1, ClientProfile o2) {
			return o1.getBase().getId() - o2.getBase().getId();
		}
	};
	
	public void clear() {
		onlineList.clear();
		offlineList.clear();
	}
	
	public boolean addClient(ClientProfile profile) {
		if(profile == null)
			return false;
		return ClientIdentities.isClientOnline(profile.getStatus()) ? addClientToOnlineList(profile) : addClientToOfflineList(profile);
	}
	
	public int addClients(Collection<ClientProfile> profiles) {
		if(profiles == null)
			return -1;
		int counter = 0;
		for(ClientProfile currentProfile : profiles)
			if(addClient(currentProfile))
				++counter;
		return counter;
	}
	
	public boolean removeClient(ClientProfile clientProfile) {
		boolean removeOnline = removeClientFromOnlineList(clientProfile);
		boolean removeOffline = removeClientFromOfflineList(clientProfile);
		return  removeOnline || removeOffline;
	}
	
	public boolean addClientToOnlineList(ClientProfile clientProfile) {
		if(clientProfile == null)
			return false;
		removeClientFromOnlineList(clientProfile);
		if(!onlineList.contains(clientProfile)) {
			onlineList.add(clientProfile);
			onlineList.sort(ClientProfileComparator);
			return true;
		}
		return false;
	}
	
	public boolean removeClientFromOnlineListById(int id) {
		return removeClientFromOnlineList(getClientProfileById(id));
	}
	
	public boolean removeClientFromOnlineList(ClientProfile clientProfile) {
		return removeClientFromOnlineList(onlineList.indexOf(clientProfile)) != null;
	}
	
	public ClientProfile removeClientFromOnlineList(int index) {
		if(index < 0 ||index >= onlineList.size())
			return null;
		return onlineList.remove(index);
	}
	
	public boolean addClientToOfflineList(ClientProfile clientProfile) {
		if(clientProfile == null)
			return false;
		removeClientFromOfflineList(clientProfile);
		if(!offlineList.contains(clientProfile)) {
			offlineList.add(clientProfile);
			offlineList.sort(ClientProfileComparator);
			return true;
		}
		return false;
	}
	
	public boolean removeClientFromOfflineListById(int id) {
		return removeClientFromOfflineList(getClientProfileById(id));
	}
	
	public boolean removeClientFromOfflineList(ClientProfile clientProfile) {
		return removeClientFromOfflineList(offlineList.indexOf(clientProfile)) != null;
	}
	
	public ClientProfile removeClientFromOfflineList(int index) {
		if(index < 0 || index >= offlineList.size())
			return null;
		return offlineList.remove(index);
	}
	
	public ClientProfile getClientProfileById(int id) {
		for(ClientProfile currentProfile : onlineList) {
			if(currentProfile.getBase().getId() == id)
				return currentProfile;
		}
		for(ClientProfile currentProfile : offlineList) {
			if(currentProfile.getBase().getId() == id)
				return currentProfile;
		}
		return null;
	}
	
	public List<ClientProfile> getOnlineMembers() {
		return onlineList;
	}
	
	public List<ClientProfile> getOfflineMembers() {
		return offlineList;
	}
	
	public List<ClientProfile> getAllMembers() {
		return getAllMembers(true);
	}
	
	public List<ClientProfile> getAllMembers(boolean sort) {
		List<ClientProfile> allMembers = new ArrayList<ClientProfile>();
		allMembers.addAll(onlineList);
		allMembers.addAll(offlineList);
		if(sort)
			allMembers.sort(ClientProfileComparator);
		return allMembers;
	}
	
	public boolean isClientOnline(int clientId) {
		return isClientOnline(getClientProfileById(clientId));
	}
	
	public boolean isClientOnline(ClientProfile clientProfile) {
		if(clientProfile == null)
			return false;
		return onlineList.contains(clientProfile);
	}
	
	public boolean isClientOffline(int clientId) {
		return isClientOffline(getClientProfileById(clientId));
	}
	
	public boolean isClientOffline(ClientProfile clientProfile) {
		if(clientProfile == null)
			return false;
		return offlineList.contains(clientProfile);
	}
	
	public int getOnlineMembersCount() {
		return onlineList.size();
	}
	
	public int getOfflineMembersCount() {
		return offlineList.size();
	}
	
	public int getAllMembersCount() {
		return onlineList.size() + offlineList.size();
	}
}
