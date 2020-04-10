package gui.animations;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class ResizeAnimation {
	private ObjectProperty<Region> nodeProperty;
	private ObjectProperty<Duration> durationProperty;
	private DoubleProperty fromWidthProperty;
	private DoubleProperty toWidthProperty;
	private DoubleProperty fromHeightProperty;
	private DoubleProperty toHeightProperty;
	private ObjectProperty<Interpolator> interpolatorProperty;
	private IntegerProperty cyclesProperty;

	private Timeline fromMinWidthPart, toMinWidthPart, fromMaxWidthPart, toMaxWidthPart, fromPrefWidthPart, toPrefWidthPart;
	private Timeline fromMinHeightPart, toMinHeightPart, fromMaxHeightPart, toMaxHeightPart, fromPrefHeightPart, toPrefHeightPart;
	private ParallelTransition resizeAnimation;

	public ResizeAnimation() {
		nodeProperty = new SimpleObjectProperty<Region>();
		nodeProperty.addListener((obs, oldV, newV) -> updateTimelines());

		durationProperty = new SimpleObjectProperty<>(Duration.seconds(0.3d));
		durationProperty.addListener((obs, oldV, newV) -> updateDuration());

		fromWidthProperty = new SimpleDoubleProperty(0d);
		fromWidthProperty.addListener((obs, oldV, newV) -> updateTimelines(fromWidthProperty));

		toWidthProperty = new SimpleDoubleProperty(Double.MAX_VALUE);
		toWidthProperty.addListener((obs, oldV, newV) -> updateTimelines(toWidthProperty));

		fromHeightProperty = new SimpleDoubleProperty(0d);
		fromHeightProperty.addListener((obs, oldV, newV) -> updateTimelines(fromHeightProperty));

		toHeightProperty = new SimpleDoubleProperty(Double.MAX_VALUE);
		toHeightProperty.addListener((obs, oldV, newV) -> updateTimelines(toHeightProperty));

		interpolatorProperty = new SimpleObjectProperty<>(Interpolator.LINEAR);
		cyclesProperty = new SimpleIntegerProperty(1);

		fromMinWidthPart = new Timeline();
		toMinWidthPart = new Timeline();
		fromMaxWidthPart = new Timeline();
		toMaxWidthPart = new Timeline();
		fromPrefWidthPart = new Timeline();
		toPrefWidthPart = new Timeline();
		fromMinHeightPart = new Timeline();
		toMinHeightPart = new Timeline();
		fromMaxHeightPart = new Timeline();
		toMaxHeightPart = new Timeline();
		fromPrefHeightPart = new Timeline();
		toPrefHeightPart = new Timeline();

		resizeAnimation = new ParallelTransition(	fromMinWidthPart, toMinWidthPart, fromMaxWidthPart, toMaxWidthPart,
													fromPrefWidthPart, toPrefWidthPart, fromMinHeightPart, toMinHeightPart, 
													fromMaxHeightPart, toMaxHeightPart, fromPrefHeightPart, toPrefHeightPart);
		resizeAnimation.interpolatorProperty().bind(interpolatorProperty);
		resizeAnimation.cycleCountProperty().bind(cyclesProperty);
	}

	public void play() {
		resizeAnimation.playFromStart();
	}

	private void updateDuration() {
		updateTimelines();
	}

	private void updateTimelines() {
		updateTimelines(fromWidthProperty());
		updateTimelines(toWidthProperty());
		updateTimelines(fromHeightProperty());
		updateTimelines(toHeightProperty());
	}

	private void updateTimelines(DoubleProperty property) {
		if (property == fromWidthProperty()) {
			fromMinWidthPart.getKeyFrames().clear();
			fromMaxWidthPart.getKeyFrames().clear();
			fromPrefWidthPart.getKeyFrames().clear();

			KeyValue newMinWidth = new KeyValue(getNode().minWidthProperty(), getFromWidth());
			KeyValue newMaxWidth = new KeyValue(getNode().maxWidthProperty(), getFromWidth());
			KeyValue newPrefWidth = new KeyValue(getNode().prefWidthProperty(), getFromWidth());
			KeyFrame newFrame = new KeyFrame(getDuration(), newMinWidth, newMaxWidth, newPrefWidth);

			fromMinWidthPart.getKeyFrames().add(newFrame);
			fromMaxWidthPart.getKeyFrames().add(newFrame);
			fromPrefWidthPart.getKeyFrames().add(newFrame);
		} 
		else if (property == toWidthProperty()) {
			toMinWidthPart.getKeyFrames().clear();
			toMaxWidthPart.getKeyFrames().clear();
			toPrefWidthPart.getKeyFrames().clear();

			KeyValue newMinWidth = new KeyValue(getNode().minWidthProperty(), getToWidth());
			KeyValue newMaxWidth = new KeyValue(getNode().maxWidthProperty(), getToWidth());
			KeyValue newPrefWidth = new KeyValue(getNode().prefWidthProperty(), getToWidth());
			KeyFrame newFrame = new KeyFrame(getDuration(), newMinWidth, newMaxWidth, newPrefWidth);

			toMinWidthPart.getKeyFrames().add(newFrame);
			toMaxWidthPart.getKeyFrames().add(newFrame);
			toPrefWidthPart.getKeyFrames().add(newFrame);
		} 
		else if (property == fromHeightProperty()) {
			fromMinHeightPart.getKeyFrames().clear();
			fromMaxHeightPart.getKeyFrames().clear();
			fromPrefHeightPart.getKeyFrames().clear();

			KeyValue newMinHeight = new KeyValue(getNode().minHeightProperty(), getFromHeight());
			KeyValue newMaxHeight = new KeyValue(getNode().maxHeightProperty(), getFromHeight());
			KeyValue newPrefHeight = new KeyValue(getNode().prefHeightProperty(), getFromHeight());
			KeyFrame newFrame = new KeyFrame(getDuration(), newMinHeight, newMaxHeight, newPrefHeight);

			fromMinHeightPart.getKeyFrames().add(newFrame);
			fromMaxHeightPart.getKeyFrames().add(newFrame);
			fromPrefHeightPart.getKeyFrames().add(newFrame);
		} 
		else if (property == toHeightProperty()) {
			toMinHeightPart.getKeyFrames().clear();
			toMaxHeightPart.getKeyFrames().clear();
			toPrefHeightPart.getKeyFrames().clear();

			KeyValue newMinHeight = new KeyValue(getNode().minHeightProperty(), getToHeight());
			KeyValue newMaxHeight = new KeyValue(getNode().maxHeightProperty(), getToHeight());
			KeyValue newPrefHeight = new KeyValue(getNode().prefHeightProperty(), getToHeight());
			KeyFrame newFrame = new KeyFrame(getDuration(), newMinHeight, newMaxHeight, newPrefHeight);

			toMinHeightPart.getKeyFrames().add(newFrame);
			toMaxHeightPart.getKeyFrames().add(newFrame);
			toPrefHeightPart.getKeyFrames().add(newFrame);
		}
	}

	public Animation get() {
		return resizeAnimation;
	}

	public boolean isRunning() {
		return resizeAnimation.getStatus() == Status.RUNNING;
	}

	public ReadOnlyObjectProperty<Region> nodeProperty() {
		return nodeProperty;
	}

	public Region getNode() {
		return nodeProperty.get();
	}

	public void setNode(Region node) {
		nodeProperty.set(node);
	}

	public ReadOnlyObjectProperty<Duration> durationProperty() {
		return durationProperty;
	}

	public Duration getDuration() {
		return durationProperty.get();
	}

	public void setDuration(Duration value) {
		durationProperty.set(value);
	}

	public DoubleProperty fromWidthProperty() {
		return fromWidthProperty;
	}

	public double getFromWidth() {
		return fromWidthProperty.get();
	}

	public void setFromWidth(double value) {
		fromWidthProperty.set(value);
	}

	public DoubleProperty toWidthProperty() {
		return toWidthProperty;
	}

	public double getToWidth() {
		return toWidthProperty.get();
	}

	public void setToWidth(double value) {
		toWidthProperty.set(value);
	}

	public DoubleProperty fromHeightProperty() {
		return fromHeightProperty;
	}

	public double getFromHeight() {
		return fromHeightProperty.get();
	}

	public void setFromHeight(double value) {
		fromHeightProperty.set(value);
	}

	public DoubleProperty toHeightProperty() {
		return toHeightProperty;
	}

	public double getToHeight() {
		return toHeightProperty.get();
	}

	public void setToHeight(double value) {
		toHeightProperty.set(value);
	}

	public ObjectProperty<Interpolator> interpolatorProperty() {
		return interpolatorProperty;
	}

	public Interpolator getInterpolator() {
		return interpolatorProperty.get();
	}

	public void setInterpolator(Interpolator interp) {
		interpolatorProperty.set(interp);
	}

	public IntegerProperty cyclesProperty() {
		return cyclesProperty;
	}

	public int getCycles() {
		return cyclesProperty.get();
	}

	public void setCycles(int value) {
		cyclesProperty.set(value);
	}
}
