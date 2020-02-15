//package gui.client.components.messages;
//
//import com.jfoenix.controls.JFXProgressBar;
//import com.jfoenix.controls.JFXSpinner;
//
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
//import javafx.application.Platform;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Label;
//import javafx.scene.input.MouseButton;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.FontWeight;
//import utils.Utils;
//
//public class CFileDownloadMessage extends GUIMessage implements FileMessage {
//	private final String key;
//
//	private VBox content;
//	private HBox messageIconLabel;
//	private HBox messageProgress;
//	private HBox downloadAndTimeBox;
//
//	private JFXProgressBar progressBar;
//	private JFXSpinner progressSpinner;
//	private Label downloadLabel;
//	private Label messageLabel;
//	private Label retryLabel;
//	private Label cancelLabel;
//	private Label resultLabel;
//	private Label fileLabel;
//
//	private ObjectProperty<FileClientState> stateProperty;
//	private EventHandler<MouseEvent> onDownloadClicked;
//
//	public CFileDownloadMessage(String sender, String key, String fileName) {
//		super(sender, fileName);
//
//		this.key = key;
//
//		this.progressBar = new JFXProgressBar(0d);
//		this.progressBar.setPrefHeight(5d);
//		this.progressBar.setSecondaryProgress(1d);
//
//		this.progressSpinner = new JFXSpinner();
//		this.progressSpinner.setVisible(false);
//		this.progressSpinner.setRadius(10d);
//
//		this.downloadLabel = new Label();
//		this.downloadLabel.setOnMouseClicked(a -> {
//			if (a.getButton() == MouseButton.PRIMARY) {
//				this.onDownloadClicked.handle(a);
//				System.out.println("Download clicked");
//			}
//		});
//		this.downloadLabel.setPickOnBounds(false);
//		FontAwesomeIconView downloadIcon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
//		downloadIcon.setGlyphSize(30d);
//		downloadIcon.setFill(Color.GREEN);
//		this.downloadLabel.setGraphic(downloadIcon);
//
//		this.messageLabel = new Label(this.getMessage());
//		this.messageLabel.setFont(Utils.CFont(18d, FontWeight.BOLD));
//
//		this.retryLabel = new Label();
//		this.retryLabel.setPickOnBounds(false);
//		FontAwesomeIconView retryIcon = new FontAwesomeIconView(FontAwesomeIcon.REPEAT);
//		retryIcon.setGlyphSize(20d);
//		retryIcon.setFill(Color.GREEN);
//		this.retryLabel.setGraphic(retryIcon);
//
//		this.cancelLabel = new Label();
//		this.cancelLabel.setPickOnBounds(false);
//		FontAwesomeIconView cancelIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
//		cancelIcon.setGlyphSize(20d);
//		cancelIcon.setFill(Color.RED);
//		this.cancelLabel.setGraphic(cancelIcon);
//
//		this.resultLabel = new Label();
//		this.resultLabel.setPadding(new Insets(10d, 0d, 10d, 0d));
//		this.resultLabel.setFont(Utils.CFont(15d, FontWeight.SEMI_BOLD));
//
//		this.stateProperty = new SimpleObjectProperty<>(FileClientState.NEW);
//
//		this.messageIconLabel = new HBox(10d);
//		this.messageIconLabel.setAlignment(Pos.CENTER_LEFT);
//		this.messageIconLabel.setPadding(new Insets(0d, 0d, 15d, 0d));
//		this.fileLabel = new Label();
//		FontAwesomeIconView fileIcon = new FontAwesomeIconView(
//				FileMessage.getFileIcon(this.getMessage().substring(this.getMessage().lastIndexOf(".") + 1)));
//		fileIcon.fillProperty().bind(this.messageLabel.textFillProperty());
//		fileIcon.setGlyphSize(30d);
//		this.fileLabel.setGraphic(fileIcon);
//		this.messageIconLabel.getChildren().addAll(this.downloadLabel, this.messageLabel);
//
//		this.messageProgress = new HBox(8d);
//		this.messageProgress.setPadding(new Insets(0d, 0d, 0d, 0d));
//		this.messageProgress.setAlignment(Pos.CENTER_LEFT);
//		HBox.setHgrow(this.progressBar, Priority.ALWAYS);
//		this.messageProgress.getChildren().addAll(this.cancelLabel, this.progressBar);
//
//		this.content = new VBox(this.messageIconLabel);
//		this.content.setAlignment(Pos.CENTER_LEFT);
//		this.content.setMinWidth(250d);
//		this.content.setSpacing(5d);
//		this.content.setFillWidth(true);
//		CFileDownloadMessage.setMargin(this.content, new Insets(15d));
//
//		this.downloadAndTimeBox = new HBox(this.progressSpinner);
//		this.downloadAndTimeBox.setSpacing(5d);
//		this.downloadAndTimeBox.setAlignment(Pos.BOTTOM_RIGHT);
//		HBox spacer = new HBox();
//		spacer.setMinWidth(250d);
//		HBox.setHgrow(spacer, Priority.SOMETIMES);
//		this.downloadAndTimeBox.getChildren().addAll(spacer, this.getTimeLabel());
//
//		this.stateProperty.addListener(new FileClientStateListener());
//		this.onDownloadClicked = a -> {
//		};
//
//		this.setCenter(this.content);
//		this.setBottom(this.downloadAndTimeBox);
//		BorderPane.setMargin(this.downloadAndTimeBox, new Insets(15d));
//	}
//
//	public String getKey() {
//		return this.key;
//	}
//
//	@Override
//	public double getProgress() {
//		return this.progressBar.getProgress();
//	}
//
//	@Override
//	public void setProgress(double value) {
//		Platform.runLater(() -> {
//			this.progressBar.setProgress(value);
//		});
//	}
//
//	@Override
//	public FileClientState getState() {
//		return this.stateProperty.get();
//	}
//
//	@Override
//	public void setState(FileClientState state) {
//		Platform.runLater(() -> {
//			this.stateProperty.set(state);
//		});
//	}
//
//	public void setOnDownloadClicked(EventHandler<MouseEvent> me) {
//		Platform.runLater(() -> this.downloadLabel.setOnMouseClicked(me));
//	}
//
//	@Override
//	public void setOnRetryClicked(EventHandler<MouseEvent> me) {
//		Platform.runLater(() -> this.retryLabel.setOnMouseClicked(me));
//	}
//
//	@Override
//	public void setOnCancelClicked(EventHandler<javafx.scene.input.MouseEvent> me) {
//		Platform.runLater(() -> this.cancelLabel.setOnMouseClicked(me));
//	}
//
//	/*
//	 * 
//	 */
//
//	public class FileClientStateListener implements ChangeListener<FileClientState> {
//		@Override
//		public void changed(ObservableValue<? extends FileClientState> obs, FileClientState oldV,
//				FileClientState newV) {
//			switch (newV) {
//			case REJECTED:
//				Platform.runLater(() -> {
//
//					messageLabel.setTextFill(Color.RED);
//					resultLabel.setText("Anfrage abgelehnt");
//					resultLabel.setTextFill(Color.ORANGERED);
//					// messageIconLabel.getChildren().add(0, downloadLabel);
//					messageIconLabel.setAlignment(Pos.CENTER_LEFT);
//					content.getChildren().remove(messageProgress);
//					content.getChildren().add(resultLabel);
//					progressSpinner.setVisible(false);
//					((HBox) getBottom()).getChildren().add(0, retryLabel);
//				});
//				break;
//			case LOADING:
//				Platform.runLater(() -> {
//
//					messageLabel.setTextFill(Color.YELLOWGREEN);
//					messageIconLabel.getChildren().remove(downloadLabel);
//					messageIconLabel.getChildren().remove(fileLabel);
//					messageIconLabel.setAlignment(Pos.CENTER_LEFT);
//					content.getChildren().add(1, messageProgress);
//					HBox.setHgrow(progressBar, Priority.ALWAYS);
//					content.getChildren().remove(resultLabel);
//					progressSpinner.setVisible(true);
//					((HBox) getBottom()).getChildren().remove(retryLabel);
//				});
//				break;
//			case SUCCEED:
//				Platform.runLater(() -> {
//					messageLabel.setTextFill(Color.DARKGREEN);
//					resultLabel.setText("Download erfolgreich");
//					resultLabel.setTextFill(Color.GREEN);
//					messageIconLabel.getChildren().add(0, fileLabel);
//					content.getChildren().remove(messageProgress);
//					content.getChildren().add(resultLabel);
//					progressSpinner.setVisible(false);
//					((HBox) getBottom()).getChildren().remove(retryLabel);
//				});
//				break;
//			case FAILED:
//				Platform.runLater(() -> {
//					messageLabel.setTextFill(Color.RED);
//					resultLabel.setText("Download fehlgeschlagen");
//					resultLabel.setTextFill(Color.ORANGERED);
//					// messageIconLabel.getChildren().add(0, downloadLabel);
//					content.getChildren().remove(messageProgress);
//					content.getChildren().add(resultLabel);
//					progressSpinner.setVisible(false);
//					((HBox) getBottom()).getChildren().add(0, retryLabel);
//				});
//				break;
//			case ERROR:
//				Platform.runLater(() -> {
//					messageLabel.setTextFill(Color.RED);
//					resultLabel.setText("Fehler");
//					resultLabel.setTextFill(Color.ORANGERED);
//					// messageIconLabel.getChildren().add(0, downloadLabel);
//					content.getChildren().remove(messageProgress);
//					content.getChildren().add(resultLabel);
//					progressSpinner.setVisible(false);
//					((HBox) getBottom()).getChildren().add(0, retryLabel);
//				});
//				break;
//			default:
//				break;
//			}
//		}
//	}
//}
