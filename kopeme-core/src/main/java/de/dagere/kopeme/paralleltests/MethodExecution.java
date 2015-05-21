package de.dagere.kopeme.paralleltests;

import de.dagere.kopeme.datacollection.TestResult;

/**
 * One method execution in a row of method executions that should be executed for a parallel test.
 * 
 * @author reichelt
 *
 */
public interface MethodExecution {
	/**
	 * Executes the method and saves the result to the given TestResult.
	 * 
	 * @param tr Object for saving the data
	 */
	void executeMethod(TestResult tr);

	/**
	 * How often the test should be called
	 * 
	 * @return Count of test calls
	 */
	int getCallCount();
}
