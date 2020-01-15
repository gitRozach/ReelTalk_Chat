package network.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


public abstract class SSLPeer {
	protected ByteBuffer myAppBuffer, myNetBuffer;
	protected ByteBuffer peerAppBuffer, peerNetBuffer;
	protected Executor ioExecutor = Executors.newSingleThreadExecutor();
	protected Executor ioTaskExecutor = Executors.newFixedThreadPool(2);
	
	protected abstract void read(SocketChannel channel, SSLEngine engine) throws IOException;
	protected abstract void write(SocketChannel channel, SSLEngine engine, String message) throws IOException;
	
	protected synchronized boolean handshake(SocketChannel channel, SSLEngine engine) throws IllegalStateException, IOException {
		HandshakeStatus handshakeStatus = engine.getHandshakeStatus();
		int appBufferSize = engine.getSession().getApplicationBufferSize();
		int packetBufferSize = engine.getSession().getPacketBufferSize();
		
		myAppBuffer = ByteBuffer.allocateDirect(appBufferSize);
		myNetBuffer = ByteBuffer.allocateDirect(packetBufferSize);
		peerAppBuffer = ByteBuffer.allocateDirect(appBufferSize);
		peerNetBuffer = ByteBuffer.allocateDirect(packetBufferSize);
		
		while(handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
			switch(engine.getHandshakeStatus()) {
			case NEED_WRAP:
				handshakeStatus = doWrap(channel, engine);
				break;
			case NEED_UNWRAP_AGAIN:
			case NEED_UNWRAP:
				handshakeStatus = doUnwrap(channel, engine);
				break;
			case NEED_TASK:
				handshakeStatus = doAsyncTask(engine);
				break;
			default:	//nur noch FINISHED oder NOT_HANDSHAKING moeglich, ignorieren
				break;
			}
		}
		return true;
	}
	
	protected SSLEngineResult encryptBufferData(SSLEngine engine) {
		SSLEngineResult result = null;
		try {
			myNetBuffer.clear();
			result = engine.wrap(myAppBuffer, myNetBuffer);
		}
		catch(SSLException se) {
			se.printStackTrace();
			engine.closeOutbound();
		}
		return result;
	}
	
	protected SSLEngineResult decryptBufferData(SSLEngine engine) {
		SSLEngineResult result = null;
		try {
			peerAppBuffer.clear();
			result = engine.unwrap(peerNetBuffer, peerAppBuffer);
		}
		catch(SSLException se) {
			se.printStackTrace();
			engine.closeOutbound();
		}
		return result;
	}
	
	private void writeEncryptedData(SocketChannel channel) throws IOException {
		myNetBuffer.flip();
		while(myNetBuffer.hasRemaining())
			channel.write(myNetBuffer);
	}
	
	private void readDecryptedData() throws IOException {
		peerAppBuffer.flip();
		System.out.println("readDecryptedData: " + new String(peerAppBuffer.array(), StandardCharsets.UTF_8));
	}

	protected void handleEncryptionFailure(SSLEngine engine, SSLEngineResult failureResult) throws IOException {
		switch(failureResult.getStatus()) {
		case OK:
			break;
		case BUFFER_OVERFLOW:
			handleBufferOverflow(engine, peerAppBuffer);
			break;
		case BUFFER_UNDERFLOW:
			handleBufferUnderflow(engine, peerNetBuffer);
			break;
		case CLOSED:
			prepareCloseHandshake();
			break;
		}
	}
	
	protected void prepareCloseHandshake() {
		peerNetBuffer.clear();
	}
	
	protected boolean handleDecryptionFailure(SSLEngine engine, SSLEngineResult failureResult) throws IOException {
		switch(failureResult.getStatus()) {
		case OK:
			break;
		case BUFFER_OVERFLOW:
			handleBufferOverflow(engine, peerAppBuffer);
			break;
		case BUFFER_UNDERFLOW:
			handleBufferUnderflow(engine, peerNetBuffer);
			break;
		case CLOSED:
			if(engine.isOutboundDone())
				return false;
			closeOutbound(engine);
			break;
		}
		return true;
	}
	
	private HandshakeStatus doWrap(SocketChannel channel, SSLEngine engine) {	
		try {
			SSLEngineResult sslResult = encryptBufferData(engine);
			if(validSSLEngineResult(sslResult))
				writeEncryptedData(channel);
			else
				handleEncryptionFailure(engine, sslResult);
		}
		catch(IllegalArgumentException | IOException e) {
			e.printStackTrace();
			engine.closeOutbound();
		}
		return engine.getHandshakeStatus();
	}
	
	private HandshakeStatus doUnwrap(SocketChannel channel, SSLEngine engine) throws IllegalStateException, IOException {	
		try {
			if(channel.read(peerNetBuffer) < 0) {
				closeInboundAndOutbound(engine);
				return engine.getHandshakeStatus();
			}
			SSLEngineResult sslResult = decryptBufferData(engine);
			if(validSSLEngineResult(sslResult))
				readDecryptedData();
			else
				handleDecryptionFailure(engine, sslResult);
		}
		catch(IOException e) {
			e.printStackTrace();
			engine.closeOutbound();
		}
		return engine.getHandshakeStatus();
	}
	
