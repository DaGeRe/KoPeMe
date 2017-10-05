package de.dagere.kopeme.junit.exampletests.time;

import de.dagere.kopeme.junit3.TimeBasedTestcase;

public class TimeExample1 extends TimeBasedTestcase {

	public void testMe() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < 10; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
	
	@Override
	public long getDuration() {
		final String env = System.getenv("DURATION");
		return env != null ? Integer.parseInt(env) : 10000;
	}
}
