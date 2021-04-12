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
		//System.out.println("Before");
		long start = System.nanoTime();
		Thread.sleep(100);
		System.out.println("spendSomeTimeBefore, slept for: " + (System.nanoTime() - start));
	}

	@AfterNoMeasurement
	public void spendSomeTimeAfter() throws InterruptedException {
		//System.out.println("After");
		long start = System.nanoTime();
		Thread.sleep(100);
		System.out.println("spendSomeTimeAfter, slept for: " + (System.nanoTime() - start));
	}

	@AfterNoMeasurement
	public void spendSomeTimeAfter2() throws InterruptedException {
		//System.out.println("After 2");
		long start = System.nanoTime();
		Thread.sleep(100);
		System.out.println("spendSomeTimeAfter2, slept for: " + (System.nanoTime() - start));
	}

	@PerformanceTest
	@Test
	public void spendTime() throws InterruptedException {
		//System.out.println("Spend Time");
		long start = System.nanoTime();
		Thread.sleep(110);
		System.out.println("spendTime(110), slept for: " + (System.nanoTime() - start));
	}
}
