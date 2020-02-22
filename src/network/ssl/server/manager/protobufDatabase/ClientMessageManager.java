package network.ssl.server.manager.protobufDatabase;

import java.io.IOException;
import java.util.ArrayList;

import protobuf.ClientMessages.ChannelClientMessage;
import protobuf.ClientMessages.PrivateClientMessage;
import protobuf.ClientMessages.ProfileComment;

public class ClientMessageManager {
	private PrivateMessageManager privateMessageManager;
	private ChannelMessageManager channelMessageManager;
	private ProfileCommentManager profileCommentManager;
	
	private String privateMessageManagerPath;
	private String channelMessageManagerPath;
	private String profileCommentManagerPath;
	
	public ClientMessageManager() throws IOException {
		privateMessageManagerPath = "";
		channelMessageManagerPath = "";
		profileCommentManagerPath = "";
	}
	
	public void initialize() throws IOException {
		privateMessageManager = new PrivateMessageManager(privateMessageManagerPath);
		channelMessageManager = new ChannelMessageManager(channelMessageManagerPath);
		profileCommentManager = new ProfileCommentManager(profileCommentManagerPath);
		privateMessageManager.initialize();
		channelMessageManager.initialize();
		profileCommentManager.initialize();
		System.out.println(channelMessageManager.getEncoding().toString());
	}
	
	public void addPrivateMessage(PrivateClientMessage message) {
		privateMessageManager.addItem(message);
	}
	
	public PrivateClientMessage getPrivateMessage(int index) {
		return privateMessageManager.getItem(index);
	}
	
	public PrivateClientMessage[] getPrivateMessages() {
		if(privateMessageManager.isEmpty())
			return new PrivateClientMessage[]{};
		return getPrivateMessages(0, privateMessageManager.size());
	}
	
	public PrivateClientMessage[] getPrivateMessages(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<PrivateClientMessage> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(privateMessageManager.getItem(i));
		}
		return resultList.toArray(new PrivateClientMessage[resultList.size()]);
	}
	
	public void addChannelMessage(ChannelClientMessage message) {
		channelMessageManager.addItem(message);
	}
	
	public ChannelClientMessage getChannelMessage(int index) {
		return channelMessageManager.getItem(index);
	}
	
	public ChannelClientMessage[] getChannelMessages() {
		if(channelMessageManager.isEmpty())
			return new ChannelClientMessage[]{};
		return getChannelMessages(0, channelMessageManager.size());
	}
	
	public ChannelClientMessage[] getChannelMessages(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<ChannelClientMessage> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(channelMessageManager.getItem(i));
		}
		return resultList.toArray(new ChannelClientMessage[resultList.size()]);
	}
	
	public void addProfileComment(ProfileComment message) {
		profileCommentManager.addItem(message);
	}
	
	public ProfileComment getProfileComment(int index) {
		return profileCommentManager.getItem(index);
	}
	
	public ProfileComment[] getProfileComments() {
		if(profileCommentManager.isEmpty())
			return new ProfileComment[]{};
		return getProfileComments(0, profileCommentManager.size());
	}
	
	public ProfileComment[] getProfileComments(int startIndexInclusive, int endIndexInclusive) {
		ArrayList<ProfileComment> resultList = new ArrayList<>();
		if(startIndexInclusive <= endIndexInclusive) {
			for(int i = startIndexInclusive; i <= endIndexInclusive; ++i) 
				resultList.add(profileCommentManager.getItem(i));
		}
		return resultList.toArray(new ProfileComment[resultList.size()]);
	}

	public PrivateMessageManager getPrivateMessageManager() {
		return privateMessageManager;
	}

	public ChannelMessageManager getChannelMessageManager() {
		return channelMessageManager;
	}
	
	public ProfileCommentManager getProfileCommentManager() {
		return profileCommentManager;
	}

	public String getPrivateMessageManagerPath() {
		return privateMessageManagerPath;
	}
	
	public String getChannelMessageManagerPath() {
		return channelMessageManagerPath;
	}

	public String getProfileCommentManagerPath() {
		return profileCommentManagerPath;
	}
	
	public void configurePrivateMessageManagerPath(String path) {
		privateMessageManagerPath = path;
	}
	
	public void configureChannelMessageManagerPath(String path) {
		channelMessageManagerPath = path;
	}
	
	public void configureProfileCommentManagerPath(String path) {
		profileCommentManagerPath = path;
	}
}
