package gui.client.components.channelBar;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;

import gui.client.components.channelBar.channelBarItems.ChannelBarChannelItem;
import gui.client.components.channelBar.channelBarItems.ChannelBarClientItem;
import gui.client.components.channelBar.channelBarItems.ChannelBarItem;
import gui.client.components.channelBar.channelBarItems.FriendChannelBarItem;
import gui.client.components.channelBar.channelBarItems.GuestChannelBarItem;
import gui.client.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.client.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.client.components.channelBar.channelBarItems.VoiceChannelBarItem;
import gui.tools.GUITools;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import network.ssl.client.SecuredChatClient;

public class ChannelBar extends StackPane {
	private VBox contentBox;
	private HBox serverBarBox;
	private HBox buttonBox;
	private ScrollPane channels;
	private JFXButton microphoneButton;
	private JFXButton speakersButton;
	//private JFXButton callButton;
	private JFXTreeView<ChannelBarItem> channelView;
	
	//private SecuredChatClient client;

	public ChannelBar()	{
		this(false, null);
	}
	
	public ChannelBar(boolean initialize) {
		this(initialize, null);
	}
	
	public ChannelBar(boolean initialize, SecuredChatClient clientAttachment) {
		super();
		
		if(initialize)
			initialize();
		if(clientAttachment != null)
			attachClient(clientAttachment);
	}
	
	public void initialize() {
		initStylesheets();
		initServerBar();
		initButtons();
		initChannels();
		initClientAttachment();
		initContentBox();
		initMouseListener();
		//loadContent(contentBox);
		getChildren().add(contentBox);
	}

	private void initServerBar() {
		serverBarBox = new HBox();
		GUITools.setFixedHeightOf(serverBarBox, 50d);
	}
	
	private void initButtons() {	
		microphoneButton = new JFXButton("", new ImageView(new Image("/resources/channelBar/microphone_enabled.png", 24d, 24d, true, false)));
		speakersButton = new JFXButton("", new ImageView(new Image("/resources/channelBar/speakers_enabled.png", 24d, 24d, true, false)));
		//callButton = new JFXButton("[Call]");
		
		buttonBox = new HBox(0d);
		buttonBox.getStyleClass().add("buttonBox");
		buttonBox.setAlignment(Pos.CENTER_LEFT);
		buttonBox.getChildren().addAll(microphoneButton, speakersButton);
		
	}
	
	private void initChannels() {
		channelView = new JFXTreeView<ChannelBarItem>(new TreeItem<ChannelBarItem>(null));
		channelView.setCellFactory((TreeView<ChannelBarItem> param) -> new ChannelCell());
		channelView.setShowRoot(false);
		
		channels = new ScrollPane();
		channels.setCenterShape(true);
		channels.setFitToHeight(true);
		channels.setFitToWidth(true);
		channels.setContent(channelView);
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/ChannelBar.css");
	}
	
	private void initClientAttachment() {
		
	}
	
	private void initContentBox() {
		contentBox = new VBox(serverBarBox, buttonBox, channels);
		contentBox.setAlignment(Pos.CENTER);
		contentBox.setFillWidth(true);
		contentBox.getStyleClass().add("root-content-box");
		contentBox.getStyleClass().add("content-box");
		VBox.setVgrow(channels, Priority.ALWAYS);
	}
	
