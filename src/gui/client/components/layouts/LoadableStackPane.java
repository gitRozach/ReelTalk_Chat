package gui.client.components.layouts;
import com.jfoenix.controls.JFXSpinner;import gui.animations.Animations;import gui.client.components.InitializableNode;import javafx.animation.Animation.Status;import javafx.animation.FadeTransition;import javafx.animation.Interpolator;import javafx.animation.ParallelTransition;import javafx.animation.ScaleTransition;import javafx.application.Platform;import javafx.beans.property.BooleanProperty;import javafx.beans.property.DoubleProperty;import javafx.beans.property.ObjectProperty;import javafx.beans.property.SimpleBooleanProperty;import javafx.beans.property.SimpleDoubleProperty;import javafx.beans.property.SimpleObjectProperty;import javafx.beans.value.ChangeListener;import javafx.beans.value.ObservableValue;import javafx.concurrent.Service;import javafx.concurrent.Task;import javafx.geometry.Insets;import javafx.geometry.Pos;import javafx.scene.Node;import javafx.scene.control.Label;import javafx.scene.layout.HBox;import javafx.scene.layout.StackPane;import javafx.scene.layout.VBox;import javafx.scene.text.Font;import javafx.util.Duration;

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
	private FadeTransition fadeIn;	private ScaleTransition scaleIn;	private ParallelTransition loadInAnimation;		private FadeTransition fadeOut;	private ScaleTransition scaleOut;
	private ParallelTransition loadOutAnimation;

	public LoadableStackPane() {
		this(false);
	}
	public LoadableStackPane(boolean initialize) {		if(initialize)			initialize();	}
		@Override
	public void initialize() {
		initProperties();
		initLoadingControls();
		initLoadingLayer();
		initAnimations();		initListeners();
		initRoot();	}
	
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
		loadingLayer = new HBox();		loadingLayer.setOnMouseClicked(a -> System.out.println("Click"));
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
		loadOutAnimation.setOnFinished(a -> {			System.out.println("FINISHED");
			loadingLayer.setVisible(false);
			loadingLayer.setPickOnBounds(false);
		});
	}		private void initListeners() {		loadingSpinner.scaleXProperty().addListener((obs, oldV, newV) -> {			loadingText.setPadding(new Insets(15d + ((newV.doubleValue() - 1d) * loadingSpinner.getWidth()), 0d, 0d, 0d));		});				loadingProperty.addListener(new LoadingListener());	}		private void initRoot()  {		setPickOnBounds(true);		getChildren().add(loadingLayer);	}

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
	}		public boolean hasContent() {		return getContent() != null;	}

	public Node getContent() {
		return contentProperty.get();
	}

	public void setContent(Node value) {		if(value == null)			return;		
		if (hasContent())			getChildren().remove(0);		contentProperty.set(value);		getChildren().add(0, getContent());		getContent().setPickOnBounds(true);		setLoading(false);
	}		public void loadContent(Node value) {		loadContent(value, 0L);	}		public void loadContent(Node value, long loadMinMillis) {		if(value == null)			return;		Service<Void> loadingService = new Service<Void>() {						Task<Void> loadingTask = new Task<Void>() {								@Override				protected Void call() throws Exception {					if(loadMinMillis > 0L)						Thread.sleep(loadMinMillis);					Platform.runLater(() -> setContent(value));					System.out.println("Setting content...");					return null;				}								@Override				protected void running() {					super.running();					Platform.runLater(() -> {						loadingLayer.setVisible(true);						loadingLayer.setPickOnBounds(true);						setLoading(true);						System.out.println("Running...");					});				}								@Override 				protected void succeeded() {					super.succeeded();					Platform.runLater(() -> {						loadingLayer.setVisible(false);						loadingLayer.setPickOnBounds(false);						setLoading(false);						System.out.println("Succeeded.");					});				}			};						@Override			protected Task<Void> createTask() {				return loadingTask;			}					};		loadingService.start();	}

	/*
	 *
	 */

	private class LoadingListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {			System.out.println("Loading: " + newValue.booleanValue());
			if(newValue == null || oldValue == null)
				return;
			if (newValue.booleanValue() && !oldValue.booleanValue())
				playLoadingInAnimation();
			else if (!newValue.booleanValue() && oldValue.booleanValue())
				playLoadingOutAnimation();
		}

		private void playLoadingInAnimation() {			System.out.println("load in");
			if (loadInAnimation == null || loadOutAnimation == null) {
				System.out.println("loadIn: loadin or loadout = null");				return;			}

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

		private void playLoadingOutAnimation() {			System.out.println("load out");
			if (loadInAnimation == null || loadOutAnimation == null) {
				System.out.println("loadOut: loadin or loadout = null");				return;			}

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
