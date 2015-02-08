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
		System.out.println("BeforeClass");
		Thread.sleep(100);
	}

	@Test
	@PerformanceTest
	public void spendTime() throws InterruptedException {
		System.out.println("SpendTime");
		Thread.sleep(100);
	}

}
