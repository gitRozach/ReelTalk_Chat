package network.peer.server.database.protobuf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

public class ProtobufFileDatabase<T extends GeneratedMessageV3> implements Closeable {
	protected final Class<T> itemClass;
	
	protected volatile boolean initialized;
	protected volatile boolean closed;
	protected volatile boolean autoSave;
	
	protected final List<T> loadedItems;
	protected final HashMap<String, List<T>> bufferedDatabases;
	
	protected RandomAccessFile databaseFile;
	protected FileChannel databaseChannel;
	protected String databaseFilePath;
	protected String currentDatabaseFilePath;
	
	public ProtobufFileDatabase(Class<T> protobufItemClass) throws IOException {
		itemClass = protobufItemClass;
		initialized = false;
		closed = false;
		autoSave = true;
		
		loadedItems = Collections.synchronizedList(new LinkedList<T>());
		bufferedDatabases = new HashMap<String, List<T>>();
	}

	public int loadFileItems(String databaseFilePath) {
		return loadFileItems(databaseFilePath, true);
	}
	
	public int loadFileItems(String filePath, boolean bufferDatabase) {
		if(!changeDatabaseFile(filePath))
			return -1;
		List<T> loadedItems = hasBufferedDatabase(filePath) ? getBufferedFileDatabases().get(databaseFilePath) : readItems();
		if(bufferDatabase && !hasBufferedDatabase(filePath))
			getBufferedFileDatabases().put(filePath, loadedItems);
		return fillItems(loadedItems, false);
	}
	
	public boolean fileExists(String filePath) {
		if(filePath == null)
			return false;
		return new File(filePath).exists();
	}
	
	public boolean fileExistsAndIsAccessible(String filePath) {
		if(filePath == null)
			return false;
		File file = new File(filePath);
		return file.exists() && file.canRead() && file.canWrite();
	}
	
	protected boolean changeDatabaseFile(String filePath) {
		try {
			databaseFile = new RandomAccessFile(filePath, "rwd");
			databaseChannel = databaseFile.getChannel();
			databaseFilePath = filePath;
			currentDatabaseFilePath = filePath;
			return true;
		}
		catch(IOException io) {
			return false;
		}
	}
	
	protected int fillItems(Collection<T> itemCollection, boolean append) {
		if(itemCollection == null)
			return -1;
		
		int itemCtr = 0;
		if(!append)
			loadedItems.clear();
		for(T currentItem : itemCollection) {
			loadedItems.add(currentItem);
			++itemCtr;
		}
		return itemCtr;
	}
	
	public boolean hasBufferedDatabase(String databaseFilePath) {
		return getBufferedFileDatabases().get(databaseFilePath) != null;
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
			for(T item : loadedItems) {
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
		if(index < 0 || index >= loadedItems.size())
			return false;
		boolean res = loadedItems.remove(index) != null;
		if(isAutoSave())
			return rewrite();
		return res;
	}
	
	public boolean removeItem(T item) {
		return item != null && removeItem(loadedItems.indexOf(item));
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
		if(loadedItems.add(newItem)) {
			if(isAutoSave())
				return rewrite();
			return true;
		}
		return false;
	}
	
	public T getItem(int index) {
		return (index < 0 || index >= loadedItems.size()) ? null : loadedItems.get(index);
	}
	
	public boolean replaceItem(T oldItem, T newItem) {
		return replaceItem(indexOf(oldItem), newItem);
	}
	
	public boolean replaceItem(int index, T newItem) {
		if(loadedItems.remove(index) != null) {
			loadedItems.add(index, newItem);
			if(isAutoSave())
				return rewrite();
			return true;
		}
		return false;
	}

	public int indexOf(T item) {
		return loadedItems.indexOf(item);
	}

	public int lastIndexOf(T item) {
		return loadedItems.lastIndexOf(item);
	}
	
	public boolean exists(T item) {
		return loadedItems.contains(item);
	}

	public synchronized void clear() {
		try {
			//TODO Backup generieren
			if(databaseChannel != null)
				databaseChannel.truncate(0L);
			loadedItems.clear();
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
			loadedItems.clear();
			bufferedDatabases.clear();
			setClosed(true);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		return loadedItems.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}
	
	public Class<T> getItemClass() {
		return itemClass;
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
	
	public List<T> getLoadedItems() {
		return loadedItems;
	}

	public HashMap<String, List<T>> getBufferedFileDatabases() {
		return bufferedDatabases;
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
}
