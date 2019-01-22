package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
public class ExampleRuleTest {
	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@Before
	public void setUp() {
		System.out.println("Führe aus");
		try {
			Thread.sleep(50);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	

	@Test
	@PerformanceTest(executionTimes = 20, timeout = 500)
	public void testNormal() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
