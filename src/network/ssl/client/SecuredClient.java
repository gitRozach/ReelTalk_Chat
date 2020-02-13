package network.ssl.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import network.ssl.SecuredPeer;
import network.ssl.communication.ByteMessage;
import network.threads.LoopingRunnable;
import utils.Utils;

public class SecuredClient extends SecuredPeer {
	protected String remoteAddress;
	protected int remotePort;
	protected volatile boolean connected;
	
	protected SSLEngine engine;
    protected SocketChannel socketChannel;
    protected Queue<ByteMessage> orderedBytes;
    
    protected ClientByteSender sender;
    protected ClientByteReceiver receiver;

    public SecuredClient(String protocol, String hostAddress, int hostPort) throws Exception  {
    	super();
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
        
        orderedBytes = new ConcurrentLinkedQueue<ByteMessage>();
        sender = new ClientByteSender(1L);
        receiver = new ClientByteReceiver(1L);
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

    protected int write(byte[] message) throws IOException {
        return write(socketChannel, engine, message);
    }

    protected byte[] read() throws Exception {
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
    
    public ByteMessage peekReceptionBytes() {
    	if(receivedBytes.isEmpty())
    		return null;
    	return receivedBytes.get(0);
    }
    
    public ByteMessage pollReceptionBytes() {
    	if(receivedBytes.isEmpty())
    		return null;
    	return receivedBytes.remove(0);
    }
    
    public boolean hasOrderedBytes() {
		return peekOrderedBytes() != null;
	}

	public ByteMessage peekOrderedBytes() {
		return orderedBytes.peek();
	}

	public ByteMessage pollOrderedBytes() {
		return orderedBytes.poll();
	}
    
    public void sendBytes(byte[] messageBytes) {
    	orderedBytes.offer(new ByteMessage(messageBytes));
    	Utils.sleep(1L);
    }
    
    @Override
	public void onBytesReceived(ByteMessage byteMessage) {
		logger.info("Client received: " + new String(byteMessage.getMessageBytes()));
	}
    
    @Override
    public void onBytesSent(ByteMessage byteMessage) {
    	logger.info("Client sent: " + new String(byteMessage.getMessageBytes()));
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

	protected class ClientByteReceiver extends LoopingRunnable {
		public ClientByteReceiver(long loopingDelay) {
			super(loopingDelay);
		}	
		@Override
		public void run() {
			super.run();
			logger.info("ClientReceiver startet...");
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
	        logger.info("ClientReceiver beendet.");
		}
	}
	    
    protected class ClientByteSender extends LoopingRunnable {
    	public ClientByteSender(long loopingDelay) {
			super(loopingDelay);
		}
    	@Override
		public void run() {
    		super.run();
			logger.info("ClientSender startet...");
			while(isRunning()) {
				ByteMessage sendingMessage = null;
				if((sendingMessage = peekOrderedBytes()) != null) {
					try {
						if(write(sendingMessage.getMessageBytes()) > 0);
							pollOrderedBytes();
		    		}
		    		catch(Exception e) {
		    			logger.severe(e.toString());
		    			continue;
		    		}
				}
				Utils.sleep(loopDelayMillis);
			}
			logger.info("ClientSender beendet.");
		}
    }
}