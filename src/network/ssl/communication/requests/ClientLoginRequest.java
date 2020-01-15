package network.ssl.communication.requests;

public class ClientLoginRequest extends ClassifiedRequest {
	private static final long serialVersionUID = 3974086064977183075L;
	
	public ClientLoginRequest(String username, String password) {
		super(username, password);
	}
}
