package network.ssl.communication.requests;

public class PrivateMessageRequest extends ClassifiedRequest {
	private static final long serialVersionUID = -6744864552049955105L;
	private int receiverId;
	private String message;
	
	public PrivateMessageRequest() {
		this("", "", -1, "");
	}
	
	public PrivateMessageRequest(String username, String password, int receiverId, String message) {
		super(username, password);
		this.receiverId = receiverId;
		this.message = message;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
