package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleBeforeClassTest {

	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@BeforeClass
	public static void beforeTest() throws InterruptedException {
		//System.out.println("BeforeClass");
		long start = System.nanoTime();
		Thread.sleep(100);
		System.out.println("BeforeClass, slept for: " + (System.nanoTime()-start));
	}

	@Test
	@PerformanceTest
	public void spendTime() throws InterruptedException {
		//System.out.println("SpendTime");
		long start = System.nanoTime();
		Thread.sleep(100);
		System.out.println("spendTime, slept for: " + (System.nanoTime() - start));
	}

}
