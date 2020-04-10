package network.peer.callbacks;

import java.util.logging.Logger;

import network.messages.ProtobufMessage;

public class LoggerCallback implements PeerCallback {
	protected Logger logger;
	
	public LoggerCallback(Logger callbackLogger) {
		logger = callbackLogger;
	}
	
	@Override
	public void connectionLost(Throwable throwable) {
		logger.info(throwable.getMessage());
	}

	@Override
	public void messageSent(ProtobufMessage message) {
		logger.info("Message (" + message.getMessage().getSerializedSize() + " Bytes) sent.");		
	}

	@Override
	public void messageReceived(ProtobufMessage message) {
		logger.info("Message (" + message.getMessage().getSerializedSize() + " Bytes) received.");
	}
	
	@Override
	public void messageTimedOut(ProtobufMessage message) {
		logger.info("Message (" + message.getMessage().getSerializedSize() + " Bytes) timed out.");
		
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
