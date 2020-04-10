package handler.events;

import com.google.protobuf.GeneratedMessageV3;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class ObjectEvent<T> extends Event {
	private static final long serialVersionUID = 5975228043408569550L;	
	public static EventType<ObjectEvent<?>> ANY = new EventType<ObjectEvent<?>>("ANY");
	public static EventType<ObjectEvent<String>> STRING = new EventType<ObjectEvent<String>>("STRING");
	public static EventType<ObjectEvent<GeneratedMessageV3>> PROTOBUF_MESSAGE = new EventType<ObjectEvent<GeneratedMessageV3>>("PROTOBUF_MESSAGE");

	protected T attachedObject;
	
	public ObjectEvent(EventType<? extends Event> type) {
		this(type, null);
	}
	
	public ObjectEvent(EventType<? extends Event> type, T attachment) {
		super(type);
		attachObject(attachment);
	}
	
	public ObjectEvent(Object source, EventTarget target, EventType<? extends Event> type) {
		this(source, target, type, null);
	}
	
	public ObjectEvent(Object source, EventTarget target, EventType<? extends Event> type, T attachment) {
		super(source, target, type);
		attachObject(attachment);
	}

	public boolean hasAttachedObject() {
		return getAttachedObject() != null;
	}
	
	public void attachObject(T attachment) {
		attachedObject = attachment;
	}
	
	public T getAttachedObject() {
		return attachedObject;
	}
}
