package network.ssl.client.callbacks;

import java.util.logging.Logger;

import network.ssl.communication.ByteMessage;

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
	public void messageSent(ByteMessage message) {
		logger.info("Message (" + message.getMessageBytes().length + " Bytes) sent.");		
	}

	@Override
	public void messageReceived(ByteMessage message) {
		logger.info("Message (" + message.getMessageBytes().length + " Bytes) received.");
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
