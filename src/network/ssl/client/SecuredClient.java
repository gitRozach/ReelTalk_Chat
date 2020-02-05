package network.ssl.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import network.ssl.SecuredPeer;
import network.ssl.client.utils.CUtils;
import network.ssl.communication.ByteMessage;

public class SecuredClient extends SecuredPeer implements SecuredClientByteReceiver , SecuredClientByteSender {
	protected String remoteAddress;
	protected int port;
	protected volatile boolean connected;
	protected volatile boolean bufferReceivedBytes;
	protected volatile boolean bufferSentBytes;
    protected volatile boolean receptionHandlerEnabled;
    protected volatile boolean sendingHandlerEnabled;
	protected SSLEngine engine;
    protected SocketChannel socketChannel;
    
    protected List<ByteMessage> receivedBytes;
    protected List<ByteMessage> sentBytes;
    protected Queue<ByteMessage> orderedBytes;
    
    protected ClientByteSender sender;
    protected ClientByteReceiver receiver;

    public SecuredClient(String protocol, String hostAddress, int hostPort) throws Exception  {
    	remoteAddress = hostAddress;
    	port = hostPort;
    	connected = false;
    	bufferReceivedBytes = false;
    	bufferSentBytes = false;
    	receptionHandlerEnabled = true;
    	sendingHandlerEnabled = true;
    	
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("src/resources/client.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());
        engine = context.createSSLEngine(remoteAddress, port);
        engine.setUseClientMode(true);

        myApplicationBuffer = ByteBuffer.allocate(engine.getSession().getApplicationBufferSize());
        myNetworkBuffer = ByteBuffer.allocate(engine.getSession().getPacketBufferSize());
        peerApplicationBuffer = ByteBuffer.allocate(engine.getSession().getApplicationBufferSize());
        peerNetworkBuffer = ByteBuffer.allocate(engine.getSession().getPacketBufferSize());
        
        sender = new ClientByteSender(1L);
        receiver = new ClientByteReceiver(1L);
        
        receivedBytes = Collections.synchronizedList(new ArrayList<ByteMessage>());
        sentBytes = Collections.synchronizedList(new ArrayList<ByteMessage>());
        orderedBytes = new ConcurrentLinkedQueue<ByteMessage>();       
    }

    public boolean connect() throws IOException {
    	socketChannel = SocketChannel.open();
    	socketChannel.configureBlocking(false);
    	socketChannel.connect(new InetSocketAddress(remoteAddress, port));
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
    
    public void sendBytes(byte[] messageBytes) {
    	orderedBytes.offer(new ByteMessage(messageBytes));
    	CUtils.sleep(1L);
    }
    
    @Override
	public void onBytesReceived(byte[] bytes) {
		logger.info("Client received: " + new String(bytes));
	}
    
    @Override
    public void onBytesSent(byte[] bytes) {
    	logger.info("Client sent: " + new String(bytes));
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
    
    public boolean isBufferingReceivedBytes() {
    	return bufferReceivedBytes;
    }
    
    public void setBufferingReceivedBytes(boolean value) {
    	bufferReceivedBytes = value;
    }
    
    public boolean isBufferingSentBytes() {
    	return bufferSentBytes;
    }
    
    public void setBufferingSentBytes(boolean value) {
    	bufferSentBytes = value;
    }
    
    public boolean isByteReceptionHandlerEnabled() {
		return receptionHandlerEnabled;
	}
	
	public boolean isByteSendingHandlerEnabled() {
		return sendingHandlerEnabled;
	}
	
	public void setByteReceptionHandlerEnabled(boolean value) {
		receptionHandlerEnabled = value;
	}
	
	public void setByteSendingHandlerEnabled(boolean value) {
		sendingHandlerEnabled = value;
	}
    
    public SocketChannel getChannel() {
    	return socketChannel;
    }

	protected class ClientByteReceiver implements Runnable {
    	private volatile boolean running;
    	private long loopDelayMillis;
    	
    	public ClientByteReceiver() {
    		this(100L);
    	}
    	
    	public ClientByteReceiver(long delayMillis) {
    		setRunning(true);
    		loopDelayMillis = delayMillis;
    	}
    	
		@Override
		public void run() {
			logger.info("ClientReceiver startet...");
	        while (isRunning()) {
				try {
					byte[] receptionBuffer = read();
					if(receptionBuffer != null) {
						if(isByteReceptionHandlerEnabled())
							onBytesReceived(receptionBuffer);
						if(isBufferingReceivedBytes())
							receivedBytes.add(new ByteMessage(receptionBuffer));
					}
				}
				catch(Exception e) {
					logger.severe(e.toString());
					continue;
				}
				CUtils.sleep(loopDelayMillis);
			}
	        logger.info("ClientReceiver beendet.");
		}
		
		public void stop() {
			setRunning(false);
		}
		
		public long getLoopDelayMillis() {
			return loopDelayMillis;
		}

		public boolean isRunning() {
			return running;
		}

		private void setRunning(boolean value) {
			running = value;
		}
	}
	    
    protected class ClientByteSender implements Runnable {
    	private volatile boolean running;
    	private long loopDelayMillis;
    	
    	public ClientByteSender() {
    		this(100L);
    	}
    	
    	public ClientByteSender(long delayMillis) {
    		setRunning(true);
    		loopDelayMillis = delayMillis;
    	}
    	
		@Override
		public void run() {
			logger.info("ClientSender startet...");
			
			while(isRunning()) {
				ByteMessage sendingMessage = null;
				if((sendingMessage = orderedBytes.peek()) != null) {
					try {
						write(sendingMessage.getMessageBytes());
						if(isByteSendingHandlerEnabled())
							onBytesSent(sendingMessage.getMessageBytes());
						if(isBufferingSentBytes())
							sentBytes.add(sendingMessage);
						orderedBytes.poll();
		    		}
		    		catch(Exception e) {
		    			logger.severe(e.toString());
		    			continue;
		    		}
				}
				CUtils.sleep(loopDelayMillis);
			}
			logger.info("ClientSender beendet.");
		}
		
		public void stop() {
			setRunning(false);
		}
		
		public long getLoopDelayMillis() {
			return loopDelayMillis;
		}

		public boolean isRunning() {
			return running;
		}

		private void setRunning(boolean value) {
			running = value;
		}
    }
}