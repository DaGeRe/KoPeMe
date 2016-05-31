package de.dagere.kopeme.junit.exampletests.runner;

import junit.framework.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class ExampleAdditionTest extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(ExampleAdditionTest.class);

	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
		log.debug("Addition beendet");
		giveMeOutput();
	}

	private void giveMeOutput() {
		System.out.println("Test");
	}

	@Override
	protected int getWarmupExecutions() {
		return 200;
	}

	@Override
	protected int getExecutionTimes() {
		return 500;
	}

	@Override
	protected boolean logFullData() {
		return false;
	}

	@Override
	protected boolean useKieker() {
		return true;
	}

	@Override
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.STANDARD;
	}
}
