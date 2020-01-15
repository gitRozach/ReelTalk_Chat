package gui.animations;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 *	This class is a collection of animations, which are either easier to create or more complex and better to use than the default ones.
 * 	@author Roman Kindler
 * 	@version 1.0
 */

public class Animations
{
	public static class CResizeAnimation
	{
		private ObjectProperty<Region> nodeProperty;
		private ObjectProperty<Duration> durationProperty;
		private DoubleProperty fromWidthProperty;
		private DoubleProperty toWidthProperty;
		private DoubleProperty fromHeightProperty;
		private DoubleProperty toHeightProperty;
		private ObjectProperty<Interpolator> interpolatorProperty;
		private IntegerProperty cyclesProperty;

		private ParallelTransition resizeAnimation;
		//Width resize timelines
		private Timeline fromMinWidthPart, toMinWidthPart, fromMaxWidthPart, toMaxWidthPart, fromPrefWidthPart, toPrefWidthPart;
		//Height resize timelines
		private Timeline fromMinHeightPart, toMinHeightPart, fromMaxHeightPart, toMaxHeightPart, fromPrefHeightPart, toPrefHeightPart;

		public CResizeAnimation(Region node)
		{
			this.nodeProperty = new SimpleObjectProperty<Region>(node);
			this.nodeProperty.addListener((obs, oldV, newV) -> this.updateTimelines());

			this.durationProperty = new SimpleObjectProperty<>(Duration.seconds(0.3d));
			this.durationProperty.addListener((obs, oldV, newV) -> this.updateDuration());

			this.fromWidthProperty = new SimpleDoubleProperty(0d);
			this.fromWidthProperty.addListener((obs, oldV, newV) -> this.updateTimelines(this.fromWidthProperty));

			this.toWidthProperty = new SimpleDoubleProperty(Double.MAX_VALUE);
			this.toWidthProperty.addListener((obs, oldV, newV) -> this.updateTimelines(this.toWidthProperty));

			this.fromHeightProperty = new SimpleDoubleProperty(0d);
			this.fromHeightProperty.addListener((obs, oldV, newV) -> this.updateTimelines(this.fromHeightProperty));

			this.toHeightProperty = new SimpleDoubleProperty(Double.MAX_VALUE);
			this.toHeightProperty.addListener((obs, oldV, newV) -> this.updateTimelines(this.toHeightProperty));

			this.interpolatorProperty = new SimpleObjectProperty<>(Interpolator.LINEAR);
			this.cyclesProperty = new SimpleIntegerProperty(1);

			this.fromMinWidthPart = new Timeline();
			this.toMinWidthPart = new Timeline();
			this.fromMaxWidthPart = new Timeline();
			this.toMaxWidthPart = new Timeline();
			this.fromPrefWidthPart = new Timeline();
			this.toPrefWidthPart = new Timeline();
			this.fromMinHeightPart = new Timeline();
			this.toMinHeightPart = new Timeline();
			this.fromMaxHeightPart = new Timeline();
			this.toMaxHeightPart = new Timeline();
			this.fromPrefHeightPart = new Timeline();
			this.toPrefHeightPart = new Timeline();

			this.resizeAnimation = new ParallelTransition(fromMinWidthPart, toMinWidthPart, fromMaxWidthPart, toMaxWidthPart,
														  fromPrefWidthPart, toPrefWidthPart, fromMinHeightPart, toMinHeightPart,
														  fromMaxHeightPart, toMaxHeightPart, fromPrefHeightPart, toPrefHeightPart);
			this.resizeAnimation.interpolatorProperty().bind(this.interpolatorProperty);
			this.resizeAnimation.cycleCountProperty().bind(this.cyclesProperty);
		}

		private void updateDuration()
		{
			this.updateTimelines();
		}

		private void updateTimelines()
		{
			this.updateTimelines(this.fromWidthProperty);
			this.updateTimelines(this.toWidthProperty);
			this.updateTimelines(this.fromHeightProperty);
			this.updateTimelines(this.toHeightProperty);
		}

