package de.dagere.kopeme.junit.exampletests.time;

import de.dagere.kopeme.junit3.TimeBasedTestcase;

public class EmptyExample  extends TimeBasedTestcase{
	public void testMe() {
		
	}

	@Override
	public long getDuration() {
		return 10000;
	}
}
