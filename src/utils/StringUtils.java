package utils;

public class StringUtils {
	public static boolean checkStringLength(String s, int minLength, int maxLength) {
		return s.length() >= minLength && s.length() <= maxLength;
	}

	public static boolean anyEmptyString(String ... strings) {
		for(String s : strings)
			if(s == null || s.isEmpty())
				return true;
		return false;
	}
	
	public static String trimFront(String value) {
		if(value == null || value.isEmpty() || !value.startsWith(" "))
			return value;
		return trimFront(value.substring(1));
	}
}
