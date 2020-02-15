package network.ssl.server.manager.clientDataManager.items;

import java.util.Map;

public class BasicClientData extends ClientData {
	private static final long serialVersionUID = -5743584214580708597L;
	protected static final String USERNAME_PROPERTY_NAME = "username";
	protected static final String DEFAULT_USERNAME = "UNDEFINED_USERNAME";
	
	protected String username;
	
	public BasicClientData() {
		this(DEFAULT_ID, DEFAULT_USERNAME);
	}
	
	public BasicClientData(int id, String name) {
		super(id);
		username = name;
	}
	
	public BasicClientData(String databaseString) {
		super(databaseString);
		initFromDatabaseString(databaseString);
	}
	
	@Override public boolean isDefaultInstance() {
		return 	super.isDefaultInstance()
				&& username.equals(DEFAULT_USERNAME);
	}
	
	@Override
	public boolean equals(final Object obj) {
		return 	super.equals(obj)
				&& obj instanceof BasicClientData
				&& ((BasicClientData)obj).getUsername().equals(username);
	}
	
	@Override
	public String toDatabaseString() {
		return 	super.toDatabaseString() + USERNAME_PROPERTY_NAME + PROPERTY_START + getUsername() + PROPERTY_END;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(USERNAME_PROPERTY_NAME)) {
			setUsername(propertyValueMap.get(USERNAME_PROPERTY_NAME));
			++counter;
		}
		return superRes + counter;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
