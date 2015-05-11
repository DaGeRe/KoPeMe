package de.dagere.kopeme.junit.exampletests.runner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleBeforeTestRunner {

	@BeforeClass
	public static void beforeClass() {
		System.out.println("Starte...");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void testTest() throws InterruptedException {
		System.out.println("Before");
		Thread.sleep(100);
	}

	@Test
	@PerformanceTest(executionTimes = 5, warmupExecutions = 2)
	public void testMethod() throws InterruptedException {
		System.out.println("Sleep Example");
		Thread.sleep(100);
	}

}
