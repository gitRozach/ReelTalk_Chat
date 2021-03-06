package gui.components.clientBar;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.components.LoadableStackPane;
import gui.components.clientBar.items.ClientBarMemberItem;
import gui.components.clientBar.items.FriendClientBarItem;
import gui.components.clientBar.items.MemberClientBarItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import utils.FXUtils;

public class ClientBar extends LoadableStackPane {
	private VBox rootContentBox;

	private VBox profileBox;
	private HBox profileImageBox;
	private VBox levelImagesBox;
	
	private Circle profilePictureCircle;
	private Label profileNameLabel;
	private ImageView adminLevelView;
	private ImageView profileLevelView;

	private JFXTabPane profileTabPane;
	private VBox messageView;
	private Tab messagesTab;
	private HBox profileTabButtonBox;
	private JFXButton messagesButton;
	private JFXButton membersButton;
	private JFXButton appsButton;

	private VBox mediaView;
	private Tab mediaTab;
	private JFXButton audioPlayerButton;
	private JFXButton videoPlayerButton;
	private JFXButton imageViewerButton;

	private JFXTabPane clientTabPane;
	private JFXListView<ClientBarMemberItem> friendsView;
	private Tab friendsTab;
	private JFXListView<ClientBarMemberItem> membersView;
	private Tab membersTab;
	private JFXListView<ClientBarMemberItem> onlineView;
	private Tab onlineTab;

	public ClientBar() {
		this(true);
	}

	public ClientBar(boolean initialize) {
		super();
		if(initialize)
			initialize();
	}
	
	public void addFriendsItem(int clientId, String clientUsername) {
		addFriendsItem(friendsView.getItems().isEmpty() ? 0 : friendsView.getItems().size() - 1, clientId, clientUsername);
	}
	
	public boolean addFriendsItem(int index, int clientId, String clientUsername) {
		if(index < 0 || index > friendsView.getItems().size() || clientUsername == null)
			return false;
		friendsView.getItems().add(index, new FriendClientBarItem(clientId, clientUsername));
		return true;
	}
	
	public void addMemberItem(int clientId, String clientUsername) {
		addMemberItem(membersView.getItems().size(), clientId, clientUsername);
	}
	
	public boolean addMemberItem(int index, int clientId, String clientUsername) {
		if(index < 0 || index > membersView.getItems().size() || clientUsername == null)
			return false;
		membersView.getItems().add(index, new MemberClientBarItem(clientId, clientUsername));
		return true;
	}
	
	public void addOnlineItem(int clientId, String clientUsername) {
		addOnlineItem(onlineView.getItems().isEmpty() ? 0 : onlineView.getItems().size() - 1, clientId, clientUsername);
	}
	
	public boolean addOnlineItem(int index, int clientId, String clientUsername) {
		if(index < 0 || index > onlineView.getItems().size() || clientUsername == null)
			return false;
		onlineView.getItems().add(index, new MemberClientBarItem(clientId, clientUsername));
		return true;
	}

	public void initialize() {
		super.initialize();
		initProfileBox();
		initMessagesView();
		initClientTabPane();
		initMediaBox();
		initProfileTabPane();
		initRootContentBox();
		initStylesheets();
		initMouseListener();
		getStyleClass().add("root-content-box");
		loadContent(rootContentBox);
	}
	
	public HBox createPrivateMessageItem(String sender, String text) {
		HBox messageGraphic = new HBox(5d);
		messageGraphic.setAlignment(Pos.CENTER_LEFT);
		
		FontAwesomeIconView senderIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE_OPEN);
		senderIcon.setGlyphSize(24d);
		senderIcon.setWrappingWidth(24d);
		
		VBox senderAndText = new VBox(3d);
		senderAndText.setPadding(new Insets(0d, 0d, 0d, 10d));
		senderAndText.setFillWidth(true);
		senderAndText.setAlignment(Pos.TOP_LEFT);
		Label senderLabel = new Label(sender);
		senderLabel.setFont(Font.font("Tahoma", 13d));
		Label textLabel = new Label(text);
		textLabel.setFont(Font.font("Tahoma", 13d));
		senderAndText.getChildren().addAll(senderLabel, textLabel);
		
		messageGraphic.getChildren().addAll(senderIcon, senderAndText);
		HBox.setHgrow(senderAndText, Priority.ALWAYS);
		
