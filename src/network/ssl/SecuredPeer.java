package network.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
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
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public abstract class SecuredPeer {	
	protected final Logger log = Logger.getLogger(getClass().getSimpleName());
	//protected final Object socketLock = new Object();

	protected ByteBuffer myAppData;
    protected ByteBuffer myNetData;
    protected ByteBuffer peerAppData;
    protected ByteBuffer peerNetData;
    
    //private final Object handshakeLock = new Object();

    protected ExecutorService asyncTaskExecutor = Executors.newSingleThreadExecutor();
    protected ExecutorService ioExecutor = Executors.newCachedThreadPool();
    
    protected abstract byte[] read(SocketChannel socketChannel, SSLEngine engine) throws Exception;
    protected abstract void write(SocketChannel socketChannel, SSLEngine engine, byte[] message) throws Exception;

    /**
     * Implements the handshake protocol between two peers, required for the establishment of the SSL/TLS connection.
     * During the handshake, encryption configuration information - such as the list of available cipher suites - will be exchanged
     * and if the handshake is successful will lead to an established SSL/TLS session.
     *
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
    protected synchronized boolean doHandshake(SocketChannel socketChannel, SSLEngine engine) throws IOException {
    	log.fine("About to do handshake...");

        SSLEngineResult result = null;
        HandshakeStatus handshakeStatus = null;

        // NioSslPeer's fields myAppData and peerAppData are supposed to be large enough to hold all message data the peer
        // will send and expects to receive from the other peer respectively. Since the messages to be exchanged will usually be less
        // than 16KB long the capacity of these fields should also be smaller. Here we initialize these two local buffers
        // to be used for the handshake, while keeping client's buffers at the same size.
        int appBufferSize = engine.getSession().getApplicationBufferSize();
        ByteBuffer myAppData = ByteBuffer.allocate(appBufferSize);
        ByteBuffer peerAppData = ByteBuffer.allocate(appBufferSize);
        myNetData.clear();
        peerNetData.clear();

        handshakeStatus = engine.getHandshakeStatus();
        while (socketChannel.isOpen() && handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
            switch (handshakeStatus) {
            case NEED_UNWRAP:
            	try {
		            if (socketChannel.read(peerNetData) < 0) {
		            	System.out.println("Handshake read < 0");
		            	if(!socketChannel.isOpen()) {
		            		engine.closeInbound();
		            		engine.closeOutbound();
		            		return false;
		            	}
//		                if (engine.isInboundDone() && engine.isOutboundDone())
//		                    return false;
		                
		                handshakeStatus = engine.getHandshakeStatus();
		                break;
		            }
                	peerNetData.flip();
                    result = engine.unwrap(peerNetData, peerAppData);
                    peerNetData.compact();
                    handshakeStatus = result.getHandshakeStatus();
                } 
                catch (Exception sslException) {
                	if(!socketChannel.isOpen()) {
                		log.severe("A problem was encountered while processing the data that caused the SSLEngine to abort. Will try to properly close connection...");
                        engine.closeOutbound();
                	}
                	handshakeStatus = engine.getHandshakeStatus();
                    break;
                }
                
                switch (result.getStatus()) {
                case OK:
                    break;
                case BUFFER_OVERFLOW:
                    // Will occur when peerAppData's capacity is smaller than the data derived from peerNetData's unwrap.
                    peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                    break;
                case BUFFER_UNDERFLOW:
                    // Will occur either when no data was read from the peer or when the peerNetData buffer was too small to hold all peer's data.
                    peerNetData = handleBufferUnderflow(engine, peerNetData);
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
                myNetData.clear();
                try {
                    result = engine.wrap(myAppData, myNetData);
                    handshakeStatus = result.getHandshakeStatus();
                } 
                catch (SSLException sslException) {
                	if(!socketChannel.isOpen()) {
                		log.severe("A problem was encountered while processing the data that caused the SSLEngine to abort. Will try to properly close connection...");
                        engine.closeOutbound();
                	}
                    handshakeStatus = engine.getHandshakeStatus();
                    break;
                }
                
                switch (result.getStatus()) {
                case OK:
                    myNetData.flip();
                    while (myNetData.hasRemaining())
                        socketChannel.write(myNetData);
                    break;
                case BUFFER_OVERFLOW:
                    myNetData = enlargePacketBuffer(engine, myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    try {
                        myNetData.flip();
                        while (myNetData.hasRemaining())
                            socketChannel.write(myNetData);
                        peerNetData.clear();
                    } 
                    catch (Exception e) {
                        log.severe("Failed to send server's CLOSE message due to socket channel's failure.");
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
    
    protected void putDataIntoBufferAndFlip(byte[] data) {
    	putDataIntoBufferAndFlip(data, true);
    }
    
    protected synchronized void putDataIntoBufferAndFlip(byte[] data, boolean clearBufferBeforeAdding) {
    	if(clearBufferBeforeAdding)
			myAppData.clear();
		myAppData.put(data);
		myAppData.flip();
	}

	protected synchronized void writeEncryptedData(SocketChannel clientChannel) throws IOException {
		myNetData.flip();
		while (myNetData.hasRemaining())
			clientChannel.write(myNetData);
	}

	protected synchronized SSLEngineResult encryptBufferedData(SSLEngine engine) {
		try {
			myNetData.clear();
			return engine.wrap(myAppData, myNetData);
		} 
		catch (SSLException ssle) {
			return null;
		}
	}

	protected boolean encryptionSucceed(SSLEngineResult result) {
		if (result == null)
			return false;
		if (result.getStatus() == Status.OK)
			return true;
		return false;
	}

	protected boolean handleEncryptionError(SSLEngine engine, SocketChannel channel, SSLEngineResult result) throws IOException {
		if (result == null)
			return false;
		switch (result.getStatus()) {
		case BUFFER_OVERFLOW:
			myNetData = enlargePacketBuffer(engine, myNetData);
			break;
		case CLOSED:
			closeConnection(channel, engine);
			return false;
		case OK:
			return true;
		default:
			throw new IOException("Invalid SSLStatus: " + result.getStatus());
		}
		return false;
	}

    protected synchronized ByteBuffer enlargePacketBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getPacketBufferSize());
    }

    protected synchronized ByteBuffer enlargeApplicationBuffer(SSLEngine engine, ByteBuffer buffer) {
        return enlargeBuffer(buffer, engine.getSession().getApplicationBufferSize());
    }

    /**
     * Compares <code>sessionProposedCapacity<code> with buffer's capacity. If buffer's capacity is smaller,
     * returns a buffer with the proposed capacity. If it's equal or larger, returns a buffer
     * with capacity twice the size of the initial one.
     *
     * @param buffer - the buffer to be enlarged.
     * @param sessionProposedCapacity - the minimum size of the new buffer, proposed by {@link SSLSession}.
     * @return A new buffer with a larger capacity.
     */
    protected synchronized ByteBuffer enlargeBuffer(ByteBuffer buffer, int sessionProposedCapacity) {
        if (sessionProposedCapacity > buffer.capacity())
            buffer = ByteBuffer.allocate(sessionProposedCapacity);
        else
            buffer = ByteBuffer.allocate(buffer.capacity() * 2);
        return buffer;
    }

    /**
     * Handles {@link SSLEngineResult.Status#BUFFER_UNDERFLOW}. Will check if the buffer is already filled, and if there is no space problem
     * will return the same buffer, so the client tries to read again. If the buffer is already filled will try to enlarge the buffer either to
     * session's proposed size or to a larger capacity. A buffer underflow can happen only after an unwrap, so the buffer will always be a
     * peerNetData buffer.
     *
     * @param buffer - will always be peerNetData buffer.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @return The same buffer if there is no space problem or a new buffer with the same data but more space.
     * @throws Exception
     */
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

    /**
     * This method should be called when this peer wants to explicitly close the connection
     * or when a close message has arrived from the other peer, in order to provide an orderly shutdown.
     * <p/>
     * It first calls {@link SSLEngine#closeOutbound()} which prepares this peer to send its own close message and
     * sets {@link SSLEngine} to the <code>NEED_WRAP</code> state. Then, it delegates the exchange of close messages
     * to the handshake method and finally, it closes socket channel.
     *
     * @param socketChannel - the transport link used between the two peers.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    protected void closeConnection(SocketChannel socketChannel, SSLEngine engine) throws IOException  {
        engine.closeOutbound();
        //doHandshake(socketChannel, engine);
        socketChannel.close();
    }

    /**
     * In addition to orderly shutdowns, an unorderly shutdown may occur, when the transport link (socket channel)
     * is severed before close messages are exchanged. This may happen by getting an -1 or {@link IOException}
     * when trying to read from the socket channel, or an {@link IOException} when trying to write to it.
     * In both cases {@link SSLEngine#closeInbound()} should be called and then try to follow the standard procedure.
     *
     * @param socketChannel - the transport link used between the two peers.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    protected void handleEndOfStream(SocketChannel socketChannel, SSLEngine engine)  {
        try {
            closeConnection(socketChannel, engine);
            engine.closeInbound();
        } 
        catch (Exception e) {
            log.severe(e.toString());
        }
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