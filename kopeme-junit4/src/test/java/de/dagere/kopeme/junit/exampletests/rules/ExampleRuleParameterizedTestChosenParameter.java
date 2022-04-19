package de.dagere.kopeme.junit.exampletests.rules;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit4.rule.KoPeMeRule;

@RunWith(Parameterized.class)
public class ExampleRuleParameterizedTestChosenParameter {
   @Rule
   public TestRule rule = new KoPeMeRule(this);

   @Parameters
   public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] { { 1000 }, {5000}, { 10000 } });
   }

   private int value;

   public ExampleRuleParameterizedTestChosenParameter(final int value) {
      this.value = value;
   }

   @Test
   @PerformanceTest(iterations = 5, warmup = 5, timeout = 50000, useKieker = false, chosenParameterIndex = 1)
   public void testNormal() {
      System.out.println("value: " + value);
      int a = 0;
      for (int i = 0; i < value; i++) {
         a += i;
      }
      Assert.assertEquals(value * (value - 1) / 2, a);
   }
}
