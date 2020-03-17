package network.peer.server.database.protobuf;

import java.io.IOException;

import protobuf.ClientChannels.TextChannel;

public class ClientChannelDatabase extends ProtobufFileDatabase<TextChannel> {
		
	public ClientChannelDatabase() throws IOException {
		super(TextChannel.class);
	}
}