		private void updateTimelines(DoubleProperty property)
		{
			if(property == this.fromWidthProperty)
			{
				this.fromMinWidthPart.getKeyFrames().clear();
				this.fromMaxWidthPart.getKeyFrames().clear();
				this.fromPrefWidthPart.getKeyFrames().clear();

				KeyValue newMinWidth = new KeyValue(this.getNode().minWidthProperty(), this.getFromWidth());
				KeyValue newMaxWidth = new KeyValue(this.getNode().maxWidthProperty(), this.getFromWidth());
				KeyValue newPrefWidth = new KeyValue(this.getNode().prefWidthProperty(), this.getFromWidth());
				KeyFrame newFrame = new KeyFrame(this.getDuration(), newMinWidth, newMaxWidth, newPrefWidth);

				this.fromMinWidthPart.getKeyFrames().add(newFrame);
				this.fromMaxWidthPart.getKeyFrames().add(newFrame);
				this.fromPrefWidthPart.getKeyFrames().add(newFrame);
			}
			else if(property == this.toWidthProperty)
			{
				this.toMinWidthPart.getKeyFrames().clear();
				this.toMaxWidthPart.getKeyFrames().clear();
				this.toPrefWidthPart.getKeyFrames().clear();

				KeyValue newMinWidth = new KeyValue(this.getNode().minWidthProperty(), this.getToWidth());
				KeyValue newMaxWidth = new KeyValue(this.getNode().maxWidthProperty(), this.getToWidth());
				KeyValue newPrefWidth = new KeyValue(this.getNode().prefWidthProperty(), this.getToWidth());
				KeyFrame newFrame = new KeyFrame(this.getDuration(), newMinWidth, newMaxWidth, newPrefWidth);

				this.toMinWidthPart.getKeyFrames().add(newFrame);
				this.toMaxWidthPart.getKeyFrames().add(newFrame);
				this.toPrefWidthPart.getKeyFrames().add(newFrame);
			}
			else if(property == this.fromHeightProperty)
			{
				this.fromMinHeightPart.getKeyFrames().clear();
				this.fromMaxHeightPart.getKeyFrames().clear();
				this.fromPrefHeightPart.getKeyFrames().clear();

				KeyValue newMinHeight = new KeyValue(this.getNode().minHeightProperty(), this.getFromHeight());
				KeyValue newMaxHeight = new KeyValue(this.getNode().maxHeightProperty(), this.getFromHeight());
				KeyValue newPrefHeight = new KeyValue(this.getNode().prefHeightProperty(), this.getFromHeight());
				KeyFrame newFrame = new KeyFrame(this.getDuration(), newMinHeight, newMaxHeight, newPrefHeight);

				this.fromMinHeightPart.getKeyFrames().add(newFrame);
				this.fromMaxHeightPart.getKeyFrames().add(newFrame);
				this.fromPrefHeightPart.getKeyFrames().add(newFrame);
			}
			else if(property == this.toHeightProperty)
			{
				this.toMinHeightPart.getKeyFrames().clear();
				this.toMaxHeightPart.getKeyFrames().clear();
				this.toPrefHeightPart.getKeyFrames().clear();

				KeyValue newMinHeight = new KeyValue(this.getNode().minHeightProperty(), this.getToHeight());
				KeyValue newMaxHeight = new KeyValue(this.getNode().maxHeightProperty(), this.getToHeight());
				KeyValue newPrefHeight = new KeyValue(this.getNode().prefHeightProperty(), this.getToHeight());
				KeyFrame newFrame = new KeyFrame(this.getDuration(), newMinHeight, newMaxHeight, newPrefHeight);

				this.toMinHeightPart.getKeyFrames().add(newFrame);
				this.toMaxHeightPart.getKeyFrames().add(newFrame);
				this.toPrefHeightPart.getKeyFrames().add(newFrame);
			}
		}

		public Animation animation()
		{
			return this.resizeAnimation;
		}

		public boolean isRunning()
		{
			return this.resizeAnimation.getStatus() == Status.RUNNING;
		}

		/*
		 *
		 */

		public ReadOnlyObjectProperty<Region> nodeProperty() {return this.nodeProperty;}
		public Region getNode() {return this.nodeProperty.get();}
		//private void setNode(Region node) {this.nodeProperty.set(node);}

		public ReadOnlyObjectProperty<Duration> durationProperty() {return this.durationProperty;}
		public Duration getDuration() {return this.durationProperty.get();}
		public void setDuration(Duration value) {this.durationProperty.set(value);}

