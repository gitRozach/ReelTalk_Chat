package network.peer.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import utils.LoopingRunnable;
import utils.ThreadUtils;

public class SecuredProtobufClient extends SecuredProtobufPeer {
	protected String remoteAddress;
	protected int remotePort;
	
	protected SSLEngine engine;
    protected SocketChannel socketChannel;

    public SecuredProtobufClient(String protocol, String hostAddress, int hostPort) throws Exception  {
    	super(protocol);
    	initClientSSLContext();
    	initBuffers();
        
        remoteAddress = hostAddress;
    	remotePort = hostPort;
        engine = context.createSSLEngine(remoteAddress, remotePort);
        engine.setUseClientMode(true);
        protobufReader = new ClientProtobufReader(1L);
        protobufWriter = new ClientProtobufWriter(1L);
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
	    		ioExecutor.submit(protobufReader);
	        	ioExecutor.submit(protobufWriter);
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
	
	public boolean hasReceivableBytes() {
		return peekReceptionBytes() != null;
	}
    
    public ProtobufMessage peekReceptionBytes() {
    	if(receivedMessages.isEmpty())
    		return null;
    	return receivedMessages.get(0);
    }
    
    public ProtobufMessage pollReceptionBytes() {
    	if(receivedMessages.isEmpty())
    		return null;
    	return receivedMessages.remove(0);
    }
    
    public boolean hasOrderedBytes() {
		return peekOrderedBytes() != null;
	}

	public ProtobufMessage peekOrderedBytes() {
		return orderedMessages.peek();
	}

	public ProtobufMessage pollOrderedBytes() {
		return orderedMessages.poll();
	}
    
    public void sendMessage(GeneratedMessageV3 message) {
    	if(message == null)
    		return;
    	orderedMessages.offer(new ProtobufMessage(getChannel(), message));
    	ThreadUtils.sleep(1L);
    }
    
    @Override
	public void close() throws IOException {
    	disconnect();
	}

    public void disconnect() throws IOException {
        logger.fine("About to close connection with the server...");
        protobufReader.stop();
        protobufWriter.stop();
        closeConnection(socketChannel, engine);
        asyncTaskExecutor.shutdown();
        ioExecutor.shutdown();
        logger.info("Connection to the host closed.");
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