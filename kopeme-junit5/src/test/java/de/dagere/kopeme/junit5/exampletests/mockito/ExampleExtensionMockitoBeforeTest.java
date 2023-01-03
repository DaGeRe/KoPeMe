package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.exampletests.mockito.mocked.MockitoCallee;
import de.dagere.kopeme.junit5.exampletests.mockito.mocked.Station;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
public class ExampleExtensionMockitoBeforeTest {

   private static MockedStatic<Station> station;

   @BeforeWithMeasurement(priority = 2)
   static void initializeMocks() {
      Mockito.clearAllCaches();
      station = Mockito.mockStatic(Station.class);
   }

   @BeforeEach
   void init() {
      station.when(() -> Station.getStation()).thenReturn("MockedStation");
   }

   @PerformanceTest(warmup = 0, iterations = 2, executeBeforeClassInMeasurement = true)
   @Test
   void testNormal() {
      final MockitoCallee exampleClazz = new MockitoCallee();
      String result = exampleClazz.method1();
      Assertions.assertEquals("MockedStation", result);
   }
}
