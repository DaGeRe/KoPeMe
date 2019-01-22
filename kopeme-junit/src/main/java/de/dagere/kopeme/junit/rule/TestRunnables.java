package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;

/**
 * Saves all test runnables, i.e. the runnables that should be executed before and after the test and the test itself.
 * 
 * @author reichelt
 *
 */
public class TestRunnables {

   public static interface ThrowingRunnable {
      void run() throws Throwable;
   }

   private static final Logger LOG = LogManager.getLogger(TestRunnables.class);

   private final ThrowingRunnable testRunnable, beforeRunnable, afterRunnable;

   /**
    * Initializes the TestRunnables
    * 
    * @param testRunnable Runnable for the test itself
    * @param testClass Class that should be tested
    * @param testObject Object that should be tested
    */
   public TestRunnables(final ThrowingRunnable testRunnable, final Class<?> testClass, final Object testObject) {
      super();
      this.testRunnable = testRunnable;
      final List<Method> beforeMethods = new LinkedList<>();
      final List<Method> afterMethods = new LinkedList<>();
      LOG.debug("Klasse: {}", testClass);
      for (final Method classMethod : testClass.getMethods()) {
         LOG.trace("Prüfe: {}", classMethod);
         if (classMethod.getAnnotation(BeforeNoMeasurement.class) != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("BeforeNoMeasurement-methods must not have arguments");
            }
            beforeMethods.add(classMethod);
         }
         if (classMethod.getAnnotation(AfterNoMeasurement.class) != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("AfterNoMeasurement-methods must not have arguments");
            }
            afterMethods.add(classMethod);
         }
      }

      beforeRunnable = new ThrowingRunnable() {

         @Override
         public void run() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            for (final Method method : beforeMethods) {
               method.invoke(testObject);
            }

         }
      };

      afterRunnable = new ThrowingRunnable() {

         @Override
         public void run() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            for (final Method method : afterMethods) {
               method.invoke(testObject);
            }
         }
      };
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
