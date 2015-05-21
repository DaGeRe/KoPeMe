package de.dagere.kopeme.datacollection;

import java.util.LinkedList;
import java.util.List;

import de.dagere.kopeme.paralleltests.MethodExecution;

/**
 * Handles Results for parallel tests.
 * 
 * @author reichelt
 *
 */
public class ParallelTestResult extends TestResult {

	private List<MethodExecution> methods;

	/**
	 * Initializes the TestResult with a Testcase-Name and the executionTimes.
	 * 
	 * @param testcase Name of the Testcase
	 * @param executionTimes Count of the planned executions
	 */
	public ParallelTestResult(final String testcase, final int executionTimes) {
		super(testcase, executionTimes);

		methods = new LinkedList<MethodExecution>();
	}

	/**
	 * Adds a parallel test executions.
	 * 
	 * @param methodExecution Method execution that should be added
	 */
	public void addParallelTest(MethodExecution methodExecution) {
		methods.add(methodExecution);
	}

	/**
	 * Gets all executions of parallel tests.
	 * 
	 * @return List of all executions
	 */
	public List<MethodExecution> getParallelTests() {
		return methods;
	}
}
