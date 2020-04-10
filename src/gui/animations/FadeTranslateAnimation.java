package gui.animations;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeTranslateAnimation {
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

	public FadeTranslateAnimation() {
		nodeProperty = new SimpleObjectProperty<>();

		fadeDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));
		translateDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));

		fadeFromValueProperty = new SimpleDoubleProperty(0d);
		fadeToValueProperty = new SimpleDoubleProperty(0d);
		translateFromXProperty = new SimpleDoubleProperty(0d);
		translateToXProperty = new SimpleDoubleProperty(0d);
		translateFromYProperty = new SimpleDoubleProperty(0d);
		translateToYProperty = new SimpleDoubleProperty(0d);

		interpolatorProperty = new SimpleObjectProperty<>(Interpolator.EASE_BOTH);
		cyclesProperty = new SimpleIntegerProperty(1);

		fadeAnimation = new FadeTransition();
		fadeAnimation.nodeProperty().bind(nodeProperty);
		fadeAnimation.durationProperty().bind(fadeDurationProperty);
		fadeAnimation.fromValueProperty().bind(fadeFromValueProperty);
		fadeAnimation.toValueProperty().bind(fadeToValueProperty);

		translateAnimation = new TranslateTransition();
		translateAnimation.nodeProperty().bind(nodeProperty);
		translateAnimation.durationProperty().bind(translateDurationProperty);
		translateAnimation.fromXProperty().bind(translateFromXProperty);
		translateAnimation.toXProperty().bind(translateToXProperty);
		translateAnimation.fromYProperty().bind(translateFromYProperty);
		translateAnimation.toYProperty().bind(translateToYProperty);

		fadeAndTranslateAnimation = new ParallelTransition(fadeAnimation, translateAnimation);
		fadeAndTranslateAnimation.interpolatorProperty().bind(interpolatorProperty);
		fadeAndTranslateAnimation.cycleCountProperty().bind(cyclesProperty);
	}
	
	public void play() {
		fadeAndTranslateAnimation.playFromStart();
	}
	
	public Animation get() {
		return (Animation) fadeAndTranslateAnimation;
	}

	public ObjectProperty<Node> nodeProperty() {
		return nodeProperty;
	}

	public Node getNode() {
		return nodeProperty.get();
	}

	public void setNode(Node value) {
		nodeProperty.set(value);
	}

	public ObjectProperty<Duration> fadeDurationProperty() {
		return fadeDurationProperty;
	}

	public Duration getFadeDuration() {
		return fadeDurationProperty.get();
	}

	public void setFadeDuration(Duration duration) {
		fadeDurationProperty.set(duration);
	}

	public ObjectProperty<Duration> translateDurationProperty() {
		return translateDurationProperty;
	}

	public Duration getTranslateDuration() {
		return translateDurationProperty.get();
	}

	public void setTranslateDuration(Duration duration) {
		translateDurationProperty.set(duration);
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

	public DoubleProperty translateFromXProperty() {
		return translateFromXProperty;
	}

	public double getTranslateFromX() {
		return translateFromXProperty.get();
	}

	public void setTranslateFromX(double value) {
		translateFromXProperty.set(value);
	}

	public DoubleProperty translateToXProperty() {
		return translateToXProperty;
	}

	public double getTranslateToX() {
		return translateToXProperty.get();
	}

	public void setTranslateToX(double value) {
		translateToXProperty.set(value);
	}

	public DoubleProperty translateFromYProperty() {
		return translateFromYProperty;
	}

	public double getTranslateFromY() {
		return translateFromYProperty.get();
	}

	public void setTranslateFromY(double value) {
		translateFromYProperty.set(value);
	}

	public DoubleProperty translateToYProperty() {
		return translateToYProperty;
	}

	public double getTranslateToY() {
		return translateToYProperty.get();
	}

	public void setTranslateToY(double value) {
		translateToYProperty.set(value);
	}

	public ObjectProperty<Interpolator> interpolatorProperty() {
		return interpolatorProperty;
	}

	public Interpolator getInterpolator() {
		return interpolatorProperty.get();
	}

	public void setInterpolator(Interpolator interpolator) {
		interpolatorProperty.set(interpolator);
	}

	public IntegerProperty cyclesProperty() {
		return cyclesProperty;
	}

	public int getCycles() {
		return cyclesProperty.get();
	}

	public void setCycles(int cycles) {
		cyclesProperty.set(cycles);
	}
}
