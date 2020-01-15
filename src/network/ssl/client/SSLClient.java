package network.ssl.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import network.ssl.SSLPeer;

public class SSLClient extends SSLPeer {
	
	protected String address;
	protected int port;
	protected SSLEngine engine;
	protected SocketChannel channel;
	
	public SSLClient(String protocol, String address, int port) throws Exception {
		this.address = address;
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
		
		SSLContext sslContext = SSLContext.getInstance(protocol);
		//sslContext.init(createKeyManagers("src/resources/client.jks", "storepass", "keypass"), createTrustManagers("src/resources/trustedCerts.jks", "storepass"), new SecureRandom());
		sslContext.init(null, trustAllCerts, new SecureRandom());
		this.engine = sslContext.createSSLEngine(address, port);
		this.engine.setUseClientMode(true);
		
		this.channel = SocketChannel.open();
		
		SSLSession sslSession = engine.getSession();
		myAppBuffer = ByteBuffer.allocateDirect(sslSession.getApplicationBufferSize());	//2048
		myNetBuffer = ByteBuffer.allocateDirect(sslSession.getPacketBufferSize());
		peerAppBuffer = ByteBuffer.allocateDirect(sslSession.getApplicationBufferSize()); //2048
		peerNetBuffer = ByteBuffer.allocateDirect(sslSession.getPacketBufferSize());
		
	}
	
	public boolean connect() throws IOException {
		channel.configureBlocking(false);
		channel.connect(new InetSocketAddress(address, port));
		channel.finishConnect();
		engine.beginHandshake();
		return handshake(channel, engine);
	}
	
	public void close() throws IOException {
		closeConnection(channel, engine);
		closeExecutors();
	}
	
	protected void read() throws IOException {
		read(channel, engine);
	}
	
	@Override
	protected void read(SocketChannel channel, SSLEngine engine) throws IOException {
		boolean exitLoop = false;
		
		while(!exitLoop) {
			peerNetBuffer.clear();
			int bytesRead = channel.read(peerNetBuffer);
				
			if(bytesRead > 0) {
				SSLEngineResult decryptRes = decryptBufferData(engine);
				if(validSSLEngineResult(decryptRes)) {
					peerAppBuffer.flip();
					System.out.println("Read: " + new String(peerAppBuffer.array(), StandardCharsets.UTF_8));
					exitLoop = true;
					break;
				}
				else
					handleDecryptionFailure(engine, decryptRes);
			}
			else {
				handleEndOfStream(channel, engine);
				return;
			}
		}
	}
	
	protected void write(String message) throws IOException {
		write(channel, engine, message);
	}

	@Override
	protected void write(SocketChannel channel, SSLEngine engine, String message) throws IOException {	
		myAppBuffer.clear();
		myAppBuffer.put(message.getBytes());
		myAppBuffer.flip();
		
		while(myAppBuffer.hasRemaining()) {
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
}
