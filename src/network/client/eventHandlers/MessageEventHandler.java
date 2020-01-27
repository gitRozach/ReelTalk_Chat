package network.client.eventHandlers;

public class MessageEventHandler extends ObjectEventHandler {
	@Override
	public void handle(ObjectEvent event) {
		handleObject(event.getAttachedObject());
	}

	@Override
	public void handleObject(Object obj) {
		
	}
}
