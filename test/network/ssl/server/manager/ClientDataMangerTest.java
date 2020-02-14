package network.ssl.server.manager;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import network.ssl.client.id.ClientAccountData;
import network.ssl.client.id.ClientProfileData;
import network.ssl.server.manager.clientManager.ClientDataManager;

class ClientDataMangerTest {
	
	@Test
	void addItemsAndInitFromDatabase_clientProfileDataTest() throws IOException {
		ClientDataManager<ClientProfileData> database = new ClientDataManager<ClientProfileData>(ClientProfileData.class, "test/testresources/clientDataManager/clientProfileDataTest.txt");
		database.clear();
		
		ClientProfileData b1 = new ClientProfileData(0, "Rozach", "default.png", 0, 5);
		ClientProfileData b2 = new ClientProfileData(1, "Jenn", "default.png", 0, 0);
		ClientProfileData b3 = new ClientProfileData(2, "Husseini", "default.png", 0, 0);
		ClientProfileData b4 = new ClientProfileData(3, "Max", "default.png", 0, 0);
		ClientProfileData b5 = new ClientProfileData(4, "Andrea", "default.png", 0, 0);
		
		assertTrue(database.addItem(b1));
		assertTrue(database.addItem(b2));
		assertTrue(database.addItem(b3));
		assertTrue(database.addItem(b4));
		assertTrue(database.addItem(b5));
		
		ClientProfileData c1 = new ClientProfileData(database.getItem(0));
		ClientProfileData c2 = new ClientProfileData(database.getItem(1));
		ClientProfileData c3 = new ClientProfileData(database.getItem(2));
		ClientProfileData c4 = new ClientProfileData(database.getItem(3));
		ClientProfileData c5 = new ClientProfileData(database.getItem(4));
		
		assertTrue(c1.equals(b1));
		assertTrue(c2.equals(b2));
		assertTrue(c3.equals(b3));
		assertTrue(c4.equals(b4));
		assertTrue(c5.equals(b5));
		
		database.close();
	}

	@Test
	void addItemsAndInitFromDatabase_clientAccountDataTest() throws IOException {
		ClientDataManager<ClientAccountData> database = new ClientDataManager<ClientAccountData>(ClientAccountData.class, "test/testresources/clientDataManager/clientAccountDataTest.txt");
		database.clear();
		
		ClientAccountData b1 = new ClientAccountData(0, "Rozach", "default.png", 0, 5, "rozett", "no_ip");
		ClientAccountData b2 = new ClientAccountData(1, "Jenn", "default.png", 0, 0, "jenn", "no_ip");
		ClientAccountData b3 = new ClientAccountData(2, "Husseini", "default.png", 0, 0, "husseini", "no_ip");
		ClientAccountData b4 = new ClientAccountData(3, "Max", "default.png", 0, 0, "max", "no_ip");
		ClientAccountData b5 = new ClientAccountData(4, "Andrea", "default.png", 0, 0, "zelle", "no_ip");
		
		assertTrue(database.addItem(b1));
		assertTrue(database.addItem(b2));
		assertTrue(database.addItem(b3));
		assertTrue(database.addItem(b4));
		assertTrue(database.addItem(b5));
		
		ClientAccountData c1 = new ClientAccountData(database.getItem(0));
		ClientAccountData c2 = new ClientAccountData(database.getItem(1));
		ClientAccountData c3 = new ClientAccountData(database.getItem(2));
		ClientAccountData c4 = new ClientAccountData(database.getItem(3));
		ClientAccountData c5 = new ClientAccountData(database.getItem(4));
		
		assertTrue(c1.equals(b1));
		assertTrue(c2.equals(b2));
		assertTrue(c3.equals(b3));
		assertTrue(c4.equals(b4));
		assertTrue(c5.equals(b5));
		
		database.close();
	}
	
