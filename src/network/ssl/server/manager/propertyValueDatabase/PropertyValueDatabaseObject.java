package network.ssl.server.manager.propertyValueDatabase;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import network.ssl.server.manager.database.StringDatabaseItem;

public abstract class PropertyValueDatabaseObject implements StringDatabaseItem {
	public static final String PROPERTY_START = "=";
	public static final String PROPERTY_END = "#";
	
	protected Map<String, String> loadProperties(String propertyDatabaseString) {
		Map<String, String> propertyValueMap = new LinkedHashMap<String, String>();
		for(String current : propertyDatabaseString.split(PROPERTY_END)) {
			try {
				String currentSplit[] = current.split(PROPERTY_START);
				propertyValueMap.put(currentSplit[0], currentSplit[1]);
			}
			catch(Exception e) {
				continue;
			}
		}
		return propertyValueMap;
	}
	
	@Override
	public Charset getEncoding() {
		return Charset.forName("utf-8");
	}
}
