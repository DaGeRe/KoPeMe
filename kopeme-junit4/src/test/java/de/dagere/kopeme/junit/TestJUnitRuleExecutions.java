package de.dagere.kopeme.junit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleTest;

/**
 * Beginn einer Implementierung einer Klasse, die prüft, ob Tests entsprechende
 * Ergebnisse liefern
 * 
 * @author reichelt
 * 
 */
public class TestJUnitRuleExecutions {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public static Logger log = LogManager.getLogger(TestJUnitRuleExecutions.class);

	@Test(timeout = 2300)
	public void testMethodTimeout() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleRuleTest.class);
	}
}
