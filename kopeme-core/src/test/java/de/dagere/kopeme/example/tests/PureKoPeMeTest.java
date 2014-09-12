package de.dagere.kopeme.example.tests;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.exampletests.pure.ExamplePurePerformanceTests;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerKoPeMe;

public class PureKoPeMeTest {
	@Test
	public void testPureKoPeMeExecution(){
		String params[] = new String[]{ExamplePurePerformanceTests.class.getName()};
		try {
			PerformanceTestRunnerKoPeMe.main(params);
		} catch (Throwable e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
}