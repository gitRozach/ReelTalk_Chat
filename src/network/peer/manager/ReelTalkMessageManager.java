package network.peer.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.PrivateMessage;

public class ReelTalkMessageManager {
	protected Map<Integer, List<ChannelMessage>> channelMessageMap;
	protected Map<Integer, List<PrivateMessage>> privateMessageMap;

	public ReelTalkMessageManager() {
		channelMessageMap = Collections.synchronizedMap(new HashMap<Integer, List<ChannelMessage>>());
		privateMessageMap = Collections.synchronizedMap(new HashMap<Integer, List<PrivateMessage>>());
	}
	
	protected static final Comparator<ChannelMessage> ChannelMessageComparator = new Comparator<ChannelMessage>() {
		@Override
		public int compare(ChannelMessage o1, ChannelMessage o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
	
	protected static final Comparator<PrivateMessage> PrivateMessageComparator = new Comparator<PrivateMessage>() {
		@Override
		public int compare(PrivateMessage o1, PrivateMessage o2) {
			return o1.getMessageBase().getMessageId() - o2.getMessageBase().getMessageId();
		}
	};
	
	public void clear() {
		channelMessageMap.clear();
		privateMessageMap.clear();
	}
	
	public void addChannelMessages(int channelId, Collection<ChannelMessage> messages) {
		if(messages != null)
			messages.forEach(a -> addChannelMessage(channelId, a));
	}
	
	public void addChannelMessage(int channelId, ChannelMessage newMessage) {
		if(newMessage == null)
			return;
		if(!channelMessageMap.containsKey(channelId)) {
			List<ChannelMessage> messageList = new ArrayList<ChannelMessage>();
			messageList.add(newMessage);
			channelMessageMap.put(channelId, messageList);
			return;
		}
		channelMessageMap.get(channelId).add(newMessage);
		channelMessageMap.get(channelId).sort(ChannelMessageComparator);
	}
	
	public void addPrivateMessages(int remoteClientId, Collection<PrivateMessage> messages) {
		if(messages != null)
			messages.forEach(a -> addPrivateMessage(remoteClientId, a));
	}
	
	public void addPrivateMessage(int remoteClientId, PrivateMessage newMessage) {
		if(newMessage == null)
			return;
		if(!privateMessageMap.containsKey(remoteClientId)) {
			List<PrivateMessage> messageList = new ArrayList<PrivateMessage>();
			messageList.add(newMessage);
			privateMessageMap.put(remoteClientId, messageList);
			return;
		}
		privateMessageMap.get(remoteClientId).add(newMessage);
		privateMessageMap.get(remoteClientId).sort(PrivateMessageComparator);
	}
	
	public List<ChannelMessage> getChannelMessages(int channelId) {
		return channelMessageMap.get(channelId);
	}
	
	public List<PrivateMessage> getPrivateMessages(int remoteClientId) {
		return privateMessageMap.get(remoteClientId);
	}
	
	public boolean removeChannelMessageFromChannel(int channelId, int messageId) {
		ChannelMessage messageToRemove = getChannelMessageById(channelId, messageId);
		if(messageToRemove == null)
			return false;
		return channelMessageMap.get(channelId).remove(messageToRemove);
	}
	
	public boolean removePrivateMessageForClient(int remoteClientId, int messageId) {
		PrivateMessage messageToRemove = getPrivateMessageById(remoteClientId, messageId);
		if(messageToRemove == null)
			return false;
		return privateMessageMap.get(remoteClientId).remove(messageToRemove);
	}
	
	public ChannelMessage getChannelMessageById(int channelId, int messageId) {
		if(!channelMessageMap.containsKey(channelId))
			return null;
		for(ChannelMessage currentMessage : channelMessageMap.get(channelId))
			if(currentMessage.getMessageBase().getMessageId() == messageId)
				return currentMessage;
		return null;
	}
	
	public PrivateMessage getPrivateMessageById(int remoteClientId, int messageId) {
		if(!privateMessageMap.containsKey(remoteClientId))
			return null;
		for(PrivateMessage currentMessage : privateMessageMap.get(remoteClientId))
			if(currentMessage.getMessageBase().getMessageId() == messageId)
				return currentMessage;
		return null;
	}
}
