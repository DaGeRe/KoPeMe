package de.dagere.kopeme.datastorage;

import de.dagere.kopeme.kopemedata.VMResult;

/**
 * Interface for storing KoPeMe-data.
 * 
 * @author reichelt
 *
 */
public interface DataStorer {

   void storeValue(VMResult performanceDataMeasure, String testcase, String collectorName);

   void storeEmptyMethod(String testcasename);
}
