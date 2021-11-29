package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.function.ThrowingRunnable;

import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.AfterWithMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;

/**
 * Saves all test runnables, i.e. the runnables that should be executed before and after the test and the test itself.
 * 
 * @author reichelt
 *
 */
public class TestRunnables {

   private static final Logger LOG = LogManager.getLogger(TestRunnables.class);

   private final ThrowingRunnable testRunnable, beforeRunnable, afterRunnable;

   /**
    * Initializes the TestRunnables
    * 
    * @param testRunnable Runnable for the test itself
    * @param testClass Class that should be tested
    * @param testObject Object that should be tested
    */
   public TestRunnables(final RunConfiguration config, final ThrowingRunnable testRunnable, final Class<?> testClass, final Object testObject) {
      final List<Method> beforeMethods = new LinkedList<>();
      final List<Method> afterMethods = new LinkedList<>();
      LOG.debug("Klasse: {}", testClass);
      for (final Method classMethod : testClass.getDeclaredMethods()) {
         LOG.trace("Prüfe: {}", classMethod);
         if (classMethod.getAnnotation(BeforeNoMeasurement.class) != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("BeforeNoMeasurement-methods must not have arguments");
            }
            beforeMethods.add(classMethod);
            classMethod.setAccessible(true);
         }
         if (classMethod.getAnnotation(AfterNoMeasurement.class) != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("AfterNoMeasurement-methods must not have arguments");
            }
            afterMethods.add(classMethod);
            classMethod.setAccessible(true);
         }
      }

      if (config.isExecuteBeforeClassInMeasurement()) {
         List<Method> beforeClassMethod = new LinkedList<Method>();
         List<Method> afterClassMethod = new LinkedList<Method>();
         for (final Method classMethod : testClass.getDeclaredMethods()) {
            System.out.println(classMethod.getName());
            if (classMethod.getAnnotation(BeforeClass.class) != null ||
                  classMethod.getAnnotation(BeforeWithMeasurement.class) != null) {
               beforeClassMethod.add(classMethod);
               classMethod.setAccessible(true);
            }
            if (classMethod.getAnnotation(AfterClass.class) != null ||
                  classMethod.getAnnotation(AfterWithMeasurement.class) != null) {
               afterClassMethod.add(classMethod);
               classMethod.setAccessible(true);
            }
         }

         this.testRunnable = new BeforeAfterMethodRunnable(beforeClassMethod, testRunnable, afterClassMethod, testObject);
      } else {
         this.testRunnable = testRunnable;
      }

      beforeRunnable = new ListOfMethodRunnable(beforeMethods, testObject);
      afterRunnable = new ListOfMethodRunnable(afterMethods, testObject);
   }

   /**
    * Returns the test Runnable
    * 
    * @return Test-Runnable
    */
   public ThrowingRunnable getTestRunnable() {
      return testRunnable;
   }

   /**
    * Returns the runnable, that should be run before the test
    * 
    * @return Before-Runnable
    */
   public ThrowingRunnable getBeforeRunnable() {
      return beforeRunnable;
   }

   /**
    * Returns the runnable, that should be run after the test
    * 
    * @return After-Runnable
    */
   public ThrowingRunnable getAfterRunnable() {
      return afterRunnable;
   }
}
