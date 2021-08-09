package de.dagere.kopeme.junit.tests;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestNoGC;


public class TestAssertion {
	
	@Test
	public void testAssertionFailure() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(JUnitAdditionTestNoGC.class);
		MatcherAssert.assertThat(r.getFailures(), Matchers.empty());
	}
	
}
