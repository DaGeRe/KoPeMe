package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.junit.function.ThrowingRunnable;

import de.dagere.kopeme.annotations.PerformanceTest;

/**
 * This Rule gives the possibility to test performance with a rule and without a testrunner; this makes it possible to use a different testrunner. Be aware that a rule-execution
 * does measure the time needed for @Before-Executions together with the main execution time, but not the @BeforeClass-Execution.
 * 
 * @author DaGeRe
 *
 */
public class KoPeMeRule implements TestRule {

   private static final Logger LOG = LogManager.getLogger(KoPeMeRule.class);

   private final Object testObject;

   public KoPeMeRule(final Object testObject) {
      this.testObject = testObject;
   }

   private KoPeMeStandardRuleStatement koPeMeStandardRuleStatement;

   @Override
   public Statement apply(final Statement stmt, final Description descr) {
      if (descr.isTest()) {

         Method testMethod = null;
         Class<?> testClass = null;
         try {
            // testClass = Class.forName(descr.getClassName());
            testClass = testObject.getClass();
            testMethod = testClass.getMethod(descr.getMethodName());
         } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
         }
         final PerformanceTest annotation = testMethod.getAnnotation(PerformanceTest.class);
         if (annotation != null) {
            final TestRunnables runnables = new TestRunnables(new ThrowingRunnable() {
               @Override
               public void run() throws Throwable {
                  stmt.evaluate();
               }
            }, testClass, testObject);

            koPeMeStandardRuleStatement = new KoPeMeStandardRuleStatement(runnables, testMethod, testClass.getName());
            return koPeMeStandardRuleStatement;
         } else {
            return stmt;
         }
      } else {
         return stmt;
      }
   }
}
