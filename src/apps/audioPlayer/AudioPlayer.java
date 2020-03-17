package apps.audioPlayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import gui.animations.Animations;
import gui.animations.Animations.FadeTranslateAnimation;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import utils.Utils;

public class AudioPlayer {
	//
	private MediaPlayer player;
	private ObservableList<URL> playlist;
	//
	private AudioPlayMode mode;
	//
	private IntegerProperty currentTrackIndexProperty;
	private IntegerProperty previousTrackIndexProperty;
	//
	private CAudioPlayerGUI gui;
	//
	private BooleanProperty partReplayProperty;
	private ObjectProperty<Duration> replayFromDurationProperty;
	private ObjectProperty<Duration> replayToDurationProperty;
	//
	private IntegerProperty audioSpectrumBandsProperty;

	//
	public AudioPlayer() {
		this.playlist = FXCollections.observableArrayList();

		this.mode = AudioPlayMode.DEFAULT;
		this.currentTrackIndexProperty = new SimpleIntegerProperty(-1);
		this.previousTrackIndexProperty = new SimpleIntegerProperty(-1);

		this.partReplayProperty = new SimpleBooleanProperty(false);
		this.replayFromDurationProperty = new SimpleObjectProperty<>(Duration.millis(0d));
		this.replayToDurationProperty = new SimpleObjectProperty<>(Duration.millis(0d));

		this.audioSpectrumBandsProperty = new SimpleIntegerProperty(25);

		this.gui = new CAudioPlayerGUI();
	}

	public double getVolume() {
		return this.gui.getVolume();
	}

	public void setVolume(double newVol) {
		this.gui.setVolume(newVol);
	}

	public int getPercentageVolume() {
		return (int) this.getVolume() * 100;
	}

	public double getRate() {
		return this.gui.getRate();
	}

	public void setRate(double newRate) {
		this.gui.setRate(newRate);
	}

	public int getPercentageRate() {
		return (int) this.getRate() * 100;
	}

