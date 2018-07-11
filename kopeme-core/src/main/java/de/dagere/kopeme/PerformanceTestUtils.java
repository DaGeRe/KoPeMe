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
			final File f = data.getFolder();
			final String testcasename = data.getTestcasename();
			if (!f.exists())
			{
				f.mkdirs();
			}
			final DataStorer xds = new XMLDataStorer(f, data.getFilename(), testcasename);
			final TestResult tr = data.getTr();
			final long timeValue = tr.getValue(TimeDataCollector.class.getName());
			if (timeValue != 0){
				LOG.info("Execution Time: {} milliseconds", timeValue / 10E3);
			}
			for (final String key : tr.getKeys()) {
				LOG.trace("Collector Key: {}", key);
				final double relativeStandardDeviation = tr.getRelativeStandardDeviation(key);
				final long value = tr.getValue(key);
				final long min = tr.getMinumumCurrentValue(key);
				final long max = tr.getMaximumCurrentValue(key);
				final double first10percentile = getPercentile(tr.getValues(key).values(), 10);
				final PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, key, value, relativeStandardDeviation,
						tr.getRealExecutions(), data.getWarmupExecutions(), data.getRepetitions(), min, max, first10percentile);
				final Map<Long, Long> values = data.isSaveValues() ? tr.getValues(key) : null;
				xds.storeValue(performanceDataMeasure, values);
				// xds.storeValue(s, getValue(s));
				LOG.trace("{}: {}, (rel. Standardabweichung: {})", key, value, relativeStandardDeviation);
			}
			for (final String additionalKey : tr.getAdditionValueKeys()) {
				if (!tr.getKeys().contains(additionalKey)) {
					final PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, additionalKey, tr.getValue(additionalKey), 0.0,
							tr.getRealExecutions(), data.getWarmupExecutions(), data.getRepetitions(), tr.getValue(additionalKey), tr.getValue(additionalKey), tr.getValue(additionalKey));
					final Map<Long, Long> values = data.isSaveValues() ? tr.getValues(additionalKey) : null;
					xds.storeValue(performanceDataMeasure, values);
				}
			}
			xds.storeData();
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

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
