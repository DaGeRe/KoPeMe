package de.dagere.kopeme;

import de.dagere.kopeme.datacollection.TestResult;

/**
 * Interface for a method checking the results of a test.
 * 
 * @author reichelt
 *
 */
public interface Checker {

	/**
	 * The method which checks the results.
	 * 
	 * @param tr Results that should be checked
	 */
	void checkValues(TestResult tr);
}