	protected boolean validSSLEngineResult(SSLEngineResult result) {
		if(anyNullValue(result))
			return false;
		if(result.getStatus() == Status.OK)
			return true;
		return false;
	}
	
	private HandshakeStatus doAsyncTask(SSLEngine engine) {
		Runnable t = null;
		while((t = engine.getDelegatedTask()) != null)
			ioTaskExecutor.execute(t);
		return engine.getHandshakeStatus();
	}
	
	protected void handleEndOfStream(SocketChannel channel, SSLEngine engine) {
		closeInbound(engine);
		closeConnection(channel, engine);
	}
	
	protected void handleBufferOverflow(SSLEngine engine, ByteBuffer peerAppBuffer) throws IOException {
		peerAppBuffer = enlargeApplicationBuffer(peerAppBuffer, engine);
	}
	
	protected void handleBufferUnderflow(SSLEngine engine, ByteBuffer packetBuffer) throws IOException {
//		if(packetBuffer.limit() < engine.getSession().getPacketBufferSize())
//			return;
		packetBuffer = enlargePacketBuffer(packetBuffer, engine);
	}
	
	protected ByteBuffer enlargeApplicationBuffer(ByteBuffer buffer, SSLEngine engine) throws IOException {
		return resizeBuffer(buffer, engine.getSession().getApplicationBufferSize());
	}
	
	protected ByteBuffer enlargePacketBuffer(ByteBuffer buffer, SSLEngine engine) throws IOException {
		return resizeBuffer(buffer, engine.getSession().getPacketBufferSize());
	}
	
	protected ByteBuffer resizeBuffer(ByteBuffer buffer, int newSize) throws IOException {
		if(buffer == null || newSize <= 0) throw new IllegalArgumentException();
		
		ByteBuffer newBuf = ByteBuffer.allocateDirect(newSize);
		buffer.flip();
		if(!buffer.hasRemaining())
			return newBuf;
		if(newSize < buffer.limit()) //newBuf.put(buffer.array(), buffer.position(), buffer.position() + newSize);
			throw new IOException(getClass().getSimpleName() + "#resizeBuffer(ByteBuffer buffer, int newSize) : newSize darf nicht kleiner als buffer.limit() sein, wenn noch Daten im Buffer enthalten sind.");
		else
			newBuf.put(buffer);
		return newBuf;
	}
	
	protected void closeInbound(SSLEngine engine) {
		try {
			engine.closeInbound();
		}
		catch(SSLException ssle) {
			ssle.printStackTrace();
		}
	}
	
	protected void closeOutbound(SSLEngine engine) {
		engine.closeOutbound();
	}
	
	protected void closeInboundAndOutbound(SSLEngine engine) {
		closeInbound(engine);
		closeOutbound(engine);
	}
	
	protected void closeExecutors() {
		((ThreadPoolExecutor)ioExecutor).shutdown();
		((ThreadPoolExecutor)ioTaskExecutor).shutdown();
	}
	
	protected void closeConnection(SocketChannel channel, SSLEngine engine) {
		try {
			closeOutbound(engine);
			handshake(channel, engine);
			channel.close();
			
			
			/*try {
				KeyStore tks = KeyStore.getInstance("JKS");
				tks.load(new FileInputStream(""), "tspwd".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(tks);
				
				KeyStore jks = KeyStore.getInstance("JKS");
				jks.load(new FileInputStream(""), "kspwd".toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(jks, "kspwd".toCharArray());
				
				SSLContext context = javax.net.ssl.SSLContext.getInstance("TLSv1.2");
				context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
				
				SSLEngine engine = context.c
			} 
			catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} 
			catch (KeyStoreException e) {
				e.printStackTrace();
			} 
			catch (CertificateException e) {
				e.printStackTrace();
			} 
			catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			} 
			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected KeyManager[] createKeyManagers(String keystorePath, String keyStorePassword, String keyPassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		InputStream keyStoreInputStream = new FileInputStream(keystorePath);
		keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
		keyStoreInputStream.close();
		
		KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyFactory.init(keyStore, keyPassword.toCharArray());
		return keyFactory.getKeyManagers();
	}
	
	protected TrustManager[] createTrustManagers(String trustStorePath, String keyStorePassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		InputStream keyStoreInputStream = new FileInputStream(trustStorePath);
		keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
		keyStoreInputStream.close();
		
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(keyStore);
		return trustFactory.getTrustManagers();
	}
	
	private boolean anyNullValue(Object ... values) {
		if(values == null)
			return true;
		for(Object o : values)
			if(o == null)
				return true;
		return false;
	}
}
