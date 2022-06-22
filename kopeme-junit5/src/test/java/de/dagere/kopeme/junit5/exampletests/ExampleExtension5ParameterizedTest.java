package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test for testing whether the KoPeMe-Extension works correct together with Parameterized
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleExtension5ParameterizedTest {

   @ParameterizedTest
   @ValueSource(ints = { 0, 1 })
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 50000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testNormal(int value) {
      System.out.println("Normal Execution: " + value);
   }

   @DisplayName("parameterTest")
   @ParameterizedTest(name = "run #{index} with [parameter1: {0}, parameter2: {1}]")
   @CsvSource({ "1, 0", "2, 0", "3, 3" })
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 50000, dataCollectors = "ONLYTIME", useKieker = false)
   void parameterizedTest(int parameter1, int parameter2) {
      System.out.println("parameter1:" + parameter1 + " parameter2: " + parameter2);
   }
}
