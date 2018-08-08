package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class TimeoutTimeoutTwoMethods extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(TimeoutTimeoutTwoMethods.class);

	public void testAddition() throws InterruptedException {
			Thread.sleep(300);
	}

	@Override
	protected int getWarmupExecutions() {
		return 2;
	}

	@Override
	protected int getExecutionTimes() {
		return 10;
	}

	@Override
	protected boolean logFullData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected boolean useKieker() {
		return false;
	}

	@Override
	protected long getMaximalTime() {
		return 1000;
	}

}
