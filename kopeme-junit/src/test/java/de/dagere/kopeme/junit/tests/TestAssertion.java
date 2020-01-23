package de.dagere.kopeme.junit.tests;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.junit.exampletests.runner.ExampleAssertionTest;
import de.dagere.kopeme.junit.exampletests.runner.ExampleNormalJUnitTestWithKoPeMe;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestNoGC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class TestAssertion {
	
	@Test
	public void testAssertionFailure() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(JUnitAdditionTestNoGC.class);
		assertThat(r.getFailures(), Matchers.empty());
	}
	
}
