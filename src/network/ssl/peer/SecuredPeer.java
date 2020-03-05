package network.ssl.peer;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

import network.ssl.client.callbacks.PeerCallback;
import network.ssl.communication.ProtobufMessage;

public abstract class SecuredPeer implements Closeable {	
	protected final Logger logger = Logger.getLogger(getClass().getSimpleName());
	
	protected PeerCallback peerCallback;
	
	protected ByteBuffer myApplicationBuffer;
    protected ByteBuffer myNetworkBuffer;
    protected ByteBuffer peerApplicationBuffer;
    protected ByteBuffer peerNetworkBuffer;
    
    private final Object handshakeLock = new Object();
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    
    protected volatile boolean bufferingReceivedMessages;
	protected volatile boolean bufferingSentMessages;
    protected volatile boolean receptionHandlerEnabled;
    protected volatile boolean sendingHandlerEnabled;
    
    protected List<ProtobufMessage> receivedMessages;
    protected List<ProtobufMessage> sentMessages;
    
    protected ExecutorService asyncTaskExecutor;
    protected ExecutorService ioExecutor;
    
    public SecuredPeer() {
    	bufferingReceivedMessages = false;
    	bufferingSentMessages = false;
    	receptionHandlerEnabled = true;
    	sendingHandlerEnabled = true;
    	receivedMessages = Collections.synchronizedList(new ArrayList<ProtobufMessage>());
        sentMessages = Collections.synchronizedList(new ArrayList<ProtobufMessage>());
    	asyncTaskExecutor = Executors.newSingleThreadExecutor();
    	ioExecutor = Executors.newCachedThreadPool();
    }
    /**
     * <p/>
     * A typical handshake will usually contain the following steps:
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
     */
    
    protected boolean doHandshake(SocketChannel socketChannel, SSLEngine engine) {
    	synchronized (handshakeLock) {
    		logger.fine("About to do handshake...");
    		HandshakeStatus handshakeStatus = engine.getHandshakeStatus();
            while (socketChannel.isOpen() && handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
                switch (handshakeStatus) {
                case NEED_UNWRAP:
                	if((handshakeStatus = doHandshakeUnwrap(socketChannel, engine)) == null)               
		            	return false;
                	break;
                case NEED_WRAP:
                    if((handshakeStatus = doHandshakeWrap(socketChannel, engine)) == null)
                    	return false;
                    break;
                case NEED_TASK:
                    handshakeStatus = doHandshakeDelegatedTasks(engine);
                    break;
                default:
                    handshakeStatus = engine.getHandshakeStatus();
                    break;
                }
            }
            return true;
		}
    }
    
    private HandshakeStatus doHandshakeWrap(SocketChannel socketChannel, SSLEngine engine) {
    	SSLEngineResult wrapResult = encryptBufferedBytes(engine);
    	if(wrapResult == null)
    		return null;
    	switch (wrapResult.getStatus()) {
         case OK:
             if(writeEncryptedBytes(socketChannel) < 0 && !handleHandshakeError(socketChannel, engine))
            	 return null;
             break;
         case BUFFER_OVERFLOW:
             myNetworkBuffer = enlargePacketBuffer(engine, myNetworkBuffer);
             break;
         case CLOSED:
        	 writeEncryptedBytes(socketChannel);
             break;
         default:
        	 break;
    	 }
    	 return engine.getHandshakeStatus();
    }
    
    private HandshakeStatus doHandshakeUnwrap(SocketChannel socketChannel, SSLEngine engine) {
    	SSLEngineResult unwrapResult = null;
    	if(readEncryptedBytes(socketChannel, false) >= 0)
    		unwrapResult = decryptBufferedBytes(engine, true);
    	else if(handleHandshakeError(socketChannel, engine))
    		return engine.getHandshakeStatus();
    	if(unwrapResult == null)
    		return null;
    	switch (unwrapResult.getStatus()) {
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
        	engine.closeOutbound();
        	break;
        default:
	        break;
        }
    	return engine.getHandshakeStatus();
    }
    
