package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
@ExtendWith(MockitoExtension.class)
public class InitializeInBeforeEach {

   @Mock
   private Station station;

   @BeforeEach
   void init() {
      Mockito.when(station.getNonStaticStation()).thenReturn("MockedStation");
   }

   @PerformanceTest(warmup = 0, iterations = 2, executeBeforeClassInMeasurement = true)
   @Test
   void testNormal() {
      String result = station.getNonStaticStation();
      Assertions.assertEquals("MockedStation", result);
   }
}
