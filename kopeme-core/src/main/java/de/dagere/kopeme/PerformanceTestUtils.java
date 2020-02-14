package de.dagere.kopeme;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.DataStorer;
import de.dagere.kopeme.datastorage.PerformanceDataMeasure;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.datastorage.XMLDataStorer;
import de.dagere.kopeme.generated.Result.Fulldata;

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
	 * @param tr
	 *            Testresult, that should be tested
	 * @param assertationvalues
	 *            Assertion values for checking
	 * @param maximalRelativeStandardDeviation
	 *            Maximale stand deviation values for validity checking
	 * @return Weather the collector is valid or not
	 */
	public static boolean checkCollectorValidity(final TestResult tr, final Map<String, Long> assertationvalues, final Map<String, Double> maximalRelativeStandardDeviation) {
		LOG.trace("Checking DataCollector validity...");
		boolean valid = true;
		for (final String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				LOG.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		String keys = "";
		for (final String key : tr.getKeys()) {
			keys += key + " ";
		}
		for (final String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				LOG.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + keys);
				for (final String key : tr.getKeys()) {
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
	 * @param testcasename
	 *            Name of the testcase
	 * @param tr
	 *            TestResult-Object that should be saved
	 * @param failure
	 *            Weather the test was a failure
	 * @param error
	 *            Weather an error occured during the test
	 * @param filename
	 *            The filename where the test should be saved
	 * @param saveValues
	 *            Weather values should be saved or only aggregates
	 */
	public static void saveData(final SaveableTestData data) {
		try {
			final File file = data.getFolder();
			final String testcasename = data.getTestcasename();
			if (!file.exists())
			{
				file.mkdirs();
			}
			final DataStorer xds = new XMLDataStorer(file, data.getFilename(), testcasename);
			final TestResult tr = data.getTr();
			final double timeValue = tr.getValue(TimeDataCollector.class.getName()).doubleValue();
			if (timeValue != 0){
				LOG.info("Execution Time: {} milliseconds", timeValue / 10E2);
			}
			for (final String key : tr.getKeys()) {
				buildKeyData(data, testcasename, xds, tr, key);
			}
			buildAdditionalKeys(data, testcasename, xds, tr);
			xds.storeData();
		} catch (final JAXBException e) {
			e.printStackTrace();
		}
	}

   private static void buildKeyData(final SaveableTestData data, final String testcasename, final DataStorer xds, final TestResult tr, final String key) {
      LOG.trace("Collector Key: {}", key);
      final PerformanceDataMeasure performanceDataMeasure = getMeasureFromTR(data, testcasename, tr, key);
      final Fulldata values = data.isSaveValues() ? tr.getFulldata(key) : null;
      tr.clearFulldata(key);
      xds.storeValue(performanceDataMeasure, values);
   }

   private static void buildAdditionalKeys(final SaveableTestData data, final String testcasename, final DataStorer xds, final TestResult tr) {
      for (final String additionalKey : tr.getAdditionValueKeys()) {
      	if (!tr.getKeys().contains(additionalKey)) {
      	   final PerformanceDataMeasure performanceDataMeasure = getMeasureFromTR(data, testcasename, tr, additionalKey);
      		final Fulldata values = data.isSaveValues() ? tr.getFulldata(additionalKey) : null;
      		tr.clearFulldata(additionalKey);
      		xds.storeValue(performanceDataMeasure, values);
      	}
      }
   }

   private static PerformanceDataMeasure getMeasureFromTR(final SaveableTestData data, final String testcasename, final TestResult tr, final String additionalKey) {
      final double relativeStandardDeviation = tr.getRelativeStandardDeviation(additionalKey);
      final double value = tr.getValue(additionalKey).doubleValue();
      final long min = tr.getMinumumCurrentValue(additionalKey);
      final long max = tr.getMaximumCurrentValue(additionalKey);
      final double first10percentile = getPercentile(tr.getValues(additionalKey), 10);
      final PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, additionalKey, value, relativeStandardDeviation,
            tr.getRealExecutions(), data.getWarmupExecutions(), data.getRepetitions(), min, max, first10percentile);
      return performanceDataMeasure;
   }

	/**
	 * Returns a given percentil for a given list of values. The n-percentil is the value for which n % of the values are less then the percentil.
	 * 
	 * @param collection
	 *            The list of values for which the percentil should be calculated
	 * @param percentil
	 *            Percentage for the percentil
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
