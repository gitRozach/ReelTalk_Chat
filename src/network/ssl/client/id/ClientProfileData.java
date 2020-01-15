package network.ssl.client.id;

public class ClientProfileData extends ClientData {
	private static final long serialVersionUID = 6601663695763028601L;
	
	public static final String USERNAME_PROPERTY_NAME = "username";
	public static final String PROFILE_PICTURE_PROPERTY_NAME = "profilePicture";
	public static final String PROFILE_LEVEL_PROPERTY_NAME = "profileLevel";
	public static final String ADMIN_LEVEL_PROPERTY_NAME = "adminLevel";
	
	public static final String DEFAULT_USERNAME = "UNDEFINED_USERNAME";
	public static final String DEFAULT_PROFILE_PICTURE = "UNDEFINED_PROFILE_PICTURE";
	public static final int DEFAULT_PROFILE_LEVEL = -1;
	public static final int DEFAULT_ADMIN_LEVEL = -1;
	
	private String username;
	private String profilePicture;
	private int profileLevel;
	private int adminLevel;
	
	public ClientProfileData() {
		this(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_PROFILE_PICTURE, DEFAULT_PROFILE_LEVEL, DEFAULT_ADMIN_LEVEL);
	}
	
	public ClientProfileData(String databaseString) {
		this();
		initFromDatabaseString(databaseString);
	}
	
	public ClientProfileData(int id, String username, String profilePicture, int profileLevel, int adminLevel) {
		super(id);
		setUsername(username);
		setProfilePicture(profilePicture);
		setProfileLevel(profileLevel);
		setAdminLevel(adminLevel);
	}
	
	@Override public boolean isDefaultInstance()
	{
		return 	super.isDefaultInstance() 
				&& username.equals(DEFAULT_USERNAME) 
				&& profilePicture.equals(DEFAULT_PROFILE_PICTURE)
				&& profileLevel == DEFAULT_PROFILE_LEVEL
				&& adminLevel == DEFAULT_ADMIN_LEVEL;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClientProfileData) {
			try {
				ClientProfileData test = (ClientProfileData) obj;
				return 	super.equals(test)
						&& username.equals(test.getUsername())
						&& profilePicture.equals(test.getProfilePicture())
						&& profileLevel == test.getProfileLevel()
						&& adminLevel == test.getAdminLevel();
			} 
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public String toDatabaseString() {
		return (ID_PROPERTY_NAME + PROPERTY_START + getId() + PROPERTY_END +
				USERNAME_PROPERTY_NAME + PROPERTY_START + getUsername() + PROPERTY_END + 
				PROFILE_PICTURE_PROPERTY_NAME + PROPERTY_START + getProfilePicture() + PROPERTY_END +
				PROFILE_LEVEL_PROPERTY_NAME + PROPERTY_START + getProfileLevel() + PROPERTY_END +
				ADMIN_LEVEL_PROPERTY_NAME + PROPERTY_START + getAdminLevel() + PROPERTY_END).trim();
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		try {
			String[] namesAndValues = databaseString.split(PROPERTY_END);
			int propSetCounter = 0;
			
			for(String current : namesAndValues) {
				String currentSplit[] = current.split(PROPERTY_START);
				String propName = currentSplit[0];
				String propValue = currentSplit[1];
				
				if(propName.equals(ID_PROPERTY_NAME)) {
					setId(Integer.parseInt(propValue));
					++propSetCounter;
				}
				else if(propName.equals(USERNAME_PROPERTY_NAME)) {
					setUsername(propValue);
					++propSetCounter;
				}
				else if(propName.equals(PROFILE_PICTURE_PROPERTY_NAME)) {
					setProfilePicture(propValue);
					++propSetCounter;
				}
				else if(propName.equals(PROFILE_LEVEL_PROPERTY_NAME)) {
					setProfileLevel(Integer.parseInt(propValue));
					++propSetCounter;
				}
				else if(propName.equals(ADMIN_LEVEL_PROPERTY_NAME)) {
					setAdminLevel(Integer.parseInt(propValue));
					++propSetCounter;
				}
			}
			return propSetCounter;
		}
		catch(Exception e) {
			return -1;
		}
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public int getProfileLevel() {
		return profileLevel;
	}
	public void setProfileLevel(int profileLevel) {
		this.profileLevel = profileLevel;
	}
	public int getAdminLevel() {
		return adminLevel;
	}
	public void setAdminLevel(int adminLevel) {
		this.adminLevel = adminLevel;
	}
}
