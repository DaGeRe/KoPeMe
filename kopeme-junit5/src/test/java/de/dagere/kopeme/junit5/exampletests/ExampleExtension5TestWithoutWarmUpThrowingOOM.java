package de.dagere.kopeme.junit5.exampletests;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(KoPeMeExtension.class)
public class ExampleExtension5TestWithoutWarmUpThrowingOOM {

   @Test
   @PerformanceTest(warmup = 0, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testNormal() {
      System.out.println("Normal Execution");
      String[] array = new String[100000 * 100000];
   }
}