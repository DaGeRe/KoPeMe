package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.jupiter.api.AfterEach;
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
		System.out.println("Executing Setup");
		try {
			Thread.sleep(5);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@AfterEach
   public void tearDown() {
      System.out.println("Executing Teardown");
      try {
         Thread.sleep(5);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME")
	public void testNormal() {
	   System.out.println("Normal Execution");
	   try {
         Thread.sleep(15);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
	}
}
