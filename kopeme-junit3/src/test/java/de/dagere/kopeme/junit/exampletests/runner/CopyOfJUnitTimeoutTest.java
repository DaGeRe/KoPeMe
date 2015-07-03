package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class CopyOfJUnitTimeoutTest extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(CopyOfJUnitTimeoutTest.class);

	public void testAddition() {
		System.out.println("Test");
		doSomething2();
	}

	public void doSomething2() {
		System.out.println("test2");
	}

	@Override
	protected int getWarmupExecutions() {
		return 3000;
	}

	@Override
	protected int getExecutionTimes() {
		return 3000;
	}

	@Override
	protected boolean logFullData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int getMaximalTime() {
		return 1000;
	}

}