		JFXButton messageButton = new JFXButton("", messageGraphic);
		messageButton.setRipplerFill(Color.DARKGRAY);
		HBox messageBox = new HBox(messageButton);
		messageButton.prefWidthProperty().bind(messageBox.widthProperty());
		return messageBox;
	}
	
	public HBox createAppItem(String appName) {
		HBox appGraphic = new HBox(15d);
		appGraphic.setAlignment(Pos.CENTER_LEFT);
		
		FontAwesomeIconView appIcon = new FontAwesomeIconView(FontAwesomeIcon.TH_LARGE);
		appIcon.setGlyphSize(24d);
		appIcon.setWrappingWidth(24d);
		
		Label textLabel = new Label(appName);
		textLabel.setFont(Font.font("Tahoma", 13d));
		
		appGraphic.getChildren().addAll(appIcon, textLabel);
		HBox.setHgrow(textLabel, Priority.ALWAYS);
		
		JFXButton messageButton = new JFXButton("", appGraphic);
		HBox appBox = new HBox(messageButton);
		messageButton.prefWidthProperty().bind(appBox.widthProperty());
		return appBox;
	}
	
	private void initProfileBox() {
		Image profileImage = new Image("/resources/icons/member.png", 128d, 128d, true, true);
		profilePictureCircle = new Circle(64d);
		profilePictureCircle.setFill(new ImagePattern(profileImage));
		profilePictureCircle.setStroke(Color.GREEN);
		profilePictureCircle.setStrokeWidth(3d);
		profilePictureCircle.setTranslateX(20d);
		
		Image adminLevelImage = new Image("/resources/icons/rank_admin.png", 32d, 32d, true, true);
		adminLevelView = new ImageView(adminLevelImage);
		adminLevelView.setTranslateX(32d);
		adminLevelView.setVisible(false);
		
		Image profileLevelImage = new Image("/resources/icons/rank_profile.png", 32d, 32d, true, true);
		profileLevelView = new ImageView(profileLevelImage);
		profileLevelView.setTranslateX(32d);
		profileLevelView.setVisible(false);
				
		levelImagesBox = new VBox(adminLevelView, profileLevelView);
		levelImagesBox.setSpacing(10d);
		levelImagesBox.setAlignment(Pos.TOP_CENTER);
		
		profileImageBox = new HBox(profilePictureCircle, levelImagesBox);
		profileImageBox.setAlignment(Pos.CENTER);
		profileImageBox.setSpacing(10d);
		profileImageBox.setPadding(new Insets(10d, 0d, 10d, 0d));
		
		profileNameLabel = new Label("Rouman");
		profileNameLabel.setFont(Font.font(18d));
		
		FontAwesomeIconView messagesIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE);
		messagesIcon.setGlyphSize(25d);
		messagesIcon.setWrappingWidth(25d);
		FontAwesomeIconView usersIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS);
		usersIcon.setGlyphSize(24d);
		usersIcon.setWrappingWidth(24d);
		FontAwesomeIconView appsIcon = new FontAwesomeIconView(FontAwesomeIcon.TH_LARGE);
		appsIcon.setGlyphSize(28d);
		appsIcon.setWrappingWidth(28d);
		
		messagesButton = new JFXButton("", messagesIcon);
		JFXBadge messagesBadge = new JFXBadge(messagesButton, Pos.TOP_RIGHT);
		messagesBadge.setText("5");
		
		FXUtils.setFixedSizeOf(messagesButton, 50d, 40d);
		messagesButton.setOnAction(a -> profileTabPane.getSelectionModel().select(0));
		membersButton = new JFXButton("", usersIcon);
		FXUtils.setFixedSizeOf(membersButton, 50d, 40d);
		membersButton.setOnAction(b -> profileTabPane.getSelectionModel().select(1));
		appsButton = new JFXButton("", appsIcon);
		FXUtils.setFixedSizeOf(appsButton, 50d, 40d);
		appsButton.setOnAction(c -> profileTabPane.getSelectionModel().select(2));
		profileTabButtonBox = new HBox(messagesBadge, membersButton, appsButton);
		profileTabButtonBox.setPadding(new Insets(10d, 0d, 10d, 0d));
		profileTabButtonBox.setAlignment(Pos.CENTER);
		
		profileBox = new VBox(profileImageBox, profileNameLabel, profileTabButtonBox);
		profileBox.setSpacing(5d);
		profileBox.setStyle("-fx-background-color: #404040;");
		profileBox.setAlignment(Pos.CENTER);
	}

	private void initProfileTabPane() {
		messagesTab = new Tab("", messageView);
		
		FontAwesomeIconView friendsIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS);
		friendsIcon.setGlyphSize(18d);
		friendsIcon.setWrappingWidth(15d);
		Label friendsLabel = new Label("Freunde");
		HBox friendsGraphic = new HBox(friendsIcon, friendsLabel);
		HBox.setMargin(friendsIcon, new Insets(0d, 15d, 0d, 0d));
		friendsGraphic.setAlignment(Pos.CENTER);
		
		FontAwesomeIconView membersIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS);
		membersIcon.setGlyphSize(18d);
		membersIcon.setWrappingWidth(15d);
		Label membersLabel = new Label("Mitglieder");
		HBox membersGraphic = new HBox(membersIcon, membersLabel);
		HBox.setMargin(membersIcon, new Insets(0d, 15d, 0d, 0d));
		membersGraphic.setAlignment(Pos.CENTER);
		
		FontAwesomeIconView onlineIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
		onlineIcon.setGlyphSize(18d);
		onlineIcon.setWrappingWidth(15d);
		Label onlineLabel = new Label("Online");
		HBox onlineGraphic = new HBox(onlineIcon, onlineLabel);
		HBox.setMargin(onlineIcon, new Insets(0d, 15d, 0d, 0d));
		onlineGraphic.setAlignment(Pos.CENTER);
		
		JFXButton bFriends = new JFXButton("", friendsGraphic);
		bFriends.setAlignment(Pos.CENTER);
		bFriends.setOnAction(a -> clientTabPane.getSelectionModel().select(0));
		JFXButton bMembers = new JFXButton("", membersGraphic);
		bMembers.setAlignment(Pos.CENTER);
		bMembers.setOnAction(a -> clientTabPane.getSelectionModel().select(1));
		JFXButton bOnline = new JFXButton("", onlineGraphic);
		bOnline.setAlignment(Pos.CENTER);
		bOnline.setOnAction(a -> clientTabPane.getSelectionModel().select(2));
		
		VBox memberBox = new VBox(bFriends, bMembers, bOnline, clientTabPane);
		memberBox.setFillWidth(true);
		memberBox.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(clientTabPane, Priority.ALWAYS);
		bFriends.prefWidthProperty().bind(memberBox.widthProperty());
		bMembers.prefWidthProperty().bind(memberBox.widthProperty());
		bOnline.prefWidthProperty().bind(memberBox.widthProperty());
		
		membersTab = new Tab("", memberBox);
		mediaTab = new Tab("", mediaView);
		
		profileTabPane = new JFXTabPane();
		profileTabPane.getTabs().addAll(messagesTab, membersTab, mediaTab);
		FXUtils.hideTabs(profileTabPane);
	}
	
	private void initRootContentBox() {
		rootContentBox = new VBox(profileBox, profileTabPane);
		rootContentBox.getStyleClass().add("root-content-box");
		VBox.setVgrow(profileTabPane, Priority.ALWAYS);
	}

	private void initMessagesView() {
		messageView = new VBox();
		messageView.setFillWidth(true);
		messageView.setAlignment(Pos.TOP_CENTER);
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/ClientBar.css");
	}
	
	private void initMouseListener() {
		friendsView.setOnMouseEntered(a -> {
			Node bar1 = friendsView.lookup(".scroll-bar:horizontal");
			Node bar2 = friendsView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		membersView.setOnMouseEntered(a -> {
			Node bar1 = membersView.lookup(".scroll-bar:horizontal");
			Node bar2 = membersView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		onlineView.setOnMouseEntered(a -> {
			Node bar1 = onlineView.lookup(".scroll-bar:horizontal");
			Node bar2 = onlineView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(1d);
			if(bar2 != null)
				bar2.setOpacity(1d);
		});
		
		friendsView.setOnMouseExited(b -> {
			Node bar1 = friendsView.lookup(".scroll-bar:horizontal");
			Node bar2 = friendsView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
		
		membersView.setOnMouseExited(b -> {
			Node bar1 = membersView.lookup(".scroll-bar:horizontal");
			Node bar2 = membersView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
		
		onlineView.setOnMouseExited(b -> {
			Node bar1 = onlineView.lookup(".scroll-bar:horizontal");
			Node bar2 = onlineView.lookup(".scroll-bar:vertical");
			if(bar1 != null)
				bar1.setOpacity(0d);
			if(bar2 != null)
				bar2.setOpacity(0d);
		});
	}
	
	private void initClientTabPane() {			
		initFriendsView();
		initMembersView();
		initOnlineView();
		
		friendsTab = new Tab("", friendsView);
		membersTab = new Tab("", membersView);
		onlineTab = new Tab("", onlineView);
		
		clientTabPane = new JFXTabPane();
		clientTabPane.getTabs().addAll(friendsTab, membersTab, onlineTab);
		FXUtils.hideTabs(clientTabPane);
	}
	
	private void initFriendsView() {
		friendsView = new JFXListView<ClientBarMemberItem>();
		friendsView.setCenterShape(true);
		friendsView.setCellFactory((ListView<ClientBarMemberItem> param) -> new ClientCell());
	}
	
	private void initMembersView() {
		membersView = new JFXListView<ClientBarMemberItem>();
		membersView.setCenterShape(true);
		membersView.setCellFactory((ListView<ClientBarMemberItem> param) -> new ClientCell());
	}
	
	private void initOnlineView() {
		onlineView = new JFXListView<ClientBarMemberItem>();
		onlineView.setCenterShape(true);
		onlineView.setCellFactory((ListView<ClientBarMemberItem> param) -> new ClientCell());
	}

	private void initMediaBox() {
		mediaView = new VBox();
		mediaView.setFillWidth(true);
		mediaView.setAlignment(Pos.TOP_CENTER);
		
		mediaView.getChildren().add(createAppItem("Musik Player"));
		mediaView.getChildren().add(createAppItem("Video Player"));
		mediaView.getChildren().add(createAppItem("Bildbetrachter"));
		mediaView.getChildren().add(createAppItem("Downloads"));
	}

	public VBox getRootContentBox() {
		return rootContentBox;
	}

	public VBox getProfileBox() {
		return profileBox;
	}

	public HBox getProfileImageBox() {
		return profileImageBox;
	}

	public VBox getLevelImagesBox() {
		return levelImagesBox;
	}

	public Circle getProfilePictureCircle() {
		return profilePictureCircle;
	}
	
	public void setProfilePicture(Image value) {
		profilePictureCircle = new Circle(64d);
		profilePictureCircle.setFill(new ImagePattern(value));
		profilePictureCircle.setStroke(Color.GREEN);
		profilePictureCircle.setStrokeWidth(2d);
		profilePictureCircle.setTranslateX(20d);
	}

	public Label getProfileNameLabel() {
		return profileNameLabel;
	}
	
	public String getProfileName() {
		return profileNameLabel.getText();
	}
	
	public void setProfileName(String value) {
		profileNameLabel.setText(value);
	}

	public ImageView getAdminLevelView() {
		return adminLevelView;
	}

	public ImageView getProfileLevelView() {
		return profileLevelView;
	}

	public JFXTabPane getProfileTabPane() {
		return profileTabPane;
	}

	public VBox getMessageView() {
		return messageView;
	}

	public Tab getMessagesTab() {
		return messagesTab;
	}

	public HBox getProfileTabButtonBox() {
		return profileTabButtonBox;
	}

	public JFXButton getMessagesButton() {
		return messagesButton;
	}

	public JFXButton getMembersButton() {
		return membersButton;
	}

	public JFXButton getAppsButton() {
		return appsButton;
	}

	public VBox getMediaView() {
		return mediaView;
	}

	public Tab getMediaTab() {
		return mediaTab;
	}

	public JFXButton getAudioPlayerButton() {
		return audioPlayerButton;
	}

	public JFXButton getVideoPlayerButton() {
		return videoPlayerButton;
	}

	public JFXButton getImageViewerButton() {
		return imageViewerButton;
	}

	public JFXTabPane getMemberTabPane() {
		return clientTabPane;
	}

	public JFXListView<ClientBarMemberItem> getFriendsView() {
		return friendsView;
	}

	public Tab getFriendsTab() {
		return friendsTab;
	}

	public JFXListView<ClientBarMemberItem> getNonFriendsView() {
		return membersView;
	}

	public Tab getMembersTab() {
		return membersTab;
	}

	public JFXListView<ClientBarMemberItem> getOnlineView() {
		return onlineView;
	}

	public Tab getOnlineTab() {
		return onlineTab;
	}

	private class ClientCell extends JFXListCell<ClientBarMemberItem> {
		
		public ClientCell() {
			super();
			setEditable(false);
		}
		
		@Override
		public void updateItem(ClientBarMemberItem item, boolean empty) {
			super.updateItem(item, empty);
			if (empty)
				updateEmptyItem();
			else if (item instanceof ClientBarMemberItem){
				ClientBarMemberItem member = (ClientBarMemberItem)item;
				Color borderColor = null;
				switch(member.getClientStatus()) {
				case ONLINE:
					borderColor = Color.GREEN;
					break;
				case OFFLINE:
					borderColor = Color.DIMGRAY;
					break;
				case AFK:
					borderColor = Color.ORANGE;
					break;
				case BUSY:
					borderColor = Color.ORANGERED;
					break;
				default:
					borderColor = Color.DIMGRAY;
					break;
				}
				
				setText(member.getUsername());
				
				Image pic = new Image(member.getProfilePicture(), 32d, 32d, true, true);
				Circle profilePic = new Circle(16d);
				profilePic.setFill(new ImagePattern(pic));
				profilePic.setStroke(borderColor);
				profilePic.setStrokeWidth(2d);
				
				HBox graphicBox = new HBox(profilePic, FXUtils.createHorizontalSpacer(10d));
				graphicBox.setPadding(new Insets(0d, 0d, 0d, 10d));
				
				setGraphic(graphicBox);
			}
		}
	
		private void updateEmptyItem() {
			setText(null);
			setTooltip(null);
			setGraphic(null);
			setOnMouseClicked(a -> {});
			setContextMenu(null);
			setStyle("");
		}
	}
}
