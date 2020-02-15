package network.ssl.server.manager.stringDatabase;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;

public class StringFileDatabase implements Closeable {
	protected volatile boolean initialized;
	protected volatile boolean closed;
	protected final List<String> items;
	protected final List<Integer> itemIndexes;
	protected final RandomAccessFile databaseFile;
	protected final FileChannel databaseChannel;
	protected final String databaseFilePath;
	protected final Charset encoding;

	public StringFileDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public StringFileDatabase(final File file) throws IOException {
		initialized = false;
		closed = false;
		items = new ArrayList<String>();
		itemIndexes = new ArrayList<Integer>();
		databaseFile = new RandomAccessFile(file, "rwd");
		databaseChannel = this.databaseFile.getChannel();
		databaseFilePath = file.getPath();
		encoding = Charsets.UTF_8;
	}

	public synchronized String readItem(int index) {
		try {
			if(index < 0)
				return null;
			
			if(index < itemIndexes.size())
				databaseFile.seek(index == 0 ? 0 : itemIndexes.get(index - 1));
			else {
				String currentItem = null;
				String currentTmp = null;
				int tempIndexCtr = itemIndexes.isEmpty() ? 0 : itemIndexes.size() - 1;
				
				databaseFile.seek(tempIndexCtr <= 0 ? 0L : itemIndexes.get(tempIndexCtr));
				while((currentItem = databaseFile.readLine()) != null && (tempIndexCtr - 1) < index) {
					currentTmp = currentItem;
					++tempIndexCtr;
				}
				if((tempIndexCtr - 1) == index)
					return currentTmp.trim();
				return null;
			}
			return databaseFile.readLine().trim();
		}
		catch(IOException io) {
			return null;
		}
	}
	
	protected synchronized String[] readAllItems() {		
		int currentIndex = 0;
		String currentItem = null;
		List<String> itemList = new ArrayList<String>();
		
		while((currentItem = readItem(currentIndex++)) != null) {
			if(!currentItem.isEmpty())
				itemList.add(currentItem);
		}
		return itemList.toArray(new String[itemList.size()]);
	}
	
	public synchronized int seekTo(int index) {
		if(index < 0 || index > items.size())
			return -1;
		try {
			int newPos = (index == 0 || itemIndexes.isEmpty()) ? 0 : (itemIndexes.size() >= index ? itemIndexes.get(index - 1) : itemIndexes.get(itemIndexes.size() - 1));
			databaseChannel.position(newPos);
			return newPos;
		} 
		catch (IOException e) {
			return -1;
		}
	}

	public int initialize() {
		try {
			items.clear();
			itemIndexes.clear();
			databaseFile.seek(0L);
			
			String currentItem = null;
			int itemCtr = 0;
			while((currentItem = databaseFile.readLine()) != null) {
				currentItem = currentItem.trim();
				if(currentItem.isEmpty())
					continue;
				items.add(currentItem);
				itemIndexes.add((int)databaseFile.getFilePointer());
				++itemCtr;
			}
			initialized = true;
			return itemCtr;
		}
		catch(IOException io) {
			return -1;
		}
	}
	
	public boolean addItem(String item) {
		return addItem(items.size(), item);
	}
	
	public synchronized boolean addItem(int index, String item) {
		if(isClosed())
			return false;
		if(item == null)
			return false;	
		item = item.trim();
		if(index < 0 || index > items.size() || item.isEmpty())
			return false;
		
		try {
			ByteBuffer bytesToWrite = encoding.encode(item + System.lineSeparator());
			int adjustmentValue = bytesToWrite.array().length;
			int bytePos = seekTo(index);
			
			if(bytePos != -1 && databaseChannel.write(encoding.encode(item + System.lineSeparator())) > 0) {
				items.add(index, item);
				itemIndexes.add(index, (int)databaseChannel.position());
				
				for(int h = index + 1; h < items.size(); ++h)
					databaseChannel.write(encoding.encode(items.get(h) + System.lineSeparator()));
				for(int i = index + 1; i < itemIndexes.size(); ++i)
					itemIndexes.set(i, itemIndexes.get(i) + adjustmentValue);
				return true;
			} 
			return false;
		}
		catch(IOException io) {
			return false;
		}
	}
	
	public boolean removeItem(int index) {
		try {
			if(index < 0 || index >= items.size())
				return false;
			
			if(seekTo(index) != -1) {
				String removedItem = items.remove(index) + System.lineSeparator();
				itemIndexes.remove(index);
				int adjustmentValue = removedItem.getBytes(encoding).length;
				
				for(int i = index; i < items.size(); ++i)
					databaseChannel.write(encoding.encode(items.get(i) + System.lineSeparator()));
				for(int j = index; j < itemIndexes.size(); ++j)
					itemIndexes.set(j, itemIndexes.get(j) - adjustmentValue);
				databaseChannel.truncate(databaseChannel.size() - adjustmentValue);
				return true;
			}
			return false;
		}
		catch(IOException io) {
			return false;
		}
	}
	
	public boolean removeItem(String item) {
		return removeItem(items.indexOf(item));
	}

	public void rewriteFile() throws IOException {
		rewriteFile(0);
	}
	
	public synchronized void rewriteFile(int startIndex) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		if(startIndex < 0 || startIndex >= items.size())
			return;
		
		databaseChannel.truncate(startIndex > 0 ? itemIndexes.get(startIndex - 1) : 0L);
		for(int i = startIndex; i < items.size(); ++i)
			databaseChannel.write(encoding.encode(items.get(i) + System.lineSeparator()));
	}
	
	@Override
	public void close() {
		if(isClosed())
			return;
		try {
			databaseFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		items.clear();
		itemIndexes.clear();
		closed = true;
	}
	
	public boolean replaceItem(String oldItem, String newItem) {
		return replaceItem(indexOf(oldItem), newItem);
	}
	
	public synchronized boolean replaceItem(int index, String newItem) {
		if(newItem == null || index < 0 || index >= items.size() || newItem.trim().isEmpty())
			return false;
		try {
			String oldItem = getItem(index);
			int oldItemByteLength = oldItem.getBytes(encoding).length;
			int newItemByteLength = newItem.getBytes(encoding).length;
			int pos = seekTo(index);
			int adjustmentValue = newItemByteLength - oldItemByteLength;
			
			if(pos == -1)
				return false;
			
			items.set(index, newItem);
			itemIndexes.set(index, itemIndexes.get(index) + adjustmentValue);
			
			databaseChannel.truncate(pos);
			databaseChannel.write(encoding.encode(newItem + System.lineSeparator()));
			
			for(int i = index + 1; i < items.size(); ++i)
				databaseChannel.write(encoding.encode(items.get(i) + System.lineSeparator()));
			for(int j = index + 1; j < itemIndexes.size(); ++j)
				itemIndexes.set(j, itemIndexes.get(j) + adjustmentValue);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	public synchronized String getItem(int index) {
		return (index < 0 || index >= items.size()) ? null : items.get(index);
	}

	public synchronized int indexOf(String item) {
		return items.indexOf(item);
	}

	public synchronized int lastIndexOf(String item) {
		return items.lastIndexOf(item);
	}

	public synchronized int size() {
		return items.size();
	}

	public synchronized boolean isEmpty() {
		return size() == 0;
	}
	
	public synchronized boolean exists(String item) {
		return items.contains(item);
	}

	public synchronized void clear() {
		try {
			databaseChannel.truncate(0L);
			items.clear();
			itemIndexes.clear();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public List<String> getItems() {
		return this.items;
	}
	
	public List<Integer> getItemIndexes() {
		return itemIndexes;
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
