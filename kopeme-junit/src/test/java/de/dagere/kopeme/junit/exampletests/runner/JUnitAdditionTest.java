package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

/**
 * Basic example test that executes additions
 * 
 * @author reichelt
 *
 */
@RunWith(PerformanceTestRunnerJUnit.class)
public class JUnitAdditionTest {
	private final static Logger log = LogManager.getLogger(JUnitAdditionTest.class);

	@Before
	public void setup() {
		log.debug("Before wird aufgerufen");
	}

	@Test
	@PerformanceTest(executionTimes = 5, repetitions = 100)
	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
		log.debug("Addition beendet");
	}
}
