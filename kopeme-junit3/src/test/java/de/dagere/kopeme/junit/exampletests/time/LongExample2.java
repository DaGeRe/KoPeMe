package de.dagere.kopeme.junit.exampletests.time;

import de.dagere.kopeme.junit3.TimeBasedTestcase;

public class LongExample2 extends TimeBasedTestcase {

	public void testMe() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < 10001; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
	
	@Override
	public long getDuration() {
		return 10000;
	}
}
