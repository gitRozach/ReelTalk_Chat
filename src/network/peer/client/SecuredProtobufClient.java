package network.peer.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.google.protobuf.GeneratedMessageV3;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import network.peer.client.callbacks.LoggerCallback;
import utils.LoopingRunnable;
import utils.Utils;

public class SecuredProtobufClient extends SecuredProtobufPeer {
	protected String remoteAddress;
	protected int remotePort;
	protected String encryptionProtocol;
	protected volatile boolean connected;
	
	protected SSLEngine engine;
    protected SocketChannel socketChannel;
    protected Queue<ProtobufMessage> orderedBytes;
    
    protected ClientProtobufWriter sender;
    protected ClientProtobufReader receiver;

    public SecuredProtobufClient(String protocol, String hostAddress, int hostPort) throws Exception  {
    	super();
    	encryptionProtocol = protocol;
    	remoteAddress = hostAddress;
    	remotePort = hostPort;
    	connected = false;
    	
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("src/resources/client.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());
        engine = context.createSSLEngine(remoteAddress, remotePort);
        engine.setUseClientMode(true);
        
        myApplicationBuffer = ByteBuffer.allocate(engine.getSession().getApplicationBufferSize());
        myNetworkBuffer = ByteBuffer.allocate(engine.getSession().getPacketBufferSize());
        peerApplicationBuffer = ByteBuffer.allocate(engine.getSession().getApplicationBufferSize());
        peerNetworkBuffer = ByteBuffer.allocate(engine.getSession().getPacketBufferSize());
        
        setPeerCallback(new LoggerCallback(logger));
        
        orderedBytes = new ConcurrentLinkedQueue<ProtobufMessage>();
        sender = new ClientProtobufWriter(1L);
        receiver = new ClientProtobufReader(1L);
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
	    		ioExecutor.submit(sender);
	        	ioExecutor.submit(receiver);
	        	setConnected(true);
	        	return true;
	    	}
	    	return false;
    	}
    	catch(IOException io) {
    		logger.severe(io.toString());
    		return false;
    	}
    }
    
    //TODO
    public boolean reconnect() {
    	return false;
    }

    protected int write(GeneratedMessageV3 message) throws IOException {
        return write(socketChannel, engine, message);
    }

    protected GeneratedMessageV3 read() throws Exception {
        return read(socketChannel, engine);
    }
    
    @Override
    protected boolean closeConnection(SocketChannel channel, SSLEngine engine) {
    	boolean closeResult = super.closeConnection(channel, engine);
    	setConnected(false);
    	return closeResult;
    }
    
    public void enableByteSender(boolean value) {
		if (sender.isRunning() == value)
			return;
		sender.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(sender);
	}

	public void enableByteReceiver(boolean value) {
		if (receiver.isRunning() == value)
			return;
		receiver.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(receiver);
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
		return orderedBytes.peek();
	}

	public ProtobufMessage pollOrderedBytes() {
		return orderedBytes.poll();
	}
    
    public void sendMessage(GeneratedMessageV3 message) {
    	if(message == null)
    		return;
    	orderedBytes.offer(new ProtobufMessage(getChannel(), message));
    	Utils.sleep(1L);
    }
    
    @Override
	public void close() throws IOException {
    	disconnect();
	}

    public void disconnect() throws IOException {
        logger.fine("About to close connection with the server...");
        sender.stop();
        receiver.stop();
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
				Utils.sleep(loopDelayMillis);
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
				Utils.sleep(loopDelayMillis);
			}
			logger.info("ClientProtobufWriter beendet.");
		}
    }
}