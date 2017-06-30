package de.dagere.kopeme.datastorage;

import java.util.List;
import java.util.Map;

/**
 * Interface for storing KoPeMe-data.
 * 
 * @author reichelt
 *
 */
public interface DataStorer {

	/**
	 * Stores all already given data to the hard disk.
	 */
	void storeData();

	void storeValue(PerformanceDataMeasure performanceDataMeasure, Map<Long, Long> values);
}
