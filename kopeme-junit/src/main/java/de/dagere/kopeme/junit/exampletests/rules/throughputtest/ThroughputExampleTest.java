package de.dagere.kopeme.junit.exampletests.rules.throughputtest;

import org.junit.Rule;
import org.junit.Test;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.throughput.KoPeMeThroughputRule;

public class ThroughputExampleTest {

	@Rule
	public KoPeMeThroughputRule rule = new KoPeMeThroughputRule(5, 5, 100, this);

	@Test
	@PerformanceTest
	public void testSomething() throws InterruptedException {
		Thread.sleep(10);
	}
}
