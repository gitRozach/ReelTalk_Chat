package network.ssl.server.manager.clientDataManager.items;

import java.util.Map;

public class ClientAccountData extends ClientProfileData {
	private static final long serialVersionUID = -3548804639821305210L;
	protected static final String PASSWORD_PROPERTY_NAME = "password";
	protected static final String IP_PROPERTY_NAME = "ip";
	protected static final String DEFAULT_PASSWORD = "UNDEFINED_PASSWORD";
	protected static final String DEFAULT_IP = "UNDEFINED_IP";
	
	protected String password;
	protected String ip;
	
	public ClientAccountData() {
		this(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_PROFILE_PICTURE, DEFAULT_PROFILE_LEVEL, DEFAULT_ADMIN_LEVEL, DEFAULT_PASSWORD, DEFAULT_IP);
	}
	
	public ClientAccountData(String databaseString) {
		super(databaseString);
		initFromDatabaseString(databaseString);
	}
	
	public ClientAccountData(int id, String username, String profilePicture, int profileLevel, int adminLevel, String password, String ip) {
		super(id, username, profilePicture, profileLevel, adminLevel);
		setPassword(password);
		setIp(ip);
	}

	@Override public boolean isDefaultInstance() {
		return super.isDefaultInstance() && password.equals(DEFAULT_PASSWORD) && ip.equals(DEFAULT_IP);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (super.equals(obj) && obj instanceof ClientAccountData) {
			try {
				ClientAccountData test = (ClientAccountData) obj;
				return 	password.equals(test.getPassword()) && ip.equals(test.getIp());
			} 
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public String toDatabaseString() {
		return 	super.toDatabaseString() + 
				PASSWORD_PROPERTY_NAME + PROPERTY_START + getPassword() + PROPERTY_END +
				IP_PROPERTY_NAME + PROPERTY_START + getIp() + PROPERTY_END;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(PASSWORD_PROPERTY_NAME)) {
			setPassword(propertyValueMap.get(PASSWORD_PROPERTY_NAME));
			++counter;
		}
		if(propertyValueMap.containsKey(IP_PROPERTY_NAME)) {
			setIp(propertyValueMap.get(IP_PROPERTY_NAME));
			++counter;
		}
		return superRes + counter;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String value) {
		password = value;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String value) {
		ip = value;
	}
}
