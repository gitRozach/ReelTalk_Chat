package gui.layouts;
import com.jfoenix.controls.JFXSpinner;

public class LoadableStackPane extends StackPane implements InitializableNode {
	private ObjectProperty<Node> contentProperty;

	private BooleanProperty loadingProperty;

	private DoubleProperty fadeStartValueProperty;
	private DoubleProperty scaleStartValueProperty;
	private ObjectProperty<Duration> loadingStartDurationProperty;

	private DoubleProperty fadeEndValueProperty;
	private DoubleProperty scaleEndValueProperty;
	private ObjectProperty<Duration> loadingEndDurationProperty;
	
	private HBox loadingLayer;
	private VBox loadingControls;
	private JFXSpinner loadingSpinner;
	private Label loadingText;

	private ParallelTransition loadOutAnimation;

	public LoadableStackPane() {
		this(false);
	}

	
	public void initialize() {
		initProperties();
		initLoadingControls();
		initLoadingLayer();
		initAnimations();
		initRoot();
	
	private void initProperties() {
		contentProperty = new SimpleObjectProperty<>(new VBox());
		loadingProperty = new SimpleBooleanProperty(false);

		fadeStartValueProperty = new SimpleDoubleProperty(0.25d);
		scaleStartValueProperty = new SimpleDoubleProperty(3d);
		loadingStartDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.5d));

		fadeEndValueProperty = new SimpleDoubleProperty(1d);
		scaleEndValueProperty = new SimpleDoubleProperty(1d);
		loadingEndDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.1d));
	}
	
	private void initLoadingControls() {
		loadingSpinner = new JFXSpinner();
		
		loadingText = new Label("Loading ...");
		loadingText.setFont(Font.font("Verdana", 22d));
		loadingControls = new VBox(loadingSpinner, loadingText);
		loadingControls.setAlignment(Pos.CENTER);
	}
	
	private void initLoadingLayer() {
		loadingLayer = new HBox();
		loadingLayer.setAlignment(Pos.CENTER);
		loadingLayer.setFillHeight(true);
		loadingLayer.getChildren().add(loadingControls);
	}
	
	private void initAnimations() {
		fadeIn = new FadeTransition();
		fadeIn.setInterpolator(Interpolator.EASE_OUT);
		fadeIn.nodeProperty().bind(contentProperty);
		fadeIn.toValueProperty().bind(fadeStartValueProperty);
		fadeIn.durationProperty().bind(loadingStartDurationProperty);

		scaleIn = new ScaleTransition();
		scaleIn.setNode(loadingSpinner);
		scaleIn.setInterpolator(Interpolator.EASE_OUT);
		scaleIn.toXProperty().bind(scaleStartValueProperty);
		scaleIn.toYProperty().bind(scaleStartValueProperty);
		scaleIn.durationProperty().bind(loadingStartDurationProperty);

		loadInAnimation = Animations.newParallelTransition(Interpolator.EASE_OUT, 1, false, fadeIn, scaleIn);

		fadeOut = new FadeTransition();
		fadeOut.setInterpolator(Interpolator.EASE_IN);
		fadeOut.nodeProperty().bind(contentProperty);
		fadeOut.toValueProperty().bind(fadeEndValueProperty);
		fadeOut.durationProperty().bind(loadingEndDurationProperty);

		scaleOut = new ScaleTransition();
		scaleOut.setNode(loadingSpinner);
		scaleOut.setInterpolator(Interpolator.EASE_IN);
		scaleOut.toXProperty().bind(scaleEndValueProperty);
		scaleOut.toYProperty().bind(scaleEndValueProperty);
		scaleOut.durationProperty().bind(loadingEndDurationProperty);

		loadOutAnimation = Animations.newParallelTransition(Interpolator.EASE_IN, 1, false, fadeOut, scaleOut);
		loadOutAnimation.setOnFinished(a -> {
			loadingLayer.setVisible(false);
			loadingLayer.setPickOnBounds(false);
		});
	}

	/*
	 *
	 */

	public BooleanProperty loadingProperty() {
		return loadingProperty;
	}

	public boolean isLoading() {
		return loadingProperty.get();
	}

	public void setLoading(boolean value) {
		loadingProperty.set(value);
	}

	public DoubleProperty fadeInValueProperty() {
		return fadeStartValueProperty;
	}

	public double getFadeInValue() {
		return fadeStartValueProperty.get();
	}

	public void setFadeInValue(double value) {
		fadeStartValueProperty.set(value);
	}

	public DoubleProperty fadeOutValueProperty() {
		return fadeEndValueProperty;
	}

	public double getFadeOutValue() {
		return fadeEndValueProperty.get();
	}

	public void setFadeOutValue(double value) {
		fadeEndValueProperty.set(value);
	}

	public DoubleProperty scaleInValueProperty() {
		return scaleStartValueProperty;
	}

	public double getScaleInValue() {
		return scaleStartValueProperty.get();
	}

	public void setScaleInValue(double value) {
		scaleStartValueProperty.set(value);
	}

	public DoubleProperty scaleOutValueProperty() {
		return scaleEndValueProperty;
	}

	public double getScaleOutValue() {
		return scaleEndValueProperty.get();
	}

	public void setScaleOutValue(double value) {
		scaleEndValueProperty.set(value);
	}

	public ObjectProperty<Duration> loadingInDurationProperty() {
		return loadingStartDurationProperty;
	}

	public Duration getLoadingInDuration() {
		return loadingStartDurationProperty.get();
	}

	public void setLoadingInDuration(Duration value) {
		loadingInDurationProperty().set(value);
	}

	public ObjectProperty<Duration> loadingOutDurationProperty() {
		return loadingEndDurationProperty;
	}

	public Duration getLoadingOutDuration() {
		return loadingEndDurationProperty.get();
	}

	public void setLoadingOutDuration(Duration value) {
		loadingOutDurationProperty().set(value);
	}

	public ObjectProperty<Node> contentProperty() {
		return contentProperty;
	}

	public Node getContent() {
		return contentProperty.get();
	}

	public void setContent(Node value) {
		if (hasContent())
	}

	/*
	 *
	 */

	private class LoadingListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(newValue == null || oldValue == null)
				return;
			if (newValue.booleanValue() && !oldValue.booleanValue())
				playLoadingInAnimation();
			else if (!newValue.booleanValue() && oldValue.booleanValue())
				playLoadingOutAnimation();
		}

		private void playLoadingInAnimation() {
			if (loadInAnimation == null || loadOutAnimation == null) {
				System.out.println("loadIn: loadin or loadout = null");
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
			if (loadInAnimation == null || loadOutAnimation == null) {
				System.out.println("loadOut: loadin or loadout = null");
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