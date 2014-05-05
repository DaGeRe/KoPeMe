package de.dagere.kopeme.example.tests.timeouttests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
@PerformanceTestingClass(overallTimeout=400)
public class ExampleClassTimeoutTest {
	
	@BeforeClass
	public static void veryLongSetup(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@PerformanceTest(executionTimes=5, warmupExecutions=2)
	public void testVeryLong() {
		System.out.println("Sleep Example");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@PerformanceTest(executionTimes=5, warmupExecutions=2)
	public void testVeryShort() {
		System.out.println("Sleep Example");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
