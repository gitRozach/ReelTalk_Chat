package database.protobuf.server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import database.protobuf.ProtobufFileDatabase;
import protobuf.ClientChannels.Channel;

public class ChannelDatabase extends ProtobufFileDatabase<Channel> {
		
	public ChannelDatabase() throws IOException {
		this("", "");
	}
	
	public ChannelDatabase(String databaseName) throws IOException {
		this(databaseName, "");
	}
	
	public ChannelDatabase(String databaseName, String filePath) throws IOException {
		super(Channel.class, databaseName, filePath);
	}
	
	@Override
	public void sort(List<Channel> items) {
		items.sort(ClientChannelComparator);
	}
	
	public static Comparator<Channel> ClientChannelComparator = new Comparator<Channel>() {
		@Override
		public int compare(Channel o1, Channel o2) {
			return o1.getBase().getId() - o2.getBase().getId();
		}
	};
}