    private boolean handleHandshakeError(SocketChannel socketChannel, SSLEngine engine) {
    	try {
	    	if(!socketChannel.isOpen()) {
	    		engine.closeInbound();
	        	engine.closeOutbound();
	    		return false;
	    	}
	    	return true;
    	}
    	catch(SSLException ssl) {
    		logger.severe(ssl.toString());
    		return false;
    	}
    }
    
    private HandshakeStatus doHandshakeDelegatedTasks(SSLEngine engine) {
    	Runnable task;
        while ((task = engine.getDelegatedTask()) != null)
            asyncTaskExecutor.execute(task);
        return engine.getHandshakeStatus();
    }
    
    protected GeneratedMessageV3 read(SocketChannel socketChannel, SSLEngine engine) {
    	synchronized (readLock) {
    		try {
				int readBytes = 0;
				if ((readBytes = readEncryptedBytes(socketChannel)) > 0) {
					byte[] receptionBuffer = retrieveDecryptedBytes(socketChannel, engine);
					Any rawMessage = Any.parseFrom(receptionBuffer);
					Class<? extends GeneratedMessageV3> messageClass = ProtobufMessage.getMessageTypeOf(rawMessage);
					System.out.println(messageClass.getSimpleName());
					GeneratedMessageV3 message = rawMessage.unpack(messageClass);
					if(receptionBuffer == null || message == null)
						return null;
					if(isBufferingReceivedMessages())
						receivedMessages.add(new ProtobufMessage(socketChannel, message));	
					if(isMessageReceptionHandlerEnabled())
						peerCallback.messageReceived(new ProtobufMessage(socketChannel, message));
					return message;
				}
				if(readBytes == -1) {
					logger.severe("Peer read -1");
					closeConnection(socketChannel, engine);
				}
				return null;
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    			return null;
    		}
		}
    }
    
    protected int write(SocketChannel socketChannel, SSLEngine engine, GeneratedMessageV3 message) throws IOException {
        synchronized (writeLock) {
        	int writtenBytes = 0;
        	Any packedMessage = Any.pack(message);
            putBytesIntoBufferAndFlip(packedMessage.toByteArray());
            while (myApplicationBuffer.hasRemaining()) {
            	SSLEngineResult encryptionResult = encryptBufferedBytes(engine);
    			if(checkEngineResult(encryptionResult))
    				writtenBytes += writeEncryptedBytes(socketChannel);
    			else
    				handleEncryptionResult(socketChannel, engine, encryptionResult);
            }
            if(writtenBytes > 0) {
            	if(isBufferingSentMessages())
            		sentMessages.add(new ProtobufMessage(socketChannel, message));		
            	if(isMessageSendingHandlerEnabled())
            		peerCallback.messageSent(new ProtobufMessage(socketChannel, message));
            }
            return writtenBytes;
		} 
    }
    
