package apps.audioPlayer;

import java.io.Closeable;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Random;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import utils.FXUtils;
import utils.Utils;

public class AudioPlayer implements Closeable {	
	private IntegerProperty currentTrackIndexProperty;
	private IntegerProperty previousTrackIndexProperty;
	private DoubleProperty volumeProperty;
	private DoubleProperty volumeBeforeMuteProperty;
	private DoubleProperty rateProperty;
	private BooleanProperty partReplayProperty;
	private ObjectProperty<Duration> replayFromDurationProperty;
	private ObjectProperty<Duration> replayToDurationProperty;
	
	private AudioPlayMode playerMode;
	private AudioPlayerUI playerUI;
	private MediaPlayer player;
	
	private ObservableList<URI> playlist;
	
	public AudioPlayer() {
		initialize();
	}
	
	public void initialize() {
		initProperties();
		initEventHandlers();
	}
	
	private void initProperties() {
		currentTrackIndexProperty = new SimpleIntegerProperty(-1);
		previousTrackIndexProperty = new SimpleIntegerProperty(-1);
		volumeProperty = new SimpleDoubleProperty(0.5d);
		volumeBeforeMuteProperty = new SimpleDoubleProperty(0.5d);
		rateProperty = new SimpleDoubleProperty(1d);
		partReplayProperty = new SimpleBooleanProperty(false);
		replayFromDurationProperty = new SimpleObjectProperty<>(Duration.millis(0d));
		replayToDurationProperty = new SimpleObjectProperty<>(Duration.millis(0d));
		
		playerMode = AudioPlayMode.DEFAULT;
		playerUI = new AudioPlayerUI();
		
		playlist = FXCollections.observableArrayList();
	}
	
