package network.peer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.SSLEngine;

import com.google.protobuf.Message;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import utils.LoopingRunnable;
import utils.ThreadUtils;

public class SecuredProtobufServer extends SecuredProtobufPeer {
	protected volatile boolean active;
	protected volatile int receptionCounter;

	protected ServerSocketChannel serverSocketChannel;
	protected Selector selector;

	public SecuredProtobufServer(String protocol, String hostAddress, int hostPort) throws Exception {
		super(protocol);
		initServerSSLContext();
		initBuffers();
		
		active = true;
		receptionCounter = 0;
		selector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(hostAddress, hostPort));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		protobufReader = new ServerProtobufReader(1L);
		protobufWriter = new ServerProtobufWriter(1L);
	}

	public void start() {
		ioExecutor.submit(protobufReader);
		ioExecutor.submit(protobufWriter);
		logger.info("Der Server ist gestartet.");
	}

	public void stop() throws IOException {
		logger.fine("Der Server wird jetzt heruntergefahren...");
		active = false;
		serverSocketChannel.socket().close();
		asyncTaskExecutor.shutdown();
		protobufReader.stop();
		protobufWriter.stop();
		ioExecutor.shutdown();
		selector.wakeup();
		logger.info("Der Server wurde heruntergefahren.");
	}

	public void enableMessageSender(boolean value) {
		if (protobufWriter.isRunning() == value)
			return;
		protobufWriter.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufWriter);
	}

	public void enableMessageReceiver(boolean value) {
		if (protobufReader.isRunning() == value)
			return;
		protobufReader.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufReader);
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

	public SSLEngine getEngineFrom(SocketChannel localClientChannel) {
		if(localClientChannel == null || localClientChannel.keyFor(selector) == null)
			return null;
		return (SSLEngine)localClientChannel.keyFor(selector).attachment();
	}
	
	public SocketChannel getLocalSocketChannel(SocketChannel remoteSocketChannel) throws IOException {
		if(remoteSocketChannel == null || !remoteSocketChannel.isOpen())
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
			if (((SocketChannel) currentKey.channel()).getRemoteAddress().equals(remoteSocketChannel.getLocalAddress()) &&
				((SocketChannel) currentKey.channel()).getLocalAddress().equals(remoteSocketChannel.getRemoteAddress()))
				return (SocketChannel) currentKey.channel();
		}
		return null;
	}
	
	public SelectionKey getLocalSelectionKey(SocketChannel localClientChannel) {
		return localClientChannel.keyFor(selector);
	}

//	public boolean hasReceivableBytes() {
//		return peekReceptionBytes() != null;
//	}
//
//	public ProtobufMessage peekReceptionBytes() {
//		if(receivedMessages.isEmpty())
//			return null;
//		return receivedMessages.get(0);
//	}
//
//	public ProtobufMessage pollReceptionBytes() {
//		if(receivedMessages.isEmpty())
//			return null;
//		return receivedMessages.remove(0);
//	}
	
	public boolean hasOrderedMessage() {
		return orderedMessages.peek() != null;
	}

	public ProtobufMessage peekOrderedMessage() {
		return orderedMessages.peek();
	}

	public ProtobufMessage dequeueOrderedMessage() {
		return orderedMessages.poll();
	}
	
	public boolean enqueueOrderedMessage(SelectionKey clientKey, Message message) {
		if(clientKey == null)
			return false;
		return enqueueOrderedMessage((SocketChannel)clientKey.channel(), message);
	}
	
	public boolean enqueueOrderedMessage(SocketChannel clientChannel, Message message) {
		if(clientChannel == null || message == null)
			return false;
		if(!clientChannel.isOpen())
			return false;
		return orderedMessages.offer(new ProtobufMessage(clientChannel, message));
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
						if (!currentKey.isValid()) {
							currentKey.cancel();
							continue;
						}
						if (currentKey.isAcceptable())
							accept(currentKey);
						else if (currentKey.isReadable())
							read((SocketChannel) currentKey.channel(), (SSLEngine) currentKey.attachment());
					}
				} 
				catch (Exception e) {
					logger.severe(e.toString());
				}
				ThreadUtils.sleep(loopDelayMillis);
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
					if ((currentMessage = peekOrderedMessage()) != null) {
						if(!currentMessage.hasSocketChannel() || !currentMessage.hasMessage()) {
							dequeueOrderedMessage();
							continue;
						}
						SelectionKey clientKey = currentMessage.getSocketChannel().keyFor(selector);
						if(write((SocketChannel) clientKey.channel(), (SSLEngine) clientKey.attachment(), currentMessage.getMessage()) > 0)
							dequeueOrderedMessage();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					logger.severe(e.toString());
					return;
				}
				ThreadUtils.sleep(loopDelayMillis);
			}
			logger.info("ServerProtobufWriter beendet.");
		}
	}
}