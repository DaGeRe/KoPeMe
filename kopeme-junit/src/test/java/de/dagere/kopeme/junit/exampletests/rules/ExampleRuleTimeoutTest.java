package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleRuleTimeoutTest {
	
	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@Before
	public void setUp() {
		System.out.println("Führe aus");
		try {
			Thread.sleep(200);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(timeout = 400)
	@PerformanceTest(executionTimes = 5, timeout = 1000)
	public void testTimeout() {
		try {
			Thread.sleep(300);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