	@Test
	void addItemsAndGenerateUniqueIds_clientAccountDataTest() throws IOException {
		ClientDataManager<ClientAccountData> database = new ClientDataManager<ClientAccountData>(ClientAccountData.class, "test/testresources/clientDataManager/generateUniqueIdsTest.txt");
		database.clear();
		
		assertTrue(database.addItem(new ClientAccountData(database.generateUniqueId(), "Rozachos", "default.png", 0, 5, "rozett", "no_ip")));
		assertTrue(database.addItem(new ClientAccountData(database.generateUniqueId(), "Husseinikus", "default.png", 0, 5, "rozett", "no_ip")));
		assertTrue(database.addItem(new ClientAccountData(database.generateUniqueId(), "Jennson", "default.png", 0, 5, "rozett", "no_ip")));
		
		ClientAccountData b1 = new ClientAccountData(database.generateUniqueId(100), "Rozach", "default.png", 0, 5, "rozett", "no_ip");
		assertTrue(b1.getId() == 100);
		assertTrue(database.addItem(b1));
		
		ClientAccountData b2 = new ClientAccountData(database.generateUniqueId(), "Jenn", "default.png", 0, 0, "jenn", "no_ip");
		assertTrue(b2.getId() == 101);
		assertTrue(database.addItem(b2));
		
		ClientAccountData b3 = new ClientAccountData(database.generateUniqueId(), "Husseini", "default.png", 0, 0, "husseini", "no_ip");
		assertTrue(b3.getId() == 102);
		assertTrue(database.addItem(b3));
		
		ClientAccountData b4 = new ClientAccountData(database.generateUniqueId(), "Max", "default.png", 0, 0, "max", "no_ip");
		assertTrue(b4.getId() == 103);
		assertTrue(database.addItem(b4));
		
		ClientAccountData b5 = new ClientAccountData(database.generateUniqueId(), "Andrea", "default.png", 0, 0, "zelle", "no_ip");
		assertTrue(b5.getId() == 104);
		assertTrue(database.addItem(b5));

		ClientAccountData c1 = new ClientAccountData(database.getItem(3));
		ClientAccountData c2 = new ClientAccountData(database.getItem(4));
		ClientAccountData c3 = new ClientAccountData(database.getItem(5));
		ClientAccountData c4 = new ClientAccountData(database.getItem(6));
		ClientAccountData c5 = new ClientAccountData(database.getItem(7));
		
		assertTrue(c1.equals(b1));
		assertTrue(c2.equals(b2));
		assertTrue(c3.equals(b3));
		assertTrue(c4.equals(b4));
		assertTrue(c5.equals(b5));
		
		database.close();
	}
	
	@Test
	void getByProperty_clientProfileDataTest() throws IOException {
		ClientDataManager<ClientAccountData> database = new ClientDataManager<ClientAccountData>(ClientAccountData.class, "src/clientData/accounts.txt");
		database.clear();
		
		ClientAccountData b1 = new ClientAccountData(0, "Rozach", "default.png", 0, 5, "jajut", "no_ip");
		ClientAccountData b2 = new ClientAccountData(1, "Jenn", "default.png", 0, 0, "jenn", "no_ip");
		ClientAccountData b3 = new ClientAccountData(2, "Husseini", "default.png", 0, 0, "husseini", "no_ip");
		ClientAccountData b4 = new ClientAccountData(3, "Max", "default.png", 0, 0, "max", "no_ip");
		ClientAccountData b5 = new ClientAccountData(4, "Andrea", "default.png", 0, 0, "andrea", "no_ip");
		
		assertTrue(database.addItem(b1));
		assertTrue(database.addItem(b2));
		assertTrue(database.addItem(b3));
		assertTrue(database.addItem(b4));
		assertTrue(database.addItem(b5));
		
		ClientAccountData c1 = new ClientAccountData(database.getByProperty("username", "Rozach"));
		ClientAccountData c2 = new ClientAccountData(database.getByProperty("id", "1"));
		ClientAccountData c3 = new ClientAccountData(database.getByProperty("password", "husseini"));
		ClientAccountData c4 = new ClientAccountData(database.getByProperty("username", "Max"));
		ClientAccountData c5 = new ClientAccountData(database.getByProperty("id", "4"));
		
		assertTrue(c1.equals(b1));
		assertTrue(c2.equals(b2));
		assertTrue(c3.equals(b3));
		assertTrue(c4.equals(b4));
		assertTrue(c5.equals(b5));
		
		database.close();
	}
}
