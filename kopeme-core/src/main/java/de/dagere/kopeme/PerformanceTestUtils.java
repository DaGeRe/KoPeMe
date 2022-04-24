package de.dagere.kopeme;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.DataStorer;
import de.dagere.kopeme.datastorage.JSONDataStorer;
import de.dagere.kopeme.datastorage.ParamNameHelper;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.VMResult;

/**
 * Some utils for performance testing.
 * 
 * @author reichelt
 *
 */
public final class PerformanceTestUtils {
   private static final Logger LOG = LogManager.getLogger(PerformanceTestUtils.class);

   /**
    * Initializes the class.
    */
   private PerformanceTestUtils() {

   }

   /**
    * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct.
    * 
    * @param tr Testresult, that should be tested
    * @param assertationvalues Assertion values for checking
    * @param maximalRelativeStandardDeviation Maximale stand deviation values for validity checking
    * @return Weather the collector is valid or not
    */
   public static boolean checkCollectorValidity(final TestResult tr, final Map<String, Long> assertationvalues, final Map<String, Double> maximalRelativeStandardDeviation) {
      LOG.trace("Checking DataCollector validity...");
      boolean valid = true;
      for (final String collectorName : assertationvalues.keySet()) {
         if (!tr.getDatacollectors().contains(collectorName)) {
            valid = false;
            LOG.warn("Invalid Collector for assertion: " + collectorName);
         }
      }
      String datacollectors = "";
      for (final String datacollector : tr.getDatacollectors()) {
         datacollectors += datacollector + " ";
      }
      for (final String collectorName : maximalRelativeStandardDeviation.keySet()) {
         if (!tr.getDatacollectors().contains(collectorName)) {
            valid = false;
            LOG.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + datacollectors);
            for (final String key : tr.getDatacollectors()) {
               LOG.warn(key + " - " + collectorName + ": " + key.equals(collectorName));
            }
         }
      }
      LOG.trace("... " + valid);
      return valid;
   }

   /**
    * Saves the measured performance data to the file system.
    * 
    * @param testcasename Name of the testcase
    * @param tr TestResult-Object that should be saved
    * @param failure Weather the test was a failure
    * @param error Weather an error occured during the test
    * @param filename The filename where the test should be saved
    * @param saveValues Weather values should be saved or only aggregates
    */
   public static void saveData(final SaveableTestData data) {
      final DataStorer xds = getDataStorer(data);

      final TestResult tr = data.getTr();
      if (tr.getValue(TimeDataCollector.class.getName()) != null) {
         final double timeValue = tr.getValue(TimeDataCollector.class.getName()).doubleValue();
         if (timeValue != 0) {
            LOG.info("Execution Time: {} milliseconds", timeValue / 10E2);
         }
      }

      for (final String datacollector : tr.getDatacollectors()) {
         buildKeyData(data, xds, tr, datacollector);
      }
   }

   private static DataStorer getDataStorer(final SaveableTestData data) {
      final File folder = data.getFolder();
      if (!folder.exists()) {
         folder.mkdirs();
      }
      String testcasename = data.getTestcasename();
      if (data.getTr().getParams() != null) {
         testcasename += "(" + ParamNameHelper.paramsToString(data.getTr().getParams()) + ")";
      }
      return new JSONDataStorer(folder, data.getFilename(), testcasename);
   }

   private static void buildKeyData(final SaveableTestData data, final DataStorer xds, final TestResult tr, final String datacollector) {
      LOG.trace("Collector Key: {}", datacollector);
      final VMResult result = getMeasureFromTR(data, tr, datacollector);
      final Fulldata fulldata = data.getConfiguration().isSaveValues() ? tr.getFulldata(datacollector) : null;
      tr.clearFulldata(datacollector);
      result.setFulldata(fulldata);
      xds.storeValue(result, data.getTestcasename(), datacollector);
   }

   private static VMResult getMeasureFromTR(final SaveableTestData data, final TestResult tr, final String additionalKey) {
      final double relativeStandardDeviation = tr.getRelativeStandardDeviation(additionalKey);
      final double value = tr.getValue(additionalKey).doubleValue();
      final double min = tr.getMinumumCurrentValue(additionalKey);
      final double max = tr.getMaximumCurrentValue(additionalKey);
      VMResult result = new VMResult();
      result.setParameters(tr.getParams());
      result.setValue(value);
      result.setDeviation(relativeStandardDeviation);
      result.setMin(min);
      result.setMax(max);
      result.setWarmup(data.getConfiguration().getWarmupExecutions());
      result.setIterations(tr.getRealExecutions());
      result.setRepetitions(data.getConfiguration().getRepetitions());
      result.getVmRunConfiguration().setRedirectToNull(data.getConfiguration().isRedirectToNull());
      result.getVmRunConfiguration().setRedirectToTemp(data.getConfiguration().isRedirectToTemp());
      result.getVmRunConfiguration().setShowStart(data.getConfiguration().isShowStart());
      result.getVmRunConfiguration().setExecuteBeforeClassInMeasurement(data.getConfiguration().isExecuteBeforeClassInMeasurement());
      result.setDate(new Date().getTime());
      result.setJavaVersion(System.getProperty("java.version"));
      return result;
   }

   /**
    * Returns a given percentil for a given list of values. The n-percentil is the value for which n % of the values are less then the percentil.
    * 
    * @param collection The list of values for which the percentil should be calculated
    * @param percentil Percentage for the percentil
    * @return The percentil value
    */
   public static double getPercentile(final Collection<Long> collection, final int percentil) {
      final double[] wertArray = new double[collection.size()];
      int i = 0;
      for (final Long l : collection) {
         wertArray[i] = l;
         i++;
      }

      final Percentile p = new Percentile(percentil);
      final double evaluate = p.evaluate(wertArray);
      LOG.trace("Perzentil: " + evaluate);
      return evaluate;
   }
}
