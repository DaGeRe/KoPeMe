package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;
import junit.framework.Assert;

public class JUnitAdditionTestOnlyTime extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(JUnitAdditionTestOnlyTime.class);

	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
		log.debug("Addition beendet");
	}

	@Override
	protected int getWarmup() {
		return 2;
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
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.ONLYTIME;
	}
}
