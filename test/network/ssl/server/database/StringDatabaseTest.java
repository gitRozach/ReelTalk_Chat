package network.ssl.server.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;

class StringDatabaseTest {
	private StringDatabase database;
	
	@AfterEach
	void tearDown() throws Exception {
		if(database != null)
			database.close();
	}
	
	void fillDatabase(int count) throws IOException {
		fillDatabase("Text", count);
	}
	
	void fillDatabase(String valuePrefix, int count) throws IOException {
		if(database == null)
			return;
		for(int i = 0; i < count; ++i)
			database.addItem(i, valuePrefix + (i+1));
	}
	
	@Test
	void initialize_itemListContainsAllStrings() throws IOException {
		database = new StringDatabase("test/databaseResources/testInit.txt");
		assertTrue(!database.isInitialized());
		
		int addedItems = database.initialize();
		
		assertTrue(database.isInitialized());
		assertTrue(addedItems == 1000);
		assertTrue(database.size() == 1000);
	}
	
	@Test
	void readItem_readsCorrectItems() throws IOException {
		database = new StringDatabase("test/databaseResources/testRead.txt", true);
		
		String item1 = database.readItem(499); //Text500
		String item2 = database.readItem(0); //Text1
		String item3 = database.readItem(1000); //null
		String item4 = database.readItem(-1); //null
		String item5 = database.readItem(110); //Text111
		
		assertTrue(item1.equals("Text500"));
		assertTrue(item2.equals("Text1"));
		assertTrue(item3 == null);
		assertTrue(item4 == null);
		assertTrue(item5.equals("Text111"));
	}
	
	@Test 
	void clear_databaseIsEmpty() throws IOException {
		database = new StringDatabase("test/databaseResources/testClear.txt", true);
		
		assertTrue(database.addItem(0, "Text1"));
		assertTrue(database.addItem(0, "Text2"));
		assertTrue(database.addItem(0, "Text3"));
		assertTrue(database.addItem(0, "Text4"));
		assertTrue(database.addItem(0, "Text5"));
		
		database.clear();
		
		assertTrue(database.isEmpty());
		assertTrue(database.getItems().isEmpty());
		assertTrue(database.getItemIndexes().isEmpty());
	}
	
	@Test
	void close_accessingFileAfterwardsThrowsExceptions() throws IOException {
		database = new StringDatabase("test/databaseResources/testClose.txt", true);
		database.close();
		
		assertThrows(IOException.class, () -> {
			database.addItem("NEWITEM");
		});
		assertThrows(IOException.class, () -> {
			database.removeItem(0);
		});
		assertThrows(IOException.class, () -> {
			database.replaceItem(0, "NEWITEM");
		});
		assertThrows(IOException.class, () -> {
			database.readItem(0);
		});
	}
	
	@Test
	void seekTo_moveFilePointerToGivenIndexes() throws IOException {
		database = new StringDatabase("test/databaseResources/testSeek.txt");
		
		assertTrue(database.addItem(0, "Text1"));
		assertTrue(database.addItem(0, "Text2"));
		assertTrue(database.addItem(0, "Text3"));
		assertTrue(database.addItem(0, "Text4"));
		assertTrue(database.addItem(0, "Text5"));
		
		int pos0 = 0;
		int pos1 = ("Text1" +  System.lineSeparator()).getBytes(Charsets.UTF_8).length;
		int pos2 = pos1 + ("Text2" +  System.lineSeparator()).getBytes(Charsets.UTF_8).length;
		int pos3 = pos2 + ("Text3" +  System.lineSeparator()).getBytes(Charsets.UTF_8).length;
		int pos4 = pos3 + ("Text4" +  System.lineSeparator()).getBytes(Charsets.UTF_8).length;
		
		int nPos0 = database.seekTo(0);
		int nPos1 = database.seekTo(1);
		int nPos2 = database.seekTo(2);
		int nPos3 = database.seekTo(3);
		int nPos4 = database.seekTo(4);
		int nPos5 = database.seekTo(-1);
		int nPos6 = database.seekTo(1000);
		
		assertTrue(nPos0 == pos0);
		assertTrue(nPos1 == pos1);
		assertTrue(nPos2 == pos2);
		assertTrue(nPos3 == pos3);
		assertTrue(nPos4 == pos4);
		assertTrue(nPos5 == -1);
		assertTrue(nPos6 == -1);
	}
	
	@Test
	void addItem_insertItems_checkFileContentAndOrder() throws IOException {
		database = new StringDatabase("test/databaseResources/testAddItems.txt");
		
		assertTrue(database.addItem(0, "Text1"));
		assertTrue(database.addItem(0, "Text2"));
		assertTrue(database.addItem(0, "Text3"));
		assertTrue(database.addItem(1, "Text4"));
		assertTrue(database.addItem(3, "Text5"));
		assertTrue(database.addItem(5, "Text6"));
		assertTrue(database.addItem("Text7"));
		assertFalse(database.addItem(-600, "InvalidIndex"));
		assertFalse(database.addItem(0, null));
		assertFalse(database.addItem(""));
		
		assertTrue(database.getItems().get(0).equals("Text3"));
		assertTrue(database.getItems().get(1).equals("Text4"));
		assertTrue(database.getItems().get(2).equals("Text2"));
		assertTrue(database.getItems().get(3).equals("Text5"));
		assertTrue(database.getItems().get(4).equals("Text1"));
		assertTrue(database.getItems().get(5).equals("Text6"));
		assertTrue(database.getItems().get(6).equals("Text7"));
		
		assertTrue(database.readItem(0).equals("Text3"));
		assertTrue(database.readItem(1).equals("Text4"));
		assertTrue(database.readItem(2).equals("Text2"));
		assertTrue(database.readItem(3).equals("Text5"));
		assertTrue(database.readItem(4).equals("Text1"));
		assertTrue(database.readItem(5).equals("Text6"));
		assertTrue(database.readItem(6).equals("Text7"));
	}
	
