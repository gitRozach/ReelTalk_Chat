package gui.components.channelBar;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.components.channelBar.channelBarItems.ChannelBarChannelItem;
import gui.components.channelBar.channelBarItems.ChannelBarClientItem;
import gui.components.channelBar.channelBarItems.ChannelBarItem;
import gui.components.channelBar.channelBarItems.FriendChannelBarItem;
import gui.components.channelBar.channelBarItems.GuestChannelBarItem;
import gui.components.channelBar.channelBarItems.MemberChannelBarItem;
import gui.components.channelBar.channelBarItems.TextChannelBarItem;
import gui.components.channelBar.channelBarItems.VoiceChannelBarItem;
import handler.ObjectEventHandler;
import handler.events.ObjectEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import utils.FXUtils;

public class ChannelBar extends StackPane {
	private VBox contentBox;
	private HBox serverBarBox;
	private HBox buttonBox;
	private ScrollPane channels;
	
	private JFXButton microphoneButton;
	private JFXButton speakersButton;
	private JFXButton callButton;
	private JFXTreeView<ChannelBarItem> channelView;
		
	private ObjectEventHandler<ChannelBarChannelItem> onChannelClickedHandler;
	private ObjectEventHandler<ChannelBarClientItem> onClientClickedHandler; 
	
	public ChannelBar()	{
		this(true);
	}
	
	public ChannelBar(boolean initialize) {
		super();
		if(initialize)
			initialize();
	}
	
	public void initialize() {
		initStylesheets();
		initServerBar();
		initButtons();
		initChannels();
		initEventHandlers();
		initContentBox();
		initMouseListener();
		getChildren().add(contentBox);
	}

	private void initServerBar() {
		serverBarBox = new HBox();
		FXUtils.setFixedHeightOf(serverBarBox, 50d);
	}
	
	private void initButtons() {	
		FontAwesomeIconView microphoneIconEnabled = new FontAwesomeIconView(FontAwesomeIcon.MICROPHONE);
		microphoneIconEnabled.setGlyphSize(25d);
		microphoneIconEnabled.setWrappingWidth(25d);
		FontAwesomeIconView headphonesIcon = new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES);
		headphonesIcon.setGlyphSize(25d);
		headphonesIcon.setWrappingWidth(25d);
		FontAwesomeIconView phoneIcon = new FontAwesomeIconView(FontAwesomeIcon.PHONE);
		phoneIcon.setGlyphSize(25d);
		phoneIcon.setWrappingWidth(25d);
		
		microphoneButton = new JFXButton("", microphoneIconEnabled);
		microphoneButton.prefWidthProperty().bind(widthProperty().divide(4d));
		FXUtils.setFixedHeightOf(microphoneButton, 40d);
		speakersButton = new JFXButton("", headphonesIcon);
		speakersButton.prefWidthProperty().bind(widthProperty().divide(4d));
		FXUtils.setFixedHeightOf(speakersButton, 40d);
		callButton = new JFXButton("", phoneIcon);
		callButton.prefWidthProperty().bind(widthProperty().divide(2d));
		FXUtils.setFixedHeightOf(callButton, 40d);
		