		public DoubleProperty fromWidthProperty() {return this.fromWidthProperty;}
		public double getFromWidth() {return this.fromWidthProperty.get();}
		public void setFromWidth(double value) {this.fromWidthProperty.set(value);}

		public DoubleProperty toWidthProperty() {return this.toWidthProperty;}
		public double getToWidth() {return this.toWidthProperty.get();}
		public void setToWidth(double value) {this.toWidthProperty.set(value);}

		public DoubleProperty fromHeightProperty() {return this.fromHeightProperty;}
		public double getFromHeight() {return this.fromHeightProperty.get();}
		public void setFromHeight(double value) {this.fromHeightProperty.set(value);}

		public DoubleProperty toHeightProperty() {return this.toHeightProperty;}
		public double getToHeight() {return this.toHeightProperty.get();}
		public void setToHeight(double value) {this.toHeightProperty.set(value);}

		public ObjectProperty<Interpolator> interpolatorProperty() {return this.interpolatorProperty;}
		public Interpolator getInterpolator() {return this.interpolatorProperty.get();}
		public void setInterpolator(Interpolator interp) {this.interpolatorProperty.set(interp);}

		public IntegerProperty cyclesProperty() {return this.cyclesProperty;}
		public int getCycles() {return this.cyclesProperty.get();}
		public void setCycles(int value) {this.cyclesProperty.set(value);}
	}

	/*
	 *
	 *
	 *
	 */

	public static class ScaleTranslateAnimation
	{
		private ObjectProperty<Node> nodeProperty;
		private ObjectProperty<Duration> scaleDurationProperty;
		private ObjectProperty<Duration> translateDurationProperty;
		private DoubleProperty scaleFromXProperty;
		private DoubleProperty scaleToXProperty;
		private DoubleProperty scaleFromYProperty;
		private DoubleProperty scaleToYProperty;
		private DoubleProperty translateFromXProperty;
		private DoubleProperty translateToXProperty;
		private DoubleProperty translateFromYProperty;
		private DoubleProperty translateToYProperty;
		private ObjectProperty<Interpolator> interpolatorProperty;
		private IntegerProperty cyclesProperty;
		
		private BooleanProperty showingProperty;

		private ScaleTransition scaleAnimation;
		private TranslateTransition translateAnimation;
		private ParallelTransition fadeAndTranslateAnimation;

