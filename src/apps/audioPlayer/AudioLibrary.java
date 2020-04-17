package apps.audioPlayer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class AudioLibrary extends TableView<AudioLibraryItem> {
	private ObservableList<AudioLibraryItem> items;
	
	private TableColumn<AudioLibraryItem, String> tableColumnTitle;
	private TableColumn<AudioLibraryItem, String> tableColumnArtist;
	private TableColumn<AudioLibraryItem, String> tableColumnAlbum;
	private TableColumn<AudioLibraryItem, String> tableColumnYear;
	
	public AudioLibrary() {
		super();
		initialize();
	}
	
	public void initialize() {
		initProperties();
		initTableColumnTitle();
		initTableColumnArtist();
		initTableColumnAlbum();
		initTableColumnYear();
		initRoot();
	}
	
	private void initProperties() {
		items = FXCollections.observableArrayList();
	}
	
	private void initTableColumnTitle() {
		tableColumnTitle = new TableColumn<>("Titel");
		tableColumnTitle.setSortable(false);
		tableColumnTitle.prefWidthProperty().bind(widthProperty().multiply(0.25d));
		tableColumnTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AudioLibraryItem, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<AudioLibraryItem, String> param) {
				return param.getValue().titleProperty();
			}
		});
	}
	
	private void initTableColumnArtist() {
		tableColumnArtist = new TableColumn<>("Interpret");
		tableColumnArtist.setSortable(false);
		tableColumnArtist.prefWidthProperty().bind(widthProperty().multiply(0.25d));
		tableColumnArtist.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AudioLibraryItem, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<AudioLibraryItem, String> param) {
				return param.getValue().artistProperty();
			}
		});
	}
	
	private void initTableColumnAlbum() {
		tableColumnAlbum = new TableColumn<>("Album");
		tableColumnAlbum.setSortable(false);
		tableColumnAlbum.prefWidthProperty().bind(widthProperty().multiply(0.25d));
		tableColumnAlbum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AudioLibraryItem, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<AudioLibraryItem, String> param) {
				return param.getValue().albumProperty();
			}
		});
	}
	
	private void initTableColumnYear() {
		tableColumnYear = new TableColumn<>("Jahr");
		tableColumnYear.setSortable(false);
		tableColumnYear.prefWidthProperty().bind(widthProperty().multiply(0.25d));
		tableColumnYear.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AudioLibraryItem, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<AudioLibraryItem, String> param) {
				return param.getValue().yearProperty();
			}
		});
	}
	
	private void initRoot() {
		getColumns().add(tableColumnTitle);
		getColumns().add(tableColumnArtist);
		getColumns().add(tableColumnAlbum);
		getColumns().add(tableColumnYear);
		setPlaceholder(new Label(""));
		setFixedCellSize(40d);
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		setColumnResizePolicy(AudioLibrary.CONSTRAINED_RESIZE_POLICY);
		setEditable(false);
		setItems(items);
	}

	public void addAudioData(AudioLibraryItem item) {
		items.add(item);
	}

	public void addAudioData(AudioLibraryItem... itemCollection) {
		items.addAll(itemCollection);
	}

	public void addAudioData(int index, AudioLibraryItem item) {
		items.add(index, item);
	}

	public void setAudioData(int index, AudioLibraryItem item) {
		items.set(index, item);
	}

	public boolean setAudioData(AudioLibraryItem oldItem, AudioLibraryItem newItem) {
		int index = items.indexOf(oldItem);
		if (index < 0)
			return false;
		items.set(index, newItem);
		return true;
	}

	public boolean removeAudioData(AudioLibraryItem item) {
		return items.remove(item);
	}

	public boolean removeAudioData(AudioLibraryItem... itemCollection) {
		return items.removeAll(itemCollection);
	}

	public boolean removeAudioData(int index) {
		return (items.remove(index) != null);
	}

	public void removeAudioData(int indexFrom, int indexTo) {
		items.remove(indexFrom, indexTo);
	}

	public int indexOf(AudioLibraryItem item) {
		return items.indexOf(item);
	}

	public ObservableList<AudioLibraryItem> getLibraryItems() {
		return items;
	}

	public void setLibraryItems(ObservableList<AudioLibraryItem> itemList) {
		items = itemList;
	}
}
