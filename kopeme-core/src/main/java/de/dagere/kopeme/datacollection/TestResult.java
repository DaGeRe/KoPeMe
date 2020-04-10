package de.dagere.kopeme.datacollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;

import de.dagere.kopeme.Checker;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;

/**
 * Saves the Data Collectors, and therefore has access to the current results of the tests. Furthermore, by invoking stopCollection, the historical values are inserted into the
 * DataCollectors
 * 
 * @author dagere
 * 
 */
public final class TestResult {
   private static final Logger LOG = LogManager.getLogger(TestResult.class);

   protected final Map<String, Number> finalValues = new HashMap<>();
   protected Map<String, DataCollector> dataCollectors;
   protected final List<Map<String, Long>> realValues;
   protected final List<Long> executionStartTimes = new LinkedList<>();
   protected int index = 0;
   protected Checker checker;
   private int realExecutions;
   private String methodName;
   private final HistoricalTestResults historicalResults;

   /**
    * Initializes the TestResult with a Testcase-Name and the executionTimes.
    * 
    * @param methodName Name of the Testcase
    * @param executionTimes Count of the planned executions
    */
   public TestResult(final String methodName, final int executionTimes, final DataCollectorList collectors) {
      realValues = new ArrayList<>(executionTimes + 1);
      this.methodName = methodName;
      historicalResults = new HistoricalTestResults(methodName);
      dataCollectors = collectors.getDataCollectors();
   }

   public void setMethodName(final String methodName) {
      this.methodName = methodName;
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
    * Sets the DatacollectorList for collecting Performance-Measures.
    * 
    * @param dcl List of Datacollectors
    */
   public void setCollectors(final DataCollectorList dcl) {
      dataCollectors = new HashMap<>();
      dataCollectors = dcl.getDataCollectors();
   }

   /**
    * Adds a DataCollector to the given collectors.
    * 
    * @param dc DataCollector that should be added
    */
   public void addDataCollector(final DataCollector dc) {
      dataCollectors.put(dc.getName(), dc);
   }

   /**
    * Gets all names of DataCollectors that are used.
    * 
    * @return Names of used DataCollectors
    */
   public Set<String> getKeys() {
      final Set<String> keySet = new HashSet<>();
      for (final DataCollector dc : dataCollectors.values()) {
         keySet.add(dc.getName());
      }

      for (int i = 0; i < realValues.size(); i++) {
         if (realValues.get(i) != null)
            keySet.addAll(realValues.get(i).keySet());
      }
      return keySet;
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
         for (final DataCollector dc : dataCollectors.values()) {
            LOG.debug("Collector: {} Collector 2:{}", dc.getName(), entry.getKey());
            if (dc.getName().equals(entry.getKey())) {
               LOG.debug("Collector: {} Value: {} Aim: {}", dc.getName(), dc.getValue(), entry.getValue());
               Assert.assertThat("Kollektor " + dc.getName() + " besitzt Wert " + dc.getValue() + ", Wert sollte aber unter " + entry.getValue()
                     + " liegen.", dc.getValue(), Matchers.lessThan(entry.getValue()));
            }
         }
      }
      LOG.debug("All measurements fine.");
   }

   private DataCollector[] sortedCollectors;

   public void beforeRun() {
      final Collection<DataCollector> dcCollection = dataCollectors.values();
      sortedCollectors = dcCollection.toArray(new DataCollector[0]);
      final Comparator<DataCollector> comparator = new Comparator<DataCollector>() {
         @Override
         public int compare(final DataCollector arg0, final DataCollector arg1) {
            return arg0.getPriority() - arg1.getPriority();
         }
      };
      Arrays.sort(sortedCollectors, comparator);
   }

   /**
    * Starts the collection of Data for all Datacollectors.
    */
   public void startCollection() {
      executionStartTimes.add(System.currentTimeMillis());
      for (final DataCollector dc : sortedCollectors) {
         LOG.trace("Starting: {}", dc.getName());
         dc.startCollection();
      }
   }

