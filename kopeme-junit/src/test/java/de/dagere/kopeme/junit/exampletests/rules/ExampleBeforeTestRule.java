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
		//System.out.println("Init");

//		long start = System.nanoTime();
//		Thread.sleep(60);
//		System.out.println("Before(60), slept for: " + (System.nanoTime()-start));

		long start = System.nanoTime();
		long delay = start + 60000000;

		while (System.nanoTime() < delay) {
			Thread.sleep(0);
		}

		System.out.println("Before(60), slept for: " + (System.nanoTime()-start));

	}

	@Test
	@PerformanceTest
	public void spendTime() throws InterruptedException {
		//System.out.println("SpendTime");
//		long start = System.nanoTime();
//		Thread.sleep(60);

		long start = System.nanoTime();
		long delay = start + 60000000;

		while (System.nanoTime() < delay) {
			Thread.sleep(0);
		}

		System.out.println("spendTime(60), slept for: " + (System.nanoTime() - start));
	}
}
