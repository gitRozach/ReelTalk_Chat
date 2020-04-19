package gui.components;

import java.net.URI;
import java.net.URL;

import com.jfoenix.controls.JFXButton;

import apps.audioPlayer.AudioPlayer;
import apps.imageViewer.ImageViewer;
import apps.videoPlayer.VideoPlayer;
import gui.animations.Animations;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MediaPane extends StackPane
{
	//
	private ObjectProperty<Node> contentProperty;
	//
	private boolean showingAudioPlayer;
	private boolean showingVideoPlayer;
	private boolean showingImageViewer;
	//
	private AudioPlayer audioPlayer;
	private VideoPlayer videoPlayer;
	private ImageViewer imageViewer;
	//
	private TranslateTransition showAudioPlayerAnimation;
	private TranslateTransition hideAudioPlayerAnimation;
	private TranslateTransition showVideoPlayerAnimation;
	private TranslateTransition hideVideoPlayerAnimation;
	private TranslateTransition showImageViewerAnimation;
	private TranslateTransition hideImageViewerAnimation;

	//
	public MediaPane(Node content)
	{
		this.contentProperty = new SimpleObjectProperty<Node>(content);

		this.heightProperty().addListener((obs, oldV, newV) ->
		{
			if(!this.showingAudioPlayer)
				this.audioPlayer.getUI().setTranslateY(-newV.doubleValue());
			if(!this.showingVideoPlayer)
				this.videoPlayer.setTranslateY(-newV.doubleValue());
			if(!this.showingImageViewer)
				this.imageViewer.setTranslateY(-newV.doubleValue());
		});

		this.showingAudioPlayer = false;
		this.showingVideoPlayer = false;
		this.showingImageViewer = false;

		this.audioPlayer = new AudioPlayer();
		this.videoPlayer = new VideoPlayer();
		this.imageViewer = new ImageViewer();

		JFXButton b1 = new JFXButton("Audio Player");
		b1.setOnAction(a ->
		{
			if(this.showingAudioPlayer && this.showAudioPlayerAnimation.getStatus() != Status.RUNNING)
				this.hideAudioPlayer();
			else if(!this.showingAudioPlayer && this.hideAudioPlayerAnimation.getStatus() != Status.RUNNING)
				this.showAudioPlayer();
		});
		JFXButton b2 = new JFXButton("Video Player");
		b2.setOnAction(a ->
		{
			if(this.showingVideoPlayer && this.showVideoPlayerAnimation.getStatus() != Status.RUNNING)
				this.hideVideoPlayer();
			else if(!this.showingVideoPlayer && this.hideVideoPlayerAnimation.getStatus() != Status.RUNNING)
				this.showVideoPlayer();
		});
		JFXButton b3 = new JFXButton("Image Viewer");
		b3.setOnAction(a ->
		{
			if(this.showingImageViewer && this.showImageViewerAnimation.getStatus() != Status.RUNNING)
				this.hideImageViewer();
			else if(!this.showingImageViewer && this.hideImageViewerAnimation.getStatus() != Status.RUNNING)
				this.showImageViewer();
		});
		HBox buttons = new HBox(b1, b2, b3);
		buttons.setPickOnBounds(false);
		buttons.setFillHeight(false);
		buttons.setAlignment(Pos.TOP_CENTER);
		//CMediaPane.setAlignment(buttons, Pos.TOP_CENTER);

		this.setAlignment(Pos.CENTER);
		this.getChildren().addAll(this.getContent(), this.audioPlayer.getUI(), this.videoPlayer, this.imageViewer, buttons);

		this.showAudioPlayerAnimation = Animations.newTranslateTransition(this.audioPlayer.getUI(), Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.showAudioPlayerAnimation.setToY(0d);
		this.hideAudioPlayerAnimation = Animations.newTranslateTransition(this.audioPlayer.getUI(), Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.hideAudioPlayerAnimation.toYProperty().bind(this.heightProperty().negate());
		//
		this.showVideoPlayerAnimation = Animations.newTranslateTransition(this.videoPlayer, Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.showVideoPlayerAnimation.setToY(0d);
		this.hideVideoPlayerAnimation = Animations.newTranslateTransition(this.videoPlayer, Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.hideVideoPlayerAnimation.toYProperty().bind(this.heightProperty().negate());
		//
		this.showImageViewerAnimation = Animations.newTranslateTransition(this.imageViewer, Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.showImageViewerAnimation.setToY(0d);
		this.hideImageViewerAnimation = Animations.newTranslateTransition(this.imageViewer, Duration.seconds(0.3d), Interpolator.EASE_BOTH, 1, false);
		this.hideImageViewerAnimation.toYProperty().bind(this.heightProperty().negate());
	}

	//
	public void showAudioPlayer()
	{
		if(this.showingVideoPlayer)
			this.hideVideoPlayer();
		else if(this.showingImageViewer)
			this.hideImageViewer();

		this.showingAudioPlayer = true;
		this.showAudioPlayerAnimation.playFromStart();
		//this.hideOthers();
	}

	//
	public void hideAudioPlayer()
	{
		this.showingAudioPlayer = false;
		this.hideAudioPlayerAnimation.playFromStart();
	}

	//
	public void showVideoPlayer()
	{
		if(this.showingAudioPlayer)
			this.hideAudioPlayer();
		else if(this.showingImageViewer)
			this.hideImageViewer();

		this.showingVideoPlayer = true;
		this.showVideoPlayerAnimation.playFromStart();
		//this.hideOthers();
	}

	//
	public void hideVideoPlayer()
	{
		this.showingVideoPlayer = false;
		this.hideVideoPlayerAnimation.playFromStart();
	}

	//
	public void showImageViewer()
	{
		if(this.showingAudioPlayer)
			this.hideAudioPlayer();
		else if(this.showingVideoPlayer)
			this.hideVideoPlayer();

		this.showingImageViewer = true;
		this.showImageViewerAnimation.playFromStart();
		//this.hideOthers();
	}

	//
	public void hideImageViewer()
	{
		this.showingImageViewer = false;
		this.hideImageViewerAnimation.playFromStart();
	}

	//
	public void hideOtherss()
	{
		if(this.showingAudioPlayer)
			this.hideAudioPlayer();
		if(this.showingVideoPlayer)
			this.hideVideoPlayer();
		if(this.showingImageViewer)
			this.hideImageViewer();
	}

	//
	public void startAudioPlayer(URI ... uris)
	{
		if(this.audioPlayer != null)
		{
			this.audioPlayer.addToPlaylist(uris);
			this.audioPlayer.play();
		}
	}

	//
	public void startVideoPlayer(URL ... urls)
	{
		if(this.videoPlayer != null)
		{

		}
	}

	//
	public void startImageViewer(URL ... urls)
	{
		if(this.imageViewer != null)
		{

		}
	}

	/*
	 *
	 */

	public ObjectProperty<Node> contentProperty(){return this.contentProperty;}
	public Node getContent() {return this.contentProperty.get();}
	public void setContent(Node value) {this.getChildren().set(this.getChildren().indexOf(this.getContent()), value); /**/ this.contentProperty.set(value);}
	public AudioPlayer getAudioPlayer() {return this.audioPlayer;}
}
