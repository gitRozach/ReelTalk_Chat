package network.ssl.communication.requests;

public class ChannelJoinRequest extends ClassifiedRequest {
	private static final long serialVersionUID = -8400911047840949173L;
	private int channelId;
	
	public ChannelJoinRequest(String username, String password, int requestedChannelId) {
		super(username, password);
		this.channelId = requestedChannelId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

}
