package de.dagere.kopeme.junit5.exampletests;

import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(KoPeMeExtension.class)
public class ExampleParallelTest {

   private static boolean shouldBeChangedWithCaution = false;

   @ValueSource(ints = {10, 15, 20, 25, 30})
   @PerformanceTest(iterations = 5, warmup = 0, executeBeforeClassInMeasurement = true, repetitions = 10, dataCollectors = "ONLYTIME_NOGC")
   @ParameterizedTest
   void testNormal(int value) throws InterruptedException {
      for (int i = 0; i < value; i++) {
         System.out.println("Testing " + i + " " + Thread.currentThread().getName());
         boolean oldValue = shouldBeChangedWithCaution;
         shouldBeChangedWithCaution = !shouldBeChangedWithCaution;
         Thread.sleep(1);
         Assert.assertNotEquals(oldValue, shouldBeChangedWithCaution);
      }
   }
}
