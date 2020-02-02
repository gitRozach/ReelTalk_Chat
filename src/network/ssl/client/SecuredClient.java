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
import javax.net.ssl.SSLSession;

import network.ssl.SecuredPeer;
import network.ssl.client.handler.ByteReceiver;

public class SecuredClient extends SecuredPeer implements ByteReceiver {
	protected String remoteAddress;
	protected int port;
	protected volatile boolean connected;
    protected SSLEngine engine;
    protected SocketChannel socketChannel;
    
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    
    protected Queue<byte[]> receptionQueue;
    protected Queue<byte[]> sendingQueue;
    
    protected ClientSender sender;
    protected ClientReceiver receiver;
    protected ClientMessageHandler handler;


    /**
     * Initiates the engine to run as a client using peer information, and allocates space for the
     * buffers that will be used by the engine.
     *
     * @param protocol The SSL/TLS protocol to be used. Java 1.6 will only run with up to TLSv1 protocol. Java 1.7 or higher also supports TLSv1.1 and TLSv1.2 protocols.
     * @param remoteAddress The IP address of the peer.
     * @param port The peer's port that will be used.
     * @throws Exception
     */
    public SecuredClient(String protocol, String remoteAddress, int port) throws Exception  {
    	this.remoteAddress = remoteAddress;
    	this.port = port;
    	this.connected = false;
    	
        SSLContext context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("src/resources/client.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());
        this.engine = context.createSSLEngine(remoteAddress, port);
        this.engine.setUseClientMode(true);

        SSLSession session = engine.getSession();
        this.myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        this.myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        this.peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
        this.peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        
        this.sender = new ClientSender(25L);
        this.receiver = new ClientReceiver(25L);
        this.handler = new ClientMessageHandler(25L);
        
        this.receptionQueue = new ConcurrentLinkedQueue<byte[]>();
        this.sendingQueue = new ConcurrentLinkedQueue<byte[]>();       
    }

    /**
     * Opens a socket channel to communicate with the configured server and tries to complete the handshake protocol.
     *
     * @return True if client established a connection with the server, false otherwise.
     * @throws Exception
     */
    public boolean connect() throws IOException {
    	socketChannel = SocketChannel.open();
    	socketChannel.configureBlocking(false);
    	socketChannel.connect(new InetSocketAddress(remoteAddress, port));
    	while (!socketChannel.finishConnect()) 
    		System.out.println("Connecting...");
    		
    	engine.beginHandshake();
    	if(doHandshake(socketChannel, engine)) {
    		ioExecutor.submit(sender);
        	ioExecutor.submit(receiver);
        	ioExecutor.submit(handler);
        	setConnected(true);
        	return true;
    	}
    	return false;
    }
    
    //TODO
    public boolean reconnect() {
    	return false;
    }

    /**
     * Public method to send a message to the server.
     *
     * @param message - message to be sent to the server.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    protected void write(byte[] message) throws IOException {
        write(socketChannel, engine, message);
    }

    /**
     * Implements the write method that sends a message to the server the client is connected to,
     * but should not be called by the user, since socket channel and engine are inner class' variables.
     * {@link NioSslClient#write(String)} should be called instead.
     *
     * @param message - message to be sent to the server.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    @Override
    protected void write(SocketChannel socketChannel, SSLEngine engine, byte[] message) throws IOException {
        log.fine("About to write to the server...");
        synchronized (writeLock) {
            putDataIntoBufferAndFlip(message);
            while (myAppData.hasRemaining())
    			if(handleWrapResult(socketChannel, engine, encryptBufferedData(engine)))
    				writeEncryptedData(socketChannel);
            try {
				Thread.sleep(50L);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
    }

    /**
     * Public method to try to read from the server.
     *
     * @throws Exception
     */
    protected byte[] read() throws Exception {
        return read(socketChannel, engine);
    }

    /**
     * Will wait for response from the remote peer, until it actually gets something.
     * Uses {@link SocketChannel#read(ByteBuffer)}, which is non-blocking, and if
     * it gets nothing from the peer, waits for {@code waitToReadMillis} and tries again.
     * <p/>
     * Just like {@link NioSslClient#read(SocketChannel, SSLEngine)} it uses inner class' socket channel
     * and engine and should not be used by the client. {@link NioSslClient#read()} should be called instead.
     * 
     * @param message - message to be sent to the server.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @throws IOException 
     * @throws Exception
     */
    
    @Override
    protected byte[] read(SocketChannel socketChannel, SSLEngine engine) throws IOException {
    	synchronized (readLock) {
    		if(!socketChannel.isOpen())
        		return null;
        	log.fine("About to read from the server...");
        	int readBytes = 0;
            if ((readBytes = readChannelBytes(socketChannel)) > 0)
            	return retrieveDecryptedBytes(socketChannel, engine);
            if(readBytes == -1)
            	disconnect();
            try {
				Thread.sleep(50L);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
            return null;
		}
    }
    
    @Override
    protected void closeConnection(SocketChannel channel, SSLEngine engine) throws IOException {
    	super.closeConnection(channel, engine);
    	setConnected(false);
    }
    
    public void enableMessageSender(boolean value) {
		if (sender.isRunning() == value)
			return;
		sender.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(sender);
	}

	public void enableMessageReceiver(boolean value) {
		if (receiver.isRunning() == value)
			return;
		receiver.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(receiver);
	}

	public void enableMessageHandler(boolean value) {
		if (handler.isRunning() == value)
			return;
		handler.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(handler);
	}
	
	public boolean hasReadableBytes() {
		return receptionQueue.peek() != null;
	}
    
    public byte[] peekBytes() {
    	return receptionQueue.peek();
    }
    
    public byte[] readBytes() {
    	return receptionQueue.poll();
    }
    
    public void sendBytes(byte[] byteMessage) {
    	sendingQueue.offer(byteMessage);
    	try {
			Thread.sleep(1L);
		} 
    	catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    @Override
	public void onBytesReceived(byte[] bytes) {
		System.out.println("Client received: " + new String(bytes));
	}
    
    @Override
	public void close() throws IOException {
    	disconnect();
	}

    public void disconnect() throws IOException {
        log.fine("About to close connection with the server...");
        sender.stop();
        receiver.stop();
        handler.stop();
        closeConnection(socketChannel, engine);
        asyncTaskExecutor.shutdown();
        ioExecutor.shutdown();
        log.info("Connection to the host closed.");
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

	protected class ClientReceiver implements Runnable {
    	private volatile boolean running;
    	private long loopDelayMillis;
    	
    	public ClientReceiver() {
    		this(100L);
    	}
    	
    	public ClientReceiver(long loopingDelayMillis) {
    		this.setRunning(true);
    		this.loopDelayMillis = loopingDelayMillis;
    	}
    	
		@Override
		public void run() {
			log.info("ClientReceiver startet...");
	    	
	        while (isRunning()) {
	        	byte[] receptionBuffer = null;
				try {
					receptionBuffer = read();
					if(receptionBuffer != null) {
						receptionQueue.offer(receptionBuffer);
						System.out.println("Read! Length: " + receptionBuffer.length);
					}
				}
				catch(Exception e) {
					log.severe(e.toString());
					continue;
				}
				
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
	    
    protected class ClientSender implements Runnable {
    	private volatile boolean running;
    	private long loopDelayMillis;
    	
    	public ClientSender() {
    		this(100L);
    	}
    	
    	public ClientSender(long loopingDelayMillis) {
    		this.setRunning(true);
    		this.loopDelayMillis = loopingDelayMillis;
    	}
    	
		@Override
		public void run() {
			log.info("ClientSender startet...");
			
			while(isRunning()) {
				byte[] sendingBuffer = null;
				if((sendingBuffer = sendingQueue.peek()) != null) {
					try {
						write(sendingBuffer);
		    		}
		    		catch(Exception e) {
		    			log.severe(e.toString());
		    			continue;
		    		}
					sendingQueue.poll();
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {}
			}
			log.info("ClientSender beendet.");
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
    
    protected class ClientMessageHandler implements Runnable {
    	private volatile boolean running;
    	private long loopDelayMillis;
    	
    	public ClientMessageHandler() {
    		this(25L);
    	}
    	
    	public ClientMessageHandler(long loopingDelayMillis) {
    		this.setRunning(true);
    		this.loopDelayMillis = loopingDelayMillis;
    	}
    	
		@Override
		public void run() {
			log.info("ClientMessageHandler startet...");
			
			while(isRunning()) {
				try {
					byte[] message = receptionQueue.poll();
					if(message != null)
						onBytesReceived(message);
				}
				catch(Exception io) {
					io.printStackTrace();
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {}
			}
			log.info("ClientMessageHandler beendet.");
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