package de.dagere.kopeme.junit.exampletests.runner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleNormalJUnitTestWithKoPeMe {

	public static final String ERROR_MESSAGE = "this test will go down";

	@Test
	public void testSimpleNonKoPeMeTest() throws Exception {
		fail(ERROR_MESSAGE);
	}
	
	@Test
	@PerformanceTest
	public void testTestWithKoPeMe() throws Exception {
		System.out.println("easy does it");
	}
	
}
