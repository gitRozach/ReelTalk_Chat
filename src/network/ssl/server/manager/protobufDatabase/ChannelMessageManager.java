package network.ssl.server.manager.protobufDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import protobuf.ClientChannels.ChannelBase;
import protobuf.ClientMessages.ChannelMessage;

public class ChannelMessageManager extends ProtobufFileDatabase<ChannelMessage>{	
	protected Map<ChannelBase, ProtobufFileDatabase<ChannelMessage>> messageDatabases;
	
	public ChannelMessageManager(String databaseFilePath) throws IOException {
		this(new File(databaseFilePath));
	}

	public ChannelMessageManager(File databaseFile) throws IOException {
		super(ChannelMessage.class, databaseFile);
	}
	
	public List<ChannelMessage> getMessagesFromByChannelId(int channelId){
		List<ChannelMessage> resultList = new ArrayList<>();
		
		
		
		return resultList;
	}
	
}
