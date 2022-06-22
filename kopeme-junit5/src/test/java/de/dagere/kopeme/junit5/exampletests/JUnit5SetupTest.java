package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class JUnit5SetupTest {

   @BeforeNoMeasurement
   public static void setUp() {
      System.out.println("Executing Setup");
      try {
         Thread.sleep(500);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

   @BeforeWithMeasurement
   public static void setUp2() {
      System.out.println("Executing Measured Setup");
      try {
         Thread.sleep(500);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

   @Test
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", executeBeforeClassInMeasurement = true)
   public void testNormal() {
      System.out.println("Normal Execution");
      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
