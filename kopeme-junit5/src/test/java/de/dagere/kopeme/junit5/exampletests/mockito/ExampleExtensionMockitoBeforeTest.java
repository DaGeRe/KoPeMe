package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
public class ExampleExtensionMockitoBeforeTest {
   
   private static MockedStatic<Station> station;
   
   @BeforeAll
   static void initializeMocks() {
       station = Mockito.mockStatic(Station.class);
   }

   @BeforeEach
   void init() {
       station.when(() -> Station.getStation()).thenReturn("MockedStation");
   }

   @PerformanceTest(warmup = 0, iterations = 2)
   @Test
   void test() {
       final MockitoCallee exampleClazz = new MockitoCallee();
       String result = exampleClazz.method1();
       Assertions.assertEquals("MockedStation", result);
   }

   @PerformanceTest(warmup = 0, iterations = 2)
   @Test
   void test2() {
       final MockitoCallee exampleClazz = new MockitoCallee();
       String result = exampleClazz.method1();
       Assertions.assertEquals("MockedStation", result);
   }
}
