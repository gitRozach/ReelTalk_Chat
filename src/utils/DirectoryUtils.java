package utils;

import java.io.File;

public class DirectoryUtils {
	public static boolean fileExists(String filePath) {
		if(filePath == null)
			return false;
		return new File(filePath).exists();
	}
	
	public static boolean fileExistsAndIsAccessible(String filePath) {
		if(filePath == null)
			return false;
		File file = new File(filePath);
		return file.exists() && file.canRead() && file.canWrite();
	}
}
