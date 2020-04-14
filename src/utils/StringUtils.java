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
		if(value == null)
			return null;
		StringBuilder builder = new StringBuilder(value);
		String trimmedReverseValue = builder.reverse().toString().trim();
		return new StringBuilder(trimmedReverseValue).reverse().toString();
	}
	
	public static String trimHeadAndTail(String value) {
		return trimFront(value.trim());
	}
}
