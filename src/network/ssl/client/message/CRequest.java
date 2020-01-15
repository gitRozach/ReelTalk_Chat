package network.ssl.client.message;

public abstract class CRequest extends CPacket
{
	private static final long serialVersionUID = -8783610718853384298L;
	private final long requestTimeMillis;

	public CRequest(long requestTimeMillis)
	{
		this.requestTimeMillis = requestTimeMillis;
	}

	public static CLoginRequest newLoginRequest(String username, String password)
	{
		return new CLoginRequest(username, password);
	}

	public static CLogoutRequest newLogoutRequest(String username, String password)
	{
		return new CLogoutRequest(username, password);
	}

	public static CRegistrationRequest newRegistrationRequest(String username, String password)
	{
		return new CRegistrationRequest(username, password);
	}
	
	public static CPingRequest newPingRequest()
	{
		return new CPingRequest();
	}

	public static CClientDataRequest newClientDataRequest(String username, String password)
	{
		return new CClientDataRequest(username, password);
	}
	
	public static CChannelDataRequest newChannelDataRequest(int channelId, String username, String password)
	{
		return new CChannelDataRequest(channelId, username, password);
	}
	
	public static CFileUploadRequest newFileUploadRequest(boolean channelUpload, String username, String password, String channelOrReceiver, String fileName, long fileSize)
	{
		return new CFileUploadRequest(channelUpload, username, password, channelOrReceiver, fileName, fileSize);
	}
	
	public static CFileDownloadRequest newFileDownloadRequest(String username, String password, String key, String fileName)
	{
		return new CFileDownloadRequest(username, password, key, fileName);
	}

	public double getRequestTimeMillis() {return this.requestTimeMillis;}

	/*
	 *
	 */
	
	/*
	 * 			All request types with login data (username and password)
	 */
	public abstract static class CClassifiedRequest extends CRequest
	{
		private static final long serialVersionUID = -9101124444139787648L;
		private String username;
		private String password;
		
		public CClassifiedRequest()
		{
			this("", "");
		}
		
		public CClassifiedRequest(String username, String password) 
		{
			super(System.currentTimeMillis());	
			this.username = username;
			this.password = password;
		}
		
		public String getUsername() {return this.username;}
		public void setUsername(String value) {this.username = value;}
		public String getPassword() {return this.password;}
		public void setPassword(String value) {this.password = value;}
	}

	public static class CLoginRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = 3985636534850800312L;

		public CLoginRequest(String username, String password)
		{
			super(username, password);
		}
	}

	/*
	 *
	 */

	public static class CLogoutRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = 3985636534850800312L;

		public CLogoutRequest(String username, String password)
		{
			super(username, password);
		}
	}

	/*
	 *
	 */

	public static class CRegistrationRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = 6056371894996393982L;

		public CRegistrationRequest(String username, String password)
		{
			super(username, password);
		}
	}

	/*
	 *
	 */
	
	public static class CPingRequest extends CRequest
	{
		private static final long serialVersionUID = 6056371894996393982L;
		private long startTimeMillis;

		public CPingRequest()
		{
			super(System.currentTimeMillis());
			this.startTimeMillis = System.currentTimeMillis();
		}

		public long getStartTimeMillis() {return this.startTimeMillis;}
		public void setStartTimeMillis(long value) {this.startTimeMillis = value;}
	}
	
	/*
	 * 
	 */

	public static class CClientDataRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = -7594495717583840203L;

		public CClientDataRequest(String username, String password)
		{
			super(username, password);
		}
	}
	
	/*
	 * 
	 */
	
	public static class CChannelJoinRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = 3658071762561341152L;
		private int channelId;
		
		public CChannelJoinRequest(String username, String password, int channelId)
		{
			super(username, password);
			this.channelId = channelId;
		}
		
		public int getChannelId() {return this.channelId;}
		public void setChannelId(int value) {this.channelId = value;}
	}
	
	public static class CChannelDataRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = 2564685783474423978L;
		private int channelId;

		public CChannelDataRequest(int channelId, String username, String password)
		{
			super(username, password);
			this.channelId = channelId;
		}

		public int getChannelId() {return this.channelId;}
		public void setChannelId(int value) {this.channelId = value;}
	}
	
	/*
	 * 
	 */
	
	public static class CFileUploadRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = -3301374829544000443L;
		private final boolean channelUpload;
		private String channel;
		private String receiver; 
		private final String fileName;
		private final long fileSize;

		public CFileUploadRequest(boolean channelUpload, String username, String password, String channelOrReceiver, String fileName, long fileSize)
		{
			super(username, password);
			this.channelUpload = channelUpload;
			this.channel = channelUpload ? channelOrReceiver : null;
			this.receiver = channelUpload ? null : channelOrReceiver;
			this.fileName = fileName;
			this.fileSize = fileSize;
		}

		public boolean isChannelUpload() {return this.channelUpload;}
		public String getChannel() {return this.channel;}
		public String getReceiver() {return this.receiver;}
		public String getFileName() {return this.fileName;}
		public long getFileSize() {return this.fileSize;}
	}
	
	public static class CFileDownloadRequest extends CClassifiedRequest
	{
		private static final long serialVersionUID = -3301374829544000443L;
		private String key;
		private String fileName;
		private long fileSize;

		public CFileDownloadRequest(String username, String password, String key, String fileName)
		{
			super(username, password);
			this.key = key;
			this.fileName = fileName;
			
			 /* Supposed to be changed from the server (the client receives back all of his requests, 
			  * so he can check the remote file size, but only if this request has been accepted*/
			this.fileSize = -1L;
		}

		public String getKey() {return this.key;}
		public void setKey(String value) {this.key = value;}
		public String getFileName() {return this.fileName;}
		public void setFileName(String value) {this.fileName = value;}
		public void setFileSize(long value) {this.fileSize = value;}
		public long getFileSize() {return this.fileSize;}
	}
}