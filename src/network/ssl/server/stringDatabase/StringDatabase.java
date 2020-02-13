package network.ssl.server.stringDatabase;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class StringDatabase implements Closeable {
	protected volatile boolean initialized;
	protected volatile boolean closed;
	protected final List<String> items;
	protected final List<Integer> itemIndexes;
	protected final RandomAccessFile databaseFile;
	protected final FileChannel databaseChannel;
	protected final String databaseFilePath;
	protected final Charset encoding;

	public StringDatabase(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public StringDatabase(final File file) throws IOException {
		initialized = false;
		closed = false;
		items = new ArrayList<String>();
		itemIndexes = new ArrayList<Integer>();
		databaseFile = new RandomAccessFile(file, "rwd");
		databaseChannel = this.databaseFile.getChannel();
		databaseFilePath = file.getPath();
		encoding = Charset.forName("UTF-8");
	}
	
	public synchronized String readItem(int index) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
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
	
	protected synchronized String[] readAllItems() throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		
		int currentIndex = 0;
		String currentItem = null;
		List<String> itemList = new ArrayList<String>();
		
		while((currentItem = readItem(currentIndex++)) != null) {
			if(!currentItem.isEmpty())
				itemList.add(currentItem);
		}
		return itemList.toArray(new String[itemList.size()]);
	}
	
	public synchronized int seekTo(int index) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
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

	public int initialize() throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		
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
	
	public boolean removeItem(int index) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
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
	
	public boolean removeItem(String item) throws IOException {
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
	public void close() throws IOException {
		if(isClosed())
			return;
		databaseFile.close();
		items.clear();
		itemIndexes.clear();
		closed = true;
	}
	
	public boolean replaceItem(String oldItem, String newItem) throws IOException {
		return replaceItem(indexOf(oldItem), newItem);
	}
	
	public synchronized boolean replaceItem(int index, String newItem) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		if(newItem == null || index < 0 || index >= items.size() || newItem.trim().isEmpty())
			return false;
		
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
	
	public synchronized String getItem(int index) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		if(index < 0 || index >= items.size())
			return null;
		return items.get(index);
	}

	public synchronized int indexOf(String item) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		return items.indexOf(item);
	}

	public synchronized int lastIndexOf(String item) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		return items.lastIndexOf(item);
	}

	public synchronized int size() throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		return items.size();
	}

	public synchronized boolean isEmpty() throws IOException {
		return size() == 0;
	}
	
	public synchronized boolean exists(String item) throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		return items.contains(item);
	}

	public synchronized void clear() throws IOException {
		if(isClosed())
			throw new IOException("Database closed");
		databaseChannel.truncate(0L);
		items.clear();
		itemIndexes.clear();
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public List<String> getItems() throws IOException {
		return this.items;
	}
	
	public List<Integer> getItemIndexes() {
		return itemIndexes;
	}
}
