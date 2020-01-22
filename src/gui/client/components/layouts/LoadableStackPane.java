package gui.client.components.layouts;
import com.jfoenix.controls.JFXSpinner;import gui.animations.Animations;import gui.client.components.InitializableNode;import javafx.animation.Animation.Status;import javafx.animation.FadeTransition;import javafx.animation.Interpolator;import javafx.animation.ParallelTransition;import javafx.animation.ScaleTransition;import javafx.application.Platform;import javafx.beans.property.BooleanProperty;import javafx.beans.property.DoubleProperty;import javafx.beans.property.ObjectProperty;import javafx.beans.property.SimpleBooleanProperty;import javafx.beans.property.SimpleDoubleProperty;import javafx.beans.property.SimpleObjectProperty;import javafx.beans.value.ChangeListener;import javafx.beans.value.ObservableValue;import javafx.concurrent.Service;import javafx.concurrent.Task;import javafx.geometry.Insets;import javafx.geometry.Pos;import javafx.scene.Node;import javafx.scene.control.Label;import javafx.scene.layout.HBox;import javafx.scene.layout.StackPane;import javafx.scene.layout.VBox;import javafx.scene.text.Font;import javafx.util.Duration;

public class LoadableStackPane extends StackPane implements InitializableNode {
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
	private FadeTransition fadeIn;	private ScaleTransition scaleIn;	private ParallelTransition loadInAnimation;		private FadeTransition fadeOut;	private ScaleTransition scaleOut;
	private ParallelTransition loadOutAnimation;

	public LoadableStackPane() {
		this(null);
	}

	public LoadableStackPane(Node content) {
		super();		initialize();		if(content != null)			setContent(content);		else			setContent(new VBox());
	}
	
	public void initialize() {
		initProperties();
		initLoadingControls();
		initLoadingLayer();
		initAnimations();		initListeners();
		setPickOnBounds(true);
		getChildren().add(loadingLayer);				setPickOnBounds(true);		setLoading(false);
	}
	
	private void initProperties() {
		contentProperty = new SimpleObjectProperty<>(new VBox());
		loadingProperty = new SimpleBooleanProperty(false);

		fadeInValueProperty = new SimpleDoubleProperty(0.25d);
		scaleInValueProperty = new SimpleDoubleProperty(3d);
		loadingInDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.5d));

		fadeOutValueProperty = new SimpleDoubleProperty(1d);
		scaleOutValueProperty = new SimpleDoubleProperty(1d);
		loadingOutDurationProperty = new SimpleObjectProperty<>(Duration.seconds(0.1d));
	}
	
	private void initLoadingControls() {
		loadingSpinner = new JFXSpinner();
		
		loadingText = new Label("Loading ...");
		loadingText.setFont(Font.font("Verdana", 22d));
		loadingControls = new VBox(loadingSpinner, loadingText);
		loadingControls.setAlignment(Pos.CENTER);
	}
	
	private void initLoadingLayer() {
		loadingLayer = new HBox();		loadingLayer.setOnMouseClicked(a -> System.out.println("Click"));//		loadingLayer.setVisible(false);//		loadingLayer.setPickOnBounds(false);
		loadingLayer.setAlignment(Pos.CENTER);
		loadingLayer.setFillHeight(true);
		loadingLayer.getChildren().add(loadingControls);
	}
	
	private void initAnimations() {
		fadeIn = new FadeTransition();
		fadeIn.setInterpolator(Interpolator.EASE_OUT);
		fadeIn.nodeProperty().bind(contentProperty);
		fadeIn.toValueProperty().bind(fadeInValueProperty);
		fadeIn.durationProperty().bind(loadingInDurationProperty);

		scaleIn = new ScaleTransition();
		scaleIn.setNode(loadingSpinner);
		scaleIn.setInterpolator(Interpolator.EASE_OUT);
		scaleIn.toXProperty().bind(scaleInValueProperty);
		scaleIn.toYProperty().bind(scaleInValueProperty);
		scaleIn.durationProperty().bind(loadingInDurationProperty);

		loadInAnimation = Animations.newParallelTransition(Interpolator.EASE_OUT, 1, false, /*fadeIn,*/ scaleIn);

		fadeOut = new FadeTransition();
		fadeOut.setInterpolator(Interpolator.EASE_IN);
		fadeOut.nodeProperty().bind(contentProperty);
		fadeOut.toValueProperty().bind(fadeOutValueProperty);
		fadeOut.durationProperty().bind(loadingOutDurationProperty);

		scaleOut = new ScaleTransition();
		scaleOut.setNode(loadingSpinner);
		scaleOut.setInterpolator(Interpolator.EASE_IN);
		scaleOut.toXProperty().bind(scaleOutValueProperty);
		scaleOut.toYProperty().bind(scaleOutValueProperty);
		scaleOut.durationProperty().bind(loadingOutDurationProperty);

		loadOutAnimation = Animations.newParallelTransition(Interpolator.EASE_IN, 1, false, /*fadeOut,*/ scaleOut);
		loadOutAnimation.setOnFinished(a -> {			System.out.println("FINISHED");
			loadingLayer.setVisible(false);
			loadingLayer.setPickOnBounds(false);
		});
	}		private void initListeners() {		loadingSpinner.scaleXProperty().addListener((obs, oldV, newV) -> {			loadingText.setPadding(new Insets(15d + ((newV.doubleValue() - 1d) * loadingSpinner.getWidth()), 0d, 0d, 0d));		});				loadingProperty.addListener(new LoadingListener());		//		contentProperty.addListener((obs, oldV, newV) -> { ////		});	}

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
		return fadeInValueProperty;
	}

	public double getFadeInValue() {
		return fadeInValueProperty.get();
	}

	public void setFadeInValue(double value) {
		fadeInValueProperty.set(value);
	}

	public DoubleProperty fadeOutValueProperty() {
		return fadeOutValueProperty;
	}

	public double getFadeOutValue() {
		return fadeOutValueProperty.get();
	}

	public void setFadeOutValue(double value) {
		fadeOutValueProperty.set(value);
	}

	public DoubleProperty scaleInValueProperty() {
		return scaleInValueProperty;
	}

	public double getScaleInValue() {
		return scaleInValueProperty.get();
	}

	public void setScaleInValue(double value) {
		scaleInValueProperty.set(value);
	}

	public DoubleProperty scaleOutValueProperty() {
		return scaleOutValueProperty;
	}

	public double getScaleOutValue() {
		return scaleOutValueProperty.get();
	}

	public void setScaleOutValue(double value) {
		scaleOutValueProperty.set(value);
	}

	public ObjectProperty<Duration> loadingInDurationProperty() {
		return loadingInDurationProperty;
	}

	public Duration getLoadingInDuration() {
		return loadingInDurationProperty.get();
	}

	public void setLoadingInDuration(Duration value) {
		loadingInDurationProperty().set(value);
	}

	public ObjectProperty<Duration> loadingOutDurationProperty() {
		return loadingOutDurationProperty;
	}

	public Duration getLoadingOutDuration() {
		return loadingOutDurationProperty.get();
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
//					if (getContent() != null)
//						getContent().setOpacity(getFadeOutValue());
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
//				if (getContent() != null)
//					getContent().setOpacity(getFadeInValue());
			}
			loadOutAnimation.playFromStart();
		}
	}
}
