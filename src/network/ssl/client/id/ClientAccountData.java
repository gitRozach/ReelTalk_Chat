package network.ssl.client.id;

public class ClientAccountData extends ClientProfileData {
	private static final long serialVersionUID = -3548804639821305210L;
	
	public static final String PASSWORD_PROPERTY_NAME = "password";
	public static final String IP_PROPERTY_NAME = "ip";
	
	public static final String DEFAULT_PASSWORD = "UNDEFINED_PASSWORD";
	public static final String DEFAULT_IP = "UNDEFINED_IP";
	
	private String password;
	private String ip;
	
	public ClientAccountData() {
		this(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_PROFILE_PICTURE, DEFAULT_PROFILE_LEVEL, DEFAULT_ADMIN_LEVEL, DEFAULT_PASSWORD, DEFAULT_IP);
	}
	
	public ClientAccountData(String databaseString) {
		this();
		initFromDatabaseString(databaseString);
	}
	
	public ClientAccountData(int id, String username, String profilePicture, int profileLevel, int adminLevel, String password, String ip) {
		super(id, username, profilePicture, profileLevel, adminLevel);
		setPassword(password);
		setIp(ip);
	}

	@Override public boolean isDefaultInstance()
	{
		return super.isDefaultInstance() 						&&
			   this.password 		.equals(DEFAULT_PASSWORD) 	&&
			   this.ip 				.equals(DEFAULT_IP);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ClientAccountData) {
			try {
				ClientAccountData test = (ClientAccountData) obj;
				return 	super.equals(test)
						&& password.equals(test.getPassword())
						&& ip.equals(test.getIp());
			} 
			catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Id: " + this.getId() + " ]";
	}

	@Override
	public String toDatabaseString() {
		String propStart = "=", propEnd = "#";

		return (ID_PROPERTY_NAME + propStart + getId() + propEnd +
				USERNAME_PROPERTY_NAME + propStart + getUsername() + propEnd + 
				PROFILE_PICTURE_PROPERTY_NAME + propStart + getProfilePicture() + propEnd +
				PROFILE_LEVEL_PROPERTY_NAME + propStart + getProfileLevel() + propEnd +
				ADMIN_LEVEL_PROPERTY_NAME + propStart + getAdminLevel() + propEnd +
				PASSWORD_PROPERTY_NAME + propStart + getPassword() + propEnd +
				IP_PROPERTY_NAME + propStart + getIp() + propEnd).trim();
	}
	
	@Override
	public int initFromDatabaseString(String databaseString) {
		String propStart = "=", propEnd = "#";
		try {
			String[] namesAndValues = databaseString.split(propEnd);
			int propSetCounter = 0;
			
			for(String current : namesAndValues) {
				String currentSplit[] = current.split(propStart);
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
				else if(propName.equals(PASSWORD_PROPERTY_NAME)) {
					setPassword(propValue);
					++propSetCounter;
				}
				else if(propName.equals(IP_PROPERTY_NAME)) {
					setIp(propValue);
					++propSetCounter;
				}
			}
			return propSetCounter;
		}
		catch(Exception e) {
			return -1;
		}
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
