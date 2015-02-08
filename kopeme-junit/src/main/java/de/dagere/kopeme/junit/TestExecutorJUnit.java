package de.dagere.kopeme.junit;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.TestResult;

/**
 * Should once become base class of several TestExecutingStatements - isn't yet.
 * 
 * @author reichelt
 *
 */
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
	}
}