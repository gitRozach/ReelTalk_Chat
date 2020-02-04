package network.ssl;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public abstract class SecuredPeer implements Closeable {	
	protected final Logger logger = Logger.getLogger(getClass().getSimpleName());

	protected ByteBuffer myApplicationBuffer;
    protected ByteBuffer myNetworkBuffer;
    protected ByteBuffer peerApplicationBuffer;
    protected ByteBuffer peerNetworkBuffer;
    
    private final Object handshakeLock = new Object();
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    
    protected ExecutorService asyncTaskExecutor = Executors.newSingleThreadExecutor();
    protected ExecutorService ioExecutor = Executors.newCachedThreadPool();
    
    /**
     * <p/>
     * A typical handshake will usually contain the following steps:
     *
     * <ul>
     *   <li>1. wrap:     ClientHello</li>
     *   <li>2. unwrap:   ServerHello/Cert/ServerHelloDone</li>
     *   <li>3. wrap:     ClientKeyExchange</li>
     *   <li>4. wrap:     ChangeCipherSpec</li>
     *   <li>5. wrap:     Finished</li>
     *   <li>6. unwrap:   ChangeCipherSpec</li>
     *   <li>7. unwrap:   Finished</li>
     * </ul>
     * <p/>
     * Handshake is also used during the end of the session, in order to properly close the connection between the two peers.
     * A proper connection close will typically include the one peer sending a CLOSE message to another, and then wait for
     * the other's CLOSE message to close the transport link. The other peer from his perspective would read a CLOSE message
     * from his peer and then enter the handshake procedure to send his own CLOSE message as well.
     *
     * @param socketChannel - the socket channel that connects the two peers.
     * @param engine - the engine that will be used for encryption/decryption of the data exchanged with the other peer.
     * @return True if the connection handshake was successful or false if an error occurred.
     * @throws IOException - if an error occurs during read/write to the socket channel.
     */
    
    protected boolean doHandshake(SocketChannel socketChannel, SSLEngine engine) throws IOException {
    	synchronized (handshakeLock) {
    		logger.fine("About to do handshake...");

    		SSLEngineResult result = null;
            HandshakeStatus handshakeStatus = null;
            myNetworkBuffer.clear();
            peerNetworkBuffer.clear();

            handshakeStatus = engine.getHandshakeStatus();
            while (socketChannel.isOpen() && handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
                switch (handshakeStatus) {
                case NEED_UNWRAP:
                	try {
    		            if (readEncryptedData(socketChannel, false) < 0) {
    		            	logger.info("Handshake read < 0");
    		            	if(!socketChannel.isOpen()) {
    		            		engine.closeInbound();
    		            		engine.closeOutbound();
    		            		return false;
    		            	}    		                
    		                handshakeStatus = engine.getHandshakeStatus();
    		                break;
    		            }
                    	peerNetworkBuffer.flip();
                        result = decryptBufferedData(engine);
                        peerNetworkBuffer.compact();
                        handshakeStatus = result.getHandshakeStatus();
                    } 
                    catch (Exception sslException) {
                    	if(!socketChannel.isOpen())
                            engine.closeOutbound();
                    	handshakeStatus = engine.getHandshakeStatus();
                        break;
                    }
                    
                    switch (result.getStatus()) {
                    case OK:
                        break;
                    case BUFFER_OVERFLOW:
                        // Will occur when peerAppData's capacity is smaller than the data derived from peerNetData's unwrap.
                        peerApplicationBuffer = enlargeApplicationBuffer(engine, peerApplicationBuffer);
                        break;
                    case BUFFER_UNDERFLOW:
                        // Will occur either when no data was read from the peer or when the peerNetData buffer was too small to hold all peer's data.
                        peerNetworkBuffer = handleBufferUnderflow(engine, peerNetworkBuffer);
                        break;
                    case CLOSED:
                    	if(engine.isOutboundDone())
                    		return false;
                    	else {
                    		engine.closeOutbound();
                    		handshakeStatus = engine.getHandshakeStatus();
                    		break;
                    	}
                    default:
                        throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                    break;
                    
                case NEED_WRAP:
                	//myNetworkBuffer.clear();
                    result = encryptBufferedData(engine);//engine.wrap(myApplicationBuffer, myNetworkBuffer);
                    handshakeStatus = result.getHandshakeStatus();
                    
                    switch (result.getStatus()) {
                    case OK:
                        myNetworkBuffer.flip();
                        while (myNetworkBuffer.hasRemaining())
                            socketChannel.write(myNetworkBuffer);
                        break;
                    case BUFFER_OVERFLOW:
                        myNetworkBuffer = enlargePacketBuffer(engine, myNetworkBuffer);
                        break;
                    case BUFFER_UNDERFLOW:
                        throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                    case CLOSED:
                        try {
                            myNetworkBuffer.flip();
                            while (myNetworkBuffer.hasRemaining())
                                socketChannel.write(myNetworkBuffer);
                            peerNetworkBuffer.clear();
                        } 
                        catch (Exception e) {
                            logger.severe("Failed to send server's CLOSE message due to socket channel's failure.");
                            handshakeStatus = engine.getHandshakeStatus();
                        }
                        break;
                    default:
                        throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                    break;
                case NEED_TASK:
                    Runnable task;
                    while ((task = engine.getDelegatedTask()) != null)
                        asyncTaskExecutor.execute(task);
                    handshakeStatus = engine.getHandshakeStatus();
                    break;
                case FINISHED:
                    break;
                case NOT_HANDSHAKING:
                    break;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
                }
            }
            return true;
		}
    }
    
    protected byte[] read(SocketChannel socketChannel, SSLEngine engine) throws Exception {
    	synchronized (readLock) {
			int readBytes = 0;
			if ((readBytes = readEncryptedData(socketChannel)) > 0)
				return retrieveDecryptedBytes(socketChannel, engine);
			if(readBytes == -1)
				closeConnection(socketChannel, engine);
			try {
				Thread.sleep(1L);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
    }
    
    protected void write(SocketChannel socketChannel, SSLEngine engine, byte[] message) throws IOException {
        synchronized (writeLock) {
            putDataIntoBufferAndFlip(message);
            while (myApplicationBuffer.hasRemaining()) {
            	SSLEngineResult encryptionResult = encryptBufferedData(engine);
    			if(checkEngineResult(encryptionResult))
    				writeEncryptedData(socketChannel);
    			else
    				handleEncryptionResult(socketChannel, engine, encryptionResult);
            }
            try {
				Thread.sleep(1L);
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 
    }
    
    protected byte[] retrieveDecryptedBytes(SocketChannel socketChannel, SSLEngine engine) throws IOException {
        peerNetworkBuffer.flip();
        while (peerNetworkBuffer.hasRemaining()) {
        	SSLEngineResult result = decryptBufferedData(engine);
            if(result == null)
            	return null;
            if(checkEngineResult(result)) {
            	peerApplicationBuffer.flip();
            	return Arrays.copyOf(peerApplicationBuffer.array(), peerApplicationBuffer.remaining());
            }
            else
            	handleDecryptionResult(socketChannel, engine, result);
        }
        return null;
    }

	protected synchronized int writeEncryptedData(SocketChannel clientChannel) {
		if(!clientChannel.isOpen())
			return -1;
		try {
			int writtenBytes = 0;
			myNetworkBuffer.flip();
			while (myNetworkBuffer.hasRemaining())
				writtenBytes += clientChannel.write(myNetworkBuffer);
			return writtenBytes;
		}
		catch(IOException io) {
			return -1;
		}
	}
	
	protected int readEncryptedData(SocketChannel socketChannel, boolean clearBeforeRead) {
		if(!socketChannel.isOpen())
			return -1;
        try {
        	if(clearBeforeRead)
        		peerNetworkBuffer.clear();
        	return socketChannel.read(peerNetworkBuffer);
        } 
        catch(IOException io) {
        	return -1;
        }
	}
	
	protected int readEncryptedData(SocketChannel socketChannel) {
		return readEncryptedData(socketChannel, true);
    }
	
	protected synchronized SSLEngineResult encryptBufferedData(SSLEngine engine) {
		try {
			myNetworkBuffer.clear();
			return engine.wrap(myApplicationBuffer, myNetworkBuffer);
		} 
		catch (SSLException ssle) {
			return null;
		}
	}
	
	protected SSLEngineResult decryptBufferedData(SSLEngine engine) {
		try {
    		peerApplicationBuffer.clear();
			return engine.unwrap(peerNetworkBuffer, peerApplicationBuffer);
		} 
    	catch (SSLException e) {
			return null;
		}
    }
	
	protected boolean checkEngineResult(SSLEngineResult result) {
		if (result == null)
			return false;
		if (result.getStatus() == Status.OK)
			return true;
		return false;
	}
	
	protected boolean handleEncryptionResult(SocketChannel socketChannel, SSLEngine engine, SSLEngineResult result) throws IOException {
    	switch (result.getStatus()) {
		 case OK:
			 return true;
		 case BUFFER_OVERFLOW:
		     myNetworkBuffer = enlargePacketBuffer(engine, myNetworkBuffer);
		     break;
		 case BUFFER_UNDERFLOW:
		     throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
		 case CLOSED:
		 	closeConnection(socketChannel, engine);
		 	break;
		 default:
		     throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
    	}
    	return false;
    }

	protected boolean handleDecryptionResult(SocketChannel socketChannel, SSLEngine engine, SSLEngineResult result) throws IOException {
    	switch (result.getStatus()) {
        case OK:
            return true;
        case BUFFER_OVERFLOW:
            peerApplicationBuffer = enlargeApplicationBuffer(engine, peerApplicationBuffer);
            break;
        case BUFFER_UNDERFLOW:
            peerNetworkBuffer = handleBufferUnderflow(engine, peerNetworkBuffer);
            break;
        case CLOSED:
        	closeConnection(socketChannel, engine);
        	break;
        default:
            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
        }
    	return false;
    }
	
	protected ByteBuffer handleBufferUnderflow(SSLEngine engine, ByteBuffer buffer) {
        if (engine.getSession().getPacketBufferSize() < buffer.limit())
            return buffer;
        else {
            ByteBuffer replaceBuffer = enlargePacketBuffer(engine, buffer);
            buffer.flip();
            replaceBuffer.put(buffer);
            return replaceBuffer;
        }
    }
    
    protected void handleEndOfStream(SocketChannel socketChannel, SSLEngine engine)  {
        try {
            closeConnection(socketChannel, engine);
            engine.closeInbound();
        } 
        catch (Exception e) {
            logger.severe(e.toString());
        }
    }
	
	protected void putDataIntoBufferAndFlip(byte[] data) {
    	putDataIntoBufferAndFlip(data, true);
    }
    
    protected synchronized void putDataIntoBufferAndFlip(byte[] data, boolean clearBufferBeforeAdding) {
    	if(clearBufferBeforeAdding)
			myApplicationBuffer.clear();
		myApplicationBuffer.put(data);
		myApplicationBuffer.flip();
	}
	
    protected synchronized ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getPacketBufferSize());
    }

    protected synchronized ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getApplicationBufferSize());
    }

    protected synchronized ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
        if (sessionProposedCapacity > buffer.capacity())
            buffer = ByteBuffer.allocate(sessionProposedCapacity);
        else
            buffer = ByteBuffer.allocate(buffer.capacity() * 2);
        return buffer;
    }
    
    protected void closeConnection(SocketChannel socketChannel, SSLEngine engine) throws IOException  {
        engine.closeOutbound();
        socketChannel.close();
    }

    /**
     * Creates the key managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @param filepath - the path to the JKS keystore.
     * @param keystorePassword - the keystore's password.
     * @param keyPassword - the key's passsword.
     * @return {@link KeyManager} array that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    protected KeyManager[] createKeyManagers(String filepath, String keystorePassword, String keyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream keyStoreIS = new FileInputStream(filepath);
        try {
            keyStore.load(keyStoreIS, keystorePassword.toCharArray());
        } 
        finally {
            if (keyStoreIS != null)
                keyStoreIS.close();
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());
        return kmf.getKeyManagers();
    }

    /**
     * Creates the trust managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @param filepath - the path to the JKS keystore.
     * @param keystorePassword - the keystore's password.
     * @return {@link TrustManager} array, that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    protected TrustManager[] createTrustManagers(String filepath, String keystorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream trustStoreIS = new FileInputStream(filepath);
        try {
            trustStore.load(trustStoreIS, keystorePassword.toCharArray());
        } 
        finally {
            if (trustStoreIS != null)
                trustStoreIS.close();
        }
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }

}