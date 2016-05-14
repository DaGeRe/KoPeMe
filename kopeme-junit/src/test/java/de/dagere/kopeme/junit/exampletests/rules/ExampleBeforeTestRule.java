package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleBeforeTestRule {

	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@Before
	public void init() throws InterruptedException {
		System.out.println("Init");
		Thread.sleep(60);
	}

	@Test
	@PerformanceTest
	public void spendTime() throws InterruptedException {
		System.out.println("SpendTime");
		Thread.sleep(60);
	}
}
