package de.dagere.kopeme.junit.exampletests.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass
@RunWith(PerformanceTestRunnerJUnit.class)
public class OverheadTest {

   @PerformanceTest(iterations = 100, warmup = 0, repetitions = 1, dataCollectors = "ONLYTIME")
   @Test
   public void testIterationOverhead() throws Exception {

   }

   @PerformanceTest(iterations = 1, warmup = 1000, repetitions = 1, dataCollectors = "ONLYTIME")
   @Test
   public void testWarmupOverhead() throws Exception {

   }

   @PerformanceTest(iterations = 1, warmup = 0, repetitions = 1000, dataCollectors = "ONLYTIME")
   @Test
   public void testRepetitionOverhead() throws Exception {

   }

   @PerformanceTest(iterations = 1000, warmup = 1000, repetitions = 1000, dataCollectors = "ONLYTIME")
   @Test
   public void testCombinedOverhead() throws Exception {

   }
}
