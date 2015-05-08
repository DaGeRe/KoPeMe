package de.dagere.kopeme.junit.example.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import de.dagere.kopeme.junit.exampletests.runner.ExampleMethodTimeoutTest;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.ExampleClassTimeoutTest;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.MultipleCallClassTimeout;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.NoTimeoutOutput;

/**
 * Beginn einer Implementierung einer Klasse, die prüft, ob Tests entsprechende Ergebnisse liefern
 * 
 * @author reichelt
 * 
 */
public class TestJUnitTimeoutExecutions {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public static Logger log = LogManager.getLogger(TestJUnitTimeoutExecutions.class);

	@Test(timeout = 500)
	public void testClassTimeout() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleClassTimeoutTest.class);
	}

	@Test(timeout = 1600)
	public void testMethodTimeout() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleMethodTimeoutTest.class);
	}

	@Test
	public void testMultipleClassTimeout() throws InterruptedException {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(MultipleCallClassTimeout.class);
		Thread.sleep(1000L);
		Assert.assertEquals("Test timed out because of class timeout", r.getFailures().get(0).getMessage());
	}

	@Test
	public void testNoTimeout() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(NoTimeoutOutput.class);
		Assert.assertEquals(2, r.getRunCount());
		Assert.assertEquals(0, r.getFailureCount());
	}
	// public void testNormalJUnitExecution(){
	// JUnitCore jc = new JUnitCore();
	// jc.run(ExampleJUnitTests.class);
	// }
}
