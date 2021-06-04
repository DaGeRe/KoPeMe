package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleBeforeClassMeasurement {

   @Rule
   public TestRule rule = new KoPeMeRule(this);

   @BeforeClass
   public static void beforeClass() throws InterruptedException {
	  TestUtils.waitSomeMilliseconds(40);
   }
   
   @AfterClass
   public static void afterClass() throws InterruptedException {
	  TestUtils.waitSomeMilliseconds(40);
   }
   
   @PerformanceTest(executeBeforeClassInMeasurement = true, iterations = 2, warmup = 2)
   @Test
   public void spendTime() throws InterruptedException {
      TestUtils.waitSomeMilliseconds(50);
   }
}
