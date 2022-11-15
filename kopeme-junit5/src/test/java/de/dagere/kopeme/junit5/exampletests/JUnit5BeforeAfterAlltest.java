package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class JUnit5BeforeAfterAlltest {

   @BeforeAll
   public static void setUp() {
      System.out.println("Setup");
   }

   @AfterAll
   public static void tearDown() {
      System.out.println("Teardown");
   }
   
   @BeforeEach
   public static void beforeEachMethod() {
      System.out.println("Setup");
   }

   @AfterEach
   public static void afterEachMethod() {
      System.out.println("Teardown");
   }
   
   

   @Test
   @PerformanceTest(warmup = 2, iterations = 2, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", executeBeforeClassInMeasurement = true)
   public void testNormal() {
      System.out.println("Normal Execution");
      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
