package gui.client.components.messages;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import gui.client.components.contextMenu.CustomContextMenu;
import gui.client.components.contextMenu.MenuItemButton;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import network.ssl.client.message.CMessage;
import utils.Utils;

public class GUIMessage extends HBox {
	private StringProperty senderProperty;
	private StringProperty messageProperty;
	private StringProperty timeProperty;
	private ObjectProperty<Color> colorProperty;
	private ObjectProperty<MessageStatus> statusProperty;

	private BorderPane rootContent;
	private Label senderLabel;
	private Label timeLabel;

	private CustomContextMenu contextMenu;

	public GUIMessage(CMessage message) {
		this(message.getSender(), message.getMessage());
	}

	public GUIMessage(String sender, String message) {
		getStylesheets().add("/stylesheets/client/GUIMessage.css");
		getStyleClass().add("gui-message");
		
		senderProperty = new SimpleStringProperty(sender != null ? sender : "");
		messageProperty = new SimpleStringProperty(message != null ? message : "");
		timeProperty = new SimpleStringProperty(Utils.getCurrentTimeHM());
		colorProperty = new SimpleObjectProperty<>(Color.BLACK);
		colorProperty.addListener(new ColorListener());
		statusProperty = new SimpleObjectProperty<>(MessageStatus.NEW);
		
		rootContent = new BorderPane();
		contextMenu = createContextMenu();
		
		senderLabel = new Label(getSender());
		senderLabel.setTextFill(getColor());
		senderLabel.setFont(Utils.CFont(18d, FontWeight.BOLD));
		
		Image pic = new Image("/resources/icons/member.png", 26d, 26d, true, true);
		Circle profilePic = new Circle(13d);
		profilePic.setFill(new ImagePattern(pic));
		profilePic.setStroke(Color.GREEN);
		profilePic.setStrokeWidth(2d);
		
		HBox senderBox = new HBox(10d);
		senderBox.setAlignment(Pos.CENTER_LEFT);
		senderBox.setPadding(new Insets(10d, 10d, 10d, 10d));
		senderBox.getChildren().addAll(profilePic, senderLabel);

		FlowPane messagePane = splitMessage();
		
		timeLabel = new Label(getTime());
		timeLabel.setTextFill(Color.web("#" + getColor().toString().substring(2)));
		timeLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 13d));

		BorderPane.setAlignment(senderLabel, Pos.TOP_CENTER);
		BorderPane.setMargin(senderLabel, new Insets(5d, 0d, 10d, 0d));
		BorderPane.setAlignment(messagePane, Pos.CENTER_LEFT);
		BorderPane.setMargin(messagePane, new Insets(0d, 10d, 10d, 10d));
		BorderPane.setAlignment(timeLabel, Pos.BOTTOM_RIGHT);
		BorderPane.setMargin(timeLabel, new Insets(0d, 10d, 5d, 0d));

		setOnContextMenuRequested(a -> onContextMenuRequested(a));
		setOnMouseClicked(b -> onMouseClicked(b));

		//rootContent.setStyle("-fx-border-color: #" + getColor().toString().substring(2));
		rootContent.setMinSize(0d, 0d);
		rootContent.setTop(senderBox);
		rootContent.setCenter(messagePane);
		rootContent.setBottom(timeLabel);
		
		getChildren().add(rootContent);
		HBox.setHgrow(rootContent, Priority.ALWAYS);
	}

	public static Image getSmileyFor(String value) {
		try {
			if(value == null || value.isEmpty())
				return null;
			if(value.startsWith(":"))
				value = value.substring(1);
			if(value.endsWith(":"))
				value = value.substring(0, value.length() - 1);
			String prefix; // Category (A, B, C, ..., H)
			String infix; // Index (1, 2, 3, ...)
			String postfix; // Skin color (A, B, C, D, E or F)
			if (value != null && isSmiley(value)) {
				prefix = value.substring(1, 2);
				infix = value.substring(2, value.length() - 2);
				postfix = value.substring(value.length() - 2, value.length() - 1);
				return new Image("/resources/smileys/category" + prefix + "/" + prefix + infix + postfix + ".png");
			} 
			else
				return null;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isLink(String value) {
		return value.startsWith("http://") || value.startsWith("https://") || value.startsWith("www.");
	}

	public static boolean isSmiley(String value) {
		return (value.startsWith(":") && value.endsWith(":") && value.length() > 3);
	}

	private FlowPane splitMessage() {
		return splitMessage(null);
	}

	private FlowPane splitMessage(Font font) {
		FlowPane messagePane = new FlowPane();
		messagePane.setAlignment(Pos.CENTER_LEFT);
		messagePane.setHgap(3d);
		messagePane.setVgap(3d);

		if (getMessage() != null || !getMessage().isEmpty()) {
			String[] words = getMessage().split("\\s+");
			for (String w : words) {
				if (isLink(w)) // Falls Link
				{
					Hyperlink link = new Hyperlink(w);
					link.setFont(font == null ? Utils.CFont(18) : font);

					link.setOnAction(a -> {
						try {
							Desktop.getDesktop().browse(URI.create(w));
							link.setVisited(true);
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					});

					messagePane.getChildren().add(link);
				} 
				else if (isSmiley(w)) // Falls Smiley
				{
					Image smiley = getSmileyFor(w);
					if (smiley != null) {
						messagePane.getChildren().add(new ImageView(smiley));
					} 
					else {
						Label messageLabel = new Label(w);
						messageLabel.setFont(font == null ? Utils.CFont(18) : font);
						messagePane.getChildren().add(messageLabel);
					}
				} 
				//Ansonsten normaler Text
				else {
					Label messageLabel = new Label(w);
					messageLabel.setFont(font == null ? Utils.CFont(18) : font);
					messagePane.getChildren().add(messageLabel);
				}
			}
		}
		return messagePane;
	}

	private CustomContextMenu createContextMenu() {
		MenuItemButton m1 = new MenuItemButton("Nachricht kopieren");
		m1.setOnMouseClicked(a -> {
			if (a.getButton() == MouseButton.PRIMARY) {
				String value = getMessage();
				Clipboard cb = Clipboard.getSystemClipboard();
				ClipboardContent ct = new ClipboardContent();
				ct.putString(value);
				cb.setContent(ct);
			}
		});

		MenuItemButton m2 = new MenuItemButton("Nachricht loeschen");
		m2.setOnMouseClicked(a -> {
			if (a.getButton() == MouseButton.PRIMARY) {
				System.out.println(getParent() instanceof BorderPane);
			}
		});
		CustomContextMenu menu = new CustomContextMenu(m1, m2);
		return menu;
	}

	private void onContextMenuRequested(ContextMenuEvent menu) {
		this.contextMenu.showAnimated(this, menu.getScreenX(), menu.getScreenY());
		menu.consume();
	}

	private void onMouseClicked(MouseEvent mouse) {
		if (mouse.getButton() == MouseButton.PRIMARY && contextMenu.isAutoHide())
			contextMenu.hide();
	}

	/*
	 * 
	 */

	public StringProperty senderProperty() {
		return senderProperty;
	}

	public String getSender() {
		return senderProperty.get();
	}

	public void setSender(String value) {
		senderProperty.set(value);
	}

	public StringProperty messageProperty() {
		return messageProperty;
	}

	public String getMessage() {
		return messageProperty.get();
	}

	public void setMessage(String value) {
		messageProperty.set(value);
	}

	public StringProperty timeProperty() {
		return timeProperty;
	}

	public String getTime() {
		return timeProperty.get();
	}

	public void setTime(String value) {
		timeProperty.set(value);
	}

	public ObjectProperty<MessageStatus> colorProperty() {
		return statusProperty();
	}

	public Color getColor() {
		return colorProperty.get();
	}

	public void setColor(Color value) {
		colorProperty.set(value);
	}

	public ObjectProperty<MessageStatus> statusProperty() {
		return statusProperty();
	}

	public MessageStatus getStatus() {
		return statusProperty.get();
	}

	public void setStatus(MessageStatus value) {
		statusProperty.set(value);
	}

	public Label getSenderLabel() {
		return senderLabel;
	}

	public Label getTimeLabel() {
		return timeLabel;
	}

	public class ColorListener implements ChangeListener<Color> {
		@Override
		public void changed(ObservableValue<? extends Color> obs, Color oldV, Color newV) {
			Platform.runLater(() -> {
				setStyle("-fx-border-color: #" + newV.toString().substring(2));
				senderLabel.setTextFill(newV);
			});
		}
	}
}
