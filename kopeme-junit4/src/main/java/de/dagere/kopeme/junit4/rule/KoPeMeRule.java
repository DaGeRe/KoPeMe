package de.dagere.kopeme.junit4.rule;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.junit.rule.BeforeAfterMethodFinderJUnit4;
import de.dagere.kopeme.junit.rule.KoPeMeRuleStatement4;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.runnables.KoPeMeThrowingRunnable;
import de.dagere.kopeme.runnables.TestRunnable;
import de.dagere.kopeme.runnables.TestRunnables;

/**
 * This Rule gives the possibility to test performance with a rule and without a testrunner; this makes it possible to use a different testrunner. Be aware that a rule-execution
 * does measure the time needed for @Before-Executions together with the main execution time, but not the @BeforeClass-Execution.
 *
 * @author DaGeRe
 */
public class KoPeMeRule implements TestRule {

   private final Object testObject;

   public KoPeMeRule(final Object testObject) {
      this.testObject = testObject;
   }

   @Override
   public Statement apply(final Statement stmt, final Description descr) {
      if (descr.isTest()) {

         Method testMethod = null;
         Class<?> testClass = null;

         try {
            testClass = testObject.getClass();
            final String methodDescription = descr.getMethodName();
            final LinkedHashMap<String, String> params;
            int squaredBracketIndex = methodDescription.indexOf('[');
            if (squaredBracketIndex != -1) {
               String methodName = methodDescription.substring(0, squaredBracketIndex);
               params = parseParams(methodDescription, squaredBracketIndex);
               testMethod = testClass.getMethod(methodName);
            } else {
               testMethod = testClass.getMethod(methodDescription);
               params = null;
            }

            final PerformanceTest annotation = testMethod.getAnnotation(PerformanceTest.class);
            if (annotation != null) {
               KoPeMeThrowingRunnable testRunnable = new KoPeMeThrowingRunnable() {
                  @Override
                  public void run() throws Throwable {
                     stmt.evaluate();
                  }
               };

               List<Method> beforeClassMethod = BeforeAfterMethodFinderJUnit4.getBeforeWithMeasurements(testClass);
               List<Method> afterClassMethod = BeforeAfterMethodFinderJUnit4.getAfterWithMeasurements(testClass);

               final TestRunnable runnables = new TestRunnables(new RunConfiguration(annotation), testRunnable, testClass, testObject,
                     beforeClassMethod, afterClassMethod);

               return new KoPeMeRuleStatement4(runnables, testMethod, testClass.getName(), params);
            } else {
               return stmt;
            }
         } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
         }
      } else {
         return stmt;
      }
   }

   private LinkedHashMap<String, String> parseParams(final String methodDescription, final int squaredBracketIndex) {
      final String indexString = methodDescription.substring(squaredBracketIndex + 1, methodDescription.length() - 1);
      final LinkedHashMap<String, String> params = new LinkedHashMap<>();
      params.put(KoPeMeConstants.JUNIT_PARAMETERIZED, indexString);
      return params;
   }
}
