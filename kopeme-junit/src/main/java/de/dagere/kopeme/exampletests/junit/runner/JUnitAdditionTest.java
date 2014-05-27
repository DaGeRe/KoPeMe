package de.dagere.kopeme.exampletests.junit.runner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class JUnitAdditionTest {
	@Test
	@PerformanceTest(executionTimes = 5)
	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
