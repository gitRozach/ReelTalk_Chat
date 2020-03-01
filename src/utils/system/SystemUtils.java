package utils.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SystemUtils {
	public static final String JRE_HOME_DIRECTORY_PROPERTY_NAME = "java.home";
	public static final String JRE_LIBRARY_PATH_PROPERTY_NAME = "java.library.path";
	public static final String JRE_CLASS_PATH_PROPERTY_NAME = "java.class.path";
	public static final String JRE_EXTENSION_LIBRARY_PATHS_PROPERTY_NAME = "java.ext.dirs";
	public static final String JDK_VERSION_PROPERTY_NAME = "java.version";
	public static final String JRE_VERSION_PROPERTY_NAME = "java.runtime.version";
	public static final String FILE_SEPARATOR_PROPERTY_NAME = "file.separator";
	public static final String PATH_SEPARATOR_PROPERTY_NAME = "path.separator";
	public static final String LINE_SEPARATOR_PROPERTY_NAME = "line.separator";
	public static final String USER_NAME_PROPERTY_NAME = "user.name";
	public static final String USER_HOME_DIRECTORY_PROPERTY_NAME = "user.home";
	public static final String USER_CURRENT_DIRECTORY_PROPERTY_NAME = "user.dir";
	public static final String OS_NAME_PROPERTY_NAME = "os.name";
	public static final String OS_VERSION_PROPERTY_NAME = "os.version";
	public static final String OS_ARCHITECTURE_PROPERTY_NAME = "os.arch";
	
	public static String getPropertyValue(String propertyName) {
		try {
			return System.getProperty(propertyName);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static String setPropertyValue(String propertyName, String propertyValue) {
		try {
			return System.setProperty(propertyName, propertyValue);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static String getJreHomeDirectory() {
		return getPropertyValue(JRE_HOME_DIRECTORY_PROPERTY_NAME);
	}
	
	public static String getJreLibraryPath() {
		return getPropertyValue(JRE_LIBRARY_PATH_PROPERTY_NAME);
	}
	
	public static String getJreClassPath() {
		return getPropertyValue(JRE_CLASS_PATH_PROPERTY_NAME);
	}
	
	public static String getJreExtensionLibraryPaths() {
		return getPropertyValue(JRE_EXTENSION_LIBRARY_PATHS_PROPERTY_NAME);
	}
	
	public static String getJdkVersion() {
		return getPropertyValue(JDK_VERSION_PROPERTY_NAME);
	}
	
	public static String getJreVersion() {
		return getPropertyValue(JRE_VERSION_PROPERTY_NAME);
	}
	
	public static String getFileSeparator() {
		return getPropertyValue(FILE_SEPARATOR_PROPERTY_NAME);
	}
	
	public static String getPathSeparator() {
		return getPropertyValue(PATH_SEPARATOR_PROPERTY_NAME);
	}
	
	public static String getLineSeparator() {
		return getPropertyValue(LINE_SEPARATOR_PROPERTY_NAME);
	}
	
	public static String getUserName() {
		return getPropertyValue(USER_NAME_PROPERTY_NAME);
	}
	
	public static String getUserHomeDirectory() {
		return getPropertyValue(USER_HOME_DIRECTORY_PROPERTY_NAME);
	}
	
	public static String getUserCurrentDirectory() {
		return getPropertyValue(USER_CURRENT_DIRECTORY_PROPERTY_NAME);
	}
	
	public static String getOsName() {
		return getPropertyValue(OS_NAME_PROPERTY_NAME);
	}
	
	public static String getOsVersion() {
		return getPropertyValue(OS_VERSION_PROPERTY_NAME);
	}
	
	public static String getOsArchitecture() {
		return getPropertyValue(OS_ARCHITECTURE_PROPERTY_NAME);
	}
	
	public static String getPublicIPv4() {
		BufferedReader in = null;
        try {
        	URL whatismyip = new URL("http://checkip.amazonaws.com");
        	in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        }
        catch(IOException io) {
        	return null;
        }
        finally {
			try {
				in.close();
			} 
			catch (IOException e) {}
        }
	}
}
