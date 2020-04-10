package apps.audioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import gui.animations.Animations;
import gui.animations.FadeTranslateAnimation;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class AudioPlayerUI extends VBox {
	private IntegerProperty audioSpectrumBandsProperty;
	private BooleanProperty showingLibraryProperty;
	private BooleanProperty showingSettingsProperty;
	
	private AudioLibrary library;
	private ImageView thumbnailView;
	
	private Label textTitle;
	private Label textAlbum;
	private Label textArtist;
	private Label textYear;
	
	private VBox settingsBox;
	private VBox audioSpectrumVisualizer;
	private Label labelCurrentTime;
	private Label labelTotalTime;

	private JFXSlider timeSlider;
	private JFXSlider volumeSlider;
	private JFXSlider rateSlider;
	
	private JFXButton buttonVolume;
	private JFXButton buttonRate;
	private JFXButton buttonLibrary;
	private JFXButton buttonRepeat;
	
	private JFXButton buttonPrevious;
	private JFXButton buttonPlayPause;
	private JFXButton buttonNext;
	
	private Label labelReplayFrom;
	private Label labelReplayTo;
	
	private JFXSlider replayFromSlider;
	private JFXSlider replayToSlider;
	
	private Transition showSettingsAnimation;
	private Transition hideSettingsAnimation;
	private Transition showLibraryAnimation;
	private Transition hideLibraryAnimation;
	private FadeTranslateAnimation showDescriptionAnimation;
	private FadeTranslateAnimation hideDescriptionAnimation;

	public static final Image IMG_PLAY = new Image("/resources/icons/img_play.png", 32d, 32d, true, true);
	public static final Image IMG_PAUSE = new Image("/resources/icons/img_pause.png", 32d, 32d, true, true);
	public static final Image IMG_PREVIOUS = new Image("/resources/icons/img_previous.png", 32d, 32d, true, true);
	public static final Image IMG_NEXT = new Image("/resources/icons/img_next.png", 32d, 32d, true, true);
	public static final Image IMG_VOLUME_MUTED = new Image("/resources/icons/img_volume_muted.png", 48d, 48d, true, true);
	public static final Image IMG_VOLUME_LOW = new Image("/resources/icons/img_volume_low.png", 48d, 48d, true, true);
	public static final Image IMG_VOLUME_MEDIUM = new Image("/resources/icons/img_volume_medium.png", 48d, 48d, true, true);
	public static final Image IMG_VOLUME_HIGH = new Image("/resources/icons/img_volume_high.png", 48d, 48d, true, true);
	public static final Image IMG_LIBRARY = new Image("/resources/icons/img_audio_library.png", 32d, 32d, true, true);
	public static final Image IMG_REPLAY_DEFAULT = new Image("/resources/icons/img_repeat_default.png", 32d, 32d, true, true);
	public static final Image IMG_REPLAY_SINGLE = new Image("/resources/icons/img_repeat_single.png", 32d, 32d, true, true);
	public static final Image IMG_SHUFFLE = new Image("/resources/icons/img_shuffle.png", 32d, 32d, true, true);
	
	public AudioPlayerUI() {
		super();

		getStylesheets().add("/stylesheets/client/defaultStyle/AudioPlayer.css");

		audioSpectrumBandsProperty = new SimpleIntegerProperty(25);
		showingLibraryProperty = new SimpleBooleanProperty(false);
		showingSettingsProperty = new SimpleBooleanProperty(false);

		setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

		thumbnailView = new ImageView(new Image("/resources/icons/cover.jpg", 256d, 256d, true, true, true));
		thumbnailView.setPreserveRatio(true);
		thumbnailView.setFitWidth(256d);
		thumbnailView.setFitHeight(256d);

		textTitle = new Label("-");
		textTitle.setStyle("-fx-text-fill: white;");
		textTitle.setFont(Font.font("Tahoma", 22d));

		textAlbum = new Label("-");
		textAlbum.setStyle("-fx-text-fill: white;");
		textAlbum.setFont(Font.font("Tahoma", 22d));

		textArtist = new Label("-");
		textArtist.setStyle("-fx-text-fill: white;");
		textArtist.setFont(Font.font("Tahoma", 22d));

		textYear = new Label("-");
		textYear.setStyle("-fx-text-fill: white;");
		textYear.setFont(Font.font("Tahoma", 22d));

		initAudioSpectrumVisualizer(getAudioSpectrumBands());
		VBox.setVgrow(audioSpectrumVisualizer, Priority.SOMETIMES);

		labelCurrentTime = new Label();
		labelCurrentTime.setFont(Font.font("Tahoma", 12d));

		labelTotalTime = new Label();
		labelTotalTime.setFont(Font.font("Tahoma", 12d));

		timeSlider = new JFXSlider();
		timeSlider.setValue(0d);

		volumeSlider = new JFXSlider(0d, 1d, 0.5d);

		rateSlider = new JFXSlider(0.5d, 2d, 1d);
		rateSlider.setSnapToTicks(true);
		rateSlider.setBlockIncrement(0.25d);
		rateSlider.setMinorTickCount(1);
		rateSlider.setMajorTickUnit(0.5d);

		buttonVolume = new JFXButton();
		buttonVolume.setManaged(true);
		buttonVolume.setGraphic(new ImageView(IMG_VOLUME_MEDIUM));

		buttonRate = new JFXButton();
		buttonRate.minWidthProperty().bind(buttonVolume.widthProperty());
		buttonRate.minHeightProperty().bind(buttonVolume.heightProperty());
		Label initRateLabel = new Label("1.0x");
		initRateLabel.setTextFill(Color.WHITE);
		initRateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
		buttonRate.setGraphic(initRateLabel);

		buttonPrevious = new JFXButton("", new ImageView(IMG_PREVIOUS));

		buttonPlayPause = new JFXButton("", new ImageView(IMG_PLAY));

		buttonNext = new JFXButton("", new ImageView(IMG_NEXT));

		library = new AudioLibrary();
		library.getStyleClass().add("c-audio-library");
		// library.setVisible(false);

		buttonLibrary = new JFXButton();
		buttonLibrary.setGraphic(new ImageView(IMG_LIBRARY));
		buttonLibrary.disableProperty().bind(Bindings.isEmpty(library.getItems()));

		buttonRepeat = new JFXButton();
		buttonRepeat.setGraphic(new ImageView(IMG_REPLAY_DEFAULT));

		labelReplayFrom = new Label("Von:");
		labelReplayFrom.setMinWidth(30d);
		labelReplayTo = new Label("Bis:");
		labelReplayTo.setMinWidth(30d);

		replayFromSlider = new JFXSlider();
		replayToSlider = new JFXSlider();

		// Text Nodes and ImageView
		VBox description = new VBox(textTitle, textAlbum, textArtist, textYear);
		description.setFillWidth(false);
		description.setSpacing(3d);

		StackPane descriptionAndLibrary = new StackPane(description, library);
		descriptionAndLibrary.setMaxHeight(thumbnailView.getFitHeight());
		showLibrary(false);

		HBox thumbnailAndDescrLibr = new HBox(thumbnailView, descriptionAndLibrary);
		HBox.setHgrow(descriptionAndLibrary, Priority.ALWAYS);
		thumbnailAndDescrLibr.setFillHeight(true);
		thumbnailAndDescrLibr.setSpacing(20d);
		HBox.setHgrow(audioSpectrumVisualizer, Priority.SOMETIMES);

		// Time Slider and Label Nodes
		HBox labelSpace = new HBox();
		HBox labelNodes = new HBox(labelCurrentTime, labelSpace, labelTotalTime);
		HBox.setHgrow(labelSpace, Priority.SOMETIMES);
		VBox sliderAndLabelNodes = new VBox(timeSlider, labelNodes);
		sliderAndLabelNodes.setAlignment(Pos.BOTTOM_CENTER);
		sliderAndLabelNodes.setSpacing(5d);

		// Button Nodes
		HBox spaceX1 = new HBox();
		HBox spaceX2 = new HBox();
		HBox controlNodes = new HBox(spaceX1, buttonPrevious, buttonPlayPause, buttonNext, spaceX2);
		HBox.setHgrow(spaceX1, Priority.SOMETIMES);
		HBox.setHgrow(spaceX2, Priority.SOMETIMES);
		controlNodes.setFillHeight(false);
		controlNodes.setAlignment(Pos.BOTTOM_CENTER);
		controlNodes.setPadding(new Insets(0d, 0d, 15d, 0d));

		VBox replayTool = new VBox(10d);
		HBox replayFrom = new HBox(labelReplayFrom, replayFromSlider);
		replayFrom.setSpacing(5d);
		HBox replayTo = new HBox(labelReplayTo, replayToSlider);
		replayTo.setSpacing(5d);
		replayTool.getChildren().addAll(replayFrom, replayTo);

		HBox volumeTool = new HBox(buttonVolume, volumeSlider);
		volumeTool.setManaged(true);
		volumeTool.setAlignment(Pos.CENTER_LEFT);
		HBox rateTool = new HBox(buttonRate, rateSlider);
		rateTool.setAlignment(Pos.CENTER_LEFT);

		settingsBox = new VBox();
		settingsBox.setPrefWidth(256d);
		settingsBox.setPadding(new Insets(15d));
		settingsBox.setSpacing(25d);
		settingsBox.getChildren().addAll(volumeTool, rateTool, replayTool);
		showSettings(false);

		HBox settingsAndVisualizer = new HBox(settingsBox, audioSpectrumVisualizer);
		settingsAndVisualizer.setPadding(new Insets(10d, 0d, 10d, 0d));
		settingsAndVisualizer.setOnMouseEntered(a -> showSettingsAnimated(true));
		settingsAndVisualizer.setOnMouseExited(b -> showSettingsAnimated(false));

		HBox.setHgrow(volumeSlider, Priority.SOMETIMES);
		HBox.setHgrow(rateSlider, Priority.SOMETIMES);
		HBox.setHgrow(replayFromSlider, Priority.SOMETIMES);
		HBox.setHgrow(replayToSlider, Priority.SOMETIMES);
		HBox.setHgrow(library, Priority.SOMETIMES);

		VBox.setVgrow(settingsAndVisualizer, Priority.SOMETIMES);

		showSettingsAnimation = Animations.newResizeFadeAnimation(settingsBox, Duration.seconds(0.25d),
				Duration.seconds(0.1d), true, 0d, 256d, 0d, 1d, Interpolator.EASE_OUT);
		showSettingsAnimation.setOnFinished(a -> setShowingSettings(true));

		hideSettingsAnimation = Animations.newResizeFadeAnimation(settingsBox, Duration.seconds(0.25d),
				Duration.seconds(0.1d), true, 256d, 0d, 1d, 0d, Interpolator.EASE_IN);
		hideSettingsAnimation.setOnFinished(a -> {
			setShowingSettings(false);
			settingsBox.setVisible(false);
		});

		showLibraryAnimation = Animations.newScaleTransition(library, Duration.seconds(0.3d), 0.5d, 1d,
				0d, 1d, Interpolator.EASE_OUT);
		showLibraryAnimation.setOnFinished(a -> setShowingLibrary(true));

		hideLibraryAnimation = Animations.newScaleTransition(library, Duration.seconds(0.2d), 1d, 0.5d,
				1d, 0d, Interpolator.EASE_IN);
		hideLibraryAnimation.setOnFinished(a -> {
			setShowingLibrary(false);
			library.setVisible(false);
		});

		showDescriptionAnimation = new FadeTranslateAnimation();
		showDescriptionAnimation.setNode(description);
		showDescriptionAnimation.setFadeFromValue(0d);
		showDescriptionAnimation.setFadeToValue(1d);
		showDescriptionAnimation.setFadeDuration(Duration.seconds(0.25d));
		showDescriptionAnimation.setTranslateFromY(20d);
		showDescriptionAnimation.setTranslateToY(0d);
		showDescriptionAnimation.setTranslateDuration(Duration.seconds(0.2d));

		hideDescriptionAnimation = new FadeTranslateAnimation();
		hideDescriptionAnimation.setNode(description);
		hideDescriptionAnimation.setFadeFromValue(1d);
		hideDescriptionAnimation.setFadeToValue(0d);
		hideDescriptionAnimation.setFadeDuration(Duration.seconds(0.25d));
		hideDescriptionAnimation.setTranslateFromY(0d);
		hideDescriptionAnimation.setTranslateToY(20d);
		hideDescriptionAnimation.setTranslateDuration(Duration.seconds(0.2d));

		setSpacing(5d);
		setPadding(new Insets(15d));
		setFillWidth(true);
		setPickOnBounds(true);
		getChildren().addAll(thumbnailAndDescrLibr, settingsAndVisualizer, sliderAndLabelNodes, controlNodes);
	}

	public void showLibrary(boolean value) {
		if (value) {
			library.setVisible(true);
			showingLibraryProperty.set(true);
		} 
		else {
			library.setVisible(false);
			showingLibraryProperty.set(false);
		}
	}

	public void showLibraryAnimated(boolean value) {
		boolean isShowing = showLibraryAnimation.getStatus() == Animation.Status.RUNNING || isShowingLibrary();
		boolean isHiding = hideLibraryAnimation.getStatus() == Animation.Status.RUNNING || !isShowingLibrary();
		if (value && !isShowing) {
			library.setVisible(true);
			showLibraryAnimation.playFromStart();
			hideDescriptionAnimation.get().playFromStart();
		} 
		else if (!value && !isHiding) {
			hideLibraryAnimation.playFromStart();
			showDescriptionAnimation.get().playFromStart();
		}
	}

	private void showSettings(boolean value) {
		double valueForAll = value ? 256d : 0d;
		settingsBox.setVisible(value);
		settingsBox.setMinWidth(valueForAll);
		settingsBox.setMaxWidth(valueForAll);
		settingsBox.setPrefWidth(valueForAll);
		setShowingSettings(value);
	}

	private void showSettingsAnimated(boolean value) {
		boolean isShowing = showSettingsAnimation.getStatus() == Animation.Status.RUNNING || isShowingSettings();
		boolean isHiding = hideSettingsAnimation.getStatus() == Animation.Status.RUNNING || !isShowingSettings();
		if (value && !isShowing) {
			settingsBox.setVisible(true);
			settingsBox.setMinWidth(0d);
			settingsBox.setMaxWidth(0d);
			settingsBox.setPrefWidth(0d);
			showSettingsAnimation.playFromStart();
		} 
		else if (!value && !isHiding) {
			settingsBox.setMinWidth(256d);
			settingsBox.setMaxWidth(256d);
			settingsBox.setPrefWidth(256d);
			hideSettingsAnimation.playFromStart();
		}
	}

	public void initAudioSpectrumVisualizer(int bands) {
		audioSpectrumVisualizer = new VBox();
		audioSpectrumVisualizer.setAlignment(Pos.BOTTOM_CENTER);
		HBox bandsBox = new HBox();
		bandsBox.maxWidthProperty().bind(audioSpectrumVisualizer.widthProperty());
		bandsBox.setSpacing(5d);
		bandsBox.setOpacity(0.6d);
		// bandsBox.setPadding(new Insets(0d));
		bandsBox.setAlignment(Pos.BOTTOM_CENTER);

		for (int i = 0; i < bands; i++) {
			HBox rect = new HBox();
			rect.setStyle("-fx-border-color: white; -fx-border-radius: 5 5 0 0; -fx-background-radius: 5 5 0 0;");
			rect.setMinWidth(5d);
			rect.maxWidthProperty().bind(bandsBox.widthProperty().divide(bands).subtract(bandsBox.getSpacing()));
			rect.setMinHeight(0d);
			rect.setMaxHeight(0d);
			bandsBox.getChildren().add(rect);
			HBox.setHgrow(rect, Priority.SOMETIMES);
		}
		audioSpectrumVisualizer.getChildren().add(bandsBox);
	}
	
	public void switchToPlayMode(AudioPlayMode mode) {
		if (mode == AudioPlayMode.DEFAULT || mode == AudioPlayMode.SHUFFLE)
			buttonRepeat.setGraphic(new ImageView(IMG_REPLAY_DEFAULT)); 
		else if (mode == AudioPlayMode.REPEAT_PLAYLIST)
			buttonRepeat.setGraphic(new ImageView(IMG_REPLAY_SINGLE)); 
		else if (mode == AudioPlayMode.REPEAT_SINGLE)
			buttonRepeat.setGraphic(new Label("No Replay"));
	}

	public void switchToPlayGraphic() {
		buttonPlayPause.setGraphic(new ImageView(IMG_PLAY));
	}

	public void switchToPauseGraphic() {
		buttonPlayPause.setGraphic(new ImageView(IMG_PAUSE));
	}
	
	public IntegerProperty audioSpectrumBandsProperty() {
		return audioSpectrumBandsProperty;
	}

	public int getAudioSpectrumBands() {
		return audioSpectrumBandsProperty.get();
	}

	public void setAudioSpectrumBands(int value) {
		audioSpectrumBandsProperty.set(value);
		initAudioSpectrumVisualizer(value);
	}

	public ReadOnlyBooleanProperty showingLibraryProperty() {
		return showingLibraryProperty;
	}

	public boolean isShowingLibrary() {
		return showingLibraryProperty.get();
	}

	private void setShowingLibrary(boolean value) {
		showingLibraryProperty.set(value);
	}

	public ReadOnlyBooleanProperty showingSettingsProperty() {
		return showingSettingsProperty;
	}

	public boolean isShowingSettings() {
		return showingSettingsProperty.get();
	}

	private void setShowingSettings(boolean value) {
		showingSettingsProperty.set(value);
	}
	
	public JFXButton getVolumeButton() {
		return buttonVolume;
	}
	
	public JFXButton getRateButton() {
		return buttonRate;
	}
	
	public JFXButton getLibraryButton() {
		return buttonLibrary;
	}
	
	public JFXButton getRepeatButton() {
		return buttonRepeat;
	}
	
	public JFXButton getPreviousButton() {
		return buttonPrevious;
	}

	public JFXButton getPlayPauseButton() {
		return buttonPlayPause;
	}

	public JFXButton getNextButton() {
		return buttonNext;
	}
	
	public JFXSlider getTimeSlider() {
		return timeSlider;
	}

	public JFXSlider getRateSlider() {
		return rateSlider;
	}

	public JFXSlider getReplayFromSlider() {
		return replayFromSlider;
	}

	public JFXSlider getReplayToSlider() {
		return replayToSlider;
	}
	
	public JFXSlider getVolumeSlider() {
		return volumeSlider;
	}
	
	public ImageView getThumbnailView() {
		return thumbnailView;
	}

	public Image getThumbnailImage() {
		return thumbnailView.getImage();
	}

	public void setThumbnailImage(Image value) {
		thumbnailView.setImage(value);
	}

	public String getTitle() {
		return textTitle.getText();
	}

	public void setTitle(String value) {
		textTitle.setText(value);
	}

	public String getAlbum() {
		return textAlbum.getText();
	}

	public void setAlbum(String value) {
		textAlbum.setText(value);
	}

	public String getArtist() {
		return textArtist.getText();
	}

	public void setArtist(String value) {
		textArtist.setText(value);
	}

	public String getYear() {
		return textYear.getText();
	}

	public void setYear(String value) {
		textYear.setText(value);
	}

	public VBox getAudioSpectrumVisualizer() {
		return audioSpectrumVisualizer;
	}

	public HBox getAudioSpectrumVisualizerBox() {
		return (HBox) audioSpectrumVisualizer.getChildren().get(0);
	}

	public String getCurrentTime() {
		return labelCurrentTime.getText();
	}

	public void setCurrentTime(String value) {
		labelCurrentTime.setText(value);
	}

	public String getTotalTime() {
		return labelTotalTime.getText();
	}

	public void setTotalTime(String value) {
		labelTotalTime.setText(value);
	}

	public AudioLibrary getLibrary() {
		return library;
	}
}
