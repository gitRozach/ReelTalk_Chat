package gui.animations;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Animations {
	public static Transition newResizeFadeAnimation(Region container, Duration durResize, Duration durFade, double fromWidth, double toWidth,
													double fromHeight, double toHeight, double fromOpacity, double toOpacity, Interpolator interpolator) {
		Transition resize = newResizeTransition(container, durResize, fromWidth, toWidth, fromHeight, toHeight, interpolator);
		FadeTransition fade = new FadeTransition(durFade, container);
		fade.setFromValue(fromOpacity);
		fade.setToValue(toOpacity);
		fade.setInterpolator(interpolator);

		ParallelTransition resizeAndFade = new ParallelTransition(resize, fade);
		resizeAndFade.setOnFinished(a -> {
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
													double fromSize, double toSize, double fromOpacity, double toOpacity, Interpolator interpolator) {
		Transition resize = newResizeAnimation(container, durResize, horizontalResize, fromSize, toSize, interpolator);
		FadeTransition fade = new FadeTransition(durFade, container);
		fade.setFromValue(fromOpacity);
		fade.setToValue(toOpacity);
		fade.setInterpolator(interpolator);
	
		ParallelTransition resizeAndFade = new ParallelTransition(resize, fade);
		resizeAndFade.setOnFinished(a -> {
			container.setMinWidth(toSize);
			container.setPrefWidth(toSize);
			container.setMaxWidth(toSize);
		});

		return resizeAndFade;
	}

	public static Transition newResizeAnimation(Region container, Duration duration, boolean horizontal, double fromValue, double toValue, Interpolator interp) {
		DoubleProperty propertyMin = horizontal ? container.minWidthProperty() : container.minHeightProperty();
		DoubleProperty propertyMax = horizontal ? container.maxWidthProperty() : container.maxHeightProperty();
		DoubleProperty propertyPref = horizontal ? container.prefWidthProperty() : container.prefHeightProperty();

		Timeline minSizePart = Animations.newTimelineAnimation(propertyMin, fromValue, toValue, duration, interp);
		Timeline maxSizePart = Animations.newTimelineAnimation(propertyMax, fromValue, toValue, duration, interp);
		Timeline prefSizePart = Animations.newTimelineAnimation(propertyPref, fromValue, toValue, duration, interp);

		return new ParallelTransition(minSizePart, maxSizePart, prefSizePart);
	}

	public static Transition newResizeTransition(Region container, Duration duration, double fromX, double toX, double fromY, double toY, Interpolator interp) {
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
	
	public static ResizeAnimation newResizeAnimation(Region container, Duration duration, double fromX, double toX, double fromY, double toY, Interpolator interp) {
		ResizeAnimation ra = new ResizeAnimation();
		ra.setNode(container);
		ra.setDuration(duration);
		ra.setFromWidth(fromX);
		ra.setToWidth(toX);
		ra.setFromHeight(fromY);
		ra.setToHeight(toY);
		ra.setInterpolator(interp);
		return ra;
	}

	public static Animation newFadeTranslateAnimation(Node node, Duration fadeDuration, Duration translateDuration,
													  double fadeTo, double toX, double toY, Interpolator interpolator) {
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
													 double fromY, double toY, Interpolator interp) {
		ScaleTransition scale = new ScaleTransition(duration, node);
		scale.setFromX(fromX);
		scale.setToX(toX);
		scale.setFromY(fromY);
		scale.setToY(toY);
		scale.setInterpolator(interp);
		return scale;
	}

	public static ScaleTransition newScaleTransition(Region node, Duration duration, DoubleProperty fromXProperty, DoubleProperty toXProperty,
													 DoubleProperty fromYProperty, DoubleProperty toYProperty, Interpolator interp) {
		ScaleTransition scale = new ScaleTransition(duration, node);
		scale.fromXProperty().bind(fromXProperty);
		scale.toXProperty().bind(toXProperty);
		scale.fromYProperty().bind(fromYProperty);
		scale.toYProperty().bind(toYProperty);
		scale.setInterpolator(interp);
		return scale;
	}

	public static TranslateTransition newTranslateTransition(Node node, Duration duration, Interpolator interpolator, int cycles, boolean autoReverse) {
		TranslateTransition transition = new TranslateTransition(duration, node);
		transition.setInterpolator(interpolator);
		transition.setCycleCount(cycles);
		transition.setAutoReverse(autoReverse);
		return transition;
	}

	public static ParallelTransition newParallelTransition(Interpolator interpolator, int cycles, boolean autoReverse, Animation... animations) {
		ParallelTransition parallelTransition = new ParallelTransition(animations);
		parallelTransition.setInterpolator(interpolator);
		parallelTransition.setCycleCount(cycles);
		parallelTransition.setAutoReverse(autoReverse);
		return parallelTransition;
	}

	public static Timeline newTimelineAnimation(DoubleProperty property, double fromValue, double toValue, Duration duration, Interpolator interpolator) {
		Timeline timeline = new Timeline();
		KeyValue keyValueFrom = new KeyValue(property, fromValue, interpolator);
		KeyValue keyValueTo = new KeyValue(property, toValue, interpolator);
		KeyFrame keyFrame = new KeyFrame(duration, keyValueFrom, keyValueTo);
		timeline.getKeyFrames().add(keyFrame);
		return timeline;
	}

	public static Animation newFadeAnimation(Region node, Duration fadeDur, double fromValue, double toValue, Interpolator interp) {
		FadeTransition result = new FadeTransition(fadeDur, node);
		result.setFromValue(fromValue);
		result.setToValue(toValue);
		result.setInterpolator(interp);
		return result;
	}
}
