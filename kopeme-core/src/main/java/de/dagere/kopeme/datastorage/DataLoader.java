package de.dagere.kopeme.datastorage;

import java.util.Date;
import java.util.Map;

/**
 * Interface for loading KoPeMe-data.
 * 
 * @author reichelt
 *
 */
public interface DataLoader {
	/**
	 * Method for loading the KoPeMe-data.
	 * 
	 * @return KoPeMe-data
	 */
	Map<String, Map<Date, Long>> getData();
}
