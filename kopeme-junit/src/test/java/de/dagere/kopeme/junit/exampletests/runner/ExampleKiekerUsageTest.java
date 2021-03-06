package de.dagere.kopeme.junit.exampletests.runner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

interface MyTestInterface {
	public default int execute(int myValue) {
		return myValue + 1;
	}
}

// TODO This exampletest is currently only used manually - a test which starts this with different writers should be added
@RunWith(PerformanceTestRunnerJUnit.class)
@PerformanceTestingClass(overallTimeout = Integer.MAX_VALUE)
public class ExampleKiekerUsageTest {

	@Test
	@PerformanceTest(timeout = Integer.MAX_VALUE, iterations = 2, warmup = 2, useKieker = true)
	public void testAssertionAddition() throws FileNotFoundException {
		final MyTestInterface impl = new MyTestInterface() {
		};
		System.out.println(impl.execute(2));
		
		int a = 0;
		OutputStream nullStream = new OutputStream() {
			@Override
			public void write(int arg0) throws IOException {
				
			}
		};
		System.setOut(new PrintStream(nullStream));
		for (int i = 0; i < 100; i++) {
			a += i;
			System.out.println(callMe(a));
		}
	}
	
	@Test
   @PerformanceTest(timeout = Integer.MAX_VALUE, iterations = 2, warmup = 2, useKieker = true)
   public void testSecondStuffAddition() throws FileNotFoundException {
      System.out.println("Just run a second method, in order to asure, that kieker.map is written twice");
   }

	public int callMe(int value) {
		return callMe2(value) * 4;
	}

	public int callMe2(int value) {
		return value * 2;
	}
	
}
