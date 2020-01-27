package network.client.eventHandlers;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import network.ssl.communication.MessagePacket;

public class MessageEvent extends Event {
	private static final long serialVersionUID = -524532712653748486L;
	protected MessagePacket message;	
	
	public MessageEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	public MessageEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
		super(source, target, eventType);
	}
	
	public void setMessage(MessagePacket messagePacket) {
		message = messagePacket;
	}
	
	public MessagePacket getMessage() {
		return message;
	}

}