		public ScaleTranslateAnimation(Node node)
		{
			this.nodeProperty = new SimpleObjectProperty<>(node);

			this.scaleDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));
			this.translateDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));

			this.scaleFromXProperty = new SimpleDoubleProperty(0d);
			this.scaleToXProperty = new SimpleDoubleProperty(1d);
			this.scaleFromYProperty = new SimpleDoubleProperty(0d);
			this.scaleToYProperty = new SimpleDoubleProperty(1d);
			this.translateFromXProperty = new SimpleDoubleProperty(0d);
			this.translateToXProperty = new SimpleDoubleProperty(0d);
			this.translateFromYProperty = new SimpleDoubleProperty(0d);
			this.translateToYProperty = new SimpleDoubleProperty(0d);

			this.interpolatorProperty = new SimpleObjectProperty<>(Interpolator.EASE_BOTH);
			this.cyclesProperty = new SimpleIntegerProperty(1);
			
			this.showingProperty = new SimpleBooleanProperty(false);

			this.scaleAnimation = new ScaleTransition();
			this.scaleAnimation.nodeProperty().bind(this.nodeProperty);
			this.scaleAnimation.durationProperty().bind(this.scaleDurationProperty);
			this.scaleAnimation.fromXProperty().bind(this.scaleFromXProperty);
			this.scaleAnimation.toXProperty().bind(this.scaleToXProperty);
			this.scaleAnimation.fromYProperty().bind(this.scaleFromYProperty);
			this.scaleAnimation.toYProperty().bind(this.scaleToYProperty);

			this.translateAnimation = new TranslateTransition();
			this.translateAnimation.nodeProperty().bind(this.nodeProperty);
			this.translateAnimation.durationProperty().bind(this.translateDurationProperty);
			this.translateAnimation.fromXProperty().bind(this.translateFromXProperty);
			this.translateAnimation.toXProperty().bind(this.translateToXProperty);
			this.translateAnimation.fromYProperty().bind(this.translateFromYProperty);
			this.translateAnimation.toYProperty().bind(this.translateToYProperty);

			this.fadeAndTranslateAnimation = new ParallelTransition(this.scaleAnimation, this.translateAnimation);
			this.fadeAndTranslateAnimation.interpolatorProperty().bind(this.interpolatorProperty);
			this.fadeAndTranslateAnimation.cycleCountProperty().bind(this.cyclesProperty);
			this.fadeAndTranslateAnimation.setOnFinished(a -> this.showingProperty.set(false));
		}
		
		//
		public Animation get()
		{
			return this.fadeAndTranslateAnimation;
		}

		/*
		 *
		 */

		public ObjectProperty<Node> nodeProperty() {return this.nodeProperty;}
		public Node getNode() {return this.nodeProperty.get();}
		public void setNode(Node value) {this.nodeProperty.set(value);}

		public ObjectProperty<Duration> scaleDurationProperty() {return this.scaleDurationProperty;}
		public Duration getScaleDuration() {return this.scaleDurationProperty.get();}
		public void setScaleDuration(Duration duration) {this.scaleDurationProperty.set(duration);}

		public ObjectProperty<Duration> translateDurationProperty() {return this.translateDurationProperty;}
		public Duration getTranslateDuration() {return this.translateDurationProperty.get();}
		public void setTranslateDuration(Duration duration) {this.translateDurationProperty.set(duration);}

		public DoubleProperty scaleFromXProperty() {return this.scaleFromXProperty;}
		public double getScaleFromX() {return this.scaleFromXProperty.get();}
		public void setScaleFromX(double value) {this.scaleFromXProperty.set(value);}
		public DoubleProperty scaleToXProperty() {return this.scaleToXProperty;}
		public double getScaleToX() {return this.scaleToXProperty.get();}
		public void setScaleToX(double value) {this.scaleToXProperty.set(value);}

		public DoubleProperty scaleFromYProperty() {return this.scaleFromYProperty;}
		public double getScaleFromY() {return this.scaleFromYProperty.get();}
		public void setScaleFromY(double value) {this.scaleFromYProperty.set(value);}
		public DoubleProperty scaleToYProperty() {return this.scaleToYProperty;}
		public double getScaleToY() {return this.scaleToYProperty.get();}
		public void setScaleToY(double value) {this.scaleToYProperty.set(value);}

		public DoubleProperty translateFromXProperty() {return this.translateFromXProperty;}
		public double getTranslateFromX() {return this.translateFromXProperty.get();}
		public void setTranslateFromX(double value) {this.translateFromXProperty.set(value);}

		public DoubleProperty translateToXProperty() {return this.translateToXProperty;}
		public double getTranslateToX() {return this.translateToXProperty.get();}
		public void setTranslateToX(double value) {this.translateToXProperty.set(value);}

		public DoubleProperty translateFromYProperty() {return this.translateFromYProperty;}
		public double getTranslateFromY() {return this.translateFromYProperty.get();}
		public void setTranslateFromY(double value) {this.translateFromYProperty.set(value);}

		public DoubleProperty translateToYProperty() {return this.translateToYProperty;}
		public double getTranslateToY() {return this.translateToYProperty.get();}
		public void setTranslateToY(double value) {this.translateToYProperty.set(value);}

		public ObjectProperty<Interpolator> interpolatorProperty() {return this.interpolatorProperty;}
		public Interpolator getInterpolator() {return this.interpolatorProperty.get();}
		public void setInterpolator(Interpolator interpolator) {this.interpolatorProperty.set(interpolator);}

		public IntegerProperty cyclesProperty() {return this.cyclesProperty;}
		public int getCycles() {return this.cyclesProperty.get();}
		public void setCycles(int cycles) {this.cyclesProperty.set(cycles);}
	}



	/*
	 *
	 *
	 *
	 */



	public static class FadeTranslateAnimation
	{
		private ObjectProperty<Node> nodeProperty;
		private ObjectProperty<Duration> fadeDurationProperty;
		private ObjectProperty<Duration> translateDurationProperty;
		private DoubleProperty fadeFromValueProperty;
		private DoubleProperty fadeToValueProperty;
		private DoubleProperty translateFromXProperty;
		private DoubleProperty translateToXProperty;
		private DoubleProperty translateFromYProperty;
		private DoubleProperty translateToYProperty;
		private ObjectProperty<Interpolator> interpolatorProperty;
		private IntegerProperty cyclesProperty;

		private FadeTransition fadeAnimation;
		private TranslateTransition translateAnimation;
		private ParallelTransition fadeAndTranslateAnimation;

		public FadeTranslateAnimation(Node node)
		{
			this.nodeProperty = new SimpleObjectProperty<>(node);

			this.fadeDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));
			this.translateDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));

			this.fadeFromValueProperty = new SimpleDoubleProperty(0d);
			this.fadeToValueProperty = new SimpleDoubleProperty(0d);
			this.translateFromXProperty = new SimpleDoubleProperty(0d);
			this.translateToXProperty = new SimpleDoubleProperty(0d);
			this.translateFromYProperty = new SimpleDoubleProperty(0d);
			this.translateToYProperty = new SimpleDoubleProperty(0d);

			this.interpolatorProperty = new SimpleObjectProperty<>(Interpolator.EASE_BOTH);
			this.cyclesProperty = new SimpleIntegerProperty(1);

			this.fadeAnimation = new FadeTransition();
			this.fadeAnimation.nodeProperty().bind(this.nodeProperty);
			this.fadeAnimation.durationProperty().bind(this.fadeDurationProperty);
			this.fadeAnimation.fromValueProperty().bind(this.fadeFromValueProperty);
			this.fadeAnimation.toValueProperty().bind(this.fadeToValueProperty);

			this.translateAnimation = new TranslateTransition();
			this.translateAnimation.nodeProperty().bind(this.nodeProperty);
			this.translateAnimation.durationProperty().bind(this.translateDurationProperty);
			this.translateAnimation.fromXProperty().bind(this.translateFromXProperty);
			this.translateAnimation.toXProperty().bind(this.translateToXProperty);
			this.translateAnimation.fromYProperty().bind(this.translateFromYProperty);
			this.translateAnimation.toYProperty().bind(this.translateToYProperty);

			this.fadeAndTranslateAnimation = new ParallelTransition(this.fadeAnimation, this.translateAnimation);
			this.fadeAndTranslateAnimation.interpolatorProperty().bind(this.interpolatorProperty);
			this.fadeAndTranslateAnimation.cycleCountProperty().bind(this.cyclesProperty);
		}

		//
		public Animation get()
		{
			return (Animation) this.fadeAndTranslateAnimation;
		}

		/*
		 *
		 */

		public ObjectProperty<Node> nodeProperty() {return this.nodeProperty;}
		public Node getNode() {return this.nodeProperty.get();}
		public void setNode(Node value) {this.nodeProperty.set(value);}
		public ObjectProperty<Duration> fadeDurationProperty() {return this.fadeDurationProperty;}
		public Duration getFadeDuration() {return this.fadeDurationProperty.get();}
		public void setFadeDuration(Duration duration) {this.fadeDurationProperty.set(duration);}
		public ObjectProperty<Duration> translateDurationProperty() {return this.translateDurationProperty;}
		public Duration getTranslateDuration() {return this.translateDurationProperty.get();}
		public void setTranslateDuration(Duration duration) {this.translateDurationProperty.set(duration);}
		public DoubleProperty fadeFromValueProperty() {return this.fadeFromValueProperty;}
		public double getFadeFromValue() {return this.fadeFromValueProperty.get();}
		public void setFadeFromValue(double value) {this.fadeFromValueProperty.set(value);}

		public DoubleProperty fadeToValueProperty() {return this.fadeToValueProperty;}
		public double getFadeToValue() {return this.fadeToValueProperty.get();}
		public void setFadeToValue(double value) {this.fadeToValueProperty.set(value);}

		public DoubleProperty translateFromXProperty() {return this.translateFromXProperty;}
		public double getTranslateFromX() {return this.translateFromXProperty.get();}
		public void setTranslateFromX(double value) {this.translateFromXProperty.set(value);}

		public DoubleProperty translateToXProperty() {return this.translateToXProperty;}
		public double getTranslateToX() {return this.translateToXProperty.get();}
		public void setTranslateToX(double value) {this.translateToXProperty.set(value);}

		public DoubleProperty translateFromYProperty() {return this.translateFromYProperty;}
		public double getTranslateFromY() {return this.translateFromYProperty.get();}
		public void setTranslateFromY(double value) {this.translateFromYProperty.set(value);}

		public DoubleProperty translateToYProperty() {return this.translateToYProperty;}
		public double getTranslateToY() {return this.translateToYProperty.get();}
		public void setTranslateToY(double value) {this.translateToYProperty.set(value);}

		public ObjectProperty<Interpolator> interpolatorProperty() {return this.interpolatorProperty;}
		public Interpolator getInterpolator() {return this.interpolatorProperty.get();}
		public void setInterpolator(Interpolator interpolator) {this.interpolatorProperty.set(interpolator);}

		public IntegerProperty cyclesProperty() {return this.cyclesProperty;}
		public int getCycles() {return this.cyclesProperty.get();}
		public void setCycles(int cycles) {this.cyclesProperty.set(cycles);}
	}



	/*
	 *
	 *
	 *
	 */



	public static Transition newResizeFadeAnimation(Region container, Duration durResize, Duration durFade, double fromWidth, double toWidth,
													double fromHeight, double toHeight, double fromOpacity, double toOpacity, Interpolator interpolator)
	{
		Transition resize = newResizeTransition(container, durResize, fromWidth, toWidth, fromHeight, toHeight, interpolator);
		FadeTransition fade = new FadeTransition(durFade, container);
		fade.setFromValue(fromOpacity);
		fade.setToValue(toOpacity);
		fade.setInterpolator(interpolator);

		ParallelTransition resizeAndFade = new ParallelTransition(resize, fade);
		resizeAndFade.setOnFinished(a ->
		{
			container.setMinWidth(toWidth);
			container.setPrefWidth(toWidth);
			container.setMaxWidth(toWidth);
			container.setMinHeight(toHeight);
			container.setMaxHeight(toHeight);
			container.setPrefHeight(toHeight);
		});

		return resizeAndFade;
	}

	public static Transition newResizeFadeAnimation(Region container, Duration durResize, Duration durFade, boolean horizontalResize,
			double fromSize, double toSize, double fromOpacity, double toOpacity, Interpolator interpolator)
	{
	Transition resize = newResizeAnimation(container, durResize, horizontalResize, fromSize, toSize, interpolator);
	FadeTransition fade = new FadeTransition(durFade, container);
	fade.setFromValue(fromOpacity);
	fade.setToValue(toOpacity);
	fade.setInterpolator(interpolator);

	ParallelTransition resizeAndFade = new ParallelTransition(resize, fade);
	resizeAndFade.setOnFinished(a ->
	{
	container.setMinWidth(toSize);
	container.setPrefWidth(toSize);
	container.setMaxWidth(toSize);
	});

return resizeAndFade;
}

	public static Transition newResizeAnimation(Region container, Duration duration, boolean horizontal, double fromValue, double toValue, Interpolator interp)
	{
		DoubleProperty propertyMin = horizontal ? container.minWidthProperty() : container.minHeightProperty();
		DoubleProperty propertyMax = horizontal ? container.maxWidthProperty() : container.maxHeightProperty();
		DoubleProperty propertyPref = horizontal ? container.prefWidthProperty() : container.prefHeightProperty();

		Timeline minSizePart = Animations.newTimelineAnimation(propertyMin, fromValue, toValue, duration, interp);
		Timeline maxSizePart = Animations.newTimelineAnimation(propertyMax, fromValue, toValue, duration, interp);
		Timeline prefSizePart = Animations.newTimelineAnimation(propertyPref, fromValue, toValue, duration, interp);

		return new ParallelTransition(minSizePart, maxSizePart, prefSizePart);
	}

	public static Transition newResizeTransition(Region container, Duration duration, double fromX, double toX, double fromY, double toY, Interpolator interp)
	{
		DoubleProperty propertyMinWidth = container.minWidthProperty();
		DoubleProperty propertyMaxWidth = container.maxWidthProperty();
		DoubleProperty propertyPrefWidth = container.prefWidthProperty();
		DoubleProperty propertyMinHeight = container.minHeightProperty();
		DoubleProperty propertyMaxHeight = container.maxHeightProperty();
		DoubleProperty propertyPrefHeight = container.prefHeightProperty();

		Timeline minWidthPart = Animations.newTimelineAnimation(propertyMinWidth, fromX, toX, duration, interp);
		Timeline maxWidthPart = Animations.newTimelineAnimation(propertyMaxWidth, fromX, toX, duration, interp);
		Timeline prefWidthPart = Animations.newTimelineAnimation(propertyPrefWidth, fromX, toX, duration, interp);
		Timeline minHeightPart = Animations.newTimelineAnimation(propertyMinHeight, fromY, toY, duration, interp);
		Timeline maxHeightPart = Animations.newTimelineAnimation(propertyMaxHeight, fromY, toY, duration, interp);
		Timeline prefHeightPart = Animations.newTimelineAnimation(propertyPrefHeight, fromY, toY, duration, interp);

		return new ParallelTransition(minWidthPart, maxWidthPart, prefWidthPart, minHeightPart, maxHeightPart, prefHeightPart);
	}
	
	public static CResizeAnimation newResizeAnimation(Region container, Duration duration, double fromX, double toX, double fromY, double toY, Interpolator interp)
	{
		CResizeAnimation ra = new CResizeAnimation(container);
		ra.setDuration(duration);
		ra.setFromWidth(fromX);
		ra.setToWidth(toX);
		ra.setFromHeight(fromY);
		ra.setToHeight(toY);
		ra.setInterpolator(interp);
		return ra;
	}

	//
	public static Animation newFadeTranslateAnimation(Node node, Duration fadeDuration, Duration translateDuration,
													  double fadeTo, double toX, double toY, Interpolator interpolator)
	{
		FadeTransition fade = new FadeTransition(fadeDuration, node);
		fade.setToValue(fadeTo);

		TranslateTransition translate = new TranslateTransition(translateDuration, node);
		translate.setToX(toX);
		translate.setToY(toY);

		ParallelTransition fadeAndTranslate = new ParallelTransition(fade, translate);
		fadeAndTranslate.setInterpolator(interpolator);
		return fadeAndTranslate;
	}

	public static ScaleTransition newScaleTransition(Region node, Duration duration, double fromX, double toX,
													 double fromY, double toY, Interpolator interp)
	{
		ScaleTransition scale = new ScaleTransition(duration, node);
		scale.setFromX(fromX);
		scale.setToX(toX);
		scale.setFromY(fromY);
		scale.setToY(toY);
		scale.setInterpolator(interp);
		return scale;
	}

	//
	public static ScaleTransition newScaleTransition(Region node, Duration duration, DoubleProperty fromXProperty, DoubleProperty toXProperty,
													 DoubleProperty fromYProperty, DoubleProperty toYProperty, Interpolator interp)
	{
		ScaleTransition scale = new ScaleTransition(duration, node);
		scale.fromXProperty().bind(fromXProperty);
		scale.toXProperty().bind(toXProperty);
		scale.fromYProperty().bind(fromYProperty);
		scale.toYProperty().bind(toYProperty);
		scale.setInterpolator(interp);
		return scale;
	}

	//
	public static TranslateTransition newTranslateTransition(Node node, Duration duration, Interpolator interpolator, int cycles, boolean autoReverse)
	{
		TranslateTransition transition = new TranslateTransition(duration, node);
		transition.setInterpolator(interpolator);
		transition.setCycleCount(cycles);
		transition.setAutoReverse(autoReverse);
		return transition;
	}

	//
	public static ParallelTransition newParallelTransition(Interpolator interpolator, int cycles, boolean autoReverse, Animation... animations)
	{
		ParallelTransition parallelTransition = new ParallelTransition(animations);
		parallelTransition.setInterpolator(interpolator);
		parallelTransition.setCycleCount(cycles);
		parallelTransition.setAutoReverse(autoReverse);
		return parallelTransition;
	}

	//
	public static Timeline newTimelineAnimation(DoubleProperty property, double fromValue, double toValue, Duration duration, Interpolator interpolator)
	{
		Timeline timeline = new Timeline();
		KeyValue keyValueFrom = new KeyValue(property, fromValue, interpolator);
		KeyValue keyValueTo = new KeyValue(property, toValue, interpolator);
		KeyFrame keyFrame = new KeyFrame(duration, keyValueFrom, keyValueTo);
		timeline.getKeyFrames().add(keyFrame);
		return timeline;
	}

	public static Animation newFadeAnimation(Region node, Duration fadeDur, double fromValue, double toValue, Interpolator interp)
	{
		FadeTransition result = new FadeTransition(fadeDur, node);
		result.setFromValue(fromValue);
		result.setToValue(toValue);
		result.setInterpolator(interp);
		return result;
	}
}
