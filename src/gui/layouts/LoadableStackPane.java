package gui.layouts;
import com.jfoenix.controls.JFXSpinner;import gui.animations.ScaleFadeAnimation;import gui.components.InitializableNode;import javafx.animation.Interpolator;import javafx.application.Platform;import javafx.beans.property.BooleanProperty;import javafx.beans.property.ObjectProperty;import javafx.beans.property.SimpleBooleanProperty;import javafx.beans.property.SimpleObjectProperty;import javafx.beans.value.ChangeListener;import javafx.beans.value.ObservableValue;import javafx.concurrent.Service;import javafx.concurrent.Task;import javafx.geometry.Insets;import javafx.geometry.Pos;import javafx.scene.Node;import javafx.scene.control.Label;import javafx.scene.layout.HBox;import javafx.scene.layout.StackPane;import javafx.scene.layout.VBox;import javafx.scene.text.Font;import javafx.util.Duration;

public class LoadableStackPane extends StackPane implements InitializableNode {
	private ObjectProperty<Node> contentProperty;
	private BooleanProperty loadingProperty;
	
	private HBox loadingLayer;
	private VBox loadingControls;
	private JFXSpinner loadingSpinner;
	private Label loadingText;		private ScaleFadeAnimation loadStartAnimation;	private ScaleFadeAnimation loadEndAnimation;

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
	
	private void initProperties() {		setStyle("-fx-background-color: rgb(0, 0, 0, 0.8);");
		contentProperty = new SimpleObjectProperty<>();
		loadingProperty = new SimpleBooleanProperty(false);
	}
	
	private void initLoadingControls() {
		loadingSpinner = new JFXSpinner();		loadingText = new Label();
		loadingText.setFont(Font.font("Verdana", 22d));
		loadingControls = new VBox(loadingSpinner, loadingText);
		loadingControls.setAlignment(Pos.CENTER);
	}
	
	private void initLoadingLayer() {
		loadingLayer = new HBox();		loadingLayer.setStyle("-fx-background-color: #4d4d4d;");		loadingLayer.setAlignment(Pos.CENTER);
		loadingLayer.setFillHeight(true);
		loadingLayer.getChildren().add(loadingControls);		showLoadingLayer(false);
	}
	
	private void initAnimations() {		loadStartAnimation = new ScaleFadeAnimation();		loadStartAnimation.setInterpolator(Interpolator.EASE_OUT);		loadStartAnimation.setFadeDuration(Duration.seconds(0.6d));		loadStartAnimation.setScaleDuration(Duration.seconds(0.6d));		loadStartAnimation.setFadeContent(loadingLayer);		loadStartAnimation.setFadeFromValue(0d);		loadStartAnimation.setFadeToValue(1d);		loadStartAnimation.setScaleContent(loadingSpinner);		loadStartAnimation.setScaleFromValue(0d);		loadStartAnimation.setScaleToValue(1.5d);		loadStartAnimation.setOnStarted(a -> {			showLoadingLayer(true);			System.out.println("Animation started: " + loadingLayer.getOpacity() + " - Is visible: " + loadingLayer.isVisible());		});				loadEndAnimation = new ScaleFadeAnimation();		loadEndAnimation.setInterpolator(Interpolator.EASE_IN);		loadEndAnimation.setFadeDuration(Duration.seconds(0.5d));		loadEndAnimation.setScaleDuration(Duration.seconds(0.1d));		loadEndAnimation.setFadeContent(loadingLayer);		loadEndAnimation.setFadeFromValue(1d);		loadEndAnimation.setFadeToValue(0d);		loadEndAnimation.setScaleContent(loadingSpinner);		loadEndAnimation.setScaleFromValue(1.5d);		loadEndAnimation.setScaleToValue(0d);
		loadEndAnimation.setOnFinished(a -> {			showLoadingLayer(false);			System.out.println("Animation ended: " + loadingLayer.getOpacity() + " - Is visible: " + loadingLayer.isVisible());		});
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

	public ObjectProperty<Node> contentProperty() {
		return contentProperty;
	}		public boolean hasContent() {		return getContent() != null;	}

	public Node getContent() {
		return contentProperty.get();
	}

	public void setContent(Node value) {		Platform.runLater(() -> {
			if (hasContent())				getChildren().remove(0);			if(value == null)				return;			contentProperty.set(value);			getChildren().add(0, getContent());			getContent().setPickOnBounds(true);			setLoading(false);		});
	}		private void showLoadingLayer(boolean value) {		Platform.runLater(() -> {			loadingLayer.setVisible(value);			loadingLayer.setPickOnBounds(value);		});	}		public void loadContent(Node value) {		loadContent(value, 0L);	}		public void loadContent(Node value, long loadMinMillis) {		if(value == null)			return;		Service<Void> loadingService = new Service<Void>() {			Task<Void> loadingTask = new Task<Void>() {								@Override				protected Void call() throws Exception {					if(loadMinMillis > 0L)						Thread.sleep(loadMinMillis);					setContent(value);					return null;				}				@Override				protected void running() {					super.running();					setLoading(true);				}				@Override 				protected void succeeded() {					super.succeeded();					setLoading(false);				}			};			@Override			protected Task<Void> createTask() {				return loadingTask;			}		};		loadingService.start();	}

	/*
	 *
	 */

	private class LoadingListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (newValue.booleanValue())
				loadStartAnimation.play();
			else if (!newValue.booleanValue())
				loadEndAnimation.play();
		}
	}
}
