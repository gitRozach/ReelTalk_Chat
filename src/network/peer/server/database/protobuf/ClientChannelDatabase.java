package network.peer.server.database.protobuf;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import protobuf.ClientChannels.ClientChannel;

public class ClientChannelDatabase extends ProtobufFileDatabase<ClientChannel> {
		
	public ClientChannelDatabase() throws IOException {
		super(ClientChannel.class);
	}
	
	public ClientChannelDatabase(String filePath) throws IOException {
		super(ClientChannel.class, filePath);
	}
	
	@Override
	public void sort(List<ClientChannel> items) {
		Collections.sort(items, ClientChannelComparator);
	}
	
	public static Comparator<ClientChannel> ClientChannelComparator = new Comparator<ClientChannel>() {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return o1.getBase().getChannelId() - o2.getBase().getChannelId();
		}
	};
}
