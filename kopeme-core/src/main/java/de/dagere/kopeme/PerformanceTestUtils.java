package de.dagere.kopeme;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TemperatureCollector;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.DataStorer;
import de.dagere.kopeme.datastorage.PerformanceDataMeasure;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.datastorage.XMLDataStorer;

public class PerformanceTestUtils {
	private static final Logger log = LogManager.getLogger(PerformanceTestUtils.class);

	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr Testresult, that should be tested
	 * @return Weather the collector is valid or not
	 */
	public static boolean checkCollectorValidity(TestResult tr, Map<String, Long> assertationvalues, Map<String, Double> maximalRelativeStandardDeviation) {
		log.trace("Checking DataCollector validity...");
		boolean valid = true;
		for (String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		String keys = "";
		for (String key : tr.getKeys()) {
			keys += key + " ";
		}
		for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + keys);
				for (String key : tr.getKeys()) {
					log.warn(key + " - " + collectorName + ": " + key.equals(collectorName));
				}
			}
		}
		log.trace("... " + valid);
		return valid;
	}

	public static void saveData(SaveableTestData data) {
		try {
			File f = data.getFolder();
			String testcasename = data.getTestcasename();
			if (!f.exists())
			{
				f.mkdirs();
			}
			DataStorer xds = new XMLDataStorer(f.getAbsolutePath(), data.getFilename(), testcasename);
			TestResult tr = data.getTr();
			for (String key : tr.getKeys()) {
				log.trace("Key: " + key);
				double relativeStandardDeviation = tr.getRelativeStandardDeviation(key);
				long value = tr.getValue(key);
				// log.info("Ermittle Minimum");
				long min = tr.getMinumumCurrentValue(key);
				// log.info("Min: " + min);
				long max = tr.getMaximumCurrentValue(key);
				double first10percentile = getPercentile(tr.getValues(key), 10);
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, key, value, relativeStandardDeviation,
						tr.getRealExecutions(), min, max, first10percentile, TemperatureCollector.getTemperature());
				List<Long> values = data.isSaveValues() ? tr.getValues(key) : null;
				xds.storeValue(performanceDataMeasure, values);
				// xds.storeValue(s, getValue(s));
				log.trace("{}: {}, (rel. Standardabweichung: {})", key, value, relativeStandardDeviation);
			}
			for (String additionalKey : tr.getAdditionValueKeys()) {
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, additionalKey, tr.getValue(additionalKey), 0.0,
						tr.getRealExecutions(), tr.getValue(additionalKey), tr.getValue(additionalKey), tr.getValue(additionalKey), TemperatureCollector.getTemperature());
				List<Long> vales = new LinkedList<Long>();
				xds.storeValue(performanceDataMeasure, vales);
			}
			xds.storeData();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
	
	public static double getPercentile(List<Long> values, int percentil) {
		double wertArray[] = new double[values.size()];
		int i = 0;
		for (Long l : values) {
			wertArray[i] = l;
			i++;
		}

		Percentile p = new Percentile(percentil);
		double evaluate = p.evaluate(wertArray);
		log.trace("Perzentil: " + evaluate);
		return evaluate;
	}
}
