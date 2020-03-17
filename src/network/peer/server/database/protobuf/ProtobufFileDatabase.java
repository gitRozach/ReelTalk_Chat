package network.peer.server.database.protobuf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

public class ProtobufFileDatabase<T extends GeneratedMessageV3> implements Closeable {
	protected final Class<T> itemClass;
	
	protected volatile boolean initialized;
	protected volatile boolean closed;
	protected volatile boolean autoSave;
	
	protected final HashMap<String, List<T>> openFileDatabases;
	protected String currentDatabaseFilePath;
	
	protected final RandomAccessFile databaseFile;
	protected final FileChannel databaseChannel;
	protected final String databaseFilePath;
	protected final Charset encoding;

	public ProtobufFileDatabase(Class<T> protobufItemClass, String databaseFilePath) throws IOException {
		this(protobufItemClass, new File(databaseFilePath));
	}

	public ProtobufFileDatabase(Class<T> protobufItemClass, File file) throws IOException {
		itemClass = protobufItemClass;
		initialized = false;
		closed = false;
		autoSave = true;
		
		openFileDatabases = new HashMap<String, List<T>>();
		currentDatabaseFilePath = "";
		
		databaseFile = new RandomAccessFile(file, "rwd");
		databaseChannel = this.databaseFile.getChannel();
		databaseFilePath = file.getPath();
		encoding = Charset.forName("utf-8");
	}
	
	public int reloadFileItems(String databaseFilePath) {
		return -1;
	}

	public int loadFileItems(String databaseFilePath) {
		return loadFileItems(databaseFilePath, true);
	}
	
	public int loadFileItems(String databaseFilePath, boolean keepOpen) {
		if(!hasOpenDatabase(databaseFilePath)) {
			//Fuege neue Datenbank hinzu, falls keepOpen
		}
		else {
			//Lade Items aus neuer Datei und speichere die Liste in HashMap, falls, keepOpen
		}
		return -1;
	}
	
	public boolean hasOpenDatabase(String databaseFilePath) {
		return getOpenFileDatabases().get(databaseFilePath) != null;
	}
	
	public List<T> getCurrentDatabaseItems() {
		return getOpenDatabaseItems(currentDatabaseFilePath);
	}
	
	public List<T> getOpenDatabaseItems(String databaseFilePath) {
		if(databaseFilePath == null || databaseFilePath.isEmpty())
			return null;
		return getOpenFileDatabases().get(databaseFilePath);
		
	}
	
	public int writeItem(T item) {
		try {
			OutputStream fileOutput = Channels.newOutputStream(databaseChannel);
			Any itemToWrite = Any.pack(item);
			itemToWrite.writeDelimitedTo(fileOutput);
			return itemToWrite.toByteArray().length;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean writeItems() {
		try {
			List<T> items = getCurrentDatabaseItems();
			if(items == null)
				return false;
			for(T item : items) {
				if(writeItem(item) <= 0)
					return false;
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public T readItem() {
		try {
			Any readMessage = Any.parseDelimitedFrom(Channels.newInputStream(databaseChannel));
			if(readMessage == null)
				return null;
			return readMessage.unpack(itemClass);
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<T> readItems() {
		List<T> resultList = new ArrayList<T>();
		try {
			T currentItem = null;
			databaseFile.seek(0L);
			while((currentItem = readItem()) != null)
				resultList.add(currentItem);
			return resultList;
		}
		catch(IOException io) {
			io.printStackTrace();
			return null;
		}
	}
	
	public boolean removeItem(int index) {
		if(index < 0 || index >= getCurrentDatabaseItems().size())
			return false;
		boolean res = items.remove(index) != null;
		if(isAutoSave())
			return rewrite();
		return res;
	}
	
	public boolean removeItem(T item) {
		return item != null && removeItem(items.indexOf(item));
	}
	
	public boolean rewrite() {
		try {
			if(isClosed())
				return false;
			databaseChannel.truncate(0L);
			return writeItems();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addItem(T newItem) {
		if(items.add(newItem)) {
			if(isAutoSave())
				return rewrite();
			return true;
		}
		return false;
	}
	
	public T getItem(int index) {
		return (index < 0 || index >= items.size()) ? null : items.get(index);
	}
	
	public boolean replaceItem(T oldItem, T newItem) {
		return replaceItem(indexOf(oldItem), newItem);
	}
	
	public boolean replaceItem(int index, T newItem) {
		if(items.remove(index) != null) {
			items.add(index, newItem);
			if(isAutoSave())
				return rewrite();
			return true;
		}
		return false;
	}

	public int indexOf(T item) {
		return items.indexOf(item);
	}

	public int lastIndexOf(T item) {
		return items.lastIndexOf(item);
	}
	
	public boolean exists(T item) {
		return items.contains(item);
	}

	public synchronized void clear() {
		try {
			//TODO Backup generieren
			databaseChannel.truncate(0L);
			items.clear();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		try {
			if(isClosed())
				return;
			rewrite();
			databaseFile.close();
			items.clear();
			setClosed(true);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		return items.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}
	
	public Class<T> getItemClass() {
		return itemClass;
	}

	private void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	protected void setClosed(boolean value) {
		closed = value;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setAutoSave(boolean value) {
		autoSave = value;
	}
	
	public boolean isAutoSave() {
		return autoSave;
	}
	
	public String getCurrentDatabaseFilePath() {
		return currentDatabaseFilePath;
	}

	private void setCurrentDatabaseFilePath(String currentDatabaseFilePath) {
		this.currentDatabaseFilePath = currentDatabaseFilePath;
	}

	public HashMap<String, List<T>> getOpenFileDatabases() {
		return openFileDatabases;
	}
	
	public RandomAccessFile getDatabaseFile() {
		return databaseFile;
	}

	public FileChannel getDatabaseChannel() {
		return databaseChannel;
	}

	public String getDatabaseFilePath() {
		return databaseFilePath;
	}

	public Charset getEncoding() {
		return encoding;
	}
}
