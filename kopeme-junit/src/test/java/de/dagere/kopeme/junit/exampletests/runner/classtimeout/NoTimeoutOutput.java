package de.dagere.kopeme.junit.exampletests.runner.classtimeout;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass(overallTimeout = 10000)
@RunWith(PerformanceTestRunnerJUnit.class)
public class NoTimeoutOutput {

	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 5, timeout = 150000)
	public void testFastMethod() throws InterruptedException {
		Thread.sleep(5);
	}

	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 5, timeout = 150000)
	public void testFastMethod2() throws InterruptedException {
		Thread.sleep(5);
	}
}
