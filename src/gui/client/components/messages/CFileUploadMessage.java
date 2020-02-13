package gui.client.components.messages;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import utils.Utils;

public class CFileUploadMessage extends GUIMessage implements FileMessage {
	private VBox content;
	private HBox messageIconLabel;
	private HBox messageProgress;
	private HBox messageProgressStatus;
	private HBox uploadAndTimeBox;

	private JFXProgressBar progressBar;
	private JFXSpinner progressSpinner;
	private Label messageLabel;
	private Label progressSpeed;
	private Label progressStatus;
	private Label retryLabel;
	private Label cancelLabel;
	private Label resultLabel;
	private FontAwesomeIconView fileIcon;

	private RotateTransition retryLabelAnimation;
	private ScaleTransition cancelLabelAnimation;

	private ObjectProperty<FileClientState> stateProperty;

	public CFileUploadMessage(String sender, String fileName) {
		super(sender, fileName);

		this.progressBar = new JFXProgressBar(0d);
		this.progressBar.progressProperty()
				.addListener((obs, oldV, newV) -> this.progressStatus.setText((int) (newV.doubleValue() * 100d) + "%"));
		this.progressBar.setPrefWidth(300d);
		this.progressBar.setPrefHeight(8d);
		this.progressBar.setSecondaryProgress(1d);
		this.progressSpinner = new JFXSpinner();
		this.progressSpinner.setVisible(false);
		this.progressSpinner.setRadius(10d);

		this.retryLabel = new Label();
		this.retryLabel.setPickOnBounds(false);
		FontAwesomeIconView retryIcon = new FontAwesomeIconView(FontAwesomeIcon.REPEAT);
		retryIcon.setGlyphSize(20d);
		retryIcon.setFill(Color.GREEN);
		retryLabel.setGraphic(retryIcon);

		this.cancelLabel = new Label();
		this.cancelLabel.setPickOnBounds(false);
		FontAwesomeIconView cancelIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
		cancelIcon.setGlyphSize(20d);
		cancelIcon.setFill(Color.RED);
		this.cancelLabel.setGraphic(cancelIcon);

		this.resultLabel = new Label();
		this.resultLabel.setPadding(new Insets(10d, 0d, 10d, 0d));
		this.resultLabel.setFont(Utils.CFont(15d, FontWeight.SEMI_BOLD));

		this.stateProperty = new SimpleObjectProperty<>(FileClientState.NEW);

		this.messageIconLabel = new HBox(10d);
		this.messageIconLabel.setAlignment(Pos.CENTER_LEFT);
		this.messageIconLabel.setPadding(new Insets(0d, 0d, 15d, 0d));
		this.messageLabel = new Label(this.getMessage());
		this.messageLabel.setFont(Utils.CFont(16d, FontWeight.BOLD));
		this.fileIcon = new FontAwesomeIconView(
				FileMessage.getFileIcon(this.getMessage().substring(this.getMessage().lastIndexOf(".") + 1)));
		this.fileIcon.fillProperty().bind(this.messageLabel.textFillProperty());
		this.fileIcon.setGlyphSize(30d);
		this.messageIconLabel.getChildren().addAll(this.messageLabel);

		this.retryLabelAnimation = new RotateTransition(Duration.seconds(0.3d), this.retryLabel);
		this.retryLabelAnimation.setOnFinished(a -> {
			Platform.runLater(() -> {
				messageLabel.setTextFill(Color.DARKGREEN);
				resultLabel.setTextFill(Color.GREEN);
				resultLabel.setText("Upload wird vorbereitet...");
				((HBox) getBottom()).getChildren().remove(retryLabel);
				progressSpinner.setVisible(true);
			});
		});
		this.retryLabelAnimation.setInterpolator(Interpolator.EASE_BOTH);
		this.retryLabelAnimation.setFromAngle(0d);
		this.retryLabelAnimation.setToAngle(360d);

		this.cancelLabelAnimation = new ScaleTransition(Duration.seconds(0.5d), this.cancelLabel);
		this.cancelLabelAnimation.setInterpolator(Interpolator.EASE_BOTH);
		this.cancelLabelAnimation.setFromX(1d);
		this.cancelLabelAnimation.setFromY(1d);
		this.cancelLabelAnimation.setToX(0d);
		this.cancelLabelAnimation.setToY(0d);

		this.messageProgress = new HBox(8d);
		this.messageProgress.setPadding(new Insets(0d, 0d, 0d, 0d));
		this.messageProgress.setAlignment(Pos.CENTER_LEFT);
		this.messageProgress.getChildren().addAll(this.cancelLabel, this.progressBar);
		HBox.setHgrow(this.progressBar, Priority.ALWAYS);

		this.messageProgressStatus = new HBox(0d);
		this.messageProgressStatus.setPadding(new Insets(0d, 0d, 0d, 0d));
		this.messageProgressStatus.setAlignment(Pos.CENTER_LEFT);
		this.progressSpeed = new Label();
		this.progressSpeed.setTextFill(Color.GREEN);
		this.progressSpeed.setFont(Utils.CFont(12d, FontWeight.SEMI_BOLD));
		this.progressStatus = new Label();
		this.progressStatus.setFont(Utils.CFont(12d, FontWeight.SEMI_BOLD));
		this.progressStatus.setTextFill(Color.GREEN);
		HBox progressStatusSpacer = new HBox();
		HBox.setHgrow(progressStatusSpacer, Priority.SOMETIMES);
		this.messageProgressStatus.getChildren().addAll(this.progressSpeed, progressStatusSpacer, this.progressStatus);

		this.content = new VBox(this.messageIconLabel);
		this.content.setAlignment(Pos.CENTER_LEFT);
		this.content.setMinWidth(250d);
		this.content.setSpacing(5d);
		this.content.setFillWidth(true);
		CFileUploadMessage.setMargin(this.content, new Insets(15d));

		this.uploadAndTimeBox = new HBox(this.progressSpinner);
		this.uploadAndTimeBox.setSpacing(5d);
		this.uploadAndTimeBox.setAlignment(Pos.BOTTOM_LEFT);
		HBox spacer = new HBox();
		spacer.setMinWidth(200d);
		HBox.setHgrow(spacer, Priority.SOMETIMES);
		this.uploadAndTimeBox.getChildren().addAll(spacer, this.getTimeLabel());
		this.getTimeLabel().setTextFill(Color.web("#" + this.getColor().toString().substring(2)));

		this.stateProperty.addListener(new FileClientStateListener());

		this.setPrefSize(357d, 190d);
		this.setCenter(this.content);
		this.setBottom(this.uploadAndTimeBox);
		BorderPane.setMargin(this.uploadAndTimeBox, new Insets(15d));
	}

