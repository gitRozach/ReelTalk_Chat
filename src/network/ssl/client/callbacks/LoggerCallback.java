package network.ssl.client.callbacks;

import java.util.logging.Logger;

import network.ssl.communication.ProtobufMessage;

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

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
