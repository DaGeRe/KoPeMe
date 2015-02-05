package de.dagere.kopeme.junit;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.TestResult;

public abstract class TestExecutorJUnit extends Statement {

	private static final Logger log = LogManager.getLogger(TestExecutorJUnit.class);

	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public TestExecutorJUnit() {
		super();
	}

	/**
	 * Saves the measured data
	 */
	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr
	 * @return
	 */
	protected boolean checkCollectorValidity(TestResult tr) {
		return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
		// log.trace("Checking DataCollector validity...");
		// boolean valid = true;
		// for (String collectorName : assertationvalues.keySet()) {
		// if (!tr.getKeys().contains(collectorName)) {
		// valid = false;
		// log.warn("Invalid Collector for assertion: " + collectorName);
		// }
		// }
		// String keys = "";
		// for (String key : tr.getKeys()) {
		// keys += key + " ";
		// }
		// for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
		// if (!tr.getKeys().contains(collectorName)) {
		// valid = false;
		// log.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + keys);
		// for (String key : tr.getKeys()) {
		// System.out.println(key + " - " + collectorName + ": " + key.equals(collectorName));
		// }
		// }
		// }
		// log.trace("... " + valid);
		// return valid;
	}
}