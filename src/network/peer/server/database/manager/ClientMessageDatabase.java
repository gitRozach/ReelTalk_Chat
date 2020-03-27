package network.peer.server.database.manager;

import java.io.IOException;
import java.util.ArrayList;

import network.peer.server.database.protobuf.ChannelMessageDatabase;
import network.peer.server.database.protobuf.PrivateMessageDatabase;
import network.peer.server.database.protobuf.ProfileCommentDatabase;
import protobuf.ClientMessages.ChannelMessage;
import protobuf.ClientMessages.ClientProfileComment;
import protobuf.ClientMessages.PrivateMessage;

public class ClientMessageDatabase {
	private PrivateMessageDatabase privateMessageManager;
	private ChannelMessageDatabase channelMessageManager;
	private ProfileCommentDatabase profileCommentManager;
		
	public ClientMessageDatabase() throws IOException {
		initialize();
	}
	
	public void initialize() throws IOException {
		privateMessageManager = new PrivateMessageDatabase();
		privateMessageManager.loadFileItems("src/clientData/messages/privateMessages.txt", true);
		channelMessageManager = new ChannelMessageDatabase();
		channelMessageManager.loadFileItems("src/clientData/messages/channelMessages.txt", true);
		profileCommentManager = new ProfileCommentDatabase();
		profileCommentManager.loadFileItems("src/clientData/messages/profileComments.txt", true);
	}
	
	public void addPrivateMessage(PrivateMessage message) {
		privateMessageManager.addItem(message);
	}
	
	public PrivateMessage getPrivateMessage(int index) {
		return privateMessageManager.getItem(index);
	}
	
	public PrivateMessage[] getPrivateMessages() {
		if(privateMessageManager.isEmpty())
			return new PrivateMessage[]{};
		return getPrivateMessages(0, privateMessageManager.size());
	}
	
	public PrivateMessage[] getPrivateMessages(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<PrivateMessage> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(privateMessageManager.getItem(i));
		}
		return resultList.toArray(new PrivateMessage[resultList.size()]);
	}
	
	public void addChannelMessage(ChannelMessage message) {
		channelMessageManager.addItem(message);
	}
	
	public ChannelMessage getChannelMessage(int index) {
		return channelMessageManager.getItem(index);
	}
	
	public ChannelMessage[] getChannelMessages() {
		if(channelMessageManager.isEmpty())
			return new ChannelMessage[]{};
		return getChannelMessages(0, channelMessageManager.size());
	}
	
	public ChannelMessage[] getChannelMessages(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<ChannelMessage> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(channelMessageManager.getItem(i));
		}
		return resultList.toArray(new ChannelMessage[resultList.size()]);
	}
	
	public void addProfileComment(ClientProfileComment message) {
		profileCommentManager.addItem(message);
	}
	
	public ClientProfileComment getProfileComment(int index) {
		return profileCommentManager.getItem(index);
	}
	
	public ClientProfileComment[] getProfileComments() {
		if(profileCommentManager.isEmpty())
			return new ClientProfileComment[]{};
		return getProfileComments(0, profileCommentManager.size());
	}
	
	public ClientProfileComment[] getProfileComments(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<ClientProfileComment> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(profileCommentManager.getItem(i));
		}
		return resultList.toArray(new ClientProfileComment[resultList.size()]);
	}

	public PrivateMessageDatabase getPrivateMessageManager() {
		return privateMessageManager;
	}

	public ChannelMessageDatabase getChannelMessageManager() {
		return channelMessageManager;
	}
	
	public ProfileCommentDatabase getProfileCommentManager() {
		return profileCommentManager;
	}
}
