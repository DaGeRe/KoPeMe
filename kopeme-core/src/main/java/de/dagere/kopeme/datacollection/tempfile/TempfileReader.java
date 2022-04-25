package de.dagere.kopeme.datacollection.tempfile;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.kopemedata.Fulldata;

public interface TempfileReader {

   void read(Throwable exception, Set<String> datacollectors);

   void readStreaming(Set<String> keys);

   Fulldata createFulldata(int warmup, String currentDatacollector);

   SummaryStatistics getCollectorSummary(String collectorName);

   List<Map<String, Long>> getRealValues();

   List<Long> getExecutionStartTimes();

   Map<String, Number> getFinalValues();

   void clear(String key);

   void deleteTempFile();

}