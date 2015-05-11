package de.dagere.kopeme.junit.exampletests.runner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleDeviationTest {

	@Test
	@PerformanceTest(executionTimes = 10, warmupExecutions = 10, timeout = 1000, assertions =
	{ @Assertion(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 15000),
			@Assertion(collectorname = "de.dagere.kopeme.datacollection.CPUUsageCollector", maxvalue = 50) })
	public void testAssertionAddition() {
		int a = 0;
		for (int i = 0; i < 100000; i++) {
			a += i;
		}
		Assert.assertEquals(100000 * 99999 / 2, a);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
