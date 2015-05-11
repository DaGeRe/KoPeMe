package de.dagere.kopeme.junit.exampletests.rules.throughputtest;

import org.junit.Rule;
import org.junit.Test;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.junit.rule.throughput.KoPeMeThroughputRule;

public class SimpleThroughputExampleTest {

	@Rule
	public KoPeMeThroughputRule rule = new KoPeMeThroughputRule(10, 100, this);

	@Test
	@PerformanceTest(assertions = { @Assertion(collectorname = TimeDataCollector.NAME, maxvalue = 400000) })
	public void throughputTest() throws InterruptedException {
		Thread.sleep(10);
	}
}
