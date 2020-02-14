package network.ssl.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import network.ssl.server.manager.database.StringDatabaseItem;

public abstract class MessagePacket implements Serializable {
	private static final long serialVersionUID = -6165719430033337912L;

	public byte[] serialize() {
		return MessagePacket.serialize(this);
	}

	public static byte[] serialize(Object obj) {
		try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bo);) {
			oo.writeObject(obj);
			oo.flush();
			return bo.toByteArray();
		} 
		catch (IOException io) {
			io.printStackTrace();
			return null;
		}
	}

	public static MessagePacket deserialize(byte[] mBytes) {
		try (ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(mBytes));) {
			return (MessagePacket) oi.readObject();
		} 
		catch (Exception e) {
			return null;
		}
	}
}
