package utils;

import java.util.Arrays;

public class CollectionUtils {
	public static byte[] concatArrays(byte[] arrayOne, byte[] arrayTwo) {
		if(arrayOne == null && arrayTwo == null)
			return null;
		if(arrayOne == null)
			return Arrays.copyOf(arrayTwo, arrayTwo.length);
		if(arrayTwo == null)
			return Arrays.copyOf(arrayOne, arrayOne.length);
		byte[] resultArray = Arrays.copyOf(arrayOne, arrayOne.length + arrayTwo.length);
		System.arraycopy(arrayTwo, 0, resultArray, arrayOne.length, arrayTwo.length);
		System.out.println("Array size: " + resultArray.length);
		return resultArray;
	}
	
	public static <T> T[] concatArrays(T[] arrayOne, T[] arrayTwo) {
		T[] resultArray = Arrays.copyOf(arrayOne, arrayOne.length + arrayTwo.length);
		System.arraycopy(arrayOne, 0, resultArray, arrayOne.length, arrayTwo.length);
		return resultArray;
	}
	
	public static byte[] convert(Byte[] array) {
		byte[] result = new byte[array.length];
		int index = 0;
		
		for(Byte b : array)
			result[index++] = b;
		return result;
	}
	
	public static Byte[] convert(byte[] array) {
		Byte[] result = new Byte[array.length];
		int index = 0;

		for(byte b : array)
			result[index++] = b;		
		return result;
	}
	
	public static int[] convert(Integer[] array) {
		int[] result = new int[array.length];
		int index = 0;
		
		for(Integer b : array)
			result[index++] = b;
		return result;
	}
	
	public static Integer[] convert(int[] array) {
		Integer[] result = new Integer[array.length];
		int index = 0;
		
		for(int b : array)
			result[index++] = b;
		return result;
	}
	
	public static byte[] trimNulls(byte[] arrayToTrim) {
		int trimmedIndex = arrayToTrim.length-1;

		while(trimmedIndex > 0 && arrayToTrim[trimmedIndex] == 0)
			--trimmedIndex;
		
		byte[] resultArray = new byte[trimmedIndex+1];
		System.arraycopy(arrayToTrim, 0, resultArray, 0, trimmedIndex+1);
		return resultArray;
	}

}
