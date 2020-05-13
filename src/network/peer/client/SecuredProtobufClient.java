package network.peer.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLEngine;

import com.google.protobuf.Message;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import network.peer.callbacks.LoggerCallback;
import utils.LoopingRunnable;
import utils.ThreadUtils;

public class SecuredProtobufClient extends SecuredProtobufPeer {
	protected String remoteAddress;
	protected int remotePort;
	protected volatile boolean connected;
	
	protected SSLEngine engine;
    protected SocketChannel socketChannel;
    protected Queue<ProtobufMessage> orderedBytes;
    
    protected LoopingRunnable protobufSender;
    protected LoopingRunnable protobufReceiver;

    public SecuredProtobufClient(String protocol, String hostAddress, int hostPort) throws Exception  {
    	super(protocol);
    	initClientSSLContext();
    	initBuffers();
    	
        setPeerCallback(new LoggerCallback(logger));
        
        remoteAddress = hostAddress;
    	remotePort = hostPort;
    	connected = false;
        engine = context.createSSLEngine(remoteAddress, remotePort);
        engine.setUseClientMode(true);
        
        orderedBytes = new ConcurrentLinkedQueue<ProtobufMessage>();
        protobufSender = new ClientProtobufWriter(10L);
        protobufReceiver = new ClientProtobufReader(10L);
    }

    public boolean connect() {
    	try {
	    	socketChannel = SocketChannel.open();
	    	socketChannel.configureBlocking(false);
	    	socketChannel.connect(new InetSocketAddress(remoteAddress, remotePort));
	    	while (!socketChannel.finishConnect()) 
	    		logger.info("Client connecting...");
	    		
	    	engine.beginHandshake();
	    	if(doHandshake(socketChannel, engine)) {
	    		ioExecutor.submit(protobufSender);
	        	ioExecutor.submit(protobufReceiver);
	        	setConnected(true);
	        	return true;
	    	}
	    	return false;
    	}
    	catch(Exception io) {
    		logger.severe(io.toString());
    		return false;
    	}
    }
    
    //TODO
    public boolean reconnect() {
    	return false;
    }

    protected int write(Message message) throws IOException {
        return write(socketChannel, engine, message);
    }

    protected Message read() throws Exception {
        return read(socketChannel, engine);
    }
    
    @Override
    protected boolean closeConnection(SocketChannel channel, SSLEngine engine) {
    	boolean closeResult = super.closeConnection(channel, engine);
    	setConnected(false);
    	return closeResult;
    }
    
    public void enableByteSender(boolean value) {
		if (protobufSender.isRunning() == value)
			return;
		protobufSender.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufSender);
	}

	public void enableByteReceiver(boolean value) {
		if (protobufReceiver.isRunning() == value)
			return;
		protobufReceiver.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufReceiver);
	}
	
	public boolean hasReceivableBytes() {
		return peekReceptionBytes() != null;
	}
    
    public ProtobufMessage peekReceptionBytes() {
    	return receivedMessages.isEmpty() ? null : receivedMessages.get(0);
    }
    
    public ProtobufMessage pollReceptionBytes() {
    	return receivedMessages.isEmpty() ? null : receivedMessages.remove(0);
    }
    
    public boolean hasOrderedBytes() {
		return peekOrderedBytes() != null;
	}

	public ProtobufMessage peekOrderedBytes() {
		return orderedBytes.peek();
	}

	public ProtobufMessage pollOrderedBytes() {
		return orderedBytes.poll();
	}
    
    public void sendMessage(Message message) {
    	if(message != null)
    		orderedBytes.offer(new ProtobufMessage(getChannel(), message));
    	ThreadUtils.sleep(100L);
    }
    
    @Override
	public void close() throws IOException {
    	disconnect();
	}

    public void disconnect() throws IOException {
        logger.fine("About to close connection with the server...");
        protobufSender.stop();
        protobufReceiver.stop();
        closeConnection(socketChannel, engine);
        asyncTaskExecutor.shutdown();
        ioExecutor.shutdown();
        logger.info("Connection to the host closed.");
    }
    
    private void setConnected(boolean value) {
    	connected = value;
    }
    
    public boolean isConnected() {
    	return connected;
    }
    
    public SocketChannel getChannel() {
    	return socketChannel;
    }

	protected class ClientProtobufReader extends LoopingRunnable {
		public ClientProtobufReader(long loopingDelay) {
			super(loopingDelay);
		}	
		@Override
		public void run() {
			super.run();
			logger.info("ClientProtobufReader startet...");
	        while (isRunning()) {
				try {
					read();
				}
				catch(Exception e) {
					logger.severe(e.toString());
					continue;
				}
				ThreadUtils.sleep(loopDelayMillis);
			}
	        logger.info("ClientProtobufReader beendet.");
		}
	}
	    
    protected class ClientProtobufWriter extends LoopingRunnable {
    	public ClientProtobufWriter(long loopingDelay) {
			super(loopingDelay);
		}
    	@Override
		public void run() {
    		super.run();
			logger.info("ClientProtobufWriter startet...");
			while(isRunning()) {
				ProtobufMessage sendingMessage = null;
				if((sendingMessage = peekOrderedBytes()) != null) {
					try {						
						if(write(sendingMessage.getMessage()) > 0)
							pollOrderedBytes();
		    		}
		    		catch(Exception e) {
		    			logger.severe(e.toString());
		    			continue;
		    		}
				}
				ThreadUtils.sleep(loopDelayMillis);
			}
			logger.info("ClientProtobufWriter beendet.");
		}
    }
}