package gui.components.messageField;

import com.jfoenix.transitions.JFXFillTransition;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class EmojiSkinChooser extends HBox {
	private DoubleProperty circleRadiusProperty;
	private ObjectProperty<Duration> durationProperty;
	private ObjectProperty<Color> fromColorProperty;
	private ObjectProperty<Color> toColorProperty;
	private volatile IntegerProperty currentColorIndexProperty;
	
	private Circle circle;
	
	private JFXFillTransition fillAnimation;
	
	
	private EventHandler<MouseEvent> onSkinChooserClicked;

	public static final Color[] SKIN_COLORS = { Color.web("ffd766"), Color.web("fae0c1"), Color.web("e3c29c"),
												Color.web("c6956c"), Color.web("a06940"), Color.web("5c473c") };

	public EmojiSkinChooser() {
		initialize();
	}
	
	private void initialize() {
		initStylesheets();
		initProperties();
		initCircle();
		initAnimations();
		initEventHandlers();
		initRoot();
	}
	
	private void initStylesheets() {
		getStyleClass().add("smiley-skin-chooser");
	}
	
	private void initProperties() {
		circleRadiusProperty = new SimpleDoubleProperty(12.5d);
		
		durationProperty = new SimpleObjectProperty<>(Duration.seconds(0.85d));
		
		fromColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
		fromColorProperty.addListener((obs, oldV, newV) -> {
			fillAnimation = new JFXFillTransition(getDuration(), this, oldV, newV);
			fillAnimation.playFromStart();
		});

		toColorProperty = new SimpleObjectProperty<>(SKIN_COLORS[0]);
		toColorProperty.addListener((obs, oldV, newV) -> {
			fillAnimation = new JFXFillTransition(getDuration(), this, oldV, newV);
			fillAnimation.playFromStart();
		});

		currentColorIndexProperty = new SimpleIntegerProperty(0);
		currentColorIndexProperty.addListener((obs, oldV, newV) -> {
			int fromColorIndex = newV.intValue() - 1;
			int toColorIndex = newV.intValue();

			if (fromColorIndex < 0)
				fromColorIndex = SKIN_COLORS.length - 1;
			else if (fromColorIndex >= SKIN_COLORS.length)
				fromColorIndex = 0;

			if (toColorIndex < 0)
				toColorIndex = SKIN_COLORS.length - 1;
			else if (toColorIndex >= SKIN_COLORS.length)
				toColorIndex = 0;
			
			fromColorProperty.set(SKIN_COLORS[fromColorIndex]);
			toColorProperty.set(SKIN_COLORS[toColorIndex]);
		});

	}
	
	private void initCircle() {
		circle = new Circle();
		circle.radiusProperty().bind(circleRadiusProperty);
		circle.setFill(SKIN_COLORS[0]);
	}
	
	private void initAnimations() {
		fillAnimation = new JFXFillTransition();
		fillAnimation.setRegion(this);
		fillAnimation.durationProperty().bindBidirectional(durationProperty);
		fillAnimation.fromValueProperty().bindBidirectional(fromColorProperty);
		fillAnimation.toValueProperty().bindBidirectional(toColorProperty);
		fillAnimation.playFromStart();
	}
	
	private void initEventHandlers() {
		onSkinChooserClicked = a -> {};
	}
	
	private void initRoot() {
		setOnMouseClicked(a -> {
			handleAction(a);
			onSkinChooserClicked.handle(a);
		});
		setShape(circle);
	}

	public void handleAction(MouseEvent mouse) {
		if (mouse.getButton() == MouseButton.PRIMARY)
			fillAnimation.playFromStart();
	}
	
	public DoubleProperty circleRadiusProperty() {
		return circleRadiusProperty;
	}
	
	public double getCircleRadius() {
		return circleRadiusProperty.get();
	}
	
	public void setCircleRadius(double value) {
		circleRadiusProperty.set(value);
	}

	public ObjectProperty<Duration> durationProperty() {
		return durationProperty;
	}

	public Duration getDuration() {
		return durationProperty.get();
	}

	public void setDuration(Duration value) {
		durationProperty.set(value);
	}

	public ObjectProperty<Color> fromColorProperty() {
		return fromColorProperty;
	}

	public Color getFromColor() {
		return fromColorProperty.get();
	}

	public void setFromColor(Color value) {
		fromColorProperty.set(value);
	}

	public ObjectProperty<Color> toColorProperty() {
		return toColorProperty;
	}

	public Color getToColor() {
		return toColorProperty.get();
	}

	public void setToColor(Color value) {
		toColorProperty.set(value);
	}

	public int getCurrentColorIndex() {
		return currentColorIndexProperty.get();
	}
	
	public void setCurrentColorIndex(int value) {
		currentColorIndexProperty.set(value);
	}
	
	public int nextColorIndex() {
		int newIndex = getCurrentColorIndex() + 1;
		currentColorIndexProperty.set(newIndex >= SKIN_COLORS.length ? 0 : newIndex);
		return currentColorIndexProperty.get();
	}
	
	public EventHandler<MouseEvent> getOnSkinChooserClicked() {
		return onSkinChooserClicked;
	}
	
	public void setOnSkinChooserClicked(EventHandler<MouseEvent> handler) {
		addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
	}
}
