package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleRule5Test {
   

	@BeforeEach
	public void setUp() {
		System.out.println("Führe aus");
		try {
			Thread.sleep(10);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	

	@Test
	@PerformanceTest(warmupExecutions = 5, executionTimes = 50, timeout = 50000)
	public void testNormal() {
	   System.out.println("Normal");
		int a = 0;
		for (int i = 0; i < 10000; i++) {
			a += i;
		}
		Assert.assertEquals(10000 * 9999 / 2, a);
	}
}