	private void initEventHandlers() {
		playerUI.setOnMouseClicked(a -> {
			if (a.getButton() == MouseButton.SECONDARY) {
				FileChooser fc = new FileChooser();
				List<File> files = fc.showOpenMultipleDialog(playerUI.getScene().getWindow());
				if (files != null) {
					for(File file : files)
						addToPlaylist(file.toURI());
					play();
				}
			}
		});
		
		playerUI.getThumbnailView().setOnMouseClicked(a -> {
			if (a.getButton() == MouseButton.PRIMARY)
				playerUI.showLibraryAnimated(true);
			else
				playerUI.showLibraryAnimated(false);
		});
		
		playerUI.getTimeSlider().valueProperty().addListener((obs, oldV, newV) -> {
			if (!playerUI.getTimeSlider().isValueChanging() && player.getTotalDuration() != null) {
				Duration currentTime = player.getCurrentTime();
				Duration dragTime = player.getTotalDuration().multiply(newV.doubleValue() / playerUI.getTimeSlider().getMax());
				if (Math.abs(currentTime.subtract(dragTime).toSeconds()) >= 0.5d)
					player.seek(dragTime);
			}
		});
		playerUI.getTimeSlider().valueChangingProperty().addListener((obs, oldV, newV) -> {
			if (!newV.booleanValue())
				player.seek(Duration.millis((playerUI.getTimeSlider().getValue() / playerUI.getTimeSlider().getMax()) * player.getTotalDuration().toMillis()));
		});
		playerUI.getTimeSlider().setValueFactory(a -> {
			StringBinding textBinding = new StringBinding() {
				{
					super.bind(playerUI.getTimeSlider().valueProperty());
				}

				@Override
				protected String computeValue() {
					if (player != null && player.getTotalDuration() != null)
						return Utils.durationToMMSS(Duration.seconds((playerUI.getTimeSlider().getValue() / playerUI.getTimeSlider().getMax()) * player.getTotalDuration().toSeconds()));
					else
						return "";
				}
			};
			return textBinding;
		});
		
		playerUI.getVolumeSlider().setValueFactory(a -> {
			return new StringBinding() {
				{
					super.bind(playerUI.getVolumeSlider().valueProperty());
				}

				@Override
				protected String computeValue() {
					return "" + (int)((playerUI.getVolumeSlider().getValue() / playerUI.getVolumeSlider().getMax()) * 100d);
				}
			};
		});
		playerUI.getVolumeSlider().valueProperty().addListener((obs, oldV, newV) -> {
			if (player != null) {
				player.setVolume(newV.doubleValue());
				setVolume(newV.doubleValue());
			}

			double ratio = newV.doubleValue() / playerUI.getVolumeSlider().getMax();

			if (ratio == 0d)
				playerUI.getVolumeButton().setGraphic(new ImageView(AudioPlayerUI.IMG_VOLUME_MUTED));
			else if (ratio > 0d && ratio <= 0.33d)
				playerUI.getVolumeButton().setGraphic(new ImageView(AudioPlayerUI.IMG_VOLUME_LOW));
			else if (ratio > 0.33d && ratio <= 0.66d)
				playerUI.getVolumeButton().setGraphic(new ImageView(AudioPlayerUI.IMG_VOLUME_MEDIUM));
			else
				playerUI.getVolumeButton().setGraphic(new ImageView(AudioPlayerUI.IMG_VOLUME_HIGH));
		});
		
		playerUI.getRateSlider().setValueFactory(a -> {
			return new StringBinding() {
				{
					super.bind(playerUI.getRateSlider().valueProperty());
				}

				@Override
				protected String computeValue() {
					return playerUI.getRateSlider().getValue() + "X";
				}
			};
		});
		playerUI.getRateSlider().valueChangingProperty().addListener((obs, oldV, newV) -> {
			if (newV.booleanValue()) {
				playerUI.getRateSlider().setShowTickLabels(true);
				playerUI.getRateSlider().setShowTickMarks(true);
			} 
			else {
				playerUI.getRateSlider().setShowTickLabels(false);
				playerUI.getRateSlider().setShowTickMarks(false);
				playerUI.getRateSlider().adjustValue(playerUI.getRateSlider().getValue());

				player.setRate(playerUI.getRateSlider().getValue() - (playerUI.getRateSlider().getValue() % playerUI.getRateSlider().getBlockIncrement()));
				setRate(player.getRate());

				Label newRate = new Label(playerUI.getRateSlider().getValue() + "X");
				newRate.setTextFill(Color.WHITE);
				newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
				playerUI.getRateButton().setGraphic(newRate);
			}
		});
		playerUI.getRateSlider().pressedProperty().addListener((obs, oldV, newV) -> {
			if (!newV.booleanValue() && oldV.booleanValue()/* !this.rateSlider.isValueChanging() */) {
				player.setRate(playerUI.getRateSlider().getValue() - (playerUI.getRateSlider().getValue() % playerUI.getRateSlider().getBlockIncrement()));
				Label newRate = new Label(playerUI.getRateSlider().getValue() + "X");
				newRate.setTextFill(Color.WHITE);
				newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
				playerUI.getRateButton().setGraphic(newRate);
			}
		});
		
		playerUI.getVolumeButton().setOnAction(a -> {
			if (player != null) {
				if (player.getVolume() > 0d) {
					setVolumeBeforeMute(playerUI.getVolumeSlider().getValue());
					playerUI.getVolumeSlider().setValue(0d);
				} 
				else
					playerUI.getVolumeSlider().setValue(getVolumeBeforeMute());
			}
		});
		
		playerUI.getRateButton().setOnAction(a -> {
			double currentRate = playerUI.getRateSlider().getValue();
			double minRate = playerUI.getRateSlider().getMin();
			double maxRate = playerUI.getRateSlider().getMax();
			double tick = playerUI.getRateSlider().getMajorTickUnit() / (playerUI.getRateSlider().getMinorTickCount() + 1d);
			if (currentRate >= maxRate)
				playerUI.getRateSlider().setValue(minRate);
			else
				playerUI.getRateSlider().setValue(currentRate + tick);

			Label newRate = new Label(playerUI.getRateSlider().getValue() + "X");
			newRate.setTextFill(Color.WHITE);
			newRate.setFont(Font.font("Arial", FontWeight.BOLD, 18d));
			playerUI.getRateButton().setGraphic(newRate);
		});
		
		playerUI.getLibraryButton().setOnAction(a -> playerUI.showLibraryAnimated(!playerUI.isShowingLibrary()));
		
		playerUI.getPlayPauseButton().setOnAction(a -> {
			if (player.getStatus().equals(Status.PLAYING))
				player.pause();
			else
				player.play();
		});
		playerUI.getNextButton().setOnAction(a -> playNext());
		playerUI.getPreviousButton().setOnAction(a -> playPrevious());
		playerUI.getRepeatButton().setOnAction(a -> onRepeatButtonClicked());
		
		playerUI.getReplayToSlider().setValueFactory(a -> {
			StringBinding textBinding = new StringBinding() {
				{
					super.bind(playerUI.getReplayToSlider().valueProperty());
				}

				@Override
				protected String computeValue() {
					if (player != null && player.getTotalDuration() != null)
						return Utils.durationToMMSS(Duration.seconds((playerUI.getReplayToSlider().getValue() / playerUI.getReplayToSlider().getMax()) * player.getTotalDuration().toSeconds()));
					else
						return "";
				}
			};
			return textBinding;
		});
		playerUI.getReplayToSlider().valueProperty().addListener((obs, oldV, newV) -> {
			if (player != null && player.getTotalDuration() != null)
				setReplayToDuration(FXUtils.sliderValueToDuration(playerUI.getReplayToSlider(), player.getTotalDuration()));
			if (playerUI.getReplayFromSlider() != null && playerUI.getReplayFromSlider().getValue() > newV.doubleValue())
				playerUI.getReplayFromSlider().setValue(newV.doubleValue());
		});
		playerUI.getReplayToSlider().setValue(playerUI.getReplayFromSlider().getMax());
		
		playerUI.getReplayFromSlider().valueProperty().addListener((obs, oldV, newV) -> {
			if (player != null && player.getTotalDuration() != null)
				setReplayFromDuration(FXUtils.sliderValueToDuration(playerUI.getReplayFromSlider(), player.getTotalDuration()));
			if (playerUI.getReplayToSlider() != null && playerUI.getReplayToSlider().getValue() < newV.doubleValue())
				playerUI.getReplayToSlider().setValue(newV.doubleValue());
		});
		playerUI.getReplayFromSlider().setValue(playerUI.getReplayFromSlider().getMin());
		playerUI.getReplayFromSlider().setValueFactory(a -> {
			StringBinding textBinding = new StringBinding() {
				{
					super.bind(playerUI.getReplayFromSlider().valueProperty());
				}

				@Override
				protected String computeValue() {
					if (player != null && player.getTotalDuration() != null)
						return Utils.durationToMMSS(Duration.seconds((playerUI.getReplayFromSlider().getValue() / playerUI.getReplayFromSlider().getMax()) * player.getTotalDuration().toSeconds()));
					else
						return "";
				}
			};
			return textBinding;
		});
		
		playerUI.getLibrary().setOnMouseClicked(a -> onLibraryItemClicked(a));
	}
	
