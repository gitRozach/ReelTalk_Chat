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
import network.ssl.client.utils.CUtils;
import network.ssl.communication.ByteMessage;
import network.threads.LoopingRunnable;

public class SecuredServer extends SecuredPeer {
	protected volatile boolean active;
	protected volatile int receptionCounter;
	protected volatile boolean connected;

	protected ServerSocketChannel serverSocketChannel;
	protected SSLContext context;
	protected Selector selector;
	protected Queue<ByteMessage> orderedBytes;
	
	protected ServerByteReceiver receiver;
	protected ServerByteSender sender;

	public SecuredServer(String protocol, String hostAddress, int hostPort) throws Exception {
		super();
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
		receiver = new ServerByteReceiver(1L);
		sender = new ServerByteSender(1L);
		active = true;
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

	private boolean accept(SelectionKey key) {
		try {
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
				return true;
			} 
			else {
				closeConnection(socketChannel, engine);
				logger.info("Die Verbindung zum Client wurde aufgrund eines Handshake-Fehlers geschlossen.");
				return false;
			}
		}
		catch(IOException io) {
			logger.severe(io.toString());
			return false;
		}
	}

	public boolean kick(SocketChannel clientChannel) throws IOException {
		return closeConnection(clientChannel, getEngineFrom(getLocalSocketChannel(clientChannel)));
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
			return sendBytes(new ByteMessage(clientChannel, message));
		return false;
	}
	
	public boolean sendBytes(ByteMessage byteMessage) {
		return orderedBytes.offer(byteMessage);
	}

	@Override
	public void onBytesReceived(ByteMessage byteMessage) {
		logger.info("Bytes received: " + new String(byteMessage.getMessageBytes(), StandardCharsets.UTF_8));
	}
	
	@Override
	public void onBytesSent(ByteMessage byteMessage) {
		logger.info("Bytes sent: " + new String(byteMessage.getMessageBytes(), StandardCharsets.UTF_8));
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

	protected class ServerByteReceiver extends LoopingRunnable {
		public ServerByteReceiver(long loopingDelay) {
			super(loopingDelay);
		}	
		@Override
		public void run() {
			super.run();
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
						else if (currentKey.isReadable())
							read((SocketChannel) currentKey.channel(), (SSLEngine) currentKey.attachment());
						else if (!currentKey.isValid())
							currentKey.cancel();
					}
				} 
				catch (Exception e) {
					logger.severe(e.toString());
				}
				CUtils.sleep(loopDelayMillis);
			}
			logger.info("ServerMessageReceiver beendet.");
		}
	}

	protected class ServerByteSender extends LoopingRunnable {
		public ServerByteSender(long loopingDelay) {
			super(loopingDelay);
		}		
		@Override
		public void run() {
			super.run();
			logger.info("ServerMessageSender startet...");
			while (isRunning()) {
				try {
					ByteMessage currentMessage = null;
					if ((currentMessage = peekOrderedBytes()) != null) {
						write((SocketChannel) currentMessage.getSocketChannel(), (SSLEngine) currentMessage.getSocketChannel().keyFor(selector).attachment(), currentMessage.getMessageBytes());
						pollOrderedBytes();
					}
				} 
				catch (Exception e) {
					logger.severe(e.toString());
				}
				CUtils.sleep(loopDelayMillis);
			}
			logger.info("ServerMessageSender beendet.");
		}
	}
}