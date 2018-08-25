package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class TimeoutSetupJUnit3 extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(TimeoutSetupJUnit3.class);

	@Override
	public void setUp() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Warte 1 Sekunde");
		}
	}

	public void testAddition() {
		try {
			Thread.sleep(300);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		return 900;
	}

}
