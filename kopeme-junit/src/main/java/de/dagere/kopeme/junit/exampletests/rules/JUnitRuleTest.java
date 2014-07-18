package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.testrunner.KoPeMeRule;

/**
 * An example test für testing weather the KoPeMe-TestRule works
 * correct
 * @author reichelt
 *
 */
public class JUnitRuleTest {
	@Rule
	public TestRule rule = new KoPeMeRule();
	
	@Before
	public void setUp(){
		System.out.println("Führe aus");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(timeout=400)
	@PerformanceTest(executionTimes = 5, timeout=1000)
	public void testTimeout() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@PerformanceTest(executionTimes = 5, timeout=1000)
	public void testNormal() {
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