   /**
    * Starts or restarts the collection for all DataCollectors, e.g. if a TimeDataCollector was started and stopped before, the time measured now is added to the original time.
    */
   public void startOrRestartCollection() {
      executionStartTimes.add(System.currentTimeMillis());
      final Collection<DataCollector> dcCollection = dataCollectors.values();
      final DataCollector[] sortedCollectors = dcCollection.toArray(new DataCollector[0]);
      final Comparator<DataCollector> comparator = new Comparator<DataCollector>() {
         @Override
         public int compare(final DataCollector arg0, final DataCollector arg1) {
            return arg0.getPriority() - arg1.getPriority();
         }
      };
      Arrays.sort(sortedCollectors, comparator);
      for (final DataCollector dc : sortedCollectors) {
         LOG.trace("Starte: {}", dc.getName());
         dc.startOrRestartCollection();
      }
   }

   /**
    * Stops the collection of data, that are collected via DataCollectors. The collection of self-defined values isn't stopped and historical data are not loaded, so assertations
    * over self-defined values and historical data is not possible. For this, call finalizeCollection.
    */
   public void stopCollection() {
      final Map<String, Long> runData = new HashMap<>();
      for (final DataCollector dc : dataCollectors.values()) {
         dc.stopCollection();
      }
      for (final DataCollector dc : dataCollectors.values()) {
         runData.put(dc.getName(), dc.getValue());
      }
      realValues.add(runData);
      index++;
   }

   /**
    * Called when the collection of data is finally finished, i.e. also the collection of self-defined values is finished. By this time, writing into the file and Assertations over
    * historical data are possible
    */
   public void finalizeCollection() {
      LOG.debug("Count of executions: {}  Values: {}", executionStartTimes.size(), realValues.size());
      if (executionStartTimes.size() != realValues.size()) {
         throw new RuntimeException("Count of executions is wrong, expected: " + executionStartTimes.size() + " but got " + realValues.size());
      }
      calculateAverages();
   }

   private void calculateAverages() {
      for (final String collectorName : getKeys()) {
         LOG.trace("Standard deviation {}: {}", collectorName, getRelativeStandardDeviation(collectorName));
         final Number result = getCollectorSummary(collectorName).getMean();
         finalValues.put(collectorName, result);
      }
   }

   private SummaryStatistics getCollectorSummary(final String collectorName) {
      SummaryStatistics stat = new SummaryStatistics();
      for (int i = 0; i < realValues.size() - 1; i++) {
         stat.addValue(realValues.get(i).get(collectorName));
      }
      return stat;
   }

   public void finalizeCollection(final Throwable thrownException) {
      if (executionStartTimes.size() != realValues.size()) {
         throw new RuntimeException("Count of executions is wrong, expected: " + executionStartTimes.size() + " but got " + realValues.size(), thrownException);
      }
      calculateAverages();
      
   }

   /**
    * Adds a self-defined value to the currently measured value. This method should be used if you want to measure data youself (e.g. done transactions in a certain time) and this
    * value should be saved along with the performance measures which where measured by KoPeMe.
    * 
    * @param name Name of the measure that should be saved
    * @param value Value of the measure
    */
   public void addValue(final String name, final long value) {
      if (dataCollectors.get(name) != null) {
         throw new Error("A self-defined value should not have the name of a DataCollector, name: " + name);
      }
      finalValues.put(name, value);
   }

   /**
    * Returns the values of measures, that are not collected via DataCollectors. After the finalization, all values are contained in order to make assertion over these values as
    * well.
    * 
    * @return Additional Values
    */
   public Set<String> getAdditionValueKeys() {
      return finalValues.keySet();
   }

   /**
    * Gets the current value of the measurement.
    * 
    * @param name Name of the measure
    * @return Value of the measure
    */
   public Number getValue(final String key) {
      if (finalValues.get(key) != null) {
         return finalValues.get(key);
      } else {
         long avg = 0;
         for (int i = 0; i < realValues.size(); i++) {
            final Map<String, Long> valueMap = realValues.get(i);
            final Long value = valueMap.get(key);
            if (value != null) {
               avg += value;
            }
         }
         return realValues.size() > 0 ? avg / realValues.size() : Long.MAX_VALUE;
      }
   }

