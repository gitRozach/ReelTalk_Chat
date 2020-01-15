package network.ssl.client.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

public abstract class CPacket implements Serializable
{
	private static final long serialVersionUID = 6353761249427547978L;

	public byte[] serialize()
	{
		return CPacket.serialize(this, null);
	}
	
	public byte[] serialize(String endSign)
	{
		return CPacket.serialize(this, endSign);
	}

	public static byte[] serialize(Object obj)
	{
		return CPacket.serialize(obj, null);
	}
	
	public static byte[] serialize(Object obj, String endSign)
	{
		try(ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bo);)
			{
				oo.writeObject(obj);
				if(endSign != null && !endSign.isEmpty())
					oo.write(endSign.getBytes(Charset.forName("utf-8")));
				oo.flush();
				return bo.toByteArray();
			}
			catch(IOException io)
			{
				System.err.println("Failed serializing.");
				io.printStackTrace();
				return null;
			}
	}

	public static CPacket deserialize(byte[] mBytes)
	{
		try(ByteArrayInputStream bi = new ByteArrayInputStream(mBytes);
			ObjectInputStream oi = new ObjectInputStream(bi);)
		{
			return (CPacket)oi.readObject();
		}
		catch(Exception e)
		{
			System.err.println("Failed deserializing.");
			e.printStackTrace();
			return null;
		}
	}
}
