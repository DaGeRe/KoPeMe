package de.dagere.kopeme.junit.exampletests.rules.throughputtest;

import org.junit.Rule;
import org.junit.Test;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.throughput.KoPeMeThroughputRule;

public class ThroughputExampleTest {

	@Rule
	private KoPeMeThroughputRule rule = new KoPeMeThroughputRule(5, 5, 100, this);

	@Test
	@PerformanceTest
	public void testSomething() throws InterruptedException {
		for (int i = 0; i < rule.getCurrentSize(); i++) {
			Thread.sleep(10);
		}
	}
}
