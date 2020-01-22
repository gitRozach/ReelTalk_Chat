package gui.client.components.clientBar;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;

import gui.client.components.clientBar.clientIBarItems.ClientBarItem;
import gui.client.components.clientBar.clientIBarItems.ClientBarMemberItem;
import gui.client.components.clientBar.clientIBarItems.FriendClientBarItem;
import gui.client.components.layouts.LoadableStackPane;
import gui.tools.GUITools;
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
import network.ssl.client.SecuredChatClient;

public class ClientBar extends LoadableStackPane {
	private VBox rootContentBox;

	private VBox profileBox;
	private HBox profileImageBox;
	private VBox levelImagesBox;
	
	private ImageView profilePictureView;
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

	// memberTab Root
	private JFXTabPane memberTabPane;
	private JFXListView<ClientBarItem> friendsView;
	private Tab friendsTab;
	private JFXListView<ClientBarItem> nonFriendsView;
	private Tab membersTab;
	private JFXListView<ClientBarItem> onlineView;
	private Tab onlineTab;

	

	public ClientBar() {
		this(false, null);
	}

	public ClientBar(boolean initialize) {
		this(initialize, null);
	}

	public ClientBar(boolean initialize, SecuredChatClient clientAttachment) {
		super();
		
		if(initialize)
			initialize();
		if(clientAttachment != null)
			attachClient(clientAttachment);
				
	}

	public void initialize() {
		super.initialize();
		initProfileBox();
		initMessagesView();
		initMemberTabPane();
		initMediaBox();
		initProfileTabPane();
		initRootContentBox();
		initStylesheets();
		initMouseListener();
		getStyleClass().add("root-content-box");
		//setContent(rootContentBox);
		loadContent(rootContentBox);
	}

	private void initProfileBox() {
		Image profileImage = new Image("/resources/icons/member.png", 128d, 128d, true, true);
		profilePictureView = new ImageView(profileImage);
		profilePictureView.setTranslateX(20d);
		
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
		
		profileImageBox = new HBox(profilePictureView, levelImagesBox);
		profileImageBox.setAlignment(Pos.CENTER);
		profileImageBox.setSpacing(10d);
		profileImageBox.setPadding(new Insets(10d, 0d, 10d, 0d));
		
		profileNameLabel = new Label("Rouman");
		profileNameLabel.setFont(Font.font("Tahoma", 16d));
		
		messagesButton = new JFXButton("", new ImageView(new Image("/resources/clientBar/messages.png", /*60*/32d, 52d, true, false)));
		messagesButton.setPrefWidth(10d);
		messagesButton.setOnAction(a -> profileTabPane.getSelectionModel().select(0));
		membersButton = new JFXButton("", new ImageView(new Image("/resources/clientBar/users.png", /*48*/ 32d, 27d, true, false)));
		membersButton.setPrefWidth(10d);
		membersButton.setOnAction(b -> profileTabPane.getSelectionModel().select(1));
		appsButton = new JFXButton("", new ImageView(new Image("/resources/clientBar/apps.png", /*63*/32d, 62d, true, false)));
		appsButton.setPrefWidth(10d);
		appsButton.setOnAction(c -> profileTabPane.getSelectionModel().select(2));
		profileTabButtonBox = new HBox(messagesButton, membersButton, appsButton);
		profileTabButtonBox.setPadding(new Insets(5d, 0d, 10d, 0d));
		profileTabButtonBox.setAlignment(Pos.CENTER);
		
		profileBox = new VBox(profileImageBox, profileNameLabel, profileTabButtonBox);
		profileBox.setSpacing(5d);
		profileBox.setStyle("-fx-background-color: #404040;");
		profileBox.setAlignment(Pos.CENTER);
	}

