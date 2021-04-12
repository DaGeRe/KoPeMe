package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class ExampleBeforeClassMeasurement {

   @Rule
   public TestRule rule = new KoPeMeRule(this);

   @BeforeClass
   public static void beforeClass() throws InterruptedException {
	  long start = System.nanoTime();
      Thread.sleep(40);
      System.out.println("beforeClass(40), slept for: " + (System.nanoTime()-start));
   }
   
   @AfterClass
   public static void afterClass() throws InterruptedException {
	  long start = System.nanoTime();
      Thread.sleep(40);
      System.out.println("afterClass(40), slept for: " + (System.nanoTime()-start));
   }
   
   @PerformanceTest(executeBeforeClassInMeasurement = true, iterations = 2, warmup = 2)
   @Test
   public void spendTime() throws InterruptedException {
      //System.out.println("Spend Time");
	  long start = System.nanoTime();
      Thread.sleep(50);
      System.out.println("spendTime(50), slept for: " + (System.nanoTime()-start));
   }
}
