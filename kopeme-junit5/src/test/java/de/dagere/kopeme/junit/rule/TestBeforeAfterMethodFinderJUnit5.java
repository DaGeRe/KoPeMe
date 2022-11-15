package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.junit5.exampletests.JUnit5BeforeAfterAlltest;

public class TestBeforeAfterMethodFinderJUnit5 {
   @Test
   public void testBeforeAll() throws ClassNotFoundException {
      Class testedClazz = Class.forName(JUnit5BeforeAfterAlltest.class.getName());
      List<Method> beforeMethods = BeforeAfterMethodFinderJUnit5.getBeforeWithMeasurements(testedClazz);

      Assert.assertEquals(1, beforeMethods.size());
      Assert.assertEquals("beforeEachMethod", beforeMethods.get(0).getName());
      
   }

   @Test
   public void testAfterAll() throws ClassNotFoundException {
      Class testedClazz = Class.forName(JUnit5BeforeAfterAlltest.class.getName());
      List<Method> afterMethods = BeforeAfterMethodFinderJUnit5.getAfterWithMeasurements(testedClazz);

      Assert.assertEquals(1, afterMethods.size());
      Assert.assertEquals("afterEachMethod", afterMethods.get(0).getName());
   }
}