	private void initProfileTabPane() {
		messagesTab = new Tab("N", messageView);
		
		JFXButton bFriends = new JFXButton("Freunde");
		bFriends.setOnAction(a -> memberTabPane.getSelectionModel().select(0));
		JFXButton bMembers = new JFXButton("Members");
		bMembers.setOnAction(a -> memberTabPane.getSelectionModel().select(1));
		JFXButton bOnline = new JFXButton("Online");
		bOnline.setOnAction(a -> memberTabPane.getSelectionModel().select(2));
		
		VBox memberBox = new VBox(bFriends, bMembers, bOnline, memberTabPane);
		bFriends.prefWidthProperty().bind(memberBox.widthProperty());
		bMembers.prefWidthProperty().bind(memberBox.widthProperty());
		bOnline.prefWidthProperty().bind(memberBox.widthProperty());
		memberBox.setFillWidth(true);
		memberBox.setAlignment(Pos.TOP_CENTER);
		VBox.setVgrow(memberTabPane, Priority.ALWAYS);
		membersTab = new Tab("M", memberBox);
		
		
		mediaTab = new Tab("A", mediaView);
		
		profileTabPane = new JFXTabPane();
		profileTabPane.getTabs().addAll(messagesTab, membersTab, mediaTab);
		GUITools.hideTabs(profileTabPane);
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
		
		messageView.getChildren().add(createPrivateMessageItem("Rozach", "1 neue Nachricht"));
		messageView.getChildren().add(createPrivateMessageItem("Jenn", "5 neue Nachrichten"));
		messageView.getChildren().add(createPrivateMessageItem("Husseini", "3 neue Nachrichten"));
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/ClientBar.css");
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
		
		nonFriendsView.setOnMouseEntered(a -> {
			Node bar1 = nonFriendsView.lookup(".scroll-bar:horizontal");
			Node bar2 = nonFriendsView.lookup(".scroll-bar:vertical");
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
		
		nonFriendsView.setOnMouseExited(b -> {
			Node bar1 = nonFriendsView.lookup(".scroll-bar:horizontal");
			Node bar2 = nonFriendsView.lookup(".scroll-bar:vertical");
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
	
	private HBox createPrivateMessageItem(String sender, String text) {
		HBox messageGraphic = new HBox(5d);
		messageGraphic.setAlignment(Pos.CENTER_LEFT);
		
		ImageView senderPicture = new ImageView(new Image("/resources/clientBar/message_received.png", 24d, 34d, true, false));
		
		VBox senderAndText = new VBox(3d);
		senderAndText.setPadding(new Insets(0d, 0d, 0d, 10d));
		senderAndText.setFillWidth(true);
		senderAndText.setAlignment(Pos.TOP_LEFT);
		Label senderLabel = new Label(sender);
		senderLabel.setFont(Font.font("Tahoma", 13d));
		Label textLabel = new Label(text);
		textLabel.setFont(Font.font("Tahoma", 13d));
		senderAndText.getChildren().addAll(senderLabel, textLabel);
		
		messageGraphic.getChildren().addAll(senderPicture, senderAndText);
		HBox.setHgrow(senderAndText, Priority.ALWAYS);
		
		JFXButton messageButton = new JFXButton("", messageGraphic);
		messageButton.setRipplerFill(Color.DARKGRAY);
		HBox messageBox = new HBox(messageButton);
		messageButton.prefWidthProperty().bind(messageBox.widthProperty());
		return messageBox;
	}
	
	private HBox createAppItem(String appName) {
		HBox appGraphic = new HBox(15d);
		appGraphic.setAlignment(Pos.CENTER_LEFT);
		
		ImageView appPicture = new ImageView(new Image("/resources/clientBar/apps.png", 24d, 24d, true, false));
		
		Label textLabel = new Label(appName);
		textLabel.setFont(Font.font("Tahoma", 13d));
		
		appGraphic.getChildren().addAll(appPicture, textLabel);
		HBox.setHgrow(textLabel, Priority.ALWAYS);
		
		JFXButton messageButton = new JFXButton("", appGraphic);
		HBox appBox = new HBox(messageButton);
		messageButton.prefWidthProperty().bind(appBox.widthProperty());
		return appBox;
	}

	private void initMemberTabPane() {			
		initFriendsView();
		initNonFriendsView();
		initOnlineView();
		
		friendsTab = new Tab("", friendsView);
		membersTab = new Tab("", nonFriendsView);
		onlineTab = new Tab("", onlineView);
		
		memberTabPane = new JFXTabPane();
		memberTabPane.getTabs().addAll(friendsTab, membersTab, onlineTab);
		GUITools.hideTabs(memberTabPane);
	}
	
	private void initFriendsView() {
		friendsView = new JFXListView<ClientBarItem>();
		friendsView.setCenterShape(true);
		friendsView.setCellFactory((ListView<ClientBarItem> param) -> new ClientCell());
		
		friendsView.getItems().add(new FriendClientBarItem(0, "Rozach"));
		friendsView.getItems().add(new FriendClientBarItem(1, "Jenn"));
		friendsView.getItems().add(new FriendClientBarItem(2, "Hendrik"));
		friendsView.getItems().add(new FriendClientBarItem(3, "Max"));
		friendsView.getItems().add(new FriendClientBarItem(4, "Husseini"));
		friendsView.getItems().add(new FriendClientBarItem(5, "Jan"));
		friendsView.getItems().add(new FriendClientBarItem(6, "Florian"));
		friendsView.getItems().add(new FriendClientBarItem(7, "Moustafa"));
		friendsView.getItems().add(new FriendClientBarItem(8, "Osama"));
		friendsView.getItems().add(new FriendClientBarItem(9, "Peter"));
	}
	
	private void initNonFriendsView() {
		nonFriendsView = new JFXListView<ClientBarItem>();
		nonFriendsView.setCenterShape(true);
		nonFriendsView.setCellFactory((ListView<ClientBarItem> param) -> new ClientCell());
		
		nonFriendsView.getItems().add(new FriendClientBarItem(0, "Rozach"));
		nonFriendsView.getItems().add(new FriendClientBarItem(1, "Jenn"));
		nonFriendsView.getItems().add(new FriendClientBarItem(2, "Hendrik"));
		nonFriendsView.getItems().add(new FriendClientBarItem(3, "Max"));
		nonFriendsView.getItems().add(new FriendClientBarItem(4, "Husseini"));
		nonFriendsView.getItems().add(new FriendClientBarItem(5, "Jan"));
		nonFriendsView.getItems().add(new FriendClientBarItem(6, "Florian"));
		nonFriendsView.getItems().add(new FriendClientBarItem(7, "Pascal"));
		nonFriendsView.getItems().add(new FriendClientBarItem(8, "Mats"));
		nonFriendsView.getItems().add(new FriendClientBarItem(9, "Thure"));
		nonFriendsView.getItems().add(new FriendClientBarItem(10, "Carlo"));
		nonFriendsView.getItems().add(new FriendClientBarItem(11, "Nick"));
		nonFriendsView.getItems().add(new FriendClientBarItem(12, "Julius"));
		nonFriendsView.getItems().add(new FriendClientBarItem(13, "Anton"));
	}
	
	private void initOnlineView() {
		onlineView = new JFXListView<ClientBarItem>();
		onlineView.setCenterShape(true);
		onlineView.setCellFactory((ListView<ClientBarItem> param) -> new ClientCell());
		
		onlineView.getItems().add(new FriendClientBarItem(0, "Rozach"));
		onlineView.getItems().add(new FriendClientBarItem(1, "Jenn"));
		onlineView.getItems().add(new FriendClientBarItem(2, "Hendrik"));
		onlineView.getItems().add(new FriendClientBarItem(3, "Max"));
		onlineView.getItems().add(new FriendClientBarItem(4, "Husseini"));
		onlineView.getItems().add(new FriendClientBarItem(5, "Jan"));
		onlineView.getItems().add(new FriendClientBarItem(6, "Florian"));
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

	public void attachClient(SecuredChatClient client) {
		
	}
	
	/*
	 * 
	 */

	private class ClientCell extends JFXListCell<ClientBarItem> {
		
		public ClientCell() {
			super();
			setEditable(false);
		}
		
		@Override
		public void updateItem(ClientBarItem item, boolean empty) {
			super.updateItem(item, empty);
			if (empty)
				updateEmptyItem();
			else if (item instanceof ClientBarMemberItem){
				ClientBarMemberItem member = (ClientBarMemberItem)item;
				setText(member.getUsername());
				
				Image pic = new Image("/resources/icons/member.png", 32d, 32d, true, true);
				Circle profilePic = new Circle(16d);
				profilePic.setFill(new ImagePattern(pic));
				profilePic.setStroke(Color.GREEN);
				profilePic.setStrokeWidth(1d);
				
				HBox graphicBox = new HBox(profilePic, GUITools.createHorizontalSpacer(10d));
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
