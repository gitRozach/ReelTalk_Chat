package network.client.eventHandlers;

import javafx.event.EventHandler;

public abstract class ObjectEventHandler implements EventHandler<ObjectEvent>{
	public abstract void handleObject(Object obj);
}