    protected byte[] retrieveDecryptedBytes(SocketChannel socketChannel, SSLEngine engine) {
        while (peerNetworkBuffer.hasRemaining()) {
        	SSLEngineResult result = decryptBufferedBytes(engine, true);
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

	protected synchronized int writeEncryptedBytes(SocketChannel clientChannel) {
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
	
	protected int readEncryptedBytes(SocketChannel socketChannel) {
		return readEncryptedBytes(socketChannel, true);
    }
	
	protected int readEncryptedBytes(SocketChannel socketChannel, boolean clearBeforeRead) {
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
	
	protected SSLEngineResult encryptBufferedBytes(SSLEngine engine) {
		return encryptBufferedBytes(engine, false);
	}
	
	protected synchronized SSLEngineResult encryptBufferedBytes(SSLEngine engine, boolean flipApplicationBuffer) {
		try {
			if(flipApplicationBuffer)
				myApplicationBuffer.flip();
			myNetworkBuffer.clear();
			return engine.wrap(myApplicationBuffer, myNetworkBuffer);
		} 
		catch (SSLException ssle) {
			return null;
		}
	}
	
	protected SSLEngineResult decryptBufferedBytes(SSLEngine engine) {
		return decryptBufferedBytes(engine, false);
	}
	
	protected SSLEngineResult decryptBufferedBytes(SSLEngine engine, boolean flipNetworkBuffer) {
		try {
			if(flipNetworkBuffer)
				peerNetworkBuffer.flip();
    		peerApplicationBuffer.clear();
			SSLEngineResult res = engine.unwrap(peerNetworkBuffer, peerApplicationBuffer);
			peerNetworkBuffer.compact();
			return res;
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
	
	protected boolean handleEncryptionResult(SocketChannel socketChannel, SSLEngine engine, SSLEngineResult result) {
    	switch (result.getStatus()) {
		 case OK:
			 return true;
		 case BUFFER_OVERFLOW:
		     myNetworkBuffer = enlargePacketBuffer(engine, myNetworkBuffer);
		     break;
		 case BUFFER_UNDERFLOW:
			 break;
		 case CLOSED:
		 	closeConnection(socketChannel, engine);
		 	break;
		 default:
		     throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
    	}
    	return false;
    }

	protected boolean handleDecryptionResult(SocketChannel socketChannel, SSLEngine engine, SSLEngineResult result) {
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
    
    protected boolean handleEndOfStream(SocketChannel socketChannel, SSLEngine engine)  {
    	try {
	    	if(closeConnection(socketChannel, engine)) {
	    		engine.closeInbound();
	    		return true;
	    	}
	    	return false;
    	}
    	catch(IOException io) {
    		return false;
    	}
    }
	
	protected void putBytesIntoBufferAndFlip(byte[] data) {
    	putBytesIntoBufferAndFlip(data, true);
    }
    
    protected synchronized void putBytesIntoBufferAndFlip(byte[] data, boolean clearBufferBeforeAdding) {
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
    	ByteBuffer enlargedBuffer = null;
        if (sessionProposedCapacity > buffer.capacity())
        	enlargedBuffer = ByteBuffer.allocate(sessionProposedCapacity);
        else
        	enlargedBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
        return enlargedBuffer;
    }
    
    protected boolean closeConnection(SocketChannel socketChannel, SSLEngine engine)  {
    	try {
	        engine.closeOutbound();
	        socketChannel.close();
    	}
    	catch(IOException io) {
    		logger.severe(io.toString());
    	}
    	return !socketChannel.isConnected();
    }
    
    public PeerCallback getPeerCallback() {
    	return peerCallback;
    }
    
    public void setPeerCallback(PeerCallback callback) {
    	peerCallback = callback;
    }
    
    public boolean isMessageReceptionHandlerEnabled() {
		return receptionHandlerEnabled;
	}
	
	public boolean isMessageSendingHandlerEnabled() {
		return sendingHandlerEnabled;
	}
	
	public void setMessageReceptionHandlerEnabled(boolean value) {
		receptionHandlerEnabled = value;
	}
	
	public void setMessageSendingHandlerEnabled(boolean value) {
		sendingHandlerEnabled = value;
	}
	
	public boolean isBufferingReceivedMessages() {
    	return bufferingReceivedMessages;
    }
    
    public void setBufferingReceivedMessages(boolean value) {
    	bufferingReceivedMessages = value;
    }
    
    public boolean isBufferingSentMessages() {
    	return bufferingSentMessages;
    }
    
    public void setBufferingSentMessages(boolean value) {
    	bufferingSentMessages = value;
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
        try(InputStream keyStoreIS = new FileInputStream(filepath)) {
            keyStore.load(keyStoreIS, keystorePassword.toCharArray());
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
        try(InputStream trustStoreIS = new FileInputStream(filepath)) {
            trustStore.load(trustStoreIS, keystorePassword.toCharArray());
        } 
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }

}