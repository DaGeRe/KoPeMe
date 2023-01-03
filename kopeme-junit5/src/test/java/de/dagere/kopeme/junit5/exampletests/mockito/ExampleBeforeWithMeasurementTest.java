package de.dagere.kopeme.junit5.exampletests.mockito;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.exampletests.mockito.mocked.Station;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(KoPeMeExtension.class)
@ExtendWith(MockitoExtension.class)
class ExampleBeforeWithMeasurementTest {
   @Mock
   private Station station;

   //This test fails if init methode run with @BeforeWithMeasurement
   //@BeforeEach
   @BeforeWithMeasurement(priority = 2)
   void init() {
      Mockito.when(station.getNonStaticStation()).thenReturn("MockedStation");
   }

   @Disabled
   @PerformanceTest(iterations = 5, warmup = 0, executeBeforeClassInMeasurement = true, repetitions = 10, dataCollectors = "ONLYTIME_NOGC")
   @Test
   void testNormal() {
      String result = station.getNonStaticStation();
      Assertions.assertEquals("MockedStation", result);
   }
}