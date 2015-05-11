package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class JUnitMultiplicationTest {
	private final static Logger log = LogManager.getLogger(JUnitMultiplicationTest.class);

	@Test
	@PerformanceTest(executionTimes = 5)
	public void testMultiplication() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i * 2;
		}
		Assert.assertEquals(10000 * 9999, a);
		log.debug("Addition finished");
	}
}
