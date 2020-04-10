package apps.audioPlayer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AudioLibraryItem {
	private StringProperty titleProperty;
	private StringProperty artistProperty;
	private StringProperty albumProperty;
	private StringProperty yearProperty;

	public AudioLibraryItem(String title, String artist, String album, String year) {
		titleProperty = new SimpleStringProperty(title);
		artistProperty = new SimpleStringProperty(artist);
		albumProperty = new SimpleStringProperty(album);
		yearProperty = new SimpleStringProperty(year);
	}

	@Override
	public String toString() {
		return "Title: " + getTitle() + " Artist: " + getArtist() + " Album: " + getAlbum() + " Year: " + getYear();
	}

	public StringProperty titleProperty() {
		return titleProperty;
	}

	public String getTitle() {
		return titleProperty.get();
	}

	public void setTitle(String value) {
		titleProperty.set(value);
	}

	public StringProperty artistProperty() {
		return artistProperty;
	}

	public String getArtist() {
		return artistProperty.get();
	}

	public void setArtist(String value) {
		artistProperty.set(value);
	}

	public StringProperty albumProperty() {
		return albumProperty;
	}

	public String getAlbum() {
		return albumProperty.get();
	}

	public void setAlbum(String value) {
		albumProperty.set(value);
	}

	public StringProperty yearProperty() {
		return yearProperty;
	}

	public String getYear() {
		return yearProperty.get();
	}

	public void setYear(String value) {
		yearProperty.set(value);
	}
}
