package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

/**
 * An example test for testing the order of BeforeWithMeasurement methods
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleBeforeWithMeasurementOrderTest {

   private static Object myStuff;
   
   @BeforeAll
   static void someClassicBefore() {
      
   }
   
   @de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement(priority = 10)
   static void highPriorityStuff() {
   }
   
   @de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement(priority = 1)
   static void setUp() {
      System.out.println("setup");
      myStuff = new Object();
   }
   
   @de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement(priority = 0)
   void init() {
      System.out.println("Hash: " + myStuff.hashCode());
   }
	
	@AfterEach
   public void tearDown() {
      
   }

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", executeBeforeClassInMeasurement = true)
	public void testNormal() {
	   System.out.println("myStuff: " + myStuff);
	   try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
	}
}
