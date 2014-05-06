package de.dagere.kopeme.example.tests.timeouttests;

import org.junit.Test;
import org.junit.runner.JUnitCore;
/**
 * Beginn einer Implementierung einer Klasse, die prüft, ob Tests entsprechende Ergebnisse liefern
 * @author reichelt
 *
 */
public class TestJUnitExecutions {
	
	@Test(timeout=500)
	public void testClassTimeout(){
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleClassTimeoutTest.class);
	}
}
