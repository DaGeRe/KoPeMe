package de.dagere.kopeme.junit.tests;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.junit.exampletests.runner.ExampleAssertionTest;
import de.dagere.kopeme.junit.exampletests.runner.ExampleNormalJUnitTestWithKoPeMe;


public class TestNoGC {
	
	@Test
	public void testAssertionFailure() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(ExampleAssertionTest.class);
		MatcherAssert.assertThat(r.getFailures(), Matchers.not(Matchers.empty()));
		Failure f = r.getFailures().get(0);
		Assert.assertEquals(AssertionError.class, f.getException().getClass());
	}
	
	@Test
	public void testNonKoPeMeTestsWillBeEvaluatedToo(){
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(ExampleNormalJUnitTestWithKoPeMe.class);
		MatcherAssert.assertThat(r.getFailures(), Matchers.not(Matchers.empty()));
		Failure f = r.getFailures().get(0);
		Assert.assertEquals(AssertionError.class, f.getException().getClass());
		Assert.assertEquals(ExampleNormalJUnitTestWithKoPeMe.ERROR_MESSAGE, f.getMessage());
		Assert.assertEquals(2, r.getRunCount());
	}
}
