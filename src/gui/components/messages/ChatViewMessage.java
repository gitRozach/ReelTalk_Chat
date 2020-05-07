package gui.components.messages;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import com.jfoenix.controls.JFXScrollPane;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import utils.FXUtils;
import utils.StringUtils;
import utils.Utils;

public class ChatViewMessage extends BorderPane {
	private StringProperty senderProperty;
	private StringProperty messageProperty;
	private LongProperty timeProperty;
	private ObjectProperty<Color> colorProperty;
	private ObjectProperty<MessageStatus> statusProperty;
	
	private Circle senderPictureCircle;
	private Label senderLabel;
	private FontAwesomeIconView menuIcon;
	private Label timeLabel;
	
	private HBox senderBox;
	private FlowPane messageFlow;
	private JFXScrollPane attachedFilesScrollPane;
	private HBox statusBox;
	
	private EventHandler<ActionEvent> onHyperlinkClickedHandler;
	
	public ChatViewMessage() {
		initStylesheets();
		initProperties();
		initEventHandlers();
		initControls();
		initAttachedFilesScrollPane();
		initRootPane();
	}

	public static Image getSmileyFor(String value) {
		if(value == null || value.isEmpty())
			return null;
		try {
			if (isSmiley(StringUtils.trimHeadAndTail(value))) {
				String prefix = value.substring(1, 2); // Category (A, B, C, ..., H)
				String infix = value.substring(2, value.length() - 2); // Index (1, 2, 3, ...)
				String postfix = value.substring(value.length() - 2, value.length() - 1); // Skin color (A, B, C, D, E or F)
				return new Image("/resources/smileys/category" + prefix + "/" + prefix + infix + postfix + ".png");
			} 
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static boolean isLink(String value) {
		return value != null && (value.startsWith("http://") || value.startsWith("https://") || value.startsWith("www."));
	}

	public static boolean isSmiley(String value) {
		return value != null && value.startsWith(":") && value.endsWith(":") && value.length() > 3;
	}

	private FlowPane splitMessage() {
		return splitMessage(null);
	}

	private FlowPane splitMessage(Font font) {
		FlowPane messagePane = new FlowPane();
		messagePane.setPadding(new Insets(10d, 15d, 0d, 15d));
		messagePane.setAlignment(Pos.CENTER_LEFT);
		messagePane.setHgap(3d);
		messagePane.setVgap(3d);

		if (!getMessage().isEmpty()) {
			String[] words = getMessage().split("\\s+");
			for (String w : words) {
				if (isLink(w)) {
					Hyperlink link = new Hyperlink(w);
					link.setFont(font == null ? FXUtils.Font(18) : font);
					link.setOnAction(a -> onHyperlinkClickedHandler.handle(a));
					messagePane.getChildren().add(link);
				} 
				else {
					Image smiley = null;
					if(isSmiley(w) && (smiley = getSmileyFor(w)) != null)
						messagePane.getChildren().add(new ImageView(smiley));
					else {
						Label messageLabel = new Label(w);
						messageLabel.setFont(font == null ? FXUtils.Font(18) : font);
						messagePane.getChildren().add(messageLabel);
					}
				}
			}
		}
		return messagePane;
	}
	
	private void initStylesheets() {
		getStylesheets().add("/stylesheets/client/defaultStyle/ChatViewMessage.css");
		getStyleClass().add("gui-message");
	}
	
	private void initProperties() {
		senderProperty = new SimpleStringProperty("");
		messageProperty = new SimpleStringProperty("");
		timeProperty = new SimpleLongProperty(System.currentTimeMillis());
		colorProperty = new SimpleObjectProperty<>(Color.BLACK);
		statusProperty = new SimpleObjectProperty<>(MessageStatus.NEW);
		statusProperty.addListener((obs, oldV, newV) -> updateMessage(newV));
	}
	
	private void initEventHandlers() {
		setOnMouseEntered(a -> menuIcon.setVisible(true));
		setOnMouseExited(b -> menuIcon.setVisible(false));
		onHyperlinkClickedHandler = a -> {
			try {
				if(a.getSource() == null || !(a.getSource() instanceof Hyperlink))
					return;
				Hyperlink link = (Hyperlink)a.getSource();
				Desktop.getDesktop().browse(URI.create(StringUtils.trimHeadAndTail(link.getText())));
				link.setVisited(true);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		};
	}
	
	private void initControls() {
		Image pic = new Image("/resources/icons/member.png", 26d, 26d, true, true);
		senderPictureCircle = new Circle(13d);
		senderPictureCircle.setFill(new ImagePattern(pic));
		senderPictureCircle.setStroke(Color.GREEN);
		senderPictureCircle.setStrokeWidth(2d);
		
		senderLabel = new Label(getSender());
		senderLabel.setTextFill(getColor());
		senderLabel.setFont(FXUtils.Font(18d, FontWeight.BOLD));
		
		HBox senderSpace = new HBox();
		
		menuIcon = new FontAwesomeIconView(FontAwesomeIcon.ELLIPSIS_V);
		menuIcon.setGlyphSize(20d);
		menuIcon.setVisible(false);
		
		senderBox = new HBox(10d);
		senderBox.setAlignment(Pos.CENTER_LEFT);
		senderBox.getChildren().addAll(senderPictureCircle, senderLabel, senderSpace, menuIcon);
		HBox.setHgrow(senderSpace, Priority.ALWAYS);

		messageFlow = splitMessage();
		
		timeLabel = new Label(Utils.durationToHHMM(Duration.millis(getTime())));
		timeLabel.setTextFill(Color.web("#" + getColor().toString().substring(2)));
		timeLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 13d));
		
		statusBox = new HBox(10d);
		statusBox.setAlignment(Pos.CENTER_RIGHT);
		statusBox.getChildren().addAll(timeLabel);
	}
	
	private void initAttachedFilesScrollPane() {
		attachedFilesScrollPane = new JFXScrollPane();
		attachedFilesScrollPane.setDisable(true);
	}
	
	private void initRootPane() {
		BorderPane.setMargin(senderBox, new Insets(10d, 15d, 10d, 15d));
		BorderPane.setMargin(messageFlow, new Insets(10d, 15d, 10d, 15d));
		BorderPane.setMargin(statusBox, new Insets(10d, 15d, 10d, 15d));

		setMinSize(0d, 0d);
		setTop(senderBox);
		setCenter(messageFlow);
		setBottom(statusBox);
	}

	private void updateMessage(MessageStatus status) {
		
	}

	public StringProperty senderProperty() {
		return senderProperty;
	}

	public String getSender() {
		return senderProperty.get();
	}

	public void setSender(String value) {
		senderProperty.set(value);
		Platform.runLater(() -> senderLabel.setText(value));
	}

	public StringProperty messageProperty() {
		return messageProperty;
	}

	public String getMessage() {
		return messageProperty.get();
	}

	public void setMessage(String value) {
		messageProperty.set(value);
		Platform.runLater(() ->  {
			messageFlow = splitMessage();
			setCenter(messageFlow);
		});
	}

	public LongProperty timeProperty() {
		return timeProperty;
	}

	public long getTime() {
		return timeProperty.get();
	}

	public void setTime(long value) {
		timeProperty.set(value);
		Platform.runLater(() -> timeLabel.setText(Utils.durationToHHMM(Duration.millis(value))));
	}

	public ObjectProperty<MessageStatus> colorProperty() {
		return statusProperty();
	}

	public Color getColor() {
		return colorProperty.get();
	}

	public void setColor(Color value) {
		colorProperty.set(value);
		Platform.runLater(() -> {
			setStyle("-fx-border-color: #" + value.toString().substring(2));
			senderLabel.setTextFill(value);
		});
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
	
	public EventHandler<ActionEvent> getOnHyperlinkClicked(){
		return onHyperlinkClickedHandler;
	}
	
	public void setOnHyperlinkClicked(EventHandler<ActionEvent> handler) {
		onHyperlinkClickedHandler = handler;
	}
}
