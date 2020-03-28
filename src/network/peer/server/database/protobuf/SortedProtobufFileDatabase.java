package network.peer.server.database.protobuf;

import java.util.List;

import com.google.protobuf.GeneratedMessageV3;

public interface SortedProtobufFileDatabase<T extends GeneratedMessageV3> {
	public void sort(List<T> items);
}
