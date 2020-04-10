package gui.animations;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class ScaleFadeAnimation {
	private ObjectProperty<Node> scaleContentProperty;
	private ObjectProperty<Node> fadeContentProperty;
	
	private ObjectProperty<Interpolator> interpolatorProperty;
	private ObjectProperty<Duration> fadeDurationProperty;
	private ObjectProperty<Duration> scaleDurationProperty;
	
	private DoubleProperty fadeFromValueProperty;
	private DoubleProperty scaleFromValueProperty;
	private DoubleProperty fadeToValueProperty;
	private DoubleProperty scaleToValueProperty;
	
	private EventHandler<ActionEvent> onStartedHandler;
	private EventHandler<ActionEvent> onFinishedHandler;
	
	private FadeTransition fadeTransition;
	private ScaleTransition scaleTransition;
	private ParallelTransition parallelTransition;
	
	public ScaleFadeAnimation() {
		scaleContentProperty = new SimpleObjectProperty<Node>();
		fadeContentProperty = new SimpleObjectProperty<Node>();
		
		interpolatorProperty = new SimpleObjectProperty<Interpolator>(Interpolator.EASE_BOTH);
		fadeDurationProperty = new SimpleObjectProperty<>(Duration.seconds(1d));
		scaleDurationProperty = new SimpleObjectProperty<>(Duration.seconds(1d));
		
		fadeFromValueProperty = new SimpleDoubleProperty(0.25d);
		scaleFromValueProperty = new SimpleDoubleProperty(0.5d);
		fadeToValueProperty = new SimpleDoubleProperty(1d);
		scaleToValueProperty = new SimpleDoubleProperty(1d);
		
		onStartedHandler = startEvent -> {};
		onFinishedHandler = finishEvent -> {};
		
		fadeTransition = new FadeTransition();
		fadeTransition.interpolatorProperty().bind(interpolatorProperty);
		fadeTransition.fromValueProperty().bind(fadeFromValueProperty);
		fadeTransition.toValueProperty().bind(fadeToValueProperty);
		fadeTransition.durationProperty().bind(fadeDurationProperty);

		scaleTransition = new ScaleTransition();
		scaleTransition.interpolatorProperty().bind(interpolatorProperty);
		scaleTransition.fromXProperty().bind(scaleFromValueProperty);
		scaleTransition.fromYProperty().bind(scaleFromValueProperty);
		scaleTransition.toXProperty().bind(scaleToValueProperty);
		scaleTransition.toYProperty().bind(scaleToValueProperty);
		scaleTransition.durationProperty().bind(scaleDurationProperty);

		parallelTransition = Animations.newParallelTransition(getInterpolator(), 1, false, fadeTransition, scaleTransition);
	}
	
	public void play() {
		onStartedHandler.handle(new ActionEvent());
		parallelTransition.playFromStart();
		parallelTransition.setOnFinished(onFinishedHandler);
	}
	
	public void stop() {
		parallelTransition.stop();
	}
	
	public boolean isRunning() {
		return parallelTransition.getStatus() == Status.RUNNING;
	}
	
	public ObjectProperty<Node> scaleContentProperty() {
		return scaleContentProperty;
	}
	
	public boolean hasScaleContent() {
		return getScaleContent() != null;
	}

	public Node getScaleContent() {
		return scaleContentProperty.get();
	}

	public void setScaleContent(Node value) {
		Platform.runLater(() -> {
			if(value != null) {
				scaleContentProperty.set(value);
				scaleTransition.setNode(getScaleContent());
			}
		});
	}
	
	public ObjectProperty<Node> fadeContentProperty() {
		return fadeContentProperty;
	}
	
	public boolean hasFadeContent() {
		return getFadeContent() != null;
	}

	public Node getFadeContent() {
		return fadeContentProperty.get();
	}

	public void setFadeContent(Node value) {
		Platform.runLater(() -> {
			if(value != null) {
				fadeContentProperty.set(value);
				fadeTransition.setNode(getFadeContent());
			}
		});
	}
	
	public ObjectProperty<Interpolator> interpolatorProperty() {
		return interpolatorProperty;
	}
	
	public Interpolator getInterpolator() {
		return interpolatorProperty.get();
	}
	
	public void setInterpolator(Interpolator value) {
		interpolatorProperty.set(value);
	}
	
	public ObjectProperty<Duration> fadeDurationProperty() {
		return fadeDurationProperty;
	}

	public Duration getFadeDuration() {
		return fadeDurationProperty.get();
	}

	public void setFadeDuration(Duration value) {
		fadeDurationProperty.set(value);
	}
	
	public ObjectProperty<Duration> scaleDurationProperty() {
		return scaleDurationProperty;
	}

	public Duration getScaleDuration() {
		return scaleDurationProperty.get();
	}

	public void setScaleDuration(Duration value) {
		scaleDurationProperty.set(value);
	}
	
	public DoubleProperty fadeFromValueProperty() {
		return fadeFromValueProperty;
	}

	public double getFadeFromValue() {
		return fadeFromValueProperty.get();
	}

	public void setFadeFromValue(double value) {
		fadeFromValueProperty.set(value);
	}

	public DoubleProperty fadeToValueProperty() {
		return fadeToValueProperty;
	}

	public double getFadeToValue() {
		return fadeToValueProperty.get();
	}

	public void setFadeToValue(double value) {
		fadeToValueProperty.set(value);
	}

	public DoubleProperty scaleFromValueProperty() {
		return scaleFromValueProperty;
	}

	public double getScaleFromValue() {
		return scaleFromValueProperty.get();
	}

	public void setScaleFromValue(double value) {
		scaleFromValueProperty.set(value);
	}

	public DoubleProperty scaleToValueProperty() {
		return scaleToValueProperty;
	}

	public double getScaleToValue() {
		return scaleToValueProperty.get();
	}

	public void setScaleToValue(double value) {
		scaleToValueProperty.set(value);
	}
	
	public EventHandler<ActionEvent> getOnStarted() {
		return onStartedHandler;
	}
	
	public void setOnStarted(EventHandler<ActionEvent> onStarted) {
		onStartedHandler = onStarted;
	}
	
	public EventHandler<ActionEvent> getOnFinished() {
		return onFinishedHandler;
	}
	
	public void setOnFinished(EventHandler<ActionEvent> onFinished) {
		onFinishedHandler = onFinished;
	}
}
