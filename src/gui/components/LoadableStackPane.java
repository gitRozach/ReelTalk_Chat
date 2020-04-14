package gui.components;
import java.util.LinkedList;

public class LoadableStackPane extends StackPane implements InitializableNode {
	private ObjectProperty<Node> contentProperty;
	private BooleanProperty loadingProperty;
	private HBox loadingLayer;
	private VBox loadingControls;
	private JFXSpinner loadingSpinner;
	private Label loadingText;

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
		contentProperty = new SimpleObjectProperty<>();
		loadingProperty = new SimpleBooleanProperty(false);
	}
	
	private void initLoadingControls() {
		loadingSpinner = new JFXSpinner();
		loadingText.setFont(Font.font("Verdana", 22d));
		loadingControls = new VBox(loadingSpinner, loadingText);
		loadingControls.setAlignment(Pos.CENTER);
	}
	
	private void initLoadingLayer() {
		loadingLayer = new HBox();
		loadingLayer.setFillHeight(true);
		loadingLayer.getChildren().add(loadingControls);
	}
	
	private void initAnimations() {
		loadEndAnimation.setOnFinished(a -> showLoadingLayer(false));
	}
}