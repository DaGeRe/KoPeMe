package de.dagere.kopeme.junit.rule;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.TimeBoundExecution.Type;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.datastorage.SaveableTestData;
import junit.framework.AssertionFailedError;

/**
 * Represents an execution of all runs of one test
 * 
 * TODO: Overthink weather directly configure test runs in KoPeMeRule would be more nice
 * 
 * @author dagere
 * 
 */
public class KoPeMeStandardRuleStatement extends KoPeMeBasicStatement {

   private static final Logger LOG = LogManager.getLogger(KoPeMeStandardRuleStatement.class);

   private final TestResult finalResult;

   public KoPeMeStandardRuleStatement(final TestRunnables runnables, final Method method, final String filename) {
      super(runnables, method, filename);
      finalResult = new TestResult(method.getName(), annotation.warmup(), datacollectors, false);
   }
   
   public KoPeMeStandardRuleStatement(final TestRunnables runnables, final Method method, final String filename, final String methodResultName) {
      super(runnables, method, filename);
      finalResult = new TestResult(methodResultName, annotation.warmup(), datacollectors, false);
   }

   @Override
   public void evaluate() throws Throwable {
      final Finishable finishable = new Finishable() {
         @Override
         public void run() {
            try {
               executeSimpleTest();
               if (!assertationvalues.isEmpty()) {
                  finalResult.checkValues(assertationvalues);
               }
            } catch (IllegalAccessException | InvocationTargetException e) {
               e.printStackTrace();
            } catch (final Throwable e) {
               e.printStackTrace();
            }
         }

         @Override
         public boolean isFinished() {
            return KoPeMeStandardRuleStatement.this.isFinished;
         }

         @Override
         public void setFinished(final boolean isFinished) {
            KoPeMeStandardRuleStatement.this.isFinished = isFinished;
         }
      };

      final TimeBoundExecution tbe = new TimeBoundExecution(finishable, annotation.timeout(), Type.METHOD, annotation.useKieker());
      tbe.execute();
      LOG.info("Test {} beendet", filename);
   }

   private void executeSimpleTest() throws Throwable {
      if (!PerformanceTestUtils.checkCollectorValidity(finalResult, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      
      final RunConfiguration configuration = new RunConfiguration(annotation);
      try {
         runWarmup();
         if (!isFinished) {
            runMainExecution(finalResult, "execution ", annotation.iterations(), annotation.repetitions());
         }
      } catch (final AssertionFailedError t) {
         finalResult.finalizeCollection(t);
         saveData(SaveableTestData.createAssertFailedTestData(finalResult.getMethodName(), filename, finalResult, configuration));
         throw t;
      } catch (final Throwable t) {
         t.printStackTrace();
         finalResult.finalizeCollection(t);
         saveData(SaveableTestData.createErrorTestData(finalResult.getMethodName(), filename, finalResult, configuration));
         throw t;
      }
      finalResult.finalizeCollection();
      saveData(SaveableTestData.createFineTestData(finalResult.getMethodName(), filename, finalResult, configuration));
   }

   private void runWarmup() throws Throwable {
      if (annotation.warmup() > 0) {
         final TestResult deletableResult = new TestResult(method.getName(), annotation.warmup(), datacollectors, true);
         runMainExecution(deletableResult, "warmup execution ", annotation.warmup(), annotation.repetitions());
         deletableResult.deleteTempFile();
      }
   }
}
