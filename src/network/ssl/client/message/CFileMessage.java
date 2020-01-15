package network.ssl.client.message;

public abstract class CFileMessage extends CMessage
{
	private static final long serialVersionUID = -3839564699906898825L;

	private final String filePath;
	private final long fileSize;
	private final String key;

	public CFileMessage(String sender, String message, String key, String filePath, long fileSize)
	{
		super(sender, message);
		this.key = key;
		this.filePath = filePath;
		this.fileSize = fileSize;
	}

	public static CChannelFileMessage newChannelFileMessage(String sender, String channel, String message, String key, String filePath, long fileSize)
	{
		return new CChannelFileMessage(sender, channel, message, key, filePath, fileSize);
	}

	public static CPrivateFileMessage newPrivateFileMessage(String sender, String receiver, String message, String key, String filePath, long fileSize)
	{
		return new CPrivateFileMessage(sender, receiver, message, key, filePath, fileSize);
	}

	public String getKey() {return this.key;}
	public String getFilePath() {return this.filePath;}
	public long getFileSize() {return this.fileSize;}
	public String getFileType() {return this.filePath.substring(this.filePath.lastIndexOf("."));}

	/*
	 *
	 */

	public static class CChannelFileMessage extends CFileMessage
	{
		private static final long serialVersionUID = 6463129847069013996L;
		private final String channel;

		public CChannelFileMessage(String sender, String channel, String message, String key, String filePath, long fileSize)
		{
			super(sender, message, key, filePath, fileSize);
			this.channel = channel;
		}

		public String getChannel() {return this.channel;}
	}

	/*
	 *
	 */

	public static class CPrivateFileMessage extends CFileMessage
	{
		private static final long serialVersionUID = -6585764947845894672L;
		private final String receiver;

		public CPrivateFileMessage(String sender, String receiver, String message, String key, String filePath, long fileSize)
		{
			super(sender, message, key, filePath, fileSize);
			this.receiver = receiver;
		}

		public String getReceiver() {return this.receiver;}
	}

}
