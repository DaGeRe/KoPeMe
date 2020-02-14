package de.dagere.kopeme.datastorage;

import java.util.Map;

import de.dagere.kopeme.generated.Result.Fulldata;

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

   void storeValue(PerformanceDataMeasure performanceDataMeasure, Fulldata fulldata);
}
