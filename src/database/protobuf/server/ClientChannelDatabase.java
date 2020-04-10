package database.protobuf.server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientChannels.ClientChannel;

public class ClientChannelDatabase extends ProtobufFileDatabase<ClientChannel> {
		
	public ClientChannelDatabase() throws IOException {
		this("", "");
	}
	
	public ClientChannelDatabase(String databaseName) throws IOException {
		this(databaseName, "");
	}
	
	public ClientChannelDatabase(String databaseName, String filePath) throws IOException {
		super(ClientChannel.class, databaseName, filePath);
	}
	
	@Override
	public void sort(List<ClientChannel> items) {
		items.sort(ClientChannelComparator);
	}
	
	public static Comparator<ClientChannel> ClientChannelComparator = new Comparator<ClientChannel>() {
		@Override
		public int compare(ClientChannel o1, ClientChannel o2) {
			return o1.getBase().getChannelId() - o2.getBase().getChannelId();
		}
	};
}
