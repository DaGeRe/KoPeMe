package de.dagere.kopeme;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.PerformanceDataMeasure;
import de.dagere.kopeme.datastorage.XMLDataStorer;

public abstract class TestExecutor extends Statement {
	
	private static final Logger log = LogManager.getLogger(TestExecutor.class);

	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public TestExecutor() {
		super();
	}

	/**
	 * Tests weather the collectors given in the assertions and the maximale
	 * relative standard deviations are correct
	 * 
	 * @param tr
	 * @return
	 */
	protected boolean checkCollectorValidity(TestResult tr) {
		log.info("Checking DataCollector validity...");
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
					System.out.println(key + " - " + collectorName + ": " + key.equals(collectorName));
				}
			}
		}
		log.info("... " + valid);
		return valid;
	}

	/**
	 * Saves the measured data
	 */
	public void saveData(String testcasename, TestResult tr, int executions, boolean failure, boolean error) {
		XMLDataStorer xds = new XMLDataStorer(filename);
		for (String s : tr.getKeys()) {
			double relativeStandardDeviation = tr.getRelativeStandardDeviation(s);
			long value = tr.getValue(s);
			long min = tr.getMinumumCurrentValue(s);
			long max = tr.getMaximumCurrentValue(s);
			xds.storeValue(new PerformanceDataMeasure(testcasename, s, value, relativeStandardDeviation, executions, min, max));
			// xds.storeValue(s, getValue(s));
			log.info("{}: {}, (rel. Standardabweichung: {})", s, value, relativeStandardDeviation);
		}
		xds.storeData();
	}

}