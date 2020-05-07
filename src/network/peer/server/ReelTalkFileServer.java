package network.peer.server;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.Map;

public class ReelTalkFileServer extends SecuredProtobufServer {
	protected Map<String, List<SelectionKey>> keyMap; //Speichert Download-Keys und die dazugehoerigen Clients
	
	
	public ReelTalkFileServer(String protocol, String hostAddress, int hostPort) throws Exception {
		super(protocol, hostAddress, hostPort);
	}

}
