package gui.client.components.layouts;
import com.jfoenix.controls.JFXSpinner;import gui.animations.Animations;import javafx.animation.Animation.Status;import javafx.animation.FadeTransition;import javafx.animation.Interpolator;import javafx.animation.ParallelTransition;import javafx.animation.ScaleTransition;import javafx.application.Platform;import javafx.beans.property.BooleanProperty;import javafx.beans.property.DoubleProperty;import javafx.beans.property.ObjectProperty;import javafx.beans.property.SimpleBooleanProperty;import javafx.beans.property.SimpleDoubleProperty;import javafx.beans.property.SimpleObjectProperty;import javafx.beans.value.ChangeListener;import javafx.beans.value.ObservableValue;import javafx.geometry.Insets;import javafx.geometry.Pos;import javafx.scene.Node;import javafx.scene.control.Label;import javafx.scene.layout.HBox;import javafx.scene.layout.StackPane;import javafx.scene.layout.VBox;import javafx.scene.text.Font;import javafx.util.Duration;

public class LoadableStackPane extends StackPane {
	private ObjectProperty<Node> contentProperty;

	private BooleanProperty loadingProperty;

	private DoubleProperty fadeInValueProperty;
	private DoubleProperty scaleInValueProperty;
	private ObjectProperty<Duration> loadingInDurationProperty;

	private DoubleProperty fadeOutValueProperty;
	private DoubleProperty scaleOutValueProperty;
	private ObjectProperty<Duration> loadingOutDurationProperty;
	
	private HBox loadingLayer;
	private VBox loadingControls;
	private JFXSpinner loadingSpinner;
	private Label loadingText;

	private ParallelTransition loadInAnimation;
	private ParallelTransition loadOutAnimation;

	public LoadableStackPane() {
		this(null, false);
	}

	public LoadableStackPane(Node content) {
		this(content, false);
	}
	
	public LoadableStackPane(boolean loading) {
		this(null, loading);
	}

	public LoadableStackPane(Node content, boolean loading) {
		super();		initialize(content, loading);
	}
	
	public void initialize(Node content, boolean loading) {
		initProperties();
		initLoadingControls();
		initLoadingLayer();
		initAnimations();		initListeners();
		setContent(content);
		//setLoading(loading);
		
		setPickOnBounds(true);
		getChildren().add(loadingLayer);
	}
	
