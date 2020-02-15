package network.ssl.server.propertyValueDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.jupiter.api.Test;

import network.ssl.server.manager.messageManager.items.PrivateMessage;
import network.ssl.server.manager.propertyValueDatabase.PropertyValueDatabase;

class PropertyValueDatabaseTest {
	protected PropertyValueDatabase<PrivateMessage> database;
	
	@After
	public void tearDown() {
		if(database != null)
			database.close();
	}

	@Test
	public void addItems_getItemsAsStringAndCompareWithAddedItems() throws IOException {
		database = new PropertyValueDatabase<PrivateMessage>(PrivateMessage.class, "test/testresources/propertyValueDatabase/addItems.txt");
		database.clear();
		
		PrivateMessage m1 = new PrivateMessage(0, "Hasan", 1, "Hallo Rozach", 0, "Rozach");
		PrivateMessage m2 = new PrivateMessage(1, "Rozach", 0, "Hallo Hasan", 1, "Hasan");
		PrivateMessage m3 = new PrivateMessage(2, "Hasan", 1, "Spast!!!", 0, "Rozach");
		PrivateMessage m4 = new PrivateMessage(3, "Rozach", 0, "Alter chill sonst klappts", 1, "Hasan");
		PrivateMessage m5 = new PrivateMessage(4, "Hasan", 1, "Huehuehuehuehue!", 0, "Rozach");
		
		assertTrue(database.addItem(m1));
		assertTrue(database.addItem(m2));
		assertTrue(database.addItem(m3));
		assertTrue(database.addItem(m4));
		assertTrue(database.addItem(m5));
		
		PrivateMessage rm1 = new PrivateMessage(database.getItem(0));
		PrivateMessage rm2 = new PrivateMessage(database.getItem(1));
		PrivateMessage rm3 = new PrivateMessage(database.getItem(2));
		PrivateMessage rm4 = new PrivateMessage(database.getItem(3));
		PrivateMessage rm5 = new PrivateMessage(database.getItem(4));
		
		assertEquals(m1, rm1);
		assertEquals(m2, rm2);
		assertEquals(m3, rm3);
		assertEquals(m4, rm4);
		assertEquals(m5, rm5);
	}
	
	@Test
	public void getItems_test() {
		
	}
	
	@Test
	public void getByProperty_test() {
		
	}
	
	@Test
	public void getByProperty_test2() {
		
	}
}