   /**
    * Returns the relative standard deviation for the given DataCollector.
    * 
    * @param datacollector Name of the DataCollector
    * @return Relative standard deviation
    */
   public double getRelativeStandardDeviation(final String collectorName) {
      final SummaryStatistics st = getCollectorSummary(collectorName);

      LOG.trace("Mean: {} Deviation: {}", st.getMean(), st.getStandardDeviation());
      return st.getStandardDeviation() / st.getMean();
   }

   /**
    * Checks weather the given real deviations are below the maximale relative standard deviations that are given.
    * 
    * @param deviations maximale relative standard deviations
    * @return Weather the test can be stopped
    */
   public boolean isRelativeStandardDeviationBelow(final Map<String, Double> deviations) {
      boolean isRelativeDeviationBelowValue = true;
      for (final String collectorName : getKeys()) {
         final Double aimStdDeviation = deviations.get(collectorName);
         if (aimStdDeviation != null) {
            final double stdDeviation = getRelativeStandardDeviation(collectorName);
            LOG.debug("Standardabweichung {}: {} Ziel-Standardabweichung: {}", collectorName, stdDeviation, aimStdDeviation);
            if (stdDeviation > aimStdDeviation) {
               LOG.info("Standard deviation is too high");
               isRelativeDeviationBelowValue = false;
               break;
            }
         }
      }
      LOG.debug("Deviation below value: {}", isRelativeDeviationBelowValue);

      return isRelativeDeviationBelowValue;
   }

   /**
    * Gets current minimum value for the measured values.
    * 
    * @param key Name of the performance measure
    * @return Minimum of the currently measured values
    */
   public long getMinumumCurrentValue(final String key) {
      long min = Long.MAX_VALUE;
      for (int i = 0; i < realValues.size(); i++) {
         if (realValues.get(i).get(key) < min)
            min = realValues.get(i).get(key);
      }
      LOG.trace("Minimum ermittelt: " + min);
      return min;
   }

   /**
    * Gets current maximum value for the measured values.
    * 
    * @param key Name of the performance measure
    * @return Maximum of the currently measured values
    */
   public long getMaximumCurrentValue(final String key) {
      long max = 0;
      for (int i = 0; i < realValues.size(); i++) {
         if (realValues.get(i).get(key) > max)
            max = realValues.get(i).get(key);
      }
      return max;
   }

   public Fulldata getFulldata(String key) {
      final Fulldata fd = new Fulldata();
      for (int i = 0; i < realValues.size(); i++) {
         final Value v = new Value();
         v.setStart(executionStartTimes.get(i));
         v.setValue("" + realValues.get(i).get(key));
         fd.getValue().add(v);
      }
      return fd;
   }
   
   public void clearFulldata(String key) {
      for (int i = 0; i < realValues.size(); i++) {
         realValues.get(i).remove(key);
      }
   }

   public List<Long> getValues(String key) {
      List<Long> currentValues = new ArrayList<>();
      for (int i = 0; i < realValues.size(); i++) {
         currentValues.add(realValues.get(i).get(key));
      }
      return currentValues;
   }

   public void setValues(final String key, final List<Long> currentValues) {
      if (currentValues.size() > realValues.size()) {
         throw new RuntimeException("Internal Error: Count of new values should not exceed count of executions");
      }
      for (int i = 0; i < realValues.size(); i++) {
         final Map<String, Long> currentEntry = realValues.get(i);
         if (currentValues.size() > i) {
            currentEntry.put(key, currentValues.get(i));
         } else {
            currentEntry.remove(i);
         }
      }
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

   /**
    * Gets the historical test results, e.g. the results in past runs.
    * 
    * @return
    */
   public HistoricalTestResults getHistoricalResults() {
      return historicalResults;
   }

   public String getMethodName() {
      return methodName;
   }
   
}
