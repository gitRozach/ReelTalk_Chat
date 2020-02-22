package network.ssl.server.manager.protobufDatabase;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

public abstract class ProtobufFileDatabase<T extends GeneratedMessageV3> implements Closeable {
	protected volatile boolean initialized;
	protected volatile boolean closed;
	protected volatile boolean autoSave;
	protected final List<T> items;
	protected final RandomAccessFile databaseFile;
	protected final FileChannel databaseChannel;
	protected final String databaseFilePath;
	protected final Charset encoding;

	public ProtobufFileDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ProtobufFileDatabase(File file) throws IOException {
		initialized = false;
		closed = false;
		autoSave = true;
		items = new ArrayList<T>();
		databaseFile = new RandomAccessFile(file, "rwd");
		databaseChannel = this.databaseFile.getChannel();
		databaseFilePath = file.getPath();
		encoding = Charset.forName("utf-8");
	}
	
	public int writeItem(T item) {
		try {
			OutputStream fileOutput = Channels.newOutputStream(databaseChannel);
			item.writeDelimitedTo(fileOutput);
			return item.toByteArray().length;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean writeItems() {
		try {
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
	
	public abstract T readItem();
	
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

	public int initialize() {
		int itemCtr = 0;
		items.clear();			
		for(T currentItem : readItems()) {
			items.add(currentItem);
			++itemCtr;
		}
		initialized = true;
		return itemCtr;
	}
	
	public boolean removeItem(int index) {
		return false;
	}
	
	public boolean removeItem(T item) {
		if(removeItem(items.indexOf(item))) {
			if(isAutoSave())
				return rewrite();
			return true;
		}
		return false;
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

	public List<T> getItems() {
		return items;
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
