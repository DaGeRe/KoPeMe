package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.exampletests.mockito.mocked.Station;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
public class ExampleCleanCachesByMockMethodTest {
   private static Station station;

   @BeforeWithMeasurement(priority = 2)
   static void init() {
      station = Mockito.mock(Station.class);
      Mockito.when(station.getNonStaticStation()).thenReturn("MockedStation");
   }

   @PerformanceTest(iterations = 5, warmup = 0, executeBeforeClassInMeasurement = true, repetitions = 10, dataCollectors = "ONLYTIME_NOGC", logFullData = true, showStart = true)
   @Test
   void testNormal() {
      String result = station.getNonStaticStation();
      Assertions.assertEquals("MockedStation", result);
   }

   @de.dagere.kopeme.junit.rule.annotations.AfterWithMeasurement(priority = 5)
   public void _peass_initializeMockito() {
      Mockito.clearAllCaches();
   }
}