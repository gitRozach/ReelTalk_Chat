package network.ssl.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import network.ssl.SecuredPeer;
import network.ssl.communication.ByteMessage;

public class SecuredServer extends SecuredPeer implements SecuredServerByteReceiver , SecuredServerByteSender {
	protected volatile boolean active;
	protected volatile boolean bufferReceivedBytes;
	protected volatile boolean bufferSentBytes;
    protected volatile boolean receptionHandlerEnabled;
    protected volatile boolean sendingHandlerEnabled;
	protected volatile int receptionCounter;

	protected ServerSocketChannel serverSocketChannel;
	protected SSLContext context;
	protected Selector selector;
	
	protected Queue<ByteMessage> orderedBytes;
	protected List<ByteMessage> receivedBytes;
	protected List<ByteMessage> sentBytes;
	
	protected ServerMessageReceiver receiver;
	protected ServerMessageSender sender;

	public SecuredServer(String protocol, String hostAddress, int hostPort) throws Exception {
		context = SSLContext.getInstance(protocol);
		context.init(createKeyManagers("src/resources/server.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());

		SSLSession dummySession = context.createSSLEngine().getSession();
		myApplicationBuffer = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		myNetworkBuffer = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		peerApplicationBuffer = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		peerNetworkBuffer = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		dummySession.invalidate();

		selector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(hostAddress, hostPort));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		orderedBytes = new ConcurrentLinkedQueue<ByteMessage>();
		receivedBytes = Collections.synchronizedList(new ArrayList<ByteMessage>());
        sentBytes = Collections.synchronizedList(new ArrayList<ByteMessage>());
       

		receiver = new ServerMessageReceiver(1L);
		sender = new ServerMessageSender(1L);

		active = true;
		bufferReceivedBytes = false;
		bufferSentBytes = false;
		receptionHandlerEnabled = true;
		sendingHandlerEnabled = true;
		receptionCounter = 0;
	}

	public void start() {
		ioExecutor.submit(receiver);
		ioExecutor.submit(sender);
		logger.info("Der Server ist gestartet.");
	}

	public void stop() throws IOException {
		logger.fine("Der Server wird jetzt heruntergefahren...");
		active = false;
		serverSocketChannel.socket().close();
		serverSocketChannel.close();
		asyncTaskExecutor.shutdown();
		sender.stop();
		receiver.stop();
		ioExecutor.shutdown();
		selector.wakeup();
		logger.info("Der Server wurde heruntergefahren.");
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

	private void accept(SelectionKey key) throws Exception {
		logger.fine("Ein neuer Client moechte sich verbinden...");

		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);
		while (!socketChannel.finishConnect())
			logger.info("Client connecting...");

		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.beginHandshake();

		if (doHandshake(socketChannel, engine)) {
			socketChannel.register(selector, SelectionKey.OP_READ, engine);
			logger.info("Ein neuer Client hat sich verbunden.");
		} else {
			closeConnection(socketChannel, engine);
			logger.info("Die Verbindung zum Client wurde aufgrund eines Handshake-Fehlers geschlossen.");
		}
	}

	public void kick(SocketChannel clientChannel) throws IOException {
		closeConnection(clientChannel, getEngineFrom(getLocalSocketChannel(clientChannel)));
	}

	public SSLEngine getEngineFrom(SocketChannel localClientChannel) throws IOException {
		Iterator<SelectionKey> keyIt = selector.keys().iterator();
		while (keyIt.hasNext()) {
			SelectionKey currentKey = keyIt.next();
			if (!(currentKey.channel() instanceof SocketChannel))
				continue;
			if (((SocketChannel) currentKey.channel()).getRemoteAddress().equals(localClientChannel.getLocalAddress()))
				return (SSLEngine) currentKey.attachment();
		}
		return null;
	}
	
	public SocketChannel getLocalSocketChannel(SocketChannel remoteClientChannel) throws IOException {
		Iterator<SelectionKey> keyIt = selector.keys().iterator();
		while (keyIt.hasNext()) {
			SelectionKey currentKey = keyIt.next();
			if (!(currentKey.channel() instanceof SocketChannel))
				continue;
			if (((SocketChannel) currentKey.channel()).getRemoteAddress().equals(remoteClientChannel.getLocalAddress()))
				return (SocketChannel) currentKey.channel();
		}
		return null;
	}
	
	public SelectionKey getLocalSelectionKey(SocketChannel localClientChannel) {
		return localClientChannel.keyFor(selector);
	}

	public boolean hasReceptionMessage() {
		return peekReceptionMessage() != null;
	}

	public ByteMessage peekReceptionMessage() {
		if(receivedBytes.isEmpty())
			return null;
		return receivedBytes.get(0);
	}

	public ByteMessage pollReceptionMessage() {
		if(receivedBytes.isEmpty())
			return null;
		return receivedBytes.remove(0);
	}
	
	public boolean hasOrderedMessage() {
		return peekOrderedMessage() != null;
	}

	public ByteMessage peekOrderedMessage() {
		return orderedBytes.peek();
	}

	public ByteMessage pollOrderedMessage() {
		return orderedBytes.poll();
	}

//    public byte[] receiveBytesFrom(SelectionKey clientKey) {
//    	Iterator<Pair<SelectionKey, byte[]>> queueIter = receptionQueue.iterator();
//    	Pair<SelectionKey, byte[]> currentPair = null;
//    	while(queueIter.hasNext()) {
//    		currentPair = queueIter.next();
//    		if(currentPair.getKey().equals(clientKey))
//    			return currentPair.getValue();
//    	}
//    	return null;
//    }

	public boolean sendBytes(SocketChannel clientChannel, byte[] message) {
		SelectionKey clientKey = clientChannel.keyFor(selector);
		if(clientKey != null)
			return sendBytes(clientKey, message);
		return false;
	}
	
	public boolean sendBytes(SelectionKey clientKey, byte[] message) {
		return orderedBytes.offer(new ByteMessage(clientKey, message));
	}

	@Override
	public void onBytesReceived(SelectionKey clientKey, byte[] requestBytes) {
		logger.info("Bytes received: " + new String(requestBytes, StandardCharsets.UTF_8));
	}
	
	@Override
	public void onBytesSent(SelectionKey clientKey, byte[] sentBytes) {
		logger.info("Bytes sent: " + new String(sentBytes, StandardCharsets.UTF_8));
	}
	
	@Override
	public void close() throws IOException {
		stop();
	}

	public boolean isActive() {
		return active;
	}
	
	public boolean bufferReceivedBytes() {
    	return bufferReceivedBytes;
    }
    
    public void setBufferReceivedBytes(boolean value) {
    	bufferReceivedBytes = value;
    }
    
    public boolean bufferSentBytes() {
    	return bufferSentBytes;
    }
    
    public void setBufferSentBytes(boolean value) {
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

	public Selector getSelector() {
		return selector;
	}

	public int getReceptionCount() {
		return receptionCounter;
	}

	protected class ServerMessageReceiver implements Runnable {

		private volatile boolean running;
		private long loopDelayMillis;

		public ServerMessageReceiver() {
			this(25L);
		}

		public ServerMessageReceiver(long loopingDelayMillis) {
			this.setRunning(true);
			this.loopDelayMillis = loopingDelayMillis;
		}

		@Override
		public void run() {
			logger.info("ServerMessageReceiver startet...");
			while (isRunning()) {
				try {
					selector.select();
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> keyIt = selectedKeys.iterator();
					while (!selectedKeys.isEmpty()) {
						SelectionKey currentKey = keyIt.next();
						keyIt.remove();
						if (!currentKey.isValid())
							continue;
						if (currentKey.isAcceptable())
							accept(currentKey);
						else if (currentKey.isReadable()) {
							byte[] readMessage = null;
							if ((readMessage = read((SocketChannel) currentKey.channel(), (SSLEngine) currentKey.attachment())) != null) {
								if(bufferReceivedBytes()) {
									if (receivedBytes.add(new ByteMessage(currentKey, readMessage)))
										++receptionCounter;
								}
								if(isByteReceptionHandlerEnabled())
									onBytesReceived(currentKey, readMessage);		
							} 
							else if (!currentKey.isValid())
								currentKey.cancel();
						}
					}
				} 
				catch (Exception e) {
					logger.severe(e.toString());
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {
				}
			}
			logger.info("ServerMessageReceiver beendet.");
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

	protected class ServerMessageSender implements Runnable {
		private volatile boolean running;
		private long loopDelayMillis;

		public ServerMessageSender() {
			this(25L);
		}

		public ServerMessageSender(long loopingDelayMillis) {
			this.setRunning(true);
			this.loopDelayMillis = loopingDelayMillis;
		}

		@Override
		public void run() {
			logger.info("ServerMessageSender startet...");
			while (isRunning()) {
				ByteMessage currentMessage = null;
				try {
					if ((currentMessage = peekOrderedMessage()) != null) {
						write((SocketChannel) currentMessage.getClientKey().channel(), (SSLEngine) currentMessage.getClientKey().attachment(), currentMessage.getMessageBytes());
						if(isByteSendingHandlerEnabled())
							onBytesSent(currentMessage.getClientKey(), currentMessage.getMessageBytes());
						pollOrderedMessage();
					}
				} 
				catch (Exception e) {
					logger.severe(e.toString());
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {}
			}
			logger.info("ServerMessageSender beendet.");
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