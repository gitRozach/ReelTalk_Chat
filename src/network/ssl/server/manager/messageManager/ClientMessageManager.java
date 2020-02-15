package network.ssl.server.manager.messageManager;

import java.io.IOException;
import java.util.ArrayList;

import network.ssl.server.manager.messageManager.items.ChannelMessage;
import network.ssl.server.manager.messageManager.items.PrivateMessage;
import network.ssl.server.manager.messageManager.items.ProfileComment;

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
	
	public void addPrivateMessage(PrivateMessage message) {
		privateMessageManager.addItem(message);
	}
	
	public PrivateMessage getPrivateMessage(int index) {
		return privateMessageManager.getDatabaseItem(index);
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
				resultList.add(privateMessageManager.getDatabaseItem(i));
		}
		return resultList.toArray(new PrivateMessage[resultList.size()]);
	}
	
	public void addChannelMessage(ChannelMessage message) {
		channelMessageManager.addItem(message);
	}
	
	public ChannelMessage getChannelMessage(int index) {
		return channelMessageManager.getDatabaseItem(index);
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
				resultList.add(channelMessageManager.getDatabaseItem(i));
		}
		return resultList.toArray(new ChannelMessage[resultList.size()]);
	}
	
	public void addProfileComment(ProfileComment message) {
		profileCommentManager.addItem(message);
	}
	
	public ProfileComment getProfileComment(int index) {
		return profileCommentManager.getDatabaseItem(index);
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
				resultList.add(profileCommentManager.getDatabaseItem(i));
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
