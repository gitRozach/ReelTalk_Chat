package utils;

import com.jfoenix.controls.JFXTabPane;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class JFXUtils {

	public static void setFixedTabWidth(JFXTabPane tabPane, double width) {
		tabPane.setTabMinWidth(width);
		tabPane.setTabMaxWidth(width);
	}
	
	public static void setFixedTabHeight(JFXTabPane tabPane, double height) {
		tabPane.setTabMinHeight(height);
		tabPane.setTabMaxHeight(height);
	}
	
	public static void setFixedTabSize(JFXTabPane tabPane, double width, double height) {
		setFixedTabWidth(tabPane, width);
		setFixedTabHeight(tabPane, height);
	}
	
	public static void hideTabs(JFXTabPane tabPane) {
		setFixedTabSize(tabPane, 0d, 0d);
	}
	
	public static Node createHorizontalSpacer(double width) {
		HBox spacer = new HBox();
		JFXUtils.setFixedWidthOf(spacer, width);
		return spacer;
	}
	
	public static void setMinWidthOf(Region node, double minWidth) {
		setMinMaxPrefWidth(node, minWidth, -1, -1);
	}
	
	public static void setMaxWidthOf(Region node, double maxWidth) {
		setMinMaxPrefWidth(node, -1, maxWidth, -1);
	}
	
	public static void setPrefWidthOf(Region node, double prefWidth) {
		setMinMaxPrefWidth(node, -1, -1, prefWidth);
	}
	
	public static void setMinHeightOf(Region node, double minHeight) {
		setMinMaxPrefHeight(node, minHeight, -1, -1);
	}
	
	public static void setMaxHeightOf(Region node, double maxHeight) {
		setMinMaxPrefWidth(node, -1, maxHeight, -1);
	}
	
	public static void setPrefHeightOf(Region node, double prefHeight) {
		setMinMaxPrefHeight(node, -1, -1, prefHeight);
	}
	
	public static void setFixedWidthOf(Region node, double width) {
		setMinMaxPrefWidth(node, width, width, width);
	}
	
	public static void setFixedHeightOf(Region node, double height) {
		setMinMaxPrefHeight(node, height, height, height);
	}
	
	public static void setFixedSizeOf(Region node, double width, double height) {
		setMinMaxPrefWidth(node, width, width, width);
		setMinMaxPrefHeight(node, height, height, height);
	}
	
	public static void setPrefSizeOf(Region node, double prefWidth, double prefHeight) {
		setPrefWidthOf(node, prefWidth);
		setPrefHeightOf(node, prefHeight);
	}
	
	public static void setPrefMinWidthOf(Region node, double prefMinWidth) {
		setMinMaxPrefWidth(node, prefMinWidth, -1, prefMinWidth);
	}
	
	public static void setPrefMaxWidthOf(Region node, double prefMaxWidth) {
		setMinMaxPrefWidth(node, -1, prefMaxWidth, prefMaxWidth);
	}
	
	public static void setPrefMinHeightOf(Region node, double prefMinHeight) {
		setMinMaxPrefHeight(node, prefMinHeight, -1, prefMinHeight);
	}
	
	public static void setPrefMaxHeightOf(Region node, double prefMaxHeight) {
		setMinMaxPrefHeight(node, -1, prefMaxHeight, prefMaxHeight);
	}
	
	public static void setPrefMinSizeOf(Region node, double minWidth, double minHeight) {
		setMinMaxPrefWidth(node, minWidth, -1, minWidth);
		setMinMaxPrefHeight(node, minHeight, -1, minHeight);
	}
	
	public static void setPrefMaxSizeOf(Region node, double maxWidth, double maxHeight) {
		setMinMaxPrefWidth(node, -1, maxWidth, maxWidth);
		setMinMaxPrefHeight(node, -1, maxHeight, maxHeight);
	}
	
	private static void setMinMaxPrefWidth(Region node, double minWidth, double maxWidth, double prefWidth) {
		if(node == null)
			return;
		if(minWidth >= 0)
			node.setMinWidth(minWidth);
		if(maxWidth >= 0)
			node.setMaxWidth(maxWidth);
		if(prefWidth >= 0)
			node.setPrefWidth(prefWidth);
	}
	
	private static void setMinMaxPrefHeight(Region node, double minHeight, double maxHeight, double prefHeight) {
		if(node == null)
			return;
		if(minHeight >= 0)
			node.setMinHeight(minHeight);
		if(maxHeight >= 0)
			node.setMaxHeight(maxHeight);
		if(prefHeight >= 0)
			node.setPrefHeight(prefHeight);
	}
}
