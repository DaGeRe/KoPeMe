package de.dagere.kopeme.datastorage;

import java.util.List;

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

	/**
	 * Stores a list of values for one execution for a given PerformanceDataMeasure, which represents the aggregated data.
	 * 
	 * @param performanceDataMeasure Aggregated data of a run.
	 * @param values List of value that should be stored
	 */
	void storeValue(PerformanceDataMeasure performanceDataMeasure, List<Long> values);
}
