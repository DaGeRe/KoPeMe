package de.dagere.kopeme.datacollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import de.dagere.kopeme.Checker;
import de.dagere.kopeme.datacollection.tempfile.ResultTempWriterBin;
import de.dagere.kopeme.datacollection.tempfile.TempfileReader;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReaderBin;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.MeasuredValue;

/**
 * Saves the Data Collectors, and therefore has access to the current results of the tests. Furthermore, by invoking stopCollection, the historical values are inserted into the
 * DataCollectors
 * 
 * @author dagere
 * 
 */
public final class TestResult {
   public static final int BOUNDARY_SAVE_FILE = 1000;

   private static final Logger LOG = LogManager.getLogger(TestResult.class);

   protected Checker checker;
   private int realExecutions;
   private final String methodName;
   private TempfileReader reader;
   private ResultTempWriterBin writer;
   private int iterations;
   private final DataCollector[] sortedCollectors;
   private final LinkedHashMap<String, String> params;

   /**
    * Initializes the TestResult with a Testcase-Name and the executionTimes.
    * 
    * @param methodName Name of the Testcase
    * @param iterations Count of the planned executions
    */
   public TestResult(final String methodName, final int iterations, final DataCollectorList collectors, final boolean warmup) {
      this(methodName, iterations, collectors, warmup, null);
   }
   
