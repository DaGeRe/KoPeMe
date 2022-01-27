package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

/**
 * An example test for testing the order of BeforeWithMeasurement methods
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleBeforeWithMeasurementOrderTest {

   private static Object myStuff;
   
   @de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement()
   static void setUp() {
      System.out.println("setup");
      myStuff = new Object();
   }
   
   @de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement()
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
