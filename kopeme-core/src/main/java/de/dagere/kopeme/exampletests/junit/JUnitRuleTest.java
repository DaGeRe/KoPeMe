package de.dagere.kopeme.exampletests.junit;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.testrunner.RunRule;

public class JUnitRuleTest {
	@Rule
	public TestRule screenshot = new RunRule();
	
	@Test
	@PerformanceTest(executionTimes = 5)
	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
