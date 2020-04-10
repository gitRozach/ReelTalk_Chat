package database.protobuf;

import java.util.List;

import com.google.protobuf.Message;

public interface SortedProtobufFileDatabase<T extends Message> {
	public void sort(List<T> items);
}
