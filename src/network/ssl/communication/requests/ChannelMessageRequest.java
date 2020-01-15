package network.ssl.communication.requests;

public class ChannelMessageRequest extends ClassifiedRequest {
	private static final long serialVersionUID = 3886899320270158016L;
	private int channelId;
	private String message;
	
	public ChannelMessageRequest() {
		this("", "", -1, "");
	}
	
	public ChannelMessageRequest(String username, String password, int channelId, String message) {
		super(username, password);
		this.channelId = channelId;
		this.message = message;
	}

	public int getChannel() {
		return channelId;
	}

	public void setChannel(int channelId) {
		this.channelId = channelId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
