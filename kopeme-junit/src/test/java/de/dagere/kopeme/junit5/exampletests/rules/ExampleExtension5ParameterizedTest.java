package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

/**
 * An example test for testing whether the KoPeMe-Extension works correct together with Parameterized
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleExtension5ParameterizedTest {

   @ParameterizedTest
   @ValueSource(ints = {0, 1})
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 50000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testNormal(int value) {
      System.out.println("Normal Execution: " + value);
   }
}
