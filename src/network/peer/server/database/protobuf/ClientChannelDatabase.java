package network.peer.server.database.protobuf;

import java.io.IOException;
import java.util.Comparator;

import protobuf.ClientChannels.ClientChannel;

public class ClientChannelDatabase extends ProtobufFileDatabase<ClientChannel> {
		
	public ClientChannelDatabase() throws IOException {
		super(ClientChannel.class);
	}
	
	public class ClientChannelComparator implements Comparator<ClientChannel> {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return 0;
		}
	}
}
