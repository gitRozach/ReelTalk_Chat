package utils;

import java.io.File;
import java.io.IOException;

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
	
	public static boolean addFolderToPath(String path, String folderName) {
		File newFolder = new File(path + "/" + folderName);
		if(newFolder.exists())
			return false;
		newFolder.mkdir();
		return true;	
	}

	public static boolean addFileToPath(String path, String fileName) {
		File newFile = new File(path + "/" + fileName);
		if(!newFile.exists()) {
			try {
				newFile.createNewFile();
				return true;
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else {
			return false;
		}
	}
}
