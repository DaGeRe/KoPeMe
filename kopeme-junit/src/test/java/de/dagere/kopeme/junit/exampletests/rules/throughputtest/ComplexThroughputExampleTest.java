package de.dagere.kopeme.junit.exampletests.rules.throughputtest;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.junit.rule.throughput.KoPeMeComplexThroughtputRule;

public class ComplexThroughputExampleTest {

	@Rule
	public KoPeMeComplexThroughtputRule rule = new KoPeMeComplexThroughtputRule(10, 10, 100, this);

	private List<Integer> waitTimes;

	@Before
	public void initList() {
		waitTimes = new LinkedList<Integer>();
		for (int i = 0; i < rule.getCurrentSize(); i++) {
			waitTimes.add(10);
		}
	}

	@Test
	@PerformanceTest(assertions = { @Assertion(collectorname = TimeDataCollector.NAME, maxvalue = 400000) })
	public void throughputTest() throws InterruptedException {
		for (Integer time : waitTimes)
			Thread.sleep(time);
	}
}
