package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class JUnit3EndlessTest extends KoPeMeTestcase {
	private final static Logger log = LogManager.getLogger(JUnit3EndlessTest.class);

	public void testEndless() {
		while (true) {
			log.debug("Simply doing nothing in endless loop..");
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	protected long getMaximalTime() {
		return 5000;
	}

	@Override
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.ONLYTIME;
	}
}
