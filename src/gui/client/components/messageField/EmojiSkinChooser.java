package gui.client.components.messageField;

import com.jfoenix.transitions.JFXFillTransition;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class EmojiSkinChooser extends HBox {
	private JFXFillTransition fillAnimation;
	private ObjectProperty<Duration> durationProperty;
	private ObjectProperty<Color> fromColorProperty;
	private ObjectProperty<Color> toColorProperty;
	private IntegerProperty currentColorIndexProperty;

	public static final Color[] SKIN_COLORS = { Color.web("ffd766"), Color.web("fae0c1"), Color.web("e3c29c"),
			Color.web("c6956c"), Color.web("a06940"), Color.web("5c473c") };

	public EmojiSkinChooser() {
		getStyleClass().add("smiley-skin-chooser");
		
		Circle circle = new Circle(12.5d);
		circle.setFill(SKIN_COLORS[0]);
		setShape(circle);
		setVisible(true);

		durationProperty = new SimpleObjectProperty<>(Duration.seconds(0.5));

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

		fillAnimation = new JFXFillTransition();
		fillAnimation.setRegion(this);
		fillAnimation.durationProperty().bindBidirectional(durationProperty);
		fillAnimation.fromValueProperty().bindBidirectional(fromColorProperty);
		fillAnimation.toValueProperty().bindBidirectional(toColorProperty);
		fillAnimation.playFromStart();

		setOnMouseClicked(a -> handleAction(a));
	}

	public void handleAction(MouseEvent mouse) {
//		if (mouse.getButton() == MouseButton.PRIMARY) {
//			fillAnimation.playFromStart();
//			currentColorIndexProperty.set((getCurrentColorIndex() + 1) >= skinColors.length ? 0 : currentColorIndexProperty.get() + 1);
//
//			for (int i = 0; i < EmojiCategory.values().length; i++)
//				smileyPane.initSmileys(EmojiCategory.getByInt(i), EmojiSkinColor.getByInt(getCurrentColorIndex()), true);
//		}
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

	private int getCurrentColorIndex() {
		return currentColorIndexProperty.get();
	}
}
