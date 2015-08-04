package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;
import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;

public class ExampleNonMeasuringBefore {

	@Rule
	public TestRule rule = new KoPeMeRule(this);

	@BeforeNoMeasurement
	public void spendSomeTimeBefore() throws InterruptedException {
		System.out.println("Before");
		Thread.sleep(100);
	}

	@AfterNoMeasurement
	public void spendSomeTimeAfter() throws InterruptedException {
		System.out.println("After");
		Thread.sleep(100);
	}

	@AfterNoMeasurement
	public void spendSomeTimeAfter2() throws InterruptedException {
		System.out.println("After 2");
		Thread.sleep(100);
	}

	@PerformanceTest
	@Test
	public void spendTime() throws InterruptedException {
		System.out.println("Spend Time");
		Thread.sleep(110);
	}
}