	@Override
	public void close() {
		playerUI = null;
		player.stop();
		playlist.clear();
	}

	public int getPercentageVolume() {
		return (int) getVolume() * 100;
	}

	public int getPercentageRate() {
		return (int) getRate() * 100;
	}

	private Media initMedia(URI mediaUri) {
		Media media = new Media(mediaUri.toString());
		media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
			Object newValue = change.getValueAdded();
			int tempIndex[] = new int[] { 0 };

			if (newValue != null) {
				switch (change.getKey().toString()) {
				case "image":
					playerUI.setThumbnailImage((Image) newValue);
					break;
				case "title":
					playlist.forEach(a -> {
						if (a.compareTo(mediaUri) == 0)
							playerUI.getLibrary().getItems().get(tempIndex[0]).setTitle((String) newValue);
						if (getCurrentTrackIndex() == playlist.indexOf(a))
							playerUI.setTitle((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "artist":
					playlist.forEach(a -> {
						if (a.compareTo(mediaUri) == 0)
							playerUI.getLibrary().getItems().get(tempIndex[0]).setArtist((String) newValue);
						if (getCurrentTrackIndex() == playlist.indexOf(a))
							playerUI.setArtist((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "album":
					playlist.forEach(a -> {
						if (a.compareTo(mediaUri) == 0)
							playerUI.getLibrary().getItems().get(tempIndex[0]).setAlbum((String) newValue);
						if (getCurrentTrackIndex() == playlist.indexOf(a))
							playerUI.setAlbum((String) newValue);
						tempIndex[0]++;
					});
					break;
				case "year":
					playlist.forEach(a -> {
						if (a.compareTo(mediaUri) == 0)
							playerUI.getLibrary().getItems().get(tempIndex[0]).setYear(Integer.toString((int) newValue));
						if (getCurrentTrackIndex() == playlist.indexOf(a))
							playerUI.setYear(Integer.toString((int) newValue));
						tempIndex[0]++;
					});
					break;
				}
			}
		});
		return media;
	}

	private MediaPlayer initPlayer(URI mediaUri, boolean autoPlay) {
		playerUI.setTitle("-");
		playerUI.setArtist("-");
		playerUI.setAlbum("-");
		playerUI.setYear("-");

		Media media = initMedia(mediaUri);

		MediaPlayer mediaPlayer = new MediaPlayer(media);

		mediaPlayer.statusProperty().addListener((obs, oldV, newV) -> {
			if (newV == Status.PLAYING)
				playerUI.switchToPauseGraphic();
			else
				playerUI.switchToPlayGraphic();

		});
		mediaPlayer.currentTimeProperty().addListener((obs, oldV, newV) -> {
			if (isPartReplay()) {
				Duration replayFrom = getReplayFromDuration();
				Duration replayTo = getReplayToDuration();
				if (newV.greaterThanOrEqualTo(replayTo) && replayTo.greaterThanOrEqualTo(replayFrom.add(Duration.seconds(1d))))
					mediaPlayer.seek(replayFrom);
			}
			if (!playerUI.getTimeSlider().isValueChanging()) {
				double sliderMax = playerUI.getTimeSlider().getMax();
				playerUI.getTimeSlider().setValue((newV.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * sliderMax);
				playerUI.setCurrentTime(Utils.durationToMMSS(newV));
			}
		});
		mediaPlayer.totalDurationProperty().addListener((obs, oldV, newV) -> {
			playerUI.setTotalTime(Utils.durationToMMSS(newV));
		});

		mediaPlayer.setOnEndOfMedia(() -> onMediaEnd());
		mediaPlayer.setAutoPlay(autoPlay);
		mediaPlayer.volumeProperty().bindBidirectional(volumeProperty());
		mediaPlayer.rateProperty().bindBidirectional(rateProperty());

		mediaPlayer.setAudioSpectrumNumBands(playerUI.getAudioSpectrumBands());
		mediaPlayer.setAudioSpectrumInterval(0.05d);
		mediaPlayer.setAudioSpectrumThreshold(-100); // A good value to display ALL bands
		mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener() {
			@Override
			public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
				if (mediaPlayer.getStatus() == Status.PLAYING) {
					for (int i = 0; i < magnitudes.length; i++) {
						double decibels = (magnitudes[i] - mediaPlayer.getAudioSpectrumThreshold());
						double paddingTop = playerUI.getAudioSpectrumVisualizerBox().getPadding().getTop();
						double paddingBottom = playerUI.getAudioSpectrumVisualizerBox().getPadding().getBottom();
						double calcHeight = (decibels / Math.abs(mediaPlayer.getAudioSpectrumThreshold())
											* playerUI.getAudioSpectrumVisualizer().getHeight() - paddingTop - paddingBottom);
						((HBox) playerUI.getAudioSpectrumVisualizerBox().getChildren().get(i)).setMinHeight(calcHeight);
						((HBox) playerUI.getAudioSpectrumVisualizerBox().getChildren().get(i)).setMaxHeight(calcHeight);
					}
				}
			}
		});
		return mediaPlayer;
	}

	public void addToPlaylist(URI... uris) {
		for (URI uri : uris)
			initMedia(uri);
		playerUI.getLibrary().addAudioData(new AudioLibraryItem("-", "-", "-", "-"));
		playlist.addAll(uris);
	}

	public boolean removeFromPlaylist(URI... uris) {
		int oldSize = playlist.size();
		int currentIndex;
		for (int i = 0; i < uris.length; i++) {
			currentIndex = playlist.indexOf(uris[i]);
			if (currentIndex >= 0) {
				playerUI.getLibrary().removeAudioData(currentIndex);
				playlist.remove(currentIndex);
			}
		}
		return playlist.size() < oldSize;
	}

	public void removeFromPlaylist(int index) {
		playerUI.getLibrary().removeAudioData(index);
		playlist.remove(index);
	}
	
	public boolean play() {
		return play(0);
	}

	public boolean play(int index) {
		if (index < 0 || index >= playlist.size())
			return false;
		try {
			if (player != null)
				player.stop();
			player = initPlayer(playlist.get(index), true);
			return true;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		finally {
			if (getCurrentTrackIndex() != index)
				setPreviousTrackIndex(getCurrentTrackIndex());
			setCurrentTrackIndex(index);
		}
	}
	
	public boolean playNext() {
		return play(getCurrentTrackIndex() + 1);
	}
	
	public boolean playNextOrStartAtBeginning() {
		return play(getCurrentTrackIndex() >= playlist.size() ? 0 : getCurrentTrackIndex() + 1);
	}

	public boolean playPrevious() {
		return play(getCurrentTrackIndex() - 1);
	}

	public boolean playRandom() {
		Random randomGen = new Random();
		int nextRandom;
		do {
			nextRandom = randomGen.nextInt(playlist.size());
		} 
		while (nextRandom == getCurrentTrackIndex());
		return play(nextRandom);
	}

	public boolean seek(Duration duration) {
		if (player == null || duration == null || duration.lessThanOrEqualTo(Duration.ZERO))
			return false;
		player.seek(duration);
		return true;
	}

	private void onMediaEnd() {
		switch (playerMode) {
		case DEFAULT:
			playNext();
			break;
		case REPEAT_SINGLE:
			play(getCurrentTrackIndex());
			break;
		case REPEAT_PLAYLIST:
			playNextOrStartAtBeginning();
			break;
		case SHUFFLE:
			playRandom();
			break;
		}
	}

	public void onRepeatButtonClicked() {
		switch(playerMode) {
		case DEFAULT:
		case SHUFFLE:
			playerMode = AudioPlayMode.REPEAT_PLAYLIST;
			break;
		case REPEAT_PLAYLIST:
			playerMode = AudioPlayMode.REPEAT_SINGLE;
			break;
		case REPEAT_SINGLE:
			playerMode = AudioPlayMode.DEFAULT;
			break;
		}
		playerUI.switchToPlayMode(playerMode);
	}
	
	//TODO
	public void onToggleReplayClicked()
	{

	}

	// TODO
	public void onShuffleButtonClicked()
	{

	}
	
	public void onLibraryItemClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			int selectedItem = playerUI.getLibrary().getSelectionModel().getSelectedIndex();
			if (getCurrentTrackIndex() != selectedItem)
				play(selectedItem);
			else
				seek(Duration.seconds(0d));
		}
	}

	public MediaPlayer getMediaPlayer() {
		return player;
	}

	public AudioPlayMode getMediaMode() {
		return playerMode;
	}

	public void setMediaMode(AudioPlayMode mode) {
		playerMode = mode;
	}

	public IntegerProperty currentTrackIndexProperty() {
		return currentTrackIndexProperty;
	}

	public int getCurrentTrackIndex() {
		return currentTrackIndexProperty.get();
	}

	public void setCurrentTrackIndex(int value) {
		currentTrackIndexProperty.set(value);
	}

	public IntegerProperty previousTrackIndexProperty() {
		return previousTrackIndexProperty;
	}

	public int getPreviousTrackIndex() {
		return previousTrackIndexProperty.get();
	}

	public void setPreviousTrackIndex(int value) {
		previousTrackIndexProperty.set(value);
	}

	public BooleanProperty partReplayProperty() {
		return partReplayProperty;
	}

	public boolean isPartReplay() {
		return partReplayProperty.get();
	}

	public void setPartReplay(boolean value) {
		partReplayProperty.set(value);
	}

	public ObjectProperty<Duration> replayFromDurationProperty() {
		return replayFromDurationProperty;
	}

	public Duration getReplayFromDuration() {
		return replayFromDurationProperty.get();
	}

	public void setReplayFromDuration(Duration value) {
		replayFromDurationProperty.set(value);
	}

	public ObjectProperty<Duration> replayToDurationProperty() {
		return replayToDurationProperty;
	}

	public Duration getReplayToDuration() {
		return replayToDurationProperty.get();
	}

	public void setReplayToDuration(Duration value) {
		replayToDurationProperty.set(value);
	}
	
	public DoubleProperty volumeProperty() {
		return volumeProperty;
	}

	public double getVolume() {
		return volumeProperty.get();
	}

	public void setVolume(double value) {
		volumeProperty.set(value);
	}

	public ReadOnlyDoubleProperty volumeBeforeMuteProperty() {
		return volumeBeforeMuteProperty;
	}

	public double getVolumeBeforeMute() {
		return volumeBeforeMuteProperty.get();
	}

	public void setVolumeBeforeMute(double value) {
		volumeBeforeMuteProperty.set(value);
	}

	public DoubleProperty rateProperty() {
		return rateProperty;
	}

	public double getRate() {
		return rateProperty.get();
	}

	public void setRate(double value) {
		rateProperty.set(value);
	}

	public AudioPlayerUI getUI() {
		return playerUI;
	}
}
