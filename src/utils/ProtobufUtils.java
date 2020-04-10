package utils;

import java.io.File;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class ProtobufUtils {
	
	public static boolean isProtobufMessage(byte[] messageBytes) {
		try {
			if(messageBytes == null)
				return false;
			boolean res = Any.parseFrom(messageBytes) != null;
			return res;
		} 
		catch (InvalidProtocolBufferException e) {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Message> getClassOf(Any message) {
		if(message == null)
			return null;
		
		String sourcePath = "src";
		String[] classNameParts = message.getTypeUrl().split("/")[1].split("\\.");
		String rawFileName = classNameParts[classNameParts.length - 1];
		String rawParentName = classNameParts[classNameParts.length - 2];
		String resultParentName = null;
		StringBuilder pathBuilder = new StringBuilder().append(sourcePath);
		
		for(int i = 0; i < classNameParts.length - 2; ++i)
			pathBuilder.append("/" + classNameParts[i]);	
		
		for(String currentFileName : new File(pathBuilder.toString()).list()) {
			String currentSimpleFileName = currentFileName.split("\\.")[0];
			if(currentSimpleFileName.equalsIgnoreCase(rawParentName)) {
				resultParentName = currentSimpleFileName;
				break;
			}
		}
		if(resultParentName != null) {
			try {
				pathBuilder = new StringBuilder();
				for(int i = 0; i < classNameParts.length - 2; ++i)
					pathBuilder.append(classNameParts[i] + ".");
				pathBuilder.append(resultParentName + "$").append(rawFileName).toString();
				return (Class<? extends Message>) Class.forName(pathBuilder.toString());
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValueOf(Message message, String fieldName) {
		if(message == null || fieldName == null || fieldName.trim().isEmpty())
			return null;
		FieldDescriptor descriptor = message.getDescriptorForType().findFieldByName(fieldName);
		return descriptor == null ? null : (T)message.getField(descriptor);
	}
	
	public static boolean isFieldTypeInt(Message message, String fieldName) {
		if(message == null || fieldName == null || fieldName.trim().isEmpty())
			return false;
		FieldDescriptor descriptor = message.getDescriptorForType().findFieldByName(fieldName);
		return descriptor != null && descriptor.getJavaType() == JavaType.INT;
	}
}
