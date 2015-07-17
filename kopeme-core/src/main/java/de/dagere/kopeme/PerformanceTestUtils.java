package de.dagere.kopeme;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.DataStorer;
import de.dagere.kopeme.datastorage.PerformanceDataMeasure;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.datastorage.XMLDataStorer;

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
		for (String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				LOG.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		String keys = "";
		for (String key : tr.getKeys()) {
			keys += key + " ";
		}
		for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				LOG.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + keys);
				for (String key : tr.getKeys()) {
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
		try {
			File f = data.getFolder();
			String testcasename = data.getTestcasename();
			if (!f.exists())
			{
				f.mkdirs();
			}
			DataStorer xds = new XMLDataStorer(f, data.getFilename(), testcasename);
			TestResult tr = data.getTr();
			for (String key : tr.getKeys()) {
				LOG.debug("Key: " + key);
				double relativeStandardDeviation = tr.getRelativeStandardDeviation(key);
				long value = tr.getValue(key);
				long min = tr.getMinumumCurrentValue(key);
				long max = tr.getMaximumCurrentValue(key);
				double first10percentile = getPercentile(tr.getValues(key), 10);
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, key, value, relativeStandardDeviation,
						tr.getRealExecutions(), min, max, first10percentile);
				List<Long> values = data.isSaveValues() ? tr.getValues(key) : null;
				xds.storeValue(performanceDataMeasure, values);
				// xds.storeValue(s, getValue(s));
				LOG.trace("{}: {}, (rel. Standardabweichung: {})", key, value, relativeStandardDeviation);
			}
			for (String additionalKey : tr.getAdditionValueKeys()) {
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, additionalKey, tr.getValue(additionalKey), 0.0,
						tr.getRealExecutions(), tr.getValue(additionalKey), tr.getValue(additionalKey), tr.getValue(additionalKey));
				List<Long> vales = new LinkedList<Long>();
				xds.storeValue(performanceDataMeasure, vales);
			}
			xds.storeData();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns a given percentil for a given list of values. The n-percentil is the value for which n % of the values are less then the percentil.
	 * 
	 * @param values The list of values for which the percentil should be calculated
	 * @param percentil Percentage for the percentil
	 * @return The percentil value
	 */
	public static double getPercentile(final List<Long> values, final int percentil) {
		double[] wertArray = new double[values.size()];
		int i = 0;
		for (Long l : values) {
			wertArray[i] = l;
			i++;
		}

		Percentile p = new Percentile(percentil);
		double evaluate = p.evaluate(wertArray);
		LOG.trace("Perzentil: " + evaluate);
		return evaluate;
	}
}
