package de.dagere.kopeme.datastorage;

import java.util.Map;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;

/**
 * Interface for storing KoPeMe-data.
 * 
 * @author reichelt
 *
 */
public interface DataStorer {


   void storeValue(Result performanceDataMeasure, String testcase, String collectorName);
}