	@Test
	void removeItem_deleteSingleItem_indexArgument() throws IOException {
		database = new StringDatabase("test/databaseResources/testRemoveSingleItemIndex.txt");
		
		database.addItem("Text1");
		database.addItem("Text2");
		database.addItem("Text3");
		database.addItem("Text4");
		database.addItem("Text5");
		database.addItem("Text6");
		
		assertTrue(database.removeItem(0));
		assertFalse(database.getItems().contains("Text1"));
		assertFalse(database.removeItem(100));
		assertFalse(database.removeItem(-1));
		
		assertTrue(database.getItems().get(0).equals("Text2"));
		assertTrue(database.readItem(0).equals("Text2"));
	}
	
	@Test
	void removeItem_deleteMultipleItems_indexArgument() throws IOException {
		database = new StringDatabase("test/databaseResources/testRemoveMultipleItemsIndexes.txt");
		
		database.addItem("Text1");
		database.addItem("Text2");
		database.addItem("Text3");
		database.addItem("Text4");
		database.addItem("Text5");
		database.addItem("Text6");
		
		assertTrue(database.removeItem(0));
		assertTrue(database.removeItem(0));
		assertTrue(database.removeItem(0));
		assertTrue(database.removeItem(0));
		assertTrue(database.removeItem(1));
		
		assertTrue(database.getItems().size() == 1);
		assertTrue(database.getItemIndexes().size() == 1);
		
		assertTrue(database.getItems().get(0).equals("Text5"));
		assertTrue(database.readItem(0).equals("Text5"));	
	}
	
	@Test
	void removeItem_deleteSingleItem_objectArgument() throws IOException {
		database = new StringDatabase("test/databaseResources/testRemoveSingleItemObject.txt");
		
		database.addItem("Text1");
		database.addItem("Text2");
		database.addItem("Text3");
		database.addItem("Text4");
		database.addItem("Text5");
		database.addItem("Text6");
		
		assertTrue(database.removeItem("Text3"));
		assertFalse(database.getItems().contains("Text3"));
		assertFalse(database.removeItem("DoesNotExist"));
		assertFalse(database.removeItem(null));
		assertFalse(database.removeItem(""));
		
		assertTrue(database.getItems().size() == 5);
		assertTrue(database.getItemIndexes().size() == 5);
		
		assertTrue(database.getItems().get(2).equals("Text4"));
		assertTrue(database.readItem(2).equals("Text4"));
	}
	
	@Test
	void removeItem_deleteMultipleItems_objectArgument() throws IOException {
		database = new StringDatabase("test/databaseResources/testRemoveMultipleItemsObjects.txt");
		
		database.addItem("Text1");
		database.addItem("Text2");
		database.addItem("Text3");
		database.addItem("Text4");
		database.addItem("Text5");
		database.addItem("Text6");
		
		assertTrue(database.removeItem("Text1"));
		assertTrue(database.removeItem("Text6"));
		assertTrue(database.removeItem("Text2"));
		assertTrue(database.removeItem("Text5"));
		assertTrue(database.removeItem("Text4"));
		
		assertTrue(database.getItems().size() == 1);
		assertTrue(database.getItemIndexes().size() == 1);
		
		assertTrue(database.getItems().get(0).equals("Text3"));
		assertTrue(database.readItem(0).equals("Text3"));	
	}
	
	@Test
	void removeItem_deleteAllItems_databaseIsEmpty() throws IOException {
		database = new StringDatabase("test/databaseResources/testRemoveAllItems.txt");
		database.clear();
				
		int itemsCount = 25;
		fillDatabase(itemsCount);
		
		assertTrue(database.size() == 25);
		
		for(int i = 0; i < itemsCount; ++i)
			database.removeItem(0);
		
		assertTrue(database.isEmpty());
		assertTrue(database.size() == 0);
	}	
	
	@Test
	void replaceItem_test() throws IOException {
		database = new StringDatabase("test/databaseResources/testReplaceItems.txt");
		
		int itemsCount = 100;
		fillDatabase("ReplacableText", itemsCount);
		
		assertTrue(database.replaceItem("ReplacableText1", "HUEHUEHUE"));
		assertTrue(database.replaceItem(1, "HUEHUEHUE2!"));
		assertFalse(database.replaceItem("ReplacableText3", null));
		assertFalse(database.replaceItem(-1, "Invalid Index!"));
		assertFalse(database.replaceItem(600, "Invalid as well!"));
		assertFalse(database.replaceItem(0, ""));
		
		assertTrue(database.readItem(0).equals("HUEHUEHUE"));
		assertTrue(database.readItem(1).equals("HUEHUEHUE2!"));
		assertTrue(database.readItem(2).equals("ReplacableText3"));
		assertTrue(database.readItem(3).equals("ReplacableText4"));
		assertTrue(database.readItem(4).equals("ReplacableText5"));
		
		assertTrue(database.size() == 100);
	}
}
