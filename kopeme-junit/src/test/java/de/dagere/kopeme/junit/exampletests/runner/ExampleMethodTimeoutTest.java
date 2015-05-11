package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleMethodTimeoutTest {

	private static final Logger log = LogManager.getLogger(ExampleMethodTimeoutTest.class);

	@Test
	@PerformanceTest(executionTimes = 5, timeout = 500)
	public void testSleep() {
		log.debug("Sleep Example");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
