package utils.reflection;

public class ReflectionUtils {
	
	public static Object getFieldValueOf(String fieldName, Class<?> fieldClass) {
		try {
			return fieldClass.getField(fieldName).get(new Object());
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean setFieldValueOf(String fieldName, Object newValue, Class<?> fieldClass) {
		try {
			fieldClass.getField(fieldName).set(getFieldValueOf(fieldName, fieldClass), newValue);
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Class<?> getFieldTypeOf(String fieldName, Class<?> fieldClass) {
		try {
			return fieldClass.getField(fieldName).getType();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
