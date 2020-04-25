package de.dagere.kopeme.junit.exampletests.runner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
@PerformanceTestingClass(overallTimeout = 100000, logFullData = true)
public class JUnitAdditionTestFullDataBig {
//   private final static Logger log = LogManager.getLogger(JUnitAdditionTestFullData.class);

   private static final int ADD_COUNT = 10;
   
   @Test
   @PerformanceTest(executionTimes = 2000, dataCollectors = "ONLYTIME_NOGC")
   public void testAddition() {
      int a = 0;
      for (int i = 0; i < ADD_COUNT; i++) {
         a += i;
      }
      Assert.assertEquals(ADD_COUNT * (ADD_COUNT - 1) / 2, a);
//      log.debug("Addition finished");
   }
}
