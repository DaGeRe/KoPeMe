package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

class Callee {
   int method1() {
      return 15;
   }
}

/**
 * An example test for testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExampleExtension5SimpleMockitoTest {

   @Mock
   static Callee myCallee;

   @BeforeWithMeasurement
   public void doSomethingWithCallee() {
      Mockito.when(myCallee.method1()).thenReturn(16);
   }
   
   @Test
   @PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testNormal() {
      Assert.assertEquals(16, myCallee.method1());
   }
}
