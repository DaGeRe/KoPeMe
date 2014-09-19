package de.dagere.kopeme.junit.exampletests.runner;

import junit.framework.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JUnitAdditionTestFullData {
	private final static Logger log = LogManager.getLogger(JUnitAdditionTestFullData.class);

	public void testAddition() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
		log.debug("Addition beendet");
	}
}
