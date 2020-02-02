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
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import network.ssl.SecuredPeer;
import network.ssl.communication.ByteMessage;
import network.ssl.server.handler.RequestHandler;

public class SecuredServer extends SecuredPeer implements RequestHandler {
	protected volatile boolean active;
	protected volatile int receptionCounter;
	
	private final Object readLock = new Object();
	private final Object writeLock = new Object();

	protected ServerSocketChannel serverSocketChannel;
	protected SSLContext context;
	protected Selector selector;
	
	protected Queue<ByteMessage> sendingQueue;
	protected Queue<ByteMessage> receptionQueue;
	
	protected ServerMessageReceiver receiver;
	protected ServerMessageSender sender;
	protected ServerMessageHandler handler;

	public SecuredServer(String protocol, String hostAddress, int port) throws Exception {
		this.context = SSLContext.getInstance(protocol);
		this.context.init(createKeyManagers("src/resources/server.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());

		SSLSession dummySession = context.createSSLEngine().getSession();
		this.myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		this.myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		this.peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		this.peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		dummySession.invalidate();

		this.selector = SelectorProvider.provider().openSelector();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(false);
		this.serverSocketChannel.bind(new InetSocketAddress(hostAddress, port));
		this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		this.receptionQueue = new ConcurrentLinkedQueue<ByteMessage>();
		this.sendingQueue = new ConcurrentLinkedQueue<ByteMessage>();

		this.receiver = new ServerMessageReceiver(25L);
		this.sender = new ServerMessageSender(25L);
		this.handler = new ServerMessageHandler(25L);

		this.active = true;
		this.receptionCounter = 0;
	}

	public void start() {
		ioExecutor.submit(receiver);
		ioExecutor.submit(sender);
		ioExecutor.submit(handler);
		log.info("Der Server ist gestartet.");
	}

	public void stop() throws IOException {
		log.fine("Der Server wird jetzt heruntergefahren...");
		active = false;
		serverSocketChannel.socket().close();
		serverSocketChannel.close();
		asyncTaskExecutor.shutdown();
		sender.stop();
		receiver.stop();
		handler.stop();
		ioExecutor.shutdown();
		selector.wakeup();
		log.info("Der Server wurde heruntergefahren.");
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

	private void accept(SelectionKey key) throws Exception {
		log.fine("Ein neuer Client moechte sich verbinden...");

		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);
		while (!socketChannel.finishConnect()) {}

		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.beginHandshake();

		if (doHandshake(socketChannel, engine)) {
			socketChannel.register(selector, SelectionKey.OP_READ, engine);
			log.info("Ein neuer Client hat sich verbunden.");
		} else {
			closeConnection(socketChannel, engine);
			log.info("Die Verbindung zum Client wurde aufgrund eines Handshake-Fehlers geschlossen.");
		}
	}

	public void kick(SocketChannel clientChannel) throws IOException {
		closeConnection(clientChannel, getEngineFrom(clientChannel));
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

	@Override
	protected byte[] read(SocketChannel socketChannel, SSLEngine engine) throws IOException {
		synchronized (readLock) {
			int readBytes = 0;
			if ((readBytes = readChannelBytes(socketChannel)) > 0)
				return retrieveDecryptedBytes(socketChannel, engine);
			if(readBytes == -1)
				closeConnection(socketChannel, engine);
			try {
				Thread.sleep(50L);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public boolean hasReceptionMessage() {
		return receptionQueue.peek() != null;
	}

	public ByteMessage peekReceptionMessage() {
		return receptionQueue.peek();
	}

	public ByteMessage pollReceptionMessage() {
		return receptionQueue.poll();
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
		return sendingQueue.offer(new ByteMessage(clientKey, message));
	}
	
	@Override
	protected void write(SocketChannel socketChannel, SSLEngine engine, byte[] message) throws IOException {
		synchronized (writeLock) {
			if (!socketChannel.isOpen())
				return;
			putDataIntoBufferAndFlip(message, true);
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

	@Override
	public void handleRequest(SelectionKey clientKey, byte[] requestBytes) {
		try {
			log.info("Incoming request message: " + new String(requestBytes, StandardCharsets.UTF_8));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		stop();
	}

	public boolean isActive() {
		return active;
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
			log.info("ServerMessageReceiver startet...");

			while (isRunning()) {
				Set<SelectionKey> selectedKeys = null;
				SelectionKey currentKey = null;
				Iterator<SelectionKey> keyIt = null;
				try {
					selector.select();
					selectedKeys = selector.selectedKeys();
					keyIt = selectedKeys.iterator();
					while (!selectedKeys.isEmpty()) {
						currentKey = keyIt.next();
						keyIt.remove();
						if (!currentKey.isValid())
							continue;
						if (currentKey.isAcceptable())
							accept(currentKey);
						else if (currentKey.isReadable()) {
							byte[] readMessage = null;
							if ((readMessage = read((SocketChannel) currentKey.channel(), (SSLEngine) currentKey.attachment())) != null) {
								log.fine("Received message from client: " + new String(readMessage));
								if (receptionQueue.add(new ByteMessage(currentKey, readMessage))) {
									++receptionCounter;
								}
							} 
							else if (!currentKey.isValid())
								currentKey.cancel();
						}
					}
				} 
				catch (Exception e) {
					log.severe(e.toString());
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {
				}
			}
			log.info("ServerMessageReceiver beendet.");
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
			log.info("ServerMessageSender startet...");

			while (isRunning()) {
				ByteMessage currentMessage = null;
				try {
					if ((currentMessage = sendingQueue.peek()) != null) {
						write((SocketChannel) currentMessage.getClientKey().channel(), (SSLEngine) currentMessage.getClientKey().attachment(), currentMessage.getMessageBytes());
						sendingQueue.poll();
					}
				} 
				catch (Exception e) {
					log.severe(e.toString());
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {}
			}
			log.info("ServerMessageSender beendet.");
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

	protected class ServerMessageHandler implements Runnable {
		private volatile boolean running;
		private long loopDelayMillis;

		public ServerMessageHandler() {
			this(25L);
		}

		public ServerMessageHandler(long loopingDelayMillis) {
			this.setRunning(true);
			this.loopDelayMillis = loopingDelayMillis;
		}

		@Override
		public void run() {
			log.fine("ServerMessageHandler startet...");

			while (isRunning()) {
				try {
					ByteMessage message = pollReceptionMessage();
					if (message != null)
						handleRequest(message.getClientKey(), message.getMessageBytes());
				} 
				catch (Exception io) {
					log.severe(io.toString());
				}
				try {
					Thread.sleep(loopDelayMillis);
				} 
				catch (InterruptedException e) {
				}
			}
			log.info("ServerMessageHandler beendet.");
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