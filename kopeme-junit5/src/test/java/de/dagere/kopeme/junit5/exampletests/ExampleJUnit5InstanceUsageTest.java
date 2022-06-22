package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
public class ExampleJUnit5InstanceUsageTest {

   private Object myObject;

   public static int finished = 0;

   @BeforeNoMeasurement(priority = 2)
   public void setupStuff() {
      System.out.println("Executing before");
      myObject = new Object();
   }

   @Test
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 50000, dataCollectors = "ONLYTIME", useKieker = false, executeBeforeClassInMeasurement = true)
   public void testNormal() {
      System.out.println(myObject.toString());

      finished++;
   }
}