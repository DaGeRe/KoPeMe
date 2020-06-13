package de.dagere.kopeme.junit3.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.TestUtils;
import junit.framework.TestCase;

/**
 * Testing needs to spawn new JVM - no unit test possible currently.
 * @author reichelt
 *
 */
public class TestTimeout extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}

	private static final Logger LOG = LogManager.getLogger(TestTimeout.class);

	public void testOnlyTimeWriting() {
//		long start = System.nanoTime();
//		TestRunner.run(TimeoutTestJUnit3.class);
//		long stop = System.nanoTime();
//
//		long duration = (long) ((stop - start) / 10E5);
//		LOG.info("Zeit: " + duration);
//		Assert.assertTrue(duration < 3500);
	}
//
//	public void testSetupTimeout() {
//		long start = System.nanoTime();
//		TestRunner.run(TimeoutSetupJUnit3.class);
//		long stop = System.nanoTime();
//
//		long duration = (long) ((stop - start) / 10E5);
//		LOG.info("Zeit: " + duration);
//		Assert.assertTrue(duration < 1500);
//	}

}