	@Override
	public double getProgress() {
		return this.progressBar.getProgress();
	}

	@Override
	public void setProgress(double value) {
		Platform.runLater(() -> {
			this.progressBar.setProgress(value);
		});
	}

	@Override
	public FileClientState getState() {
		return this.stateProperty.get();
	}

	@Override
	public void setState(FileClientState state) {
		Platform.runLater(() -> {
			this.stateProperty.set(state);
		});
	}

	@Override
	public void setOnRetryClicked(EventHandler<javafx.scene.input.MouseEvent> me) {
		Platform.runLater(() -> {
			this.retryLabel.setOnMouseClicked(a -> {
				if (this.retryLabelAnimation != null && this.retryLabelAnimation.getStatus() != Status.RUNNING) {
					me.handle(a);
					this.retryLabelAnimation.playFromStart();
				}
			});
		});
	}

	@Override
	public void setOnCancelClicked(EventHandler<javafx.scene.input.MouseEvent> me) {
		Platform.runLater(() -> {
			this.cancelLabel.setOnMouseClicked(a -> {
				if (this.cancelLabelAnimation != null && this.cancelLabelAnimation.getStatus() != Status.RUNNING) {
					me.handle(a);
					this.cancelLabelAnimation.playFromStart();
				}
			});
		});
	}

	/*
	 * 
	 */

	public class FileClientStateListener implements ChangeListener<FileClientState> {
		@Override
		public void changed(ObservableValue<? extends FileClientState> obs, FileClientState oldV,
				FileClientState newV) {
			if (newV != oldV) {
				switch (newV) {
				case REJECTED:
					this.failure("Upload abgelehnt.");
					break;
				case LOADING:
					this.loading();
					break;
				case SUCCEED:
					this.success("Upload erfolgreich.");
					break;
				case FAILED:
					this.failure("Upload fehlgeschlagen.");
					break;
				case ERROR:
					this.failure("Upload Fehler.");
					break;
				default:
					break;
				}
			}
		}

		private void success(String message) {
			Platform.runLater(() -> {
				System.out.println(getHeight());
				messageLabel.setTextFill(Color.DARKGREEN);
				resultLabel.setText(message != null && !message.isEmpty() ? message : "Upload erfolgreich.");
				resultLabel.setTextFill(Color.GREEN);
				messageIconLabel.getChildren().add(0, fileIcon);
				content.getChildren().remove(messageProgress);
				content.getChildren().remove(messageProgressStatus);
				content.getChildren().add(resultLabel);
				progressSpinner.setVisible(false);
			});

		}

		private void failure(String message) {
			Platform.runLater(() -> {
				messageLabel.setTextFill(Color.RED);
				resultLabel.setText(message != null && !message.isEmpty() ? message : "Upload fehlgeschlagen.");
				resultLabel.setTextFill(Color.ORANGERED);
				messageIconLabel.getChildren().set(0, fileIcon);
				content.getChildren().remove(messageProgress);
				content.getChildren().remove(messageProgressStatus);
				// content.getChildren().add(resultLabel);
				progressSpinner.setVisible(false);
				((HBox) getBottom()).getChildren().add(0, retryLabel);
			});
		}

		private void loading() {
			System.out.println(getWidth());
			Platform.runLater(() -> {
				messageLabel.setTextFill(Color.YELLOWGREEN);
				messageIconLabel.getChildren().remove(fileIcon);
				messageIconLabel.setAlignment(Pos.CENTER_LEFT);
				cancelLabel.setScaleX(1d);
				cancelLabel.setScaleY(1d);
				content.getChildren().add(1, messageProgress);
				content.getChildren().add(2, messageProgressStatus);
				content.getChildren().remove(resultLabel);
				progressSpinner.setVisible(true);
				((HBox) getBottom()).getChildren().remove(retryLabel);
			});
		}
	}
}
