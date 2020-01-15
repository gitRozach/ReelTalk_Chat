package network.ssl.communication.requests;

import network.ssl.communication.MessagePacket;

public abstract class ClassifiedRequest extends MessagePacket {
	private static final long serialVersionUID = -2235717894635566585L;
	private String username;
	private String password;

	public ClassifiedRequest() {
		this("", "");
	}

	public ClassifiedRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String value) {
		this.username = value;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String value) {
		this.password = value;
	}
}
