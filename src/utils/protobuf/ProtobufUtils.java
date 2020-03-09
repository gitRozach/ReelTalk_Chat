package utils.protobuf;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.GeneratedMessageV3;

public class ProtobufUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValueOf(GeneratedMessageV3 message, String fieldName) {
		if(message == null || fieldName == null || fieldName.trim().isEmpty())
			return null;
		FieldDescriptor descriptor = message.getDescriptorForType().fi
		return descriptor == null ? null : (T)message.getField(descriptor);
	}
	
	public static boolean isFieldTypeInt(GeneratedMessageV3 message, String fieldName) {
		if(message == null || fieldName == null || fieldName.trim().isEmpty())
			return false;
		FieldDescriptor descriptor = message.getDescriptorForType().findFieldByName(fieldName);
		System.out.println("Field found ? " + (descriptor != null));
		return descriptor != null && descriptor.getJavaType() == JavaType.INT;
	}
}
