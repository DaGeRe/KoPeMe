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
import de.dagere.kopeme.datastorage.XMLDataStorer;

/**
 * Some utils for performance testing.
 * 
 * @author reichelt
 *
 */
public class PerformanceTestUtils {
	private static final Logger LOG = LogManager.getLogger(PerformanceTestUtils.class);

	public static final String PERFORMANCEFOLDER = "performanceresults";

	private PerformanceTestUtils() {

	}

	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr Testresult, that should be tested
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

	public static void saveData(final String testcasename, final TestResult tr, final boolean failure, final boolean error, final String filename, final boolean saveValues) {
		try {
			File f = new File(PERFORMANCEFOLDER);
			if (!f.exists()) {
				f.mkdir();
			}
			DataStorer xds = new XMLDataStorer(PERFORMANCEFOLDER + "/", filename, testcasename);
			for (String key : tr.getKeys()) {
				LOG.trace("Key: " + key);
				double relativeStandardDeviation = tr.getRelativeStandardDeviation(key);
				long value = tr.getValue(key);
				// log.info("Ermittle Minimum");
				long min = tr.getMinumumCurrentValue(key);
				// log.info("Min: " + min);
				long max = tr.getMaximumCurrentValue(key);
				double first10percentile = getPercentile(tr.getValues(key), 10);
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, key, value, relativeStandardDeviation,
						tr.getRealExecutions(), min, max, first10percentile);
				List<Long> values = saveValues ? tr.getValues(key) : null;
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
		double wertArray[] = new double[values.size()];
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
