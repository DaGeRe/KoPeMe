package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleNoBeforeTest {
	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@Test
	@PerformanceTest
	public void spendTime() throws InterruptedException {
		System.out.println("SpendTime");
		Thread.sleep(100);
	}
}
