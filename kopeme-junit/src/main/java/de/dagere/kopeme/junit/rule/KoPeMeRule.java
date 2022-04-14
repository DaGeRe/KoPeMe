package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.function.ThrowingRunnable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.runnables.TestRunnable;
import de.dagere.kopeme.runnables.TestRunnables;

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
            final String methodDescription = descr.getMethodName();
            final Params params;
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
               ThrowingRunnable testRunnable = new ThrowingRunnable() {
                  @Override
                  public void run() throws Throwable {
                     stmt.evaluate();
                  }
               };
               final TestRunnable runnables = new TestRunnables(new RunConfiguration(annotation), testRunnable, testClass, testObject);

               koPeMeStandardRuleStatement = new KoPeMeStandardRuleStatement(runnables, testMethod, testClass.getName(), params);
               return koPeMeStandardRuleStatement;
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

   private Params parseParams(final String methodDescription, final int squaredBracketIndex) {
      final Params params;
      String indexString = methodDescription.substring(squaredBracketIndex + 1, methodDescription.length() - 1);
      params = new Params();
      Result.Params.Param param = new Result.Params.Param();
      param.setKey(KoPeMeConstants.JUNIT_PARAMETERIZED);
      param.setValue(indexString);
      params.getParam().add(param);
      return params;
   }
}
