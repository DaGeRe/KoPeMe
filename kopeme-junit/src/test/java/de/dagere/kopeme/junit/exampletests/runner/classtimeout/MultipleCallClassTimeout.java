package de.dagere.kopeme.junit.exampletests.runner.classtimeout;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

/**
 * Example-Testclass for Methods, that does not time out, but take more time than the overall class timeout allows.
 * 
 * @author reichelt
 *
 */
@PerformanceTestingClass(overallTimeout = 1100)
@RunWith(PerformanceTestRunnerJUnit.class)
public class MultipleCallClassTimeout {

	@Test
	@PerformanceTest(warmupExecutions = 10, executionTimes = 10, timeout = 100000)
	public void testClassTimeout() throws InterruptedException {
		Thread.sleep(10);
	}

	@Test
	@PerformanceTest(warmupExecutions = 10, executionTimes = 10, timeout = 100000)
	public void testClassTimeout2() throws InterruptedException {
		Thread.sleep(10);
	}

	@Test
	@PerformanceTest(warmupExecutions = 10, executionTimes = 10, timeout = 100000)
	public void testClassTimeout3() throws InterruptedException {
		Thread.sleep(10);
	}
}