	private void initProperties() {
		this.contentProperty = new SimpleObjectProperty<>(null);
		this.loadingProperty = new SimpleBooleanProperty(false);

		this.fadeInValueProperty = new SimpleDoubleProperty(0.25d);
		this.scaleInValueProperty = new SimpleDoubleProperty(3d);
		this.loadingInDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.5d));

		this.fadeOutValueProperty = new SimpleDoubleProperty(1d);
		this.scaleOutValueProperty = new SimpleDoubleProperty(1d);
		this.loadingOutDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.1d));
	}
	
	private void initLoadingControls() {
		this.loadingSpinner = new JFXSpinner();
		
		this.loadingText = new Label("Loading ...");
		this.loadingText.setFont(Font.font("Verdana", 22d));
		this.loadingControls = new VBox(this.loadingSpinner, this.loadingText);
		this.loadingControls.setAlignment(Pos.CENTER);
	}
	
	private void initLoadingLayer() {
		this.loadingLayer = new HBox();
		this.loadingLayer.setAlignment(Pos.CENTER);
		this.loadingLayer.setFillHeight(true);
		this.loadingLayer.getChildren().add(this.loadingControls);
	}
	
	private void initAnimations() {
		FadeTransition fadeIn = new FadeTransition();
		fadeIn.setInterpolator(Interpolator.EASE_OUT);
		fadeIn.nodeProperty().bind(this.contentProperty);
		fadeIn.toValueProperty().bind(this.fadeInValueProperty);
		fadeIn.durationProperty().bind(this.loadingInDurationProperty);

		ScaleTransition scaleIn = new ScaleTransition();
		scaleIn.setNode(this.loadingSpinner);
		scaleIn.setInterpolator(Interpolator.EASE_OUT);
		scaleIn.toXProperty().bind(this.scaleInValueProperty);
		scaleIn.toYProperty().bind(this.scaleInValueProperty);
		scaleIn.durationProperty().bind(this.loadingInDurationProperty);

		this.loadInAnimation = Animations.newParallelTransition(Interpolator.EASE_OUT, 1, false, fadeIn, scaleIn);

		FadeTransition fadeOut = new FadeTransition();
		fadeOut.setInterpolator(Interpolator.EASE_IN);
		fadeOut.nodeProperty().bind(this.contentProperty);
		fadeOut.toValueProperty().bind(this.fadeOutValueProperty);
		fadeOut.durationProperty().bind(this.loadingOutDurationProperty);

		ScaleTransition scaleOut = new ScaleTransition();
		scaleOut.setNode(this.loadingSpinner);
		scaleOut.setInterpolator(Interpolator.EASE_IN);
		scaleOut.toXProperty().bind(this.scaleOutValueProperty);
		scaleOut.toYProperty().bind(this.scaleOutValueProperty);
		scaleOut.durationProperty().bind(this.loadingOutDurationProperty);

		this.loadOutAnimation = Animations.newParallelTransition(Interpolator.EASE_IN, 1, false, fadeOut, scaleOut);
		this.loadOutAnimation.setOnFinished(a -> {
			this.loadingLayer.setVisible(false);
			this.loadingLayer.setPickOnBounds(false);
		});
	}		private void initListeners() {		this.loadingProperty.addListener(new LoadingListener());				this.loadingSpinner.scaleXProperty().addListener((obs, oldV, newV) -> {			this.loadingText.setPadding(new Insets(15d + ((newV.doubleValue() - 1d) * this.loadingSpinner.getWidth()), 0d, 0d, 0d));		});				contentProperty.addListener((obs, oldV, newV) -> { 		});	}

	/*
	 *
	 */

	public BooleanProperty loadingProperty() {
		return this.loadingProperty;
	}

	public boolean isLoading() {
		return this.loadingProperty.get();
	}

	public void setLoading(boolean value) {
		this.loadingProperty.set(value);
	}

	public DoubleProperty fadeInValueProperty() {
		return this.fadeInValueProperty;
	}

	public double getFadeInValue() {
		return this.fadeInValueProperty.get();
	}

	public void setFadeInValue(double value) {
		this.fadeInValueProperty.set(value);
	}

	public DoubleProperty fadeOutValueProperty() {
		return this.fadeOutValueProperty;
	}

	public double getFadeOutValue() {
		return this.fadeOutValueProperty.get();
	}

	public void setFadeOutValue(double value) {
		this.fadeOutValueProperty.set(value);
	}

	public DoubleProperty scaleInValueProperty() {
		return this.scaleInValueProperty;
	}

	public double getScaleInValue() {
		return this.scaleInValueProperty.get();
	}

	public void setScaleInValue(double value) {
		this.scaleInValueProperty.set(value);
	}

	public DoubleProperty scaleOutValueProperty() {
		return this.scaleOutValueProperty;
	}

	public double getScaleOutValue() {
		return this.scaleOutValueProperty.get();
	}

	public void setScaleOutValue(double value) {
		this.scaleOutValueProperty.set(value);
	}

	public ObjectProperty<Duration> loadingInDurationProperty() {
		return this.loadingInDurationProperty;
	}

	public Duration getLoadingInDuration() {
		return this.loadingInDurationProperty.get();
	}

	public void setLoadingInDuration(Duration value) {
		this.loadingInDurationProperty().set(value);
	}

	public ObjectProperty<Duration> loadingOutDurationProperty() {
		return this.loadingOutDurationProperty;
	}

	public Duration getLoadingOutDuration() {
		return this.loadingOutDurationProperty.get();
	}

	public void setLoadingOutDuration(Duration value) {
		this.loadingOutDurationProperty().set(value);
	}

	public ObjectProperty<Node> contentProperty() {
		return this.contentProperty;
	}

	public Node getContent() {
		return this.contentProperty.get();
	}

	public void setContent(Node value) {
		if(value == null)
			return;
		Platform.runLater(() -> {
			if (this.getContent() != null)
				this.getChildren().remove(this.getContent());
			this.getChildren().add(0, value);
			value.setPickOnBounds(true);
			this.contentProperty.set(value);
			this.loadingLayer.setVisible(this.isLoading());
		});
	}

	/*
	 *
	 */

	private class LoadingListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(/*loadInAnimation != null || loadOutAnimation == null ||*/ newValue == null || oldValue == null)
				return;
			if (newValue.booleanValue() && !oldValue.booleanValue())
				playLoadingInAnimation();
			else if (!newValue.booleanValue() && oldValue.booleanValue())
				playLoadingOutAnimation();
		}

		private void playLoadingInAnimation() {
			if (loadInAnimation == null || loadOutAnimation == null)
				return;

			if (loadInAnimation.getStatus() != Status.RUNNING) {
				if (loadOutAnimation.getStatus() == Status.RUNNING) {
					loadOutAnimation.stop();
					loadingSpinner.setScaleX(getScaleOutValue());
					loadingSpinner.setScaleY(getScaleOutValue());
					if (getContent() != null)
						getContent().setOpacity(getFadeOutValue());
				}
				loadingLayer.setVisible(true);
				loadingLayer.setPickOnBounds(true);
				loadInAnimation.playFromStart();
			}
		}

		private void playLoadingOutAnimation() {
			if (loadInAnimation == null || loadOutAnimation == null)
				return;

			if (loadInAnimation.getStatus() == Status.RUNNING) {
				loadInAnimation.stop();
				loadingSpinner.setScaleX(getScaleInValue());
				loadingSpinner.setScaleY(getScaleInValue());
				if (getContent() != null)
					getContent().setOpacity(getFadeInValue());
			}
			loadOutAnimation.playFromStart();
		}
	}
}
