package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;
import junit.framework.Assert;

public class ExampleAdditionTest extends KoPeMeTestcase {
	private final static Logger LOG = LogManager.getLogger(ExampleAdditionTest.class);

	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 1000; i++) {
			a += i;
		}
		Assert.assertEquals(1000 * 999 / 2, a);
		LOG.debug("Addition beendet");
		giveMeOutput();
	}

	private void giveMeOutput() {
		System.out.println("Test");
	}

	@Override
	protected int getWarmup() {
		return 50;
	}

	@Override
	protected int getIterations() {
		return 10;
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
