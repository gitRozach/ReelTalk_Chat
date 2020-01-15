package network.ssl.communication.events;

public class RequestDeniedEvent extends ClientEvent {
	private static final long serialVersionUID = -2555962608697556988L;
	private Class<?> deniedRequestClass;
	
	public RequestDeniedEvent(Class<?> requestClass) {
		super();
		this.deniedRequestClass = requestClass;
	}

	public void setDeniedRequestClass(Class<?> value) {
		this.deniedRequestClass = value;
	}
	
	public Class<?> getDeniedRequestClass(){
		return deniedRequestClass;
	}
}
