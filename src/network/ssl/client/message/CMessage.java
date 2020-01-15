package network.ssl.client.message;

public abstract class CMessage extends CPacket
{
	private static final long serialVersionUID = 8411588841439311310L;
	private final String sender;
	private final String message;

	public CMessage(String sender, String message)
	{
		this.sender = sender;
		this.message = message;
	}

	public static CChannelMessage newChannelMessage(String sender, String channel, String message)
	{
		return new CChannelMessage(sender, channel, message);
	}

	public static CPrivateMessage newPrivateMessage(String sender, String receiver, String message)
	{
		return new CPrivateMessage(sender, receiver, message);
	}

	public String getSender() {return this.sender;}
	public String getMessage() {return this.message;}

	/*
	 *
	 */

	public static class CChannelMessage extends CMessage
	{
		private static final long serialVersionUID = 1181543996590537631L;
		private String channelName;
		private int channelId;

		public CChannelMessage(String sender, String channelName, String message)
		{
			this(sender, -1, channelName, message);
		}
		
		public CChannelMessage(String sender, int channelId, String channelName, String message)
		{
			super(sender, message);
			this.channelId = channelId;
			this.channelName = channelName;
		}

		@Override public String toString()
		{
			return "[" + this.getSender() + " : " + channelName + " : " + this.getMessage() + "]";
		}

		public String getChannelName() {return this.channelName;}
		public int getChannelId() {return this.channelId;}
	}

	/*
	 *
	 */

	public static class CPrivateMessage extends CMessage
	{
		private static final long serialVersionUID = -6741992521843551421L;
		private final String receiver;

		public CPrivateMessage(String sender, String receiver, String message)
		{
			super(sender, message);
			this.receiver = receiver;
		}

		public String getReceiver() {return this.receiver;}
	}
}