	private void initMouseListener() {
		channelView.setOnMouseEntered(a -> {
			Node bar1 = channelView.lookup(".scroll-bar:horizontal");
			Node bar2 = channelView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		channelView.setOnMouseExited(b -> {
			Node bar1 = channelView.lookup(".scroll-bar:horizontal");
			Node bar2 = channelView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
	}
	
	public void addChannel(ChannelBarChannelItem channelItem) {
		TreeItem<ChannelBarItem> newChannel = new TreeItem<ChannelBarItem>(channelItem);
		newChannel.setExpanded(true);
		channelView.getRoot().getChildren().add(newChannel);
	}
	
	public void addChannel(int index, ChannelBarChannelItem channelItem) {
		channelView.getRoot().getChildren().add(index, new TreeItem<ChannelBarItem>(channelItem));
	}
	
	public boolean addClient(int channelId, ChannelBarClientItem clientItem) {
		TreeItem<ChannelBarItem> channel = getChannelById(channelId);
		
		if(channel == null) {
			return false;
		}
		return channel.getChildren().add(new TreeItem<ChannelBarItem>(clientItem));
	}
	
	public boolean removeChannel(int channelId) {
		for(int i = 0; i < channelView.getRoot().getChildren().size(); ++i) {
			ChannelBarChannelItem channel = (ChannelBarChannelItem)channelView.getRoot().getChildren().get(i).getValue();
			if(channel.getChannelId() == channelId)
				return channelView.getRoot().getChildren().remove(i) != null;
		}
		return false;
	}
	
	public boolean removeClientFromChannel(int clientId, int channelId) {
		TreeItem<ChannelBarItem> channelItem = getChannelById(channelId);
		
		if(channelItem == null)
			return false;
		for(int i = 0; i < channelItem.getChildren().size(); ++i) {
			ChannelBarClientItem client = (ChannelBarClientItem)channelItem.getChildren().get(i).getValue();
			if(client.getClientId() == clientId) {
				return channelItem.getChildren().remove(i) != null;
			}
		}
		return false;
	}
	
	private TreeItem<ChannelBarItem> getChannelById(int id) {
		if(channelView == null || channelView.getRoot() == null)
			return null;
		for(int i = 0; i < channelView.getRoot().getChildren().size(); ++i) {
			ChannelBarChannelItem channel = (ChannelBarChannelItem)channelView.getRoot().getChildren().get(i).getValue();
			if(channel.getChannelId() == id)
				return channelView.getRoot().getChildren().get(i);
		}
		return null;
	}
	
	private TreeItem<ChannelBarItem> getClientById(int id) {
		if(channelView == null || channelView.getRoot() == null)
			return null;
		for(int i = 0; i < channelView.getRoot().getChildren().size(); ++i) {
			TreeItem<ChannelBarItem> channel = channelView.getRoot().getChildren().get(i);
			for(int y = 0; y < channel.getChildren().size(); ++y) {
				ChannelBarClientItem client = (ChannelBarClientItem) channel.getChildren().get(y).getValue();
				if(client.getClientId() == id)
					return channel.getChildren().get(y);
			}
		}
		return null;
	}
	
	public void replaceClientWith() {
		
	}
	
	public void replaceChannelWith() {
		
	}
	
	public int indexOfChannel(int channelId) {
		int[] currentIndex = {-1};
		channelView.getRoot().getChildren().forEach(currentChannel -> {
			++currentIndex[0];
			if(((ChannelBarChannelItem)(currentChannel.getValue())).getChannelId() == channelId) {
				return;
			}
		});
		return currentIndex[0];
	}
	
	public int indexOfClient(int clientId) {
		return -1;
	}
	
	public void attachClient(SecuredChatClient client) {
		
	}
	
	private class ChannelCell extends TreeCell<ChannelBarItem> {
		public ChannelCell() {
			super();
			setEditable(false);
		}
		
		@Override public void updateItem(ChannelBarItem item, boolean empty) {
			super.updateItem(item, empty);
			if(empty)
				updateEmptyItem();
			else if(item instanceof ChannelBarChannelItem)
				updateChannelItem(item);
			else if(item instanceof ChannelBarClientItem)
				updateClientItem(item);
		}
		
		private void updateEmptyItem() {
			setText(null);
			setTooltip(null);
			setGraphic(null);
			setOnMouseClicked(a -> {});
			setContextMenu(null);
			setStyle("");
		}
		
		private void updateChannelItem(ChannelBarItem item) {
			if(item == null) 
				updateEmptyItem();
			else if(item instanceof TextChannelBarItem) {
				TextChannelBarItem text = (TextChannelBarItem)item;
				getStyleClass().add("channel-tree-cell");
				setText(text.getChannelName());
				setGraphic(null);
				setPrefWidth(200d);
				setPrefHeight(50d);
			}
			else if(item instanceof VoiceChannelBarItem) {
				VoiceChannelBarItem voice = (VoiceChannelBarItem)item;
				getStyleClass().add("channel-tree-cell");
				setText(voice.getChannelName());
				//setGraphic(new Text("[Voice ("+ voice.getChannelId() +")]"));
				setGraphic(null);
				setOnMouseClicked(a -> {
					addClient(voice.getChannelId(), new MemberChannelBarItem(420, "CHÄFF!"));
					a.consume();
				});
				setPrefWidth(200d);
				setPrefHeight(50d);
				addEventHandler(TreeItem.branchCollapsedEvent(), a -> a.getTreeItem().setExpanded(true));
			}
		}
		
		private void updateClientItem(ChannelBarItem item) {
			if(item == null)
				updateEmptyItem();
			else {
				ChannelBarClientItem clientItem = (ChannelBarClientItem) item;
				setText(clientItem.getClientName());
				
				Image pic = new Image("/resources/icons/member.png", 24d, 24d, true, true);
				Circle profilePic = new Circle(12d);
				profilePic.setFill(new ImagePattern(pic));
				profilePic.setStroke(Color.GREEN);
				profilePic.setStrokeWidth(2d);
				
				HBox graphicBox = new HBox(profilePic, GUITools.createHorizontalSpacer(10d));
				
				setGraphic(graphicBox);
				setPrefWidth(150d);
				setPrefHeight(50d);
			}
			
			if(item instanceof MemberChannelBarItem) {

			}
			else if(item instanceof FriendChannelBarItem) {
				
			}
			else if(item instanceof GuestChannelBarItem) {
				
			}
		}
	}
}
