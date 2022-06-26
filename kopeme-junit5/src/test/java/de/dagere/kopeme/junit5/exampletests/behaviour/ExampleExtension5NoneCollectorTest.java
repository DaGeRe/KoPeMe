package de.dagere.kopeme.junit5.exampletests.behaviour;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test for testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleExtension5NoneCollectorTest {

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "NONE", useKieker = false)
	public void testNormal() {
	   System.out.println("Normal Execution");
	   try {
         Thread.sleep(5);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
	}
}
