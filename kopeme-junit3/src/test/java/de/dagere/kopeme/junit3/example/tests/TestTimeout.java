package de.dagere.kopeme.junit3.example.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.junit.exampletests.runner.JUnitTimeoutTest;

public class TestTimeout extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}
	
	private static final Logger logger = LogManager.getLogger(TestTimeout.class);

	public void testOnlyTimeWriting() {
		long start = System.nanoTime();
		TestRunner.run(JUnitTimeoutTest.class);
		long stop = System.nanoTime();

		long duration = (long) ((stop - start) / 10E5);
		logger.info("Zeit: " + duration);
		Assert.assertTrue(duration < 1500);
	}

}
