package de.dagere.kopeme.junit5;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.junit.rule.BeforeAfterMethodFinderJUnit5;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleBeforeWithMeasurementOrderTest;

public class TestBeforeAfterMethodFinder {
   
   @Test
   public void testFindingOrder() {
      Class<?> testClazz = ExampleBeforeWithMeasurementOrderTest.class;
      
      List<Method> methods = BeforeAfterMethodFinderJUnit5.getBeforeWithMeasurements(testClazz);
      
      Assert.assertEquals("someClassicBefore", methods.get(0).getName());
      Assert.assertEquals("highPriorityStuff", methods.get(1).getName());
      Assert.assertEquals("setUp", methods.get(2).getName());
      Assert.assertEquals("init", methods.get(3).getName());
   }
}
