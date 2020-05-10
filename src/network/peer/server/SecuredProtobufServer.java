package network.peer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.SSLEngine;

import com.google.protobuf.Message;

import network.messages.ProtobufMessage;
import network.peer.SecuredProtobufPeer;
import network.peer.callbacks.LoggerCallback;
import utils.LoopingRunnable;
import utils.ThreadUtils;

public class SecuredProtobufServer extends SecuredProtobufPeer {
	protected volatile boolean connected;

	protected ServerSocketChannel serverSocketChannel;
	protected Selector selector;
	protected Queue<ProtobufMessage> orderedBytes;
	
	protected LoopingRunnable protobufReceiver;
	protected LoopingRunnable protobufSender;

	public SecuredProtobufServer(String protocol, String hostAddress, int hostPort) throws Exception {
		super(protocol);
		initServerSSLContext();
		initBuffers();
		
		setPeerCallback(new LoggerCallback(logger));
		
		selector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.bind(new InetSocketAddress(hostAddress, hostPort));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		orderedBytes = new ConcurrentLinkedQueue<ProtobufMessage>();
		protobufReceiver = new ServerProtobufReader(10L);
		protobufSender = new ServerProtobufWriter(10L);
	}

	public void start() {
		ioExecutor.submit(protobufReceiver);
		ioExecutor.submit(protobufSender);
		logger.info("Der Server ist gestartet.");
	}

	public void stop() throws IOException {
		logger.fine("Der Server wird jetzt heruntergefahren...");
		serverSocketChannel.socket().close();
		serverSocketChannel.close();
		asyncTaskExecutor.shutdown();
		protobufSender.stop();
		protobufReceiver.stop();
		ioExecutor.shutdown();
		selector.wakeup();
		logger.info("Der Server wurde heruntergefahren.");
	}

	public void enableByteSender(boolean value) {
		if (protobufSender.isRunning() == value)
			return;
		protobufSender.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufSender);
	}

	public void enableByteReceiver(boolean value) {
		if (protobufReceiver.isRunning() == value)
			return;
		protobufReceiver.setRunning(value);
		if(value && !ioExecutor.isShutdown())
			ioExecutor.submit(protobufReceiver);
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
//		Iterator<SelectionKey> keyIt = selector.keys().iterator();
//		while (keyIt.hasNext()) {
//			SelectionKey currentKey = keyIt.next();
//			if (!(currentKey.channel() instanceof SocketChannel))
//				continue;
//			if (((SocketChannel) currentKey.channel()).getRemoteAddress().equals(localClientChannel.getLocalAddress()))
//				return (SSLEngine) currentKey.attachment();
//		}
//		return null;
	}
	
	public SocketChannel getLocalSocketChannel(SocketChannel remoteClientChannel) {
		if(remoteClientChannel == null || !remoteClientChannel.isOpen())
			return null;
		try {
			Iterator<SelectionKey> keyIt = selector.keys().iterator();
			while (keyIt.hasNext()) {
				SelectionKey currentKey = keyIt.next();
				if(!currentKey.isValid()) {
					currentKey.cancel();
					continue;
				}
				if (!(currentKey.channel() instanceof SocketChannel))
					continue;
				if (((SocketChannel) currentKey.channel()).getRemoteAddress().equals(remoteClientChannel.getLocalAddress()) &&
					((SocketChannel) currentKey.channel()).getLocalAddress().equals(remoteClientChannel.getRemoteAddress()))
					return (SocketChannel) currentKey.channel();
			}
		}
		catch(IOException io) {
			io.printStackTrace();
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
	
	public boolean sendMessage(SelectionKey clientKey, Message message) {
		return (clientKey == null || message == null) ? false : sendMessage((SocketChannel)clientKey.channel(), message);
	}
	
	public boolean sendMessage(SocketChannel clientChannel, Message message) {
		if(clientChannel == null || message == null)
			return false;
		SocketChannel channelToWrite = clientChannel.isRegistered() ? clientChannel : getLocalSocketChannel(clientChannel);
		return orderedBytes.offer(new ProtobufMessage(channelToWrite, message));
	}
	
	@Override
	public void close() throws IOException {
		stop();
	}

	public Selector getSelector() {
		return selector;
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
					if ((currentMessage = peekOrderedBytes()) != null) {
						if(!currentMessage.hasSocketChannel() || !currentMessage.hasMessage()) {
							pollOrderedBytes();
							continue;
						}
						SelectionKey clientKey = currentMessage.getSocketChannel().keyFor(selector);
						clientKey.channel();
						clientKey.attachment();
						currentMessage.getMessage();
						if(write((SocketChannel) clientKey.channel(), (SSLEngine) clientKey.attachment(), currentMessage.getMessage()) > 0)
							pollOrderedBytes();
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