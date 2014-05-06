package de.dagere.kopeme.example.tests;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.exampletests.junit.ExampleClassTimeoutTest;
import de.dagere.kopeme.exampletests.junit.ExampleMethodTimeoutTest;
/**
 * Beginn einer Implementierung einer Klasse, die prüft, ob Tests entsprechende Ergebnisse liefern
 * @author reichelt
 *
 */
public class TestJUnitTimeoutExecutions {
	
	public static Logger log = LogManager.getLogger(TestJUnitTimeoutExecutions.class);
	
	@Test(timeout=500)
	public void testClassTimeout(){
		log.debug("Teste Klassen-Timeout");
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleClassTimeoutTest.class);
	}
	
	@Test(timeout=1600)
	public void testMethodTimeout(){
		log.debug("Teste Methoden-Timeout");
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleMethodTimeoutTest.class);
	}
}
