package gui.animations;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public class ScaleTranslateAnimation {
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

	public ScaleTranslateAnimation() {
		nodeProperty = new SimpleObjectProperty<>();

		scaleDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));
		translateDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0d));

		scaleFromXProperty = new SimpleDoubleProperty(0d);
		scaleToXProperty = new SimpleDoubleProperty(1d);
		scaleFromYProperty = new SimpleDoubleProperty(0d);
		scaleToYProperty = new SimpleDoubleProperty(1d);
		translateFromXProperty = new SimpleDoubleProperty(0d);
		translateToXProperty = new SimpleDoubleProperty(0d);
		translateFromYProperty = new SimpleDoubleProperty(0d);
		translateToYProperty = new SimpleDoubleProperty(0d);

		interpolatorProperty = new SimpleObjectProperty<>(Interpolator.EASE_BOTH);
		cyclesProperty = new SimpleIntegerProperty(1);

		showingProperty = new SimpleBooleanProperty(false);

		scaleAnimation = new ScaleTransition();
		scaleAnimation.nodeProperty().bind(nodeProperty);
		scaleAnimation.durationProperty().bind(scaleDurationProperty);
		scaleAnimation.fromXProperty().bind(scaleFromXProperty);
		scaleAnimation.toXProperty().bind(scaleToXProperty);
		scaleAnimation.fromYProperty().bind(scaleFromYProperty);
		scaleAnimation.toYProperty().bind(scaleToYProperty);

		translateAnimation = new TranslateTransition();
		translateAnimation.nodeProperty().bind(nodeProperty);
		translateAnimation.durationProperty().bind(translateDurationProperty);
		translateAnimation.fromXProperty().bind(translateFromXProperty);
		translateAnimation.toXProperty().bind(translateToXProperty);
		translateAnimation.fromYProperty().bind(translateFromYProperty);
		translateAnimation.toYProperty().bind(translateToYProperty);

		fadeAndTranslateAnimation = new ParallelTransition(scaleAnimation, translateAnimation);
		fadeAndTranslateAnimation.interpolatorProperty().bind(interpolatorProperty);
		fadeAndTranslateAnimation.cycleCountProperty().bind(cyclesProperty);
		fadeAndTranslateAnimation.setOnFinished(a -> setShowing(false));
	}

	public void play() {
		fadeAndTranslateAnimation.playFromStart();
	}
	
	public Animation get() {
		return fadeAndTranslateAnimation;
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

	public ObjectProperty<Duration> scaleDurationProperty() {
		return scaleDurationProperty;
	}

	public Duration getScaleDuration() {
		return scaleDurationProperty.get();
	}

	public void setScaleDuration(Duration duration) {
		scaleDurationProperty.set(duration);
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

	public DoubleProperty scaleFromXProperty() {
		return scaleFromXProperty;
	}

	public double getScaleFromX() {
		return scaleFromXProperty.get();
	}

	public void setScaleFromX(double value) {
		scaleFromXProperty.set(value);
	}

	public DoubleProperty scaleToXProperty() {
		return scaleToXProperty;
	}

	public double getScaleToX() {
		return scaleToXProperty.get();
	}

	public void setScaleToX(double value) {
		scaleToXProperty.set(value);
	}

	public DoubleProperty scaleFromYProperty() {
		return scaleFromYProperty;
	}

	public double getScaleFromY() {
		return scaleFromYProperty.get();
	}

	public void setScaleFromY(double value) {
		scaleFromYProperty.set(value);
	}

	public DoubleProperty scaleToYProperty() {
		return scaleToYProperty;
	}

	public double getScaleToY() {
		return scaleToYProperty.get();
	}

	public void setScaleToY(double value) {
		scaleToYProperty.set(value);
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
	
	public BooleanProperty showingProperty() {
		return showingProperty;
	}
	
	public boolean isShowing() {
		return showingProperty.get();
	}
	
	public void setShowing(boolean value) {
		showingProperty.set(value);
	}
}
