package de.dagere.kopeme.junit.exampletests.timebased;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.time.TimeBasedTestRunner;

@RunWith(TimeBasedTestRunner.class)
public class TimeExample2 {

	@Test
	@PerformanceTest(duration=1000, repetitions = 100)
	public void testMe() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < 6; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
	
}
