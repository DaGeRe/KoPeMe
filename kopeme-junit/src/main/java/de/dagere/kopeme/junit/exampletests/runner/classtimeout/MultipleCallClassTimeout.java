package de.dagere.kopeme.junit.exampletests.runner.classtimeout;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass(overallTimeout = 250)
@RunWith(PerformanceTestRunnerJUnit.class)
public class MultipleCallClassTimeout {

	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 5, timeout = 100000)
	public void test() throws InterruptedException {
		Thread.sleep(5);
	}

	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 5, timeout = 100000)
	public void test2() throws InterruptedException {
		Thread.sleep(5);
	}
	
	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 5, timeout = 100000)
	public void test3() throws InterruptedException {
		Thread.sleep(5);
	}
}
