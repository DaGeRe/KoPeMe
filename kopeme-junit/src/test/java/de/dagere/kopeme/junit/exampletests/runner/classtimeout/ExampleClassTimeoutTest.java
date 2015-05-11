package de.dagere.kopeme.junit.exampletests.runner.classtimeout;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
@PerformanceTestingClass(overallTimeout=400)
public class ExampleClassTimeoutTest {
	
	@BeforeClass
	public static void veryLongSetup(){
		System.out.println("Starte...");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@PerformanceTest(executionTimes=5, warmupExecutions=2)
	public void testMethod() {
		System.out.println("Sleep Example");
//		Thread.dumpStack();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
