package gui.components.messages;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import utils.Utils;

public class ChatViewDateSeparator extends HBox {
	private LongProperty timestampMillisProperty;
	private Label dateDescriptionLabel;
	
	public ChatViewDateSeparator() {
		this(System.currentTimeMillis());
	}
	
	public ChatViewDateSeparator(long dateTimestampMillis) {
		timestampMillisProperty = new SimpleLongProperty(dateTimestampMillis);
		dateDescriptionLabel = new Label(convertMillisToDescriptionText(dateTimestampMillis));
	}
	
	public static String convertMillisToDescriptionText(long millis) {
		String resultText = "";
		long currentTimeMillis = System.currentTimeMillis();
		Duration durationGap = Utils.durationBetween(currentTimeMillis, millis);
		if(durationGap.greaterThanOrEqualTo(Duration.hours(24d*3d)))
			resultText = "Am " + "";
		else if(durationGap.greaterThanOrEqualTo(Duration.hours(24d*2d)))
			resultText = "Vor zwei Tagen";
		return resultText;
	}
	
	public LongProperty timestampMillisProperty() {
		return timestampMillisProperty;
	}
	
	public long getTimestampMillis() {
		return timestampMillisProperty.get();
	}
	
	public void setTimestampMillis(long value) {
		timestampMillisProperty.set(value);
	}
	
	public Label getDateDescriptionLabel() {
		return dateDescriptionLabel;
	}
	
	public void setDateDescriptionLabelText(String text) {
		dateDescriptionLabel.setText(text);
	}
	
	public String getDateDescriptionLabelText() {
		return dateDescriptionLabel.getText();
	}
}
