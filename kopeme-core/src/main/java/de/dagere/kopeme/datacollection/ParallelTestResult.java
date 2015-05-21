package de.dagere.kopeme.datacollection;

import java.util.LinkedList;
import java.util.List;

import de.dagere.kopeme.paralleltests.MethodExecution;

/**
 * Handles Results for parallel tests
 * 
 * @author reichelt
 *
 */
public class ParallelTestResult extends TestResult {

	/**
	 * Initializes the TestResult with a Testcase-Name and the executionTimes.
	 * 
	 * @param testcase Name of the Testcase
	 * @param executionTimes Count of the planned executions
	 */
	public ParallelTestResult(String testcase, int executionTimes) {
		super(testcase, executionTimes);

		methods = new LinkedList<MethodExecution>();
	}

	private List<MethodExecution> methods;

	/**
	 * Adds a parallel test executions
	 * 
	 * @param methodExecution
	 */
	public void addParallelTest(MethodExecution methodExecution) {
		methods.add(methodExecution);
	}

	/**
	 * Gets all executions of parallel tests
	 * 
	 * @return
	 */
	public List<MethodExecution> getParallelTests() {
		return methods;
	}
}
