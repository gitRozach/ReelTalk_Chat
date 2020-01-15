package gui.client.components.contextMenu;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class CustomContextMenu extends Popup {
	//
	private VBox content;
	//
	private Duration animationDuration;
	private DoubleProperty animationTranslateWidthProperty;
	private DoubleProperty animationTranslateHeightProperty;
	//
	private ParallelTransition scaleAndTranslateAnimation;
	private ScaleTransition scaleAnimation;
	private TranslateTransition translateAnimation;
	//
	private volatile BooleanProperty animatingProperty;

	//
	public CustomContextMenu() {
		this.setAutoFix(true);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, a -> hide());

		this.content = new VBox();
		this.content.setPadding(new Insets(3d));
		this.content.setEffect(new DropShadow(10d, 5d, 5d, Color.rgb(0, 0, 0, 0.5d)));
		this.content.setId("root");
		this.content.getStylesheets().add("/stylesheets/client/CustomContextMenu.css");
		this.getContent().add(this.content);

		this.animationDuration = Duration.seconds(0.15d);
		this.animationTranslateWidthProperty = new SimpleDoubleProperty(100d);
		this.animationTranslateHeightProperty = new SimpleDoubleProperty(0d);

		this.initAnimations();
		this.animatingProperty = new SimpleBooleanProperty(false);
	}

	//
	public CustomContextMenu(MenuItemButton... items) {
		this();
		content.getChildren().addAll(items);
	}

	//
	public void showAnimated(Window ownerWindow, double screenX, double screenY) {
		if (!isAnimating()) {
			animatingProperty.set(true);
			scaleAndTranslateAnimation.playFromStart();
			show(ownerWindow, screenX - getAnimationTranslateWidth(),
					screenY - getAnimationTranslateHeight());
			content.requestFocus();
		}
	}

	//
	public void showAnimated(Node anchorNode, double screenX, double screenY) {
		if (!isAnimating()) {
			animatingProperty.set(true);
			scaleAndTranslateAnimation.playFromStart();
			show(anchorNode, screenX - getAnimationTranslateWidth(),
					screenY - getAnimationTranslateHeight());
			content.requestFocus();
		}
	}

	//
	public void add(MenuItemButton item) {
		getContent().add(item);
	}

	//
	public void addAll(MenuItemButton... items) {
		getContent().addAll(items);
	}

	//
	public MenuItemButton remove(int index) {
		return (MenuItemButton) (getContent().remove(index));
	}

	//
	public boolean remove(MenuItemButton item) {
		return getContent().remove(item);
	}

	//
	public boolean removeAll(MenuItemButton... items) {
		return getContent().removeAll(items);
	}

	//
	public int indexOf(MenuItemButton item) {
		int index = 0;
		for (Node n : getContent()) {
			if (n instanceof MenuItemButton && ((MenuItemButton) n).equals(item))
				return index;
			index++;
		}
		return -1;
	}

	/*
	 *
	 */

	//
	private void initAnimations() {
		scaleAnimation = new ScaleTransition(animationDuration, content);
		scaleAnimation.setFromX(0.3d);
		scaleAnimation.setToX(1d);
		scaleAnimation.setFromY(0.3d);
		scaleAnimation.setToY(1d);

		translateAnimation = new TranslateTransition(animationDuration, content);
		translateAnimation.setFromX(0d);
		translateAnimation.setToX(getAnimationTranslateWidth());
		translateAnimation.toXProperty().bind(animationTranslateWidthProperty);
		translateAnimation.setFromY(0d);
		translateAnimation.setToY(getAnimationTranslateHeight());
		translateAnimation.toYProperty().bind(animationTranslateHeightProperty);

		scaleAndTranslateAnimation = new ParallelTransition(scaleAnimation, translateAnimation);
		scaleAndTranslateAnimation.setOnFinished(a -> animatingProperty.set(false));
	}

	/*
	 *
	 */

	public Duration getAnimationDuration() {
		return animationDuration;
	}

	public double getAnimationTranslateWidth() {
		return animationTranslateWidthProperty.get();
	}

	public void setAnimationTranslateWidth(double value) {
		animationTranslateWidthProperty.set(value);
	}

	public double getAnimationTranslateHeight() {
		return animationTranslateHeightProperty.get();
	}

	public void setAnimationTranslateHeight(double value) {
		animationTranslateHeightProperty.set(value);
	}

	public boolean isAnimating() {
		return animatingProperty.get();
	}
}
