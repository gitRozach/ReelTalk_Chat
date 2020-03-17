package network.peer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import com.google.protobuf.GeneratedMessageV3;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import network.peer.client.callbacks.PeerCallback;
import utils.LoopingRunnable;
import utils.Utils;

public class SecuredProtobufServer extends SecuredProtobufPeer {
	protected volatile boolean active;
	protected volatile int receptionCounter;
	protected volatile boolean connected;

	protected ServerSocketChannel serverSocketChannel;
	protected SSLContext context;
	protected Selector selector;
	protected Queue<ProtobufMessage> orderedBytes;
	
	protected ServerProtobufReader receiver;
	protected ServerProtobufWriter sender;

	public SecuredProtobufServer(String protocol, String hostAddress, int hostPort) throws Exception {
		super();
		context = SSLContext.getInstance(protocol);
		context.init(createKeyManagers("src/resources/server.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());

		SSLSession dummySession = context.createSSLEngine().getSession();
		myApplicationBuffer = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		myNetworkBuffer = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		peerApplicationBuffer = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		peerNetworkBuffer = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		dummySession.invalidate();

		setPeerCallback(new PeerCallback() {
			@Override
			public void messageReceived(ProtobufMessage byteMessage) {
				logger.info("Server received: " + byteMessage.getMessage().toString());
			}
			@Override
			public void messageSent(ProtobufMessage byteMessage) {
				logger.info("Server sent: " + byteMessage.getMessage().toString());
			}
			@Override
			public void connectionLost(Throwable throwable) {
				logger.info("Server lost network connection: " + throwable.getMessage());
			}
		});
		
		selector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(hostAddress, hostPort));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		orderedBytes = new ConcurrentLinkedQueue<ProtobufMessage>();
		receiver = new ServerProtobufReader(1L);
		sender = new ServerProtobufWriter(1L);
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
			logger.fine("Client is about to connect...");
			SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
			socketChannel.configureBlocking(false);
			while (!socketChannel.finishConnect())
				logger.info("Client connecting...");
	
			SSLEngine engine = context.createSSLEngine();
			engine.setUseClientMode(false);
			engine.beginHandshake();
	
			if (doHandshake(socketChannel, engine)) {
				socketChannel.register(selector, SelectionKey.OP_READ, engine);
				logger.info("Client connected.");
				return true;
			} 
			else {
				closeConnection(socketChannel, engine);
				logger.info("The connection to the client was closed due to a handshake error.");
				return false;
			}
		}
		catch(IOException io) {
			logger.severe(io.toString());
			return false;
		}
	}

	public boolean kick(SocketChannel clientChannel) throws IOException {
		return closeConnection(clientChannel, getEngineFrom(clientChannel));
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
		if(remoteClientChannel == null || !remoteClientChannel.isOpen())
			return null;
		Iterator<SelectionKey> keyIt = selector.keys().iterator();
		while (keyIt.hasNext()) {
			SelectionKey currentKey = keyIt.next();
			if(!currentKey.isValid()) {
				currentKey.cancel();
				continue;
			}
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
	
	public boolean sendMessage(SelectionKey clientKey, GeneratedMessageV3 message) {
		if(clientKey == null)
			return false;
		return sendMessage((SocketChannel)clientKey.channel(), message);
	}
	
	public boolean sendMessage(SocketChannel clientChannel, GeneratedMessageV3 message) {
		if(clientChannel == null || message == null)
			return false;
		if(!clientChannel.isOpen())
			return false;
		return orderedBytes.offer(new ProtobufMessage(clientChannel, message));
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

	protected class ServerProtobufReader extends LoopingRunnable {
		public ServerProtobufReader(long loopingDelay) {
			super(loopingDelay);
		}	
		@Override
		public synchronized void run() {
			super.run();
			logger.info("ServerProtobufReader startet...");
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
				Utils.sleep(loopDelayMillis);
			}
			logger.info("ServerProtobufReader beendet.");
		}
	}

	protected class ServerProtobufWriter extends LoopingRunnable {
		public ServerProtobufWriter(long loopingDelay) {
			super(loopingDelay);
		}		
		@Override
		public void run() {
			super.run();
			logger.info("ServerProtobufWriter startet...");
			while (isRunning()) {
				try {
					ProtobufMessage currentMessage = null;
					if ((currentMessage = peekOrderedBytes()) != null) {
						SocketChannel localChannel = getLocalSocketChannel(currentMessage.getSocketChannel());
						if(localChannel == null)
							localChannel = currentMessage.getSocketChannel();
						if(write(localChannel, (SSLEngine) localChannel.keyFor(selector).attachment(), currentMessage.getMessage()) > 0)
							pollOrderedBytes();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					logger.severe(e.toString());
					return;
				}
				Utils.sleep(loopDelayMillis);
			}
			logger.info("ServerProtobufWriter beendet.");
		}
	}
}