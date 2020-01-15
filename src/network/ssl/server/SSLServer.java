package network.ssl.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import network.ssl.SSLPeer;

public class SSLServer extends SSLPeer {
	protected boolean running;
	protected String hostAddress;
	protected int port;
	protected SSLContext context;
	protected Selector clientSelector;
	protected ServerSocketChannel serverChannel;
	private ExecutorService ioExecutor;
	
	public SSLServer(String protocol, String hostAddress, int port) throws Exception {
		this.running = false;
		this.hostAddress = hostAddress;
		this.port = port;
		
		TrustManager[] trustAllCerts = new TrustManager[] { 
			    new X509TrustManager() {     
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
			            return new X509Certificate[0];
			        } 
			        public void checkClientTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			            } 
			        public void checkServerTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    } 
		}; 
		
		this.context = SSLContext.getInstance(protocol);
		//this.context.init(createKeyManagers("src/resources/server.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());
		this.context.init(null, trustAllCerts, new SecureRandom());
		SSLSession session = context.createSSLEngine().getSession();
		myAppBuffer = ByteBuffer.allocateDirect(session.getApplicationBufferSize());
		myNetBuffer = ByteBuffer.allocateDirect(session.getPacketBufferSize());
		peerAppBuffer = ByteBuffer.allocateDirect(session.getApplicationBufferSize());
		peerNetBuffer = ByteBuffer.allocateDirect(session.getPacketBufferSize());
		session.invalidate();
		
		this.serverChannel = ServerSocketChannel.open();
		this.clientSelector = Selector.open();
		this.ioExecutor = Executors.newFixedThreadPool(2);
	}
	
	public void start() throws IOException {
		serverChannel.configureBlocking(false);
		serverChannel.bind(new InetSocketAddress(hostAddress, port));
		serverChannel.register(clientSelector, SelectionKey.OP_ACCEPT);
		ioExecutor.execute(new SSLServerReader());
	}
	
	public void stop() throws IOException {
		setRunning(false);
		closeExecutors();
		clientSelector.wakeup();
	}
	
	public boolean isRunning() {
		return running;
	}

	public void accept(SelectionKey clientKey) throws IOException {
		SocketChannel clientChannel = ((ServerSocketChannel)clientKey.channel()).accept();
		clientChannel.configureBlocking(false);
		
		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.beginHandshake();
		
		if(handshake(clientChannel, engine))
			clientChannel.register(clientSelector, SelectionKey.OP_READ, engine); //SSLEngine als Attachment
		else
			clientChannel.close();
	}
	
	@Override
	public void read(SocketChannel channel, SSLEngine engine) throws IOException {
		boolean exitLoop = false;
		
		while(!exitLoop) {
			peerNetBuffer.clear();
			int bytesRead = channel.read(peerNetBuffer);
			
			if(bytesRead > 0) {
				peerNetBuffer.flip();
				SSLEngineResult decryptRes = decryptBufferData(engine);
				if(validSSLEngineResult(decryptRes)) {
					peerAppBuffer.flip();
					System.out.println("Server read: " + new String(peerAppBuffer.array(), StandardCharsets.UTF_8));
					exitLoop = true;
					break;
				}
				else
					handleDecryptionFailure(engine, decryptRes);
			}
			else
				handleEndOfStream(channel, engine);
		}
	}

	@Override
	public void write(SocketChannel channel, SSLEngine engine, String message) throws IOException {
		myAppBuffer.clear();
		myAppBuffer.put(message.getBytes());
		myAppBuffer.flip();
		
		while(myAppBuffer.hasRemaining()) {
			myNetBuffer.clear();
			SSLEngineResult encryptRes = encryptBufferData(engine);
			if(validSSLEngineResult(encryptRes)) {
				myNetBuffer.flip();
				while(myNetBuffer.hasRemaining())
					channel.write(myNetBuffer);
			}
			else
				handleEncryptionFailure(engine, encryptRes);
		}
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerSocketChannel getServerChannel() {
		return serverChannel;
	}

	public void setServerChannel(ServerSocketChannel serverChannel) {
		this.serverChannel = serverChannel;
	}
	
	protected class SSLServerReader implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					clientSelector.select();
					Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
					Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
					while(keyIterator.hasNext()) {
						SelectionKey currentKey = keyIterator.next();
						keyIterator.remove();
						
						if(!currentKey.isValid())
							continue;
						if(currentKey.isAcceptable())
							accept(currentKey);
						else if(currentKey.isReadable())
							read((SocketChannel)currentKey.channel(), (SSLEngine)currentKey.attachment());
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}	
	}
}