		buttonBox = new HBox(10d);
		buttonBox.getStyleClass().add("buttonBox");
		buttonBox.setAlignment(Pos.CENTER_LEFT);
		buttonBox.getChildren().addAll(microphoneButton, speakersButton, callButton);		
		HBox.setHgrow(callButton, Priority.ALWAYS);
	}
	
	private void initChannels() {
		channelView = new JFXTreeView<ChannelBarItem>(new TreeItem<ChannelBarItem>(null));
		channelView.setFocusTraversable(false);
		channelView.setCellFactory((TreeView<ChannelBarItem> param) -> new ChannelCell());
		channelView.setShowRoot(false);
				
		channels = new ScrollPane();
		channels.setCenterShape(true);
		channels.setFitToHeight(true);
		channels.setFitToWidth(true);
		channels.setContent(channelView);
	}
	
	private void initEventHandlers() {
		onChannelClickedHandler = new ObjectEventHandler<ChannelBarChannelItem>() {
			@Override
			public void handle(ObjectEvent<ChannelBarChannelItem> event) {}
		};
		onClientClickedHandler = new ObjectEventHandler<ChannelBarClientItem>() {
			@Override
			public void handle(ObjectEvent<ChannelBarClientItem> event) {}
		};
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/ChannelBar.css");
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
	
	public TreeItem<ChannelBarItem> getChannelById(int id) {
		if(channelView == null || channelView.getRoot() == null)
			return null;
		for(int i = 0; i < channelView.getRoot().getChildren().size(); ++i) {
			ChannelBarChannelItem channel = (ChannelBarChannelItem)channelView.getRoot().getChildren().get(i).getValue();
			if(channel.getChannelId() == id)
				return channelView.getRoot().getChildren().get(i);
		}
		return null;
	}
	
	public TreeItem<ChannelBarItem> getClientById(int id) {
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
	
	public boolean containsChannelWithId(int id) {
		return getChannelById(id) != null;
	}
	
	public boolean channelContainsClientWithId(int channelId, int clientId) {
		TreeItem<ChannelBarItem> channelBarItem = getChannelById(channelId);
		TreeItem<ChannelBarItem> clientBarItem = getClientById(clientId);
		if(channelBarItem == null || clientBarItem == null)
			return false;
		return indexOfClient(((ChannelBarChannelItem)(channelBarItem.getValue())).getChannelId(), 
							((ChannelBarClientItem)(clientBarItem.getValue())).getClientId()) != -1;
	}
	
	public int indexOfChannel(int channelId) {
		int currentIndex = -1;
		for(TreeItem<ChannelBarItem> currentItem : channelView.getRoot().getChildren()) {
			++currentIndex;
			if(((ChannelBarChannelItem)(currentItem.getValue())).getChannelId() == channelId) {
				return currentIndex;
			}
		}
		return -1;
	}
	
	public int indexOfClient(int channelId, int clientId) {
		int currentIndex = -1;
		int channelIndex = indexOfChannel(channelId);
		if(channelIndex == -1)
			return -1;
		for(TreeItem<ChannelBarItem> currentChildren : channelView.getRoot().getChildren().get(channelIndex).getChildren()) {
			++currentIndex;
			if(((ChannelBarClientItem)(currentChildren.getValue())).getClientId() == clientId)
				return currentIndex;
		}
		return -1;
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
				setGraphic(new FontAwesomeIconView(FontAwesomeIcon.HASHTAG));
				setOnMouseClicked(a -> onChannelClickedHandler.handle(	new ObjectEvent<ChannelBarChannelItem>(ObjectEvent.ANY, text) {
																		private static final long serialVersionUID = -8246283748073868549L;
				}));
				setPrefWidth(200d);
				setPrefHeight(50d);
				addEventHandler(TreeItem.branchCollapsedEvent(), a -> a.getTreeItem().setExpanded(true));
			}
			else if(item instanceof VoiceChannelBarItem) {
				VoiceChannelBarItem voice = (VoiceChannelBarItem)item;
				getStyleClass().add("channel-tree-cell");
				setText(voice.getChannelName());
				setGraphic(new FontAwesomeIconView(FontAwesomeIcon.VOLUME_DOWN));
				setOnMouseReleased(a -> onChannelClickedHandler.handle(	new ObjectEvent<ChannelBarChannelItem>(ObjectEvent.ANY, voice) {
																		private static final long serialVersionUID = -8246283748073868549L;
				}));
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
				
				HBox graphicBox = new HBox(profilePic, FXUtils.createHorizontalSpacer(10d));
				
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
	
	public ObjectEventHandler<ChannelBarChannelItem> getOnChannelClicked() {
		return onChannelClickedHandler;
	}
	
	public void setOnChannelClicked(ObjectEventHandler<ChannelBarChannelItem> handler) {
		onChannelClickedHandler = handler;
	}
	
	public ObjectEventHandler<ChannelBarClientItem> getOnClientClicked() {
		return onClientClickedHandler;
	}
	
	public void setOnClientClicked(ObjectEventHandler<ChannelBarClientItem> handler) {
		onClientClickedHandler = handler;
	}
}