   public TestResult(final String methodName, final int iterations, final DataCollectorList collectors, final boolean warmup, final LinkedHashMap<String, String> params) {
      this.methodName = methodName;
      this.iterations = iterations;

      final Collection<DataCollector> dcCollection = collectors.getDataCollectors().values();
      sortedCollectors = dcCollection.toArray(new DataCollector[0]);
      final Comparator<DataCollector> comparator = new Comparator<DataCollector>() {
         @Override
         public int compare(final DataCollector arg0, final DataCollector arg1) {
            return arg0.getPriority() - arg1.getPriority();
         }
      };
      Arrays.sort(sortedCollectors, comparator);  
      this.params = params;
      
      try {
         writer = new ResultTempWriterBin(warmup);
         writer.setDataCollectors(sortedCollectors);
         reader = new WrittenResultReaderBin(writer.getTempFile());
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public LinkedHashMap<String, String> getParams() {
      return params;
   }

   /**
    * Returns the name of the TestCase for which the result is saved.
    * 
    * @return Name of the Testcase
    */
   public String getTestcase() {
      return methodName;
   }

   /**
    * Gets all names of DataCollectors that are used.
    * 
    * @return Names of used DataCollectors
    */
   public Set<String> getDatacollectors() {
      final Set<String> datacollectorSet = new HashSet<>();
      for (final DataCollector dc : sortedCollectors) {
         datacollectorSet.add(dc.getName());
      }

      return datacollectorSet;
   }

   /**
    * Sets the checker, that is checking weather the performance measures are good enough for a stable build.
    * 
    * @param c Checker for checking the values
    */
   public void setChecker(final Checker c) {
      this.checker = c;
   }

   /**
    * Checks, weather the values are good enough.
    */
   public void checkValues() {
      if (checker != null)
         checker.checkValues(this);
   }

   /**
    * Checks the current list of performance measures are less than the given values.
    * 
    * @param assertationvalues Threshold values
    */
   public void checkValues(final Map<String, Long> assertationvalues) {
      for (final Map.Entry<String, Long> entry : assertationvalues.entrySet()) {
         for (final DataCollector dc : sortedCollectors) {
            LOG.debug("Collector: {} Collector 2:{}", dc.getName(), entry.getKey());
            if (dc.getName().equals(entry.getKey())) {
               LOG.debug("Collector: {} Value: {} Aim: {}", dc.getName(), dc.getValue(), entry.getValue());
               MatcherAssert.assertThat("Kollektor " + dc.getName() + " besitzt Wert " + dc.getValue() + ", Wert sollte aber unter " + entry.getValue()
                     + " liegen.", dc.getValue(), Matchers.lessThan(entry.getValue()));
            }
         }
      }
      LOG.debug("All measurements fine.");
   }

   public void beforeRun() {

   }

   /**
    * Starts the collection of Data for all Datacollectors.
    */
   public void startCollection() {
      writeStartTime();
      for (final DataCollector dc : sortedCollectors) {
         dc.startCollection();
      }
   }

   private void writeStartTime() {
      writer.executionStart(System.currentTimeMillis());
   }

   /**
    * Stops the collection of data, that are collected via DataCollectors. The collection of self-defined values isn't stopped and historical data are not loaded, so assertations
    * over self-defined values and historical data is not possible. For this, call finalizeCollection.
    */
   public void stopCollection() {
      // final Map<String, Long> runData = new HashMap<>();
      for (final DataCollector dc : sortedCollectors) {
         dc.stopCollection();
      }
      writer.writeValues(sortedCollectors);
   }

   /**
    * Called when the collection of data is finally finished, i.e. also the collection of self-defined values is finished. By this time, writing into the file and Assertations over
    * historical data are possible
    */
   public void finalizeCollection() {
      finalizeCollection(null);
   }

   public void finalizeCollection(final Throwable thrownException) {
      writer.finalizeCollection();
      if (iterations < BOUNDARY_SAVE_FILE) {
         reader.read(thrownException, getDatacollectors());
         reader.deleteTempFile();
      } else {
         reader.readStreaming(getDatacollectors());
      }
   }

   /**
    * Gets the current value of the measurement.
    * 
    * @param name Name of the measure
    * @return Value of the measure
    */
   public Number getValue(final String key) {
      final Map<String, Number> finalValues = reader.getFinalValues();
      if (finalValues.get(key) != null) {
         return finalValues.get(key);
      } else {
         return null;
      }
   }

   /**
    * Gets current minimum value for the measured values.
    * 
    * @param key Name of the performance measure
    * @return Minimum of the currently measured values
    */
   public double getMinumumCurrentValue(final String key) {
      return reader.getCollectorSummary(key).getMin();
   }

   /**
    * Gets current maximum value for the measured values.
    * 
    * @param key Name of the performance measure
    * @return Maximum of the currently measured values
    */
   public double getMaximumCurrentValue(final String key) {
      return reader.getCollectorSummary(key).getMax();
   }

   public Fulldata getFulldata(final String key) {
      final Fulldata fd = new Fulldata();
      if (iterations < BOUNDARY_SAVE_FILE) {
         for (int i = 0; i < reader.getRealValues().size(); i++) {
            final MeasuredValue v = new MeasuredValue();
            v.setStartTime(reader.getExecutionStartTimes().get(i));
            v.setValue(reader.getRealValues().get(i).get(key));
            fd.getValues().add(v);
         }
      } else {
         fd.setFileName(writer.getTempFile().getAbsolutePath());
      }

      return fd;
   }

   public void clearFulldata(final String key) {
      reader.clear(key);
   }

   public List<Long> getValues(final String key) {
      List<Long> currentValues = new ArrayList<>();
      for (int i = 0; i < reader.getRealValues().size(); i++) {
         currentValues.add(reader.getRealValues().get(i).get(key));
      }
      return currentValues;
   }

   /**
    * Returns count of real executions.
    * 
    * @return Count of real Executions
    */
   public int getRealExecutions() {
      return realExecutions;
   }

   /**
    * Sets count of real executions.
    * 
    * @param realExecutions Count of real executions
    */
   public void setRealExecutions(final int realExecutions) {
      this.realExecutions = realExecutions;
   }

   public String getMethodName() {
      return methodName;
   }

   public double getRelativeStandardDeviation(final String additionalKey) {
      final SummaryStatistics collectorSummary = reader.getCollectorSummary(additionalKey);
      return collectorSummary.getStandardDeviation() / collectorSummary.getMean();
   }

   public void deleteTempFile() {
      writer.finalizeCollection();
      reader.deleteTempFile();
   }

}
