package de.dagere.kopeme.junit.exampletests.runner;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass
@RunWith(PerformanceTestRunnerJUnit.class)
public class OverheadTest {

   @PerformanceTest(executionTimes = 10000, warmupExecutions = 0, repetitions = 1, dataCollectors = "ONLYTIME")
   @Test
   public void testIterationOverhead() throws Exception {

   }

   @PerformanceTest(executionTimes = 1, warmupExecutions = 1000, repetitions = 1, dataCollectors = "ONLYTIME")
   @Test
   public void testWarmupOverhead() throws Exception {

   }

   @PerformanceTest(executionTimes = 1, warmupExecutions = 0, repetitions = 1000, dataCollectors = "ONLYTIME")
   @Test
   public void testRepetitionOverhead() throws Exception {

   }

   @PerformanceTest(executionTimes = 1000, warmupExecutions = 1000, repetitions = 1000, dataCollectors = "ONLYTIME")
   @Test
   public void testCombinedOverhead() throws Exception {

   }
}
