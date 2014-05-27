package de.dagere.kopeme.exampletests.junit.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleMethodTimeoutTest {

	@Test
	@PerformanceTest(executionTimes=5, timeout=500)
	public void testSleep() {
		System.out.println("Sleep Example");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