	//
	private Media initMedia(URL mediaUrl) {
		assert mediaUrl != null;

		Media media = new Media(mediaUrl.toString());
		media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
			Object newValue = change.getValueAdded();
			int tempIndex[] = new int[] { 0 };

			if (newValue != null) {
				switch (change.getKey().toString()) {
				case "image":
					System.out.println("Image found");
					this.gui.setThumbnailImage((Image) newValue);
					break;
				case "title":
					this.playlist.forEach(a -> {
						if (a.sameFile(mediaUrl))
							this.gui.getLibrary().getData().get(tempIndex[0]).setTitle((String) newValue);
						if (this.getCurrentTrackIndex() == this.playlist.indexOf(a))
							this.gui.setTitle((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "artist":
					this.playlist.forEach(a -> {
						if (a.sameFile(mediaUrl))
							this.gui.getLibrary().getData().get(tempIndex[0]).setArtist((String) newValue);
						if (this.getCurrentTrackIndex() == this.playlist.indexOf(a))
							this.gui.setArtist((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "album":
					this.playlist.forEach(a -> {
						if (a.sameFile(mediaUrl))
							this.gui.getLibrary().getData().get(tempIndex[0]).setAlbum((String) newValue);
						if (this.getCurrentTrackIndex() == this.playlist.indexOf(a))
							this.gui.setAlbum((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "year":
					this.playlist.forEach(a -> {
						if (a.sameFile(mediaUrl))
							this.gui.getLibrary().getData().get(tempIndex[0]).setYear(Integer.toString((int) newValue));
						if (this.getCurrentTrackIndex() == this.playlist.indexOf(a))
							this.gui.setYear(Integer.toString((int) newValue));
						tempIndex[0]++;
					});
					break;
				}
			}
		});
		return media;
	}

	//
	private MediaPlayer initPlayer(URL mediaUrl, boolean autoPlay) throws Exception {
		this.gui.setTitle("-");
		this.gui.setArtist("-");
		this.gui.setAlbum("-");
		this.gui.setYear("-");

		Media media = this.initMedia(mediaUrl);

		MediaPlayer mediaPlayer = new MediaPlayer(media);

		// TODO
//		Timeline volumeInAnimation = CAnimations.newTimelineAnimation(mediaPlayer.volumeProperty(), this.getVolume(), Duration.seconds(2d), Interpolator.EASE_OUT, 1, false);
//		Timeline volumeOutAnimation = CAnimations.newTimelineAnimation(mediaPlayer.volumeProperty(), 0.1d, Duration.seconds(2d), Interpolator.EASE_IN, 1, false);
//		volumeOutAnimation.setOnFinished(a ->
//		{
//			mediaPlayer.seek(CUtils.sliderValueToDuration(gui.getReplayFromSlider(), mediaPlayer.getTotalDuration()));
//			volumeInAnimation.playFromStart();
//		});

		mediaPlayer.statusProperty().addListener((obs, oldV, newV) -> {
			if (newV == Status.PLAYING)
				this.gui.switchToPauseGraphic();
			else
				this.gui.switchToPlayGraphic();

		});
		mediaPlayer.currentTimeProperty().addListener((obs, oldV, newV) -> {
			if (this.isPartReplay()) {
				// Duration replayTo =
				// this.audioController.sliderValueToDuration(audioController.getReplayToSlider(),
				// mediaPlayer.getTotalDuration());
				// Duration replayFrom =
				// this.audioController.sliderValueToDuration(audioController.getReplayFromSlider(),
				// mediaPlayer.getTotalDuration());
				Duration replayFrom = this.getReplayFromDuration();
				Duration replayTo = this.getReplayToDuration();
				if (newV.greaterThanOrEqualTo(replayTo)
						&& replayTo.greaterThanOrEqualTo(replayFrom.add(Duration.seconds(1d))))
					mediaPlayer.seek(replayFrom);
				// volumeOutAnimation.playFromStart();
			}
			if (!this.gui.getTimeSlider().isValueChanging()) {
				double sliderMax = this.gui.getTimeSlider().getMax();
				this.gui.getTimeSlider()
						.setValue((newV.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * sliderMax);
				this.gui.setCurrentTime(Utils.durationToMMSS(newV));
				// this.audioController.setTotalTime("-" +
				// CUtils.durationToMMSS(media.getDuration().subtract(newV)));
			}
		});
		mediaPlayer.totalDurationProperty().addListener((obs, oldV, newV) -> {
			this.gui.setTotalTime(Utils.durationToMMSS(newV));
		});

		mediaPlayer.setOnEndOfMedia(() -> this.onMediaEnd());
		mediaPlayer.setAutoPlay(autoPlay);
		mediaPlayer.volumeProperty().bindBidirectional(this.gui.volumeProperty);
		mediaPlayer.rateProperty().bindBidirectional(this.gui.rateProperty);
		// mediaPlayer.setVolume(this.getVolume());
		// mediaPlayer.setRate(this.getRate());

		mediaPlayer.setAudioSpectrumNumBands(this.getAudioSpectrumBands());
		mediaPlayer.setAudioSpectrumInterval(0.05d);
		mediaPlayer.setAudioSpectrumThreshold(-100); // A good value to display ALL bands
		mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener() {
			@Override
			public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
				if (mediaPlayer.getStatus() == Status.PLAYING) {
					for (int i = 0; i < magnitudes.length; i++) {
						double decibels = (magnitudes[i] - mediaPlayer.getAudioSpectrumThreshold());
						double paddingTop = gui.getAudioSpectrumVisualizerBox().getPadding().getTop();
						double paddingBottom = gui.getAudioSpectrumVisualizerBox().getPadding().getBottom();
						double calcHeight = (decibels / Math.abs(mediaPlayer.getAudioSpectrumThreshold())
								* gui.getAudioSpectrumVisualizer().getHeight() - paddingTop - paddingBottom);
						((HBox) gui.getAudioSpectrumVisualizerBox().getChildren().get(i)).setMinHeight(calcHeight);
						((HBox) gui.getAudioSpectrumVisualizerBox().getChildren().get(i)).setMaxHeight(calcHeight);
					}
				}
			}
		});
		return mediaPlayer;
	}

	//
	public void addToPlaylist(URL... urls) {
		for (URL url : urls)
			this.initMedia(url);
		this.gui.getLibrary().addAudioData(new CAudioData("-", "-", "-", "-"));
		this.playlist.addAll(urls);
	}

	//
	public boolean removeFromPlaylist(URL... urls) {
		int oldSize = this.playlist.size();
		int currentIndex;
		for (int i = 0; i < urls.length; i++) {
			currentIndex = this.playlist.indexOf(urls[i]);
			if (currentIndex >= 0) {
				this.gui.getLibrary().removeAudioData(currentIndex);
				this.playlist.remove(currentIndex);
			}
		}
		return this.playlist.size() < oldSize;
	}

	//
	public void removeFromPlaylist(int index) {
		this.gui.getLibrary().removeAudioData(index);
		this.playlist.remove(index);
	}

	//
	public boolean play() {
		return this.play(0);
	}

	//
	public boolean play(int index) {
		if (index >= 0 && index < this.playlist.size()) {
			try {
				if (this.player != null) {
					this.player.stop();
					this.player = null;
				}
				this.player = this.initPlayer(this.playlist.get(index), true);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (this.getCurrentTrackIndex() != index)
					this.setPreviousTrackIndex(this.getCurrentTrackIndex());
				this.setCurrentTrackIndex(index);
			}
		} else
			return false;
	}

	//
	public boolean playNext() {
		return this.play(this.getCurrentTrackIndex() + 1);
	}

	//
	public boolean playPrevious() {
		return this.play(this.getCurrentTrackIndex() - 1);
	}

	//
	public boolean playRandom() {
		Random randomGen = new Random();
		int nextRandom;
		do {
			nextRandom = randomGen.nextInt(this.playlist.size());
		} while (nextRandom == this.getCurrentTrackIndex());
		return this.play(nextRandom);
	}

	//
	public boolean seek(Duration duration) {
		if (this.player != null && duration.toSeconds() >= 0d) {
			this.player.seek(duration);
			return true;
		} else
			return false;
	}

	// Plays the next media selected by considering the current play mode
	private void onMediaEnd() {
		switch (this.mode) {
		case DEFAULT:
			this.playNext();
			break;
		case REPEAT_SINGLE:
			this.play(this.getCurrentTrackIndex());
			break;
		case REPEAT_PLAYLIST:
			if (this.getCurrentTrackIndex() >= (this.playlist.size() - 1))
				this.play(0);
			else
				this.playNext();
			break;
		case SHUFFLE:
			this.playRandom();
			break;
		}
	}

	/*
	 *
	 */

	public MediaPlayer getMediaPlayer() {
		return this.player;
	}

	public AudioPlayMode getMediaMode() {
		return this.mode;
	}

	public void setMediaMode(AudioPlayMode mode) {
		this.mode = mode;
	}

	public IntegerProperty currentTrackIndexProperty() {
		return this.currentTrackIndexProperty;
	}

	public int getCurrentTrackIndex() {
		return this.currentTrackIndexProperty.get();
	}

	public void setCurrentTrackIndex(int value) {
		this.currentTrackIndexProperty.set(value);
	}

	public IntegerProperty previousTrackIndexProperty() {
		return this.previousTrackIndexProperty;
	}

	public int getPreviousTrackIndex() {
		return this.previousTrackIndexProperty.get();
	}

	public void setPreviousTrackIndex(int value) {
		this.previousTrackIndexProperty.set(value);
	}

	public BooleanProperty partReplayProperty() {
		return this.partReplayProperty;
	}

	public boolean isPartReplay() {
		return this.partReplayProperty.get();
	}

	public void setPartReplay(boolean value) {
		this.partReplayProperty.set(value);
	}

	public ObjectProperty<Duration> replayFromDurationProperty() {
		return this.replayFromDurationProperty;
	}

	public Duration getReplayFromDuration() {
		return this.replayFromDurationProperty.get();
	}

	public void setReplayFromDuration(Duration value) {
		this.replayFromDurationProperty.set(value);
	}

	public ObjectProperty<Duration> replayToDurationProperty() {
		return this.replayToDurationProperty;
	}

	public Duration getReplayToDuration() {
		return this.replayToDurationProperty.get();
	}

	public void setReplayToDuration(Duration value) {
		this.replayToDurationProperty.set(value);
	}

	public IntegerProperty audioSpectrumBandsProperty() {
		return this.audioSpectrumBandsProperty;
	}

	public int getAudioSpectrumBands() {
		return this.audioSpectrumBandsProperty.get();
	}

	public void setAudioSpectrumBands(int value) {
		this.audioSpectrumBandsProperty.set(value);
		gui.audioSpectrumVisualizer = gui.initAudioSpectrumVisualizer(getAudioSpectrumBands());
	}

	public CAudioPlayerGUI getGUI() {
		return this.gui;
	}

	/*
	 *
	 */

	public class CAudioPlayerGUI extends VBox {
		private DoubleProperty volumeProperty;
		private DoubleProperty volumeBeforeMuteProperty;
		private DoubleProperty rateProperty;
		//
		private BooleanProperty showingLibraryProperty;
		private BooleanProperty showingSettingsProperty;
		//
		private ImageView thumbnailView;
		//
		private Label textTitle;
		private Label textAlbum;
		private Label textArtist;
		private Label textYear;
		//
		private VBox settingsBox;
		private VBox audioSpectrumVisualizer;
		private Label labelCurrentTime;
		private Label labelTotalTime;
		//
		private JFXSlider timeSlider;
		private JFXSlider volumeSlider;
		private JFXSlider rateSlider;
		//
		private JFXButton buttonVolume;
		private JFXButton buttonRate;
		private JFXButton buttonLibrary;
		private JFXButton buttonRepeat;
		//
		private JFXButton buttonPrevious;
		private JFXButton buttonPlayPause;
		private JFXButton buttonNext;
		//
		private CAudioLibrary library;
		//
		private Label labelReplayFrom;
		private Label labelReplayTo;
		//
		private JFXSlider replayFromSlider;
		private JFXSlider replayToSlider;
		//
		private Transition showSettingsAnimation;
		private Transition hideSettingsAnimation;
		//
		private Transition showLibraryAnimation;
		private Transition hideLibraryAnimation;
		//
		private FadeTranslateAnimation showDescriptionAnimation;
		private FadeTranslateAnimation hideDescriptionAnimation;
		//
		private final Image IMG_PLAY = new Image("/resources/icons/img_play.png", 32d, 32d, true, true);
		private final Image IMG_PAUSE = new Image("/resources/icons/img_pause.png", 32d, 32d, true, true);
		private final Image IMG_PREVIOUS = new Image("/resources/icons/img_previous.png", 32d, 32d, true, true);
		private final Image IMG_NEXT = new Image("/resources/icons/img_next.png", 32d, 32d, true, true);
		private final Image IMG_VOLUME_MUTED = new Image("/resources/icons/img_volume_muted.png", 48d, 48d, true, true);
		private final Image IMG_VOLUME_LOW = new Image("/resources/icons/img_volume_low.png", 48d, 48d, true, true);
		private final Image IMG_VOLUME_MEDIUM = new Image("/resources/icons/img_volume_medium.png", 48d, 48d, true,
				true);
		private final Image IMG_VOLUME_HIGH = new Image("/resources/icons/img_volume_high.png", 48d, 48d, true, true);
		private final Image IMG_LIBRARY = new Image("/resources/icons/img_audio_library.png", 32d, 32d, true, true);
		private final Image IMG_REPLAY_DEFAULT = new Image("/resources/icons/img_repeat_default.png", 32d, 32d, true,
				true);
		private final Image IMG_REPLAY_SINGLE = new Image("/resources/icons/img_repeat_single.png", 32d, 32d, true,
				true);
		// private final Image IMG_SHUFFLE = new
		// Image("/resources/icons/img_shuffle.png", 32d, 32d, true, true);
		//

		//
		public CAudioPlayerGUI() {
			super();

			this.getStylesheets().add("/stylesheets/client/defaultStyle/AudioPlayer.css");

			this.volumeProperty = new SimpleDoubleProperty(0.5d);
			this.volumeBeforeMuteProperty = new SimpleDoubleProperty(0.5d);
			this.rateProperty = new SimpleDoubleProperty(1d);

			this.showingLibraryProperty = new SimpleBooleanProperty(false);
			this.showingSettingsProperty = new SimpleBooleanProperty(false);

			this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
			this.setOnMouseClicked(a -> {
				if (a.getButton() == MouseButton.SECONDARY) {
					FileChooser fc = new FileChooser();
					List<File> files = fc.showOpenMultipleDialog(this.getScene().getWindow());
					if (files != null) {
						files.forEach(file -> {
							try {
								addToPlaylist(file.toURI().toURL());
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
						});
						play();
					}
				}
			});

			this.thumbnailView = new ImageView(new Image("/resources/icons/cover.jpg", 256d, 256d, true, true, true));
			this.thumbnailView.setOnMouseClicked(a -> {
				if (a.getButton() == MouseButton.PRIMARY)
					this.showLibraryAnimated(true);
				else
					this.showLibraryAnimated(false);
			});
			this.thumbnailView.setPreserveRatio(true);
			this.thumbnailView.setFitWidth(256d);
			this.thumbnailView.setFitHeight(256d);

			this.textTitle = new Label("-");
			this.textTitle.setStyle("-fx-text-fill: white;");
			this.textTitle.setFont(Font.font("Tahoma", 22d));

			this.textAlbum = new Label("-");
			this.textAlbum.setStyle("-fx-text-fill: white;");
			this.textAlbum.setFont(Font.font("Tahoma", 22d));

			this.textArtist = new Label("-");
			this.textArtist.setStyle("-fx-text-fill: white;");
			this.textArtist.setFont(Font.font("Tahoma", 22d));

			this.textYear = new Label("-");
			this.textYear.setStyle("-fx-text-fill: white;");
			this.textYear.setFont(Font.font("Tahoma", 22d));

			this.audioSpectrumVisualizer = this.initAudioSpectrumVisualizer(getAudioSpectrumBands());
			VBox.setVgrow(this.audioSpectrumVisualizer, Priority.SOMETIMES);

			this.labelCurrentTime = new Label();
			this.labelCurrentTime.setFont(Font.font("Tahoma", 12d));

			this.labelTotalTime = new Label();
			this.labelTotalTime.setFont(Font.font("Tahoma", 12d));

			this.timeSlider = new JFXSlider();
			this.timeSlider.setValueFactory(a -> {
				StringBinding textBinding = new StringBinding() {
					{
						super.bind(timeSlider.valueProperty());
					}

					@Override
					protected String computeValue() {
						if (player != null && player.getTotalDuration() != null)
							return Utils.durationToMMSS(Duration.seconds((timeSlider.getValue() / timeSlider.getMax())
									* player.getTotalDuration().toSeconds()));
						else
							return "";
					}
				};
				return textBinding;
			});
			this.timeSlider.setValue(0d);
			this.timeSlider.valueProperty().addListener((obs, oldV, newV) -> {
				if (!timeSlider.isValueChanging() && player.getTotalDuration() != null) {
					Duration currentTime = player.getCurrentTime();
					Duration dragTime = player.getTotalDuration()
							.multiply(newV.doubleValue() / this.timeSlider.getMax());

					if (Math.abs(currentTime.subtract(dragTime).toSeconds()) >= 0.5d)
						player.seek(dragTime);
				}
			});
			this.timeSlider.valueChangingProperty().addListener((obs, oldV, newV) -> {
				if (!newV.booleanValue())
					player.seek(Duration.millis(
							(timeSlider.getValue() / timeSlider.getMax()) * player.getTotalDuration().toMillis()));
			});

			this.volumeSlider = new JFXSlider(0d, 1d, 0.5d);
			this.volumeSlider.setValueFactory(a -> {
				return new StringBinding() {
					{
						super.bind(volumeSlider.valueProperty());
					}

					@Override
					protected String computeValue() {
						return "" + (int) ((volumeSlider.getValue() / volumeSlider.getMax()) * 100d);
					}
				};
			});
			this.volumeSlider.valueProperty().addListener((obs, oldV, newV) -> {
				if (player != null) {
					player.setVolume(newV.doubleValue());
					this.setVolume(newV.doubleValue());
				}

				double ratio = newV.doubleValue() / this.volumeSlider.getMax();

				if (ratio == 0d)
					this.buttonVolume.setGraphic(new ImageView(this.IMG_VOLUME_MUTED));
				else if (ratio > 0d && ratio <= 0.33d)
					this.buttonVolume.setGraphic(new ImageView(this.IMG_VOLUME_LOW));
				else if (ratio > 0.33d && ratio <= 0.66d)
					this.buttonVolume.setGraphic(new ImageView(this.IMG_VOLUME_MEDIUM));
				else
					this.buttonVolume.setGraphic(new ImageView(this.IMG_VOLUME_HIGH));
			});

			this.rateSlider = new JFXSlider(0.5d, 2d, 1d);
			this.rateSlider.setValueFactory(a -> {
				return new StringBinding() {
					{
						super.bind(rateSlider.valueProperty());
					}

					@Override
					protected String computeValue() {
						return rateSlider.getValue() + "X";
					}
				};
			});
			this.rateSlider.setSnapToTicks(true);
			this.rateSlider.setBlockIncrement(0.25d);
			this.rateSlider.setMinorTickCount(1);
			this.rateSlider.setMajorTickUnit(0.5d);
			this.rateSlider.valueChangingProperty().addListener((obs, oldV, newV) -> {
				if (newV.booleanValue()) {
					this.rateSlider.setShowTickLabels(true);
					this.rateSlider.setShowTickMarks(true);
				} else {
					this.rateSlider.setShowTickLabels(false);
					this.rateSlider.setShowTickMarks(false);
					this.rateSlider.adjustValue(this.rateSlider.getValue());

					player.setRate(this.rateSlider.getValue()
							- (this.rateSlider.getValue() % this.rateSlider.getBlockIncrement()));
					this.setRate(player.getRate());

					Label newRate = new Label(this.rateSlider.getValue() + "X");
					newRate.setTextFill(Color.WHITE);
					newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
					this.buttonRate.setGraphic(newRate);
				}
			});
			this.rateSlider.pressedProperty().addListener((obs, oldV, newV) -> {
				if (!newV.booleanValue() && oldV.booleanValue()/* !this.rateSlider.isValueChanging() */) {
					player.setRate(this.rateSlider.getValue()
							- (this.rateSlider.getValue() % this.rateSlider.getBlockIncrement()));
					Label newRate = new Label(this.rateSlider.getValue() + "X");
					newRate.setTextFill(Color.WHITE);
					newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
					this.buttonRate.setGraphic(newRate);
				}
			});

			this.buttonVolume = new JFXButton();
			this.buttonVolume.setManaged(true);
			this.buttonVolume.setGraphic(new ImageView(this.IMG_VOLUME_MEDIUM));
			this.buttonVolume.setOnAction(a -> {
				if (player != null) {
					System.out.println(buttonVolume.getWidth() + " " + buttonVolume.getHeight());
					if (player.getVolume() > 0d) {
						setVolumeBeforeMute(this.volumeSlider.getValue());// TODO
						this.volumeSlider.setValue(0d);
					} else {
						this.volumeSlider.setValue(getVolumeBeforeMute());// TODO
					}
				}
			});

			this.buttonRate = new JFXButton();
			this.buttonRate.minWidthProperty().bind(this.buttonVolume.widthProperty());
			this.buttonRate.minHeightProperty().bind(this.buttonVolume.heightProperty());
			Label initRateLabel = new Label("1.0x");
			initRateLabel.setTextFill(Color.WHITE);
			initRateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
			this.buttonRate.setGraphic(initRateLabel);
			this.buttonRate.setOnAction(a -> {
				double currentRate = this.rateSlider.getValue();
				double minRate = this.rateSlider.getMin();
				double maxRate = this.rateSlider.getMax();
				double tick = this.rateSlider.getMajorTickUnit() / (this.rateSlider.getMinorTickCount() + 1d);
				if (currentRate >= maxRate)
					this.rateSlider.setValue(minRate);
				else
					this.rateSlider.setValue(currentRate + tick);

				Label newRate = new Label(this.rateSlider.getValue() + "X");
				newRate.setTextFill(Color.WHITE);
				newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
				this.buttonRate.setGraphic(newRate);

				this.setRate(this.rateSlider.getValue());
			});

			this.buttonPrevious = new JFXButton("", new ImageView(IMG_PREVIOUS));
			this.buttonPrevious.setOnAction(a -> playPrevious());

			this.buttonPlayPause = new JFXButton("", new ImageView(IMG_PLAY));
			this.buttonPlayPause.setOnAction(a -> {
				if (player.getStatus().equals(Status.PLAYING))
					player.pause();
				else
					player.play();
			});

			this.buttonNext = new JFXButton("", new ImageView(IMG_NEXT));
			this.buttonNext.setOnAction(a -> playNext());

			this.library = new CAudioLibrary();
			this.library.getStyleClass().add("c-audio-library");
			// this.library.setVisible(false);

			this.buttonLibrary = new JFXButton();
			this.buttonLibrary.setGraphic(new ImageView(this.IMG_LIBRARY));
			this.buttonLibrary.disableProperty().bind(Bindings.isEmpty(this.library.getData()));
			this.buttonLibrary.setOnAction(a -> {
				if (this.isShowingLibrary())
					this.showLibraryAnimated(false);
				else
					this.showLibraryAnimated(true);
			});

			this.buttonRepeat = new JFXButton();
			this.buttonRepeat.setGraphic(new ImageView(this.IMG_REPLAY_DEFAULT));
			this.buttonRepeat.setOnAction(a -> this.onRepeatButtonClicked());

			this.labelReplayFrom = new Label("Von:");
			this.labelReplayFrom.setMinWidth(30d);
			this.labelReplayTo = new Label("Bis:");
			this.labelReplayTo.setMinWidth(30d);

			this.replayFromSlider = new JFXSlider();
			this.replayFromSlider.valueProperty().addListener((obs, oldV, newV) -> {
				if (player != null && player.getTotalDuration() != null)
					setReplayFromDuration(Utils.sliderValueToDuration(replayFromSlider, player.getTotalDuration()));
				if (this.replayToSlider != null && this.replayToSlider.getValue() < newV.doubleValue())
					this.replayToSlider.setValue(newV.doubleValue());
			});
			this.replayFromSlider.setValue(replayFromSlider.getMin());
			this.replayFromSlider.setValueFactory(a -> {
				StringBinding textBinding = new StringBinding() {
					{
						super.bind(replayFromSlider.valueProperty());
					}

					@Override
					protected String computeValue() {
						if (player != null && player.getTotalDuration() != null)
							return Utils.durationToMMSS(
									Duration.seconds((replayFromSlider.getValue() / replayFromSlider.getMax())
											* player.getTotalDuration().toSeconds()));
						else
							return "";
					}
				};
				return textBinding;
			});

			this.replayToSlider = new JFXSlider();
			this.replayToSlider.valueProperty().addListener((obs, oldV, newV) -> {
				if (player != null && player.getTotalDuration() != null)
					setReplayToDuration(Utils.sliderValueToDuration(replayToSlider, player.getTotalDuration()));
				if (this.replayFromSlider != null && this.replayFromSlider.getValue() > newV.doubleValue())
					this.replayFromSlider.setValue(newV.doubleValue());
			});
			this.replayToSlider.setValue(replayFromSlider.getMax());
			this.replayToSlider.setValueFactory(a -> {
				StringBinding textBinding = new StringBinding() {
					{
						super.bind(replayToSlider.valueProperty());
					}

					@Override
					protected String computeValue() {
						if (player != null && player.getTotalDuration() != null)
							return Utils.durationToMMSS(
									Duration.seconds((replayToSlider.getValue() / replayToSlider.getMax())
											* player.getTotalDuration().toSeconds()));
						else
							return "";
					}
				};
				return textBinding;
			});

			// Text Nodes and ImageView
			VBox description = new VBox(this.textTitle, this.textAlbum, this.textArtist, this.textYear);
			description.setFillWidth(false);
			description.setSpacing(3d);

			StackPane descriptionAndLibrary = new StackPane(description, this.library);
			descriptionAndLibrary.setMaxHeight(this.thumbnailView.getFitHeight());
			this.showLibrary(false);

			HBox thumbnailAndDescrLibr = new HBox(this.thumbnailView, descriptionAndLibrary);
			HBox.setHgrow(descriptionAndLibrary, Priority.ALWAYS);
			thumbnailAndDescrLibr.setFillHeight(true);
			thumbnailAndDescrLibr.setSpacing(20d);
			HBox.setHgrow(audioSpectrumVisualizer, Priority.SOMETIMES);

			// Time Slider and Label Nodes
			HBox labelSpace = new HBox();
			HBox labelNodes = new HBox(this.labelCurrentTime, labelSpace, this.labelTotalTime);
			HBox.setHgrow(labelSpace, Priority.SOMETIMES);
			VBox sliderAndLabelNodes = new VBox(this.timeSlider, labelNodes);
			sliderAndLabelNodes.setAlignment(Pos.BOTTOM_CENTER);
			sliderAndLabelNodes.setSpacing(5d);

			// Button Nodes
			HBox spaceX1 = new HBox();
			HBox spaceX2 = new HBox();
			HBox controlNodes = new HBox(spaceX1, this.buttonPrevious, this.buttonPlayPause, this.buttonNext, spaceX2);
			HBox.setHgrow(spaceX1, Priority.SOMETIMES);
			HBox.setHgrow(spaceX2, Priority.SOMETIMES);
			controlNodes.setFillHeight(false);
			controlNodes.setAlignment(Pos.BOTTOM_CENTER);
			controlNodes.setPadding(new Insets(0d, 0d, 15d, 0d));

			VBox replayTool = new VBox(10d);
			HBox replayFrom = new HBox(this.labelReplayFrom, this.replayFromSlider);
			replayFrom.setSpacing(5d);
			HBox replayTo = new HBox(this.labelReplayTo, this.replayToSlider);
			replayTo.setSpacing(5d);
			replayTool.getChildren().addAll(replayFrom, replayTo);

			HBox volumeTool = new HBox(this.buttonVolume, this.volumeSlider);
			volumeTool.setManaged(true);
			volumeTool.setAlignment(Pos.CENTER_LEFT);
			HBox rateTool = new HBox(this.buttonRate, this.rateSlider);
			rateTool.setAlignment(Pos.CENTER_LEFT);

			this.settingsBox = new VBox();
			this.settingsBox.setPrefWidth(256d);
			this.settingsBox.setPadding(new Insets(15d));
			this.settingsBox.setSpacing(25d);
			this.settingsBox.getChildren().addAll(volumeTool, rateTool, replayTool);
			this.showSettings(false);

			HBox settingsAndVisualizer = new HBox(this.settingsBox, this.audioSpectrumVisualizer);
			settingsAndVisualizer.setPadding(new Insets(10d, 0d, 10d, 0d));
			settingsAndVisualizer.setOnMouseEntered(a -> this.showSettingsAnimated(true));
			settingsAndVisualizer.setOnMouseExited(b -> this.showSettingsAnimated(false));

			HBox.setHgrow(this.volumeSlider, Priority.SOMETIMES);
			HBox.setHgrow(this.rateSlider, Priority.SOMETIMES);
			HBox.setHgrow(this.replayFromSlider, Priority.SOMETIMES);
			HBox.setHgrow(this.replayToSlider, Priority.SOMETIMES);
			HBox.setHgrow(library, Priority.SOMETIMES);

			VBox.setVgrow(settingsAndVisualizer, Priority.SOMETIMES);

			this.showSettingsAnimation = Animations.newResizeFadeAnimation(this.settingsBox, Duration.seconds(0.25d),
					Duration.seconds(0.1d), true, 0d, 256d, 0d, 1d, Interpolator.EASE_OUT);
			this.showSettingsAnimation.setOnFinished(a -> this.setShowingSettings(true));

			this.hideSettingsAnimation = Animations.newResizeFadeAnimation(this.settingsBox, Duration.seconds(0.25d),
					Duration.seconds(0.1d), true, 256d, 0d, 1d, 0d, Interpolator.EASE_IN);
			this.hideSettingsAnimation.setOnFinished(a -> {
				this.setShowingSettings(false);
				this.settingsBox.setVisible(false);
			});

			//

			this.showLibraryAnimation = Animations.newScaleTransition(this.library, Duration.seconds(0.3d), 0.5d, 1d,
					0d, 1d, Interpolator.EASE_OUT);
			this.showLibraryAnimation.setOnFinished(a -> this.setShowingLibrary(true));

			this.hideLibraryAnimation = Animations.newScaleTransition(this.library, Duration.seconds(0.2d), 1d, 0.5d,
					1d, 0d, Interpolator.EASE_IN);
			this.hideLibraryAnimation.setOnFinished(a -> {
				this.setShowingLibrary(false);
				this.library.setVisible(false);
			});

			//

			this.showDescriptionAnimation = new FadeTranslateAnimation(description);
			this.showDescriptionAnimation.setFadeFromValue(0d);
			this.showDescriptionAnimation.setFadeToValue(1d);
			this.showDescriptionAnimation.setFadeDuration(Duration.seconds(0.25d));
			this.showDescriptionAnimation.setTranslateFromY(20d);
			this.showDescriptionAnimation.setTranslateToY(0d);
			this.showDescriptionAnimation.setTranslateDuration(Duration.seconds(0.2d));

			this.hideDescriptionAnimation = new FadeTranslateAnimation(description);
			this.hideDescriptionAnimation.setFadeFromValue(1d);
			this.hideDescriptionAnimation.setFadeToValue(0d);
			this.hideDescriptionAnimation.setFadeDuration(Duration.seconds(0.25d));
			this.hideDescriptionAnimation.setTranslateFromY(0d);
			this.hideDescriptionAnimation.setTranslateToY(20d);
			this.hideDescriptionAnimation.setTranslateDuration(Duration.seconds(0.2d));

			this.setSpacing(5d);
			this.setPadding(new Insets(15d));
			this.setFillWidth(true);
			this.setPickOnBounds(true);
			this.getChildren().addAll(thumbnailAndDescrLibr, settingsAndVisualizer, sliderAndLabelNodes, controlNodes);
		}

		//
		public void showLibrary(boolean value) {
			if (value) {
				this.library.setVisible(true);
				this.showingLibraryProperty.set(true);
			} else {
				this.library.setVisible(false);
				this.showingLibraryProperty.set(false);
			}
		}

		//
		public void showLibraryAnimated(boolean value) {
			assert this.showLibraryAnimation != null && this.hideLibraryAnimation != null;
			boolean isShowing = this.showLibraryAnimation.getStatus() == Animation.Status.RUNNING
					|| this.isShowingLibrary();
			boolean isHiding = this.hideLibraryAnimation.getStatus() == Animation.Status.RUNNING
					|| !this.isShowingLibrary();
			if (value && !isShowing) {
				this.library.setVisible(true);
				this.showLibraryAnimation.playFromStart();
				this.hideDescriptionAnimation.get().playFromStart();
			} else if (!value && !isHiding) {
				this.hideLibraryAnimation.playFromStart();
				this.showDescriptionAnimation.get().playFromStart();
			}
		}

		private void showSettings(boolean value) {
			double valueForAll = value ? 256d : 0d;
			this.settingsBox.setVisible(value);
			this.settingsBox.setMinWidth(valueForAll);
			this.settingsBox.setMaxWidth(valueForAll);
			this.settingsBox.setPrefWidth(valueForAll);
			this.setShowingSettings(value);
		}

		//
		private void showSettingsAnimated(boolean value) {
			assert this.showSettingsAnimation != null && this.hideSettingsAnimation != null;
			boolean isShowing = this.showSettingsAnimation.getStatus() == Animation.Status.RUNNING
					|| this.isShowingSettings();
			boolean isHiding = this.hideSettingsAnimation.getStatus() == Animation.Status.RUNNING
					|| !this.isShowingSettings();
			if (value && !isShowing) {
				this.settingsBox.setVisible(true);
				this.settingsBox.setMinWidth(0d);
				this.settingsBox.setMaxWidth(0d);
				this.settingsBox.setPrefWidth(0d);
				this.showSettingsAnimation.playFromStart();
			} else if (!value && !isHiding) {
				this.settingsBox.setMinWidth(256d);
				this.settingsBox.setMaxWidth(256d);
				this.settingsBox.setPrefWidth(256d);
				this.hideSettingsAnimation.playFromStart();
			}
		}

		//
//		private void onToggleReplayClicked()
//		{
//
//		}

		//
		private void onRepeatButtonClicked() {
			if (mode == AudioPlayMode.DEFAULT || mode == AudioPlayMode.SHUFFLE) {
				mode = AudioPlayMode.REPEAT_PLAYLIST;
				this.buttonRepeat.setGraphic(new ImageView(this.IMG_REPLAY_DEFAULT));
			} else if (mode == AudioPlayMode.REPEAT_PLAYLIST) {
				mode = AudioPlayMode.REPEAT_SINGLE;
				this.buttonRepeat.setGraphic(new ImageView(this.IMG_REPLAY_SINGLE));
			} else if (mode == AudioPlayMode.REPEAT_SINGLE) {
				mode = AudioPlayMode.DEFAULT;
				this.buttonRepeat.setGraphic(new Label("No Replay"));
			}
		}

		// TODO
//		private void onShuffleButtonClicked()
//		{
//
//		}

		//
		private VBox initAudioSpectrumVisualizer(int bands) {
			VBox root = new VBox();
			root.setAlignment(Pos.BOTTOM_CENTER);
			HBox bandsBox = new HBox();
			bandsBox.maxWidthProperty().bind(root.widthProperty());
			bandsBox.setSpacing(5d);
			bandsBox.setOpacity(0.6d);
			// bandsBox.setPadding(new Insets(0d));
			bandsBox.setAlignment(Pos.BOTTOM_CENTER);

			for (int i = 0; i < bands; i++) {
				HBox rect = new HBox();
				rect.setStyle("-fx-border-color: white; -fx-border-radius: 5 5 0 0; -fx-background-radius: 5 5 0 0;");
				rect.setMinWidth(5d);
				rect.maxWidthProperty().bind(bandsBox.widthProperty().divide(bands)
						.subtract(bandsBox.getSpacing())/* .subtract(paddingLeft).subtract(paddingRight) */);
				rect.setMinHeight(0d);
				rect.setMaxHeight(0d);
				bandsBox.getChildren().add(rect);
				HBox.setHgrow(rect, Priority.SOMETIMES);
			}
			root.getChildren().add(bandsBox);
			return root;
		}

		//
		public void switchToPlayGraphic() {
			this.buttonPlayPause.setGraphic(new ImageView(this.IMG_PLAY));
		}

		//
		public void switchToPauseGraphic() {
			this.buttonPlayPause.setGraphic(new ImageView(this.IMG_PAUSE));
		}

		/*
		 *
		 */
		public ReadOnlyDoubleProperty volumeProperty() {
			return this.volumeProperty;
		}

		public double getVolume() {
			return this.volumeProperty.get();
		}

		public void setVolume(double value) {
			this.volumeProperty.set(value);
		}

		public ReadOnlyDoubleProperty volumeBeforeMuteProperty() {
			return this.volumeBeforeMuteProperty;
		}

		public double getVolumeBeforeMute() {
			return this.volumeBeforeMuteProperty.get();
		}

		private void setVolumeBeforeMute(double value) {
			this.volumeBeforeMuteProperty.set(value);
		}

		public ReadOnlyDoubleProperty rateProperty() {
			return this.rateProperty;
		}

		public double getRate() {
			return this.rateProperty.get();
		}

		public void setRate(double value) {
			this.rateProperty.set(value);
		}

		public ReadOnlyBooleanProperty showingLibraryProperty() {
			return this.showingLibraryProperty;
		}

		public boolean isShowingLibrary() {
			return this.showingLibraryProperty.get();
		}

		private void setShowingLibrary(boolean value) {
			this.showingLibraryProperty.set(value);
		}

		public ReadOnlyBooleanProperty showingSettingsProperty() {
			return this.showingSettingsProperty;
		}

		public boolean isShowingSettings() {
			return this.showingSettingsProperty.get();
		}

		private void setShowingSettings(boolean value) {
			this.showingSettingsProperty.set(value);
		}

		public Image getThumbnailImage() {
			return this.thumbnailView.getImage();
		}

		public void setThumbnailImage(Image value) {
			this.thumbnailView.setImage(value);
		}

		public String getTitle() {
			return this.textTitle.getText();
		}

		public void setTitle(String value) {
			this.textTitle.setText(value);
		}

		public String getAlbum() {
			return this.textAlbum.getText();
		}

		public void setAlbum(String value) {
			this.textAlbum.setText(value);
		}

		public String getArtist() {
			return this.textArtist.getText();
		}

		public void setArtist(String value) {
			this.textArtist.setText(value);
		}

		public String getYear() {
			return this.textYear.getText();
		}

		public void setYear(String value) {
			this.textYear.setText(value);
		}

		public VBox getAudioSpectrumVisualizer() {
			return this.audioSpectrumVisualizer;
		}

		public HBox getAudioSpectrumVisualizerBox() {
			return (HBox) this.audioSpectrumVisualizer.getChildren().get(0);
		}

		public JFXSlider getTimeSlider() {
			return this.timeSlider;
		}

		public JFXSlider getRateSlider() {
			return this.rateSlider;
		}

		public JFXSlider getReplayFromSlider() {
			return this.replayFromSlider;
		}

		public JFXSlider getReplayToSlider() {
			return this.replayToSlider;
		}

		public String getCurrentTime() {
			return this.labelCurrentTime.getText();
		}

		public void setCurrentTime(String value) {
			this.labelCurrentTime.setText(value);
		}

		public String getTotalTime() {
			return this.labelTotalTime.getText();
		}

		public void setTotalTime(String value) {
			this.labelTotalTime.setText(value);
		}

		public JFXButton getPreviousButton() {
			return this.buttonPrevious;
		}

		public JFXButton getPlayPauseButton() {
			return this.buttonPlayPause;
		}

		public JFXButton getNextButton() {
			return this.buttonNext;
		}

		public CAudioLibrary getLibrary() {
			return this.library;
		}
	}

	//
	public class CAudioLibrary extends TableView<CAudioData> {
		private ObservableList<CAudioData> data;
		//
		private TableColumn<CAudioData, String> tableColumnTitle;
		private TableColumn<CAudioData, String> tableColumnArtist;
		private TableColumn<CAudioData, String> tableColumnAlbum;
		private TableColumn<CAudioData, String> tableColumnYear;
		//

		//
		public CAudioLibrary() {
			super();

			this.data = FXCollections.observableArrayList();

			this.tableColumnTitle = new TableColumn<>("Titel");
			this.tableColumnTitle.setSortable(false);
			this.tableColumnTitle.prefWidthProperty().bind(this.widthProperty().multiply(0.25d));
			this.tableColumnTitle.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<CAudioData, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(CellDataFeatures<CAudioData, String> param) {
							return param.getValue().titleProperty();
						}
					});

			this.tableColumnArtist = new TableColumn<>("Interpret");
			this.tableColumnArtist.setSortable(false);
			this.tableColumnArtist.prefWidthProperty().bind(this.widthProperty().multiply(0.25d));
			this.tableColumnArtist.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<CAudioData, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(CellDataFeatures<CAudioData, String> param) {
							return param.getValue().artistProperty();
						}
					});

			this.tableColumnAlbum = new TableColumn<>("Album");
			this.tableColumnAlbum.setSortable(false);
			this.tableColumnAlbum.prefWidthProperty().bind(this.widthProperty().multiply(0.25d));
			this.tableColumnAlbum.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<CAudioData, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(CellDataFeatures<CAudioData, String> param) {
							return param.getValue().albumProperty();
						}
					});

			this.tableColumnYear = new TableColumn<>("Jahr");
			this.tableColumnYear.setSortable(false);
			this.tableColumnYear.prefWidthProperty().bind(this.widthProperty().multiply(0.25d));
			this.tableColumnYear.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<CAudioData, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(CellDataFeatures<CAudioData, String> param) {
							return param.getValue().yearProperty();
						}
					});

			this.setOnMouseClicked(a -> this.onLibraryItemClicked(a));

			this.setPlaceholder(new Label(""));
			this.setFixedCellSize(40d);
			this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.setColumnResizePolicy(CAudioLibrary.CONSTRAINED_RESIZE_POLICY);
			this.setEditable(false);

			this.getColumns().add(this.tableColumnTitle);
			this.getColumns().add(this.tableColumnArtist);
			this.getColumns().add(this.tableColumnAlbum);
			this.getColumns().add(this.tableColumnYear);
			this.setItems(this.data);
		}

		//
		public void addAudioData(CAudioData data) {
			this.data.add(data);
		}

		//
		public void addAudioData(CAudioData... data) {
			this.data.addAll(data);
		}

		//
		public void addAudioData(int index, CAudioData data) {
			this.data.add(index, data);
		}

		//
		public void setAudioData(int index, CAudioData data) {
			this.data.set(index, data);
		}

		//
		public boolean setAudioData(CAudioData oldData, CAudioData newData) {
			int index = this.data.indexOf(oldData);
			if (index >= 0) {
				this.data.set(index, newData);
				return true;
			} else
				return false;
		}

		//
		public boolean removeAudioData(CAudioData data) {
			return this.data.remove(data);
		}

		//
		public boolean removeAudioData(CAudioData... data) {
			return this.data.removeAll(data);
		}

		//
		public boolean removeAudioData(int index) {
			return (this.data.remove(index) != null);
		}

		//
		public void removeAudioData(int indexFrom, int indexTo) {
			this.data.remove(indexFrom, indexTo);
		}

		//
		public int indexOf(CAudioData data) {
			return this.data.indexOf(data);
		}

		//
		private void onLibraryItemClicked(MouseEvent e) {
			if (e.getClickCount() >= 2) {
				int selectedItem = this.getSelectionModel().getSelectedIndex();
				if (getCurrentTrackIndex() != selectedItem)
					play(selectedItem);
				else
					seek(Duration.seconds(0d));
			}
		}

		/*
		 *
		 */

		public ObservableList<CAudioData> getData() {
			return this.data;
		}

		public void setData(ObservableList<CAudioData> data) {
			this.data = data;
		}
	}

	//
	public class CAudioData {
		//
		private StringProperty titleProperty;
		private StringProperty artistProperty;
		private StringProperty albumProperty;
		private StringProperty yearProperty;

		//
		public CAudioData(String title, String artist, String album, String year) {
			this.titleProperty = new SimpleStringProperty(title);
			this.artistProperty = new SimpleStringProperty(artist);
			this.albumProperty = new SimpleStringProperty(album);
			this.yearProperty = new SimpleStringProperty(year);
		}

		//
		@Override
		public String toString() {
			return "Title: " + this.getTitle() + " Artist: " + this.getArtist() + " Album: " + this.getAlbum()
					+ " Year: " + this.getYear();
		}

		/*
		 *
		 */

		public StringProperty titleProperty() {
			return this.titleProperty;
		}

		public String getTitle() {
			return this.titleProperty.get();
		}

		public void setTitle(String value) {
			this.titleProperty.set(value);
		}

		public StringProperty artistProperty() {
			return this.artistProperty;
		}

		public String getArtist() {
			return this.artistProperty.get();
		}

		public void setArtist(String value) {
			this.artistProperty.set(value);
		}

		public StringProperty albumProperty() {
			return this.albumProperty;
		}

		public String getAlbum() {
			return this.albumProperty.get();
		}

		public void setAlbum(String value) {
			this.albumProperty.set(value);
		}

		public StringProperty yearProperty() {
			return this.yearProperty;
		}

		public String getYear() {
			return this.yearProperty.get();
		}

		public void setYear(String value) {
			this.yearProperty.set(value);
		}
	}
}
