package network.ssl.server.manager.clientDataManager.items;

import java.util.Map;

public class ClientProfileData extends BasicClientData {
	private static final long serialVersionUID = 6601663695763028601L;
	public static final String PROFILE_PICTURE_PROPERTY_NAME = "profilePicture";
	public static final String PROFILE_LEVEL_PROPERTY_NAME = "profileLevel";
	public static final String ADMIN_LEVEL_PROPERTY_NAME = "adminLevel";
	public static final String DEFAULT_PROFILE_PICTURE = "UNDEFINED_PROFILE_PICTURE";
	public static final int DEFAULT_PROFILE_LEVEL = -1;
	public static final int DEFAULT_ADMIN_LEVEL = -1;
	
	protected String profilePicture;
	protected int profileLevel;
	protected int adminLevel;
	
	public ClientProfileData() {
		this(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_PROFILE_PICTURE, DEFAULT_PROFILE_LEVEL, DEFAULT_ADMIN_LEVEL);
	}
	
	public ClientProfileData(String databaseString) {
		super(databaseString);
		initFromDatabaseString(databaseString);
	}
	
	public ClientProfileData(int id, String username, String profilePicture, int profileLevel, int adminLevel) {
		super(id, username);
		setUsername(username);
		setProfilePicture(profilePicture);
		setProfileLevel(profileLevel);
		setAdminLevel(adminLevel);
	}
	
	@Override public boolean isDefaultInstance()
	{
		return 	super.isDefaultInstance() 
				&& profilePicture.equals(DEFAULT_PROFILE_PICTURE)
				&& profileLevel == DEFAULT_PROFILE_LEVEL
				&& adminLevel == DEFAULT_ADMIN_LEVEL;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (super.equals(obj) && obj instanceof ClientProfileData) {
			try {
				ClientProfileData test = (ClientProfileData) obj;
				return 	profilePicture.equals(test.getProfilePicture()) &&
						profileLevel == test.getProfileLevel() &&
						adminLevel == test.getAdminLevel();
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
				PROFILE_PICTURE_PROPERTY_NAME + PROPERTY_START + getProfilePicture() + PROPERTY_END +
				PROFILE_LEVEL_PROPERTY_NAME + PROPERTY_START + getProfileLevel() + PROPERTY_END +
				ADMIN_LEVEL_PROPERTY_NAME + PROPERTY_START + getAdminLevel() + PROPERTY_END;
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		int superRes = super.initFromDatabaseString(databaseString);
		int counter = 0;
		Map<String, String> propertyValueMap = loadProperties(databaseString);
		if(propertyValueMap.containsKey(PROFILE_PICTURE_PROPERTY_NAME)) {
			setProfilePicture(propertyValueMap.get(PROFILE_PICTURE_PROPERTY_NAME));
			++counter;
		}
		if(propertyValueMap.containsKey(PROFILE_LEVEL_PROPERTY_NAME)) {
			setProfileLevel(Integer.parseInt(propertyValueMap.get(PROFILE_LEVEL_PROPERTY_NAME)));
			++counter;
		}
		if(propertyValueMap.containsKey(ADMIN_LEVEL_PROPERTY_NAME)) {
			setAdminLevel(Integer.parseInt(propertyValueMap.get(ADMIN_LEVEL_PROPERTY_NAME)));
			++counter;
		}
		return superRes + counter;
	}
	
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String value) {
		profilePicture = value;
	}
	public int getProfileLevel() {
		return profileLevel;
	}
	public void setProfileLevel(int value) {
		profileLevel = value;
	}
	public int getAdminLevel() {
		return adminLevel;
	}
	public void setAdminLevel(int value) {
		adminLevel = value;
	}
}
