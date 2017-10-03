package de.dagere.kopeme.junit.exampletests.time;

import java.util.Random;

import de.dagere.kopeme.junit3.TimeBasedTestcase;

public class SysoutExample extends TimeBasedTestcase {

	public void testMe() {
		System.out.println(new Random().nextInt(100));
	}
	
	@Override
	public long getDuration() {
		return 10000;
	}
}
