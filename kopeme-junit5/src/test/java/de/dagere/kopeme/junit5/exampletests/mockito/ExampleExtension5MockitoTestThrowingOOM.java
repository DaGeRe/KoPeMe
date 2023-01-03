package de.dagere.kopeme.junit5.exampletests.mockito;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

@ExtendWith(KoPeMeExtension.class)
public class ExampleExtension5MockitoTestThrowingOOM {

   @InjectMocks
   private MyMockClazz mapper;

   @BeforeEach
   public void setUp() {
      System.out.println("Executing Setup");
      try {
         Thread.sleep(5);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

   @Test
   @PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
   public void testMockitoNormal() {
      System.out.println("Normal Execution");
      //throw new OutOfMemoryError("OOM Error");
      String[] array = new String[100000 * 100000];
   }
}