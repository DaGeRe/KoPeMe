package de.dagere.kopeme.junit.tests;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.junit.exampletests.runner.ExampleAssertionTest;

public class TestAssertion {
	@Test
	public void testAssertionFailure() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(ExampleAssertionTest.class);
		Assert.assertThat(r.getFailures(), Matchers.not(Matchers.empty()));
		Failure f = r.getFailures().get(0);
		Assert.assertEquals(AssertionError.class, f.getException().getClass());
	}
}
