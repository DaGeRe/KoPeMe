package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test for testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleUnallowedBeforeWithMeasurement {

   @BeforeWithMeasurement
   public void doSomethingWithCallee() {
      System.out.println("I am not allowed");
   }

   @Test
   @PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testNormal() {
   }
}
