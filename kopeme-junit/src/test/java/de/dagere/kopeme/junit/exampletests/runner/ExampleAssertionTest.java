package de.dagere.kopeme.junit.exampletests.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

class TestXYZ {
   public void calculateThing(final int i) {
      System.out.println("hallo " + i * 3 + 5 + i * i);
   }
}

@RunWith(PerformanceTestRunnerJUnit.class)
@PerformanceTestingClass(overallTimeout = Integer.MAX_VALUE)
public class ExampleAssertionTest {

   public final int MAX_VALUE = 1000;
   
   @Test
   @PerformanceTest(timeout = Integer.MAX_VALUE, executionTimes = 10, warmupExecutions = 10, deviations = {
         @MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 0.01) }, assertions = {
               @Assertion(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 150) })
   public void testAssertionAddition() throws FileNotFoundException {
      int a = 0;
      System.setOut(new PrintStream(new File("target/test.txt")));
      for (int i = 0; i < MAX_VALUE; i++) {
         a += i;
         giveMeOutput(i);
      }
      Assert.assertEquals(MAX_VALUE * (MAX_VALUE-1) / 2, a);
      try {
         Thread.sleep(20);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

   public void giveMeOutput(final int i) {
      System.out.println("test" + i);
      giveMeOutput2(i);
      new TestXYZ().calculateThing(i);
   }

   public void giveMeOutput2(final int i) {
      System.out.println("test" + i * 2);
      giveMeOutput3(i);
   }

   public void giveMeOutput3(final int i) {
      System.out.println("test" + i * 3);
   }

   @Ignore
   @Test
   @PerformanceTest(executionTimes = 10, warmupExecutions = 10, assertions = {
         @Assertion(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 15000) }, deviations = {
               @MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 15000) }, useKieker = true)
   public void testAssertionAdditionDoubleTest() {
      int a = 0;
      for (int i = 0; i < 100000; i++) {
         a += i;
      }
      Assert.assertEquals(100000 * 99999 / 2, a);
      try {
         Thread.sleep(20);
      } catch (final InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
