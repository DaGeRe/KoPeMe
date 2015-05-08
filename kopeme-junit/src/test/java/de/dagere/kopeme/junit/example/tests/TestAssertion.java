package de.dagere.kopeme.junit.example.tests;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.junit.example.tests.assertation.ExampleNormalJUnitTestWithKoPeMe;
import de.dagere.kopeme.junit.exampletests.runner.ExampleAssertionTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class TestAssertion {
	
	@Test
	public void testAssertionFailure() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(ExampleAssertionTest.class);
		assertThat(r.getFailures(), Matchers.not(Matchers.empty()));
		Failure f = r.getFailures().get(0);
		assertEquals(AssertionError.class, f.getException().getClass());
	}
	
	@Test
	public void testNonKoPeMeTestsWillBeEvaluatedToo(){
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(ExampleNormalJUnitTestWithKoPeMe.class);
		assertThat(r.getFailures(), Matchers.not(Matchers.empty()));
		Failure f = r.getFailures().get(0);
		assertEquals(AssertionError.class, f.getException().getClass());
		assertEquals(ExampleNormalJUnitTestWithKoPeMe.ERROR_MESSAGE, f.getMessage());
		assertEquals(2, r.getRunCount());
	}
}
