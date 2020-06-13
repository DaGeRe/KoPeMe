package de.dagere.kopeme.datastorage;

import de.dagere.kopeme.generated.Result;

/**
 * Interface for storing KoPeMe-data.
 * 
 * @author reichelt
 *
 */
public interface DataStorer {


   void storeValue(Result performanceDataMeasure, String testcase, String collectorName);
}
