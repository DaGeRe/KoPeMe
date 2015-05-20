package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class RuleAssertionTest {
	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@Test
	@PerformanceTest(executionTimes = 5, assertions = { @Assertion(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 200000) })
	public void testTimeout() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
