package de.dagere.kopeme.exampletests.junit.rules;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.testrunner.RunRule;

public class JUnitRuleTest {
	@Rule
	public TestRule rule = new RunRule();
	
	@Test(timeout=400)
	@PerformanceTest(executionTimes = 5, timeout=1000)
	public void testTimeout() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@PerformanceTest(executionTimes = 5, timeout=1000)
	public void testNormal() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
