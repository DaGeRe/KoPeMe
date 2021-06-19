package de.dagere.kopeme.junit.testrunner;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.TimeBoundExecution.Type;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class PerformanceMethodStatement extends KoPeMeBasicStatement {

   private static final Logger LOG = LogManager.getLogger(PerformanceMethodStatement.class);

   protected final String className, methodName;
   protected Finishable mainRunnable;
   

   public PerformanceMethodStatement(final TestRunnables runnables, final String filename, final Class<?> calledClass, final FrameworkMethod method,
         final boolean saveValuesClass) {
      super(runnables, method.getMethod(), filename);
      if (saveValuesClass) {
         configuration.setSaveValues(saveValuesClass);
      }
      this.methodName = method.getName();
      this.className = calledClass.getSimpleName(); // The name of the testcase-class is recorded; if tests of subclasses are called, they belong to the testcase of the superclass
                                                    // anyway
   }

   @Override
   public void evaluate() throws Throwable {

      mainRunnable = new Finishable() {

         @Override
         public void run() {
            try {
               runWarmup();
               final TestResult tr = executeSimpleTest(annotation.iterations());
               tr.checkValues();
               if (!assertationvalues.isEmpty()) {
                  LOG.info("Checking: " + assertationvalues.size());
                  tr.checkValues(assertationvalues);
               }
            } catch (final Exception e) {
               if (e instanceof RuntimeException) {
                  e.printStackTrace();
                  throw (RuntimeException) e;
               }
               if (e instanceof InterruptedException) {
                  throw new RuntimeException(e);
               }
               LOG.error("Caught Exception: {}", e.getLocalizedMessage());
               e.printStackTrace();
            } catch (final Throwable t) {
               if (t instanceof Error)
                  throw (Error) t;
               LOG.error("Unknown Type: " + t.getClass() + " " + t.getLocalizedMessage());
            }
         }

         @Override
         public void setFinished(final boolean isFinished) {
            PerformanceMethodStatement.this.isFinished = isFinished;
         }

         @Override
         public boolean isFinished() {
            return isFinished;
         }
      };
      if (!isFinished) {
         final TimeBoundExecution tbe = new TimeBoundExecution(mainRunnable, annotation.timeout(), Type.METHOD, annotation.useKieker());
         tbe.execute();
      }
      LOG.debug("Timebounded execution finished");
   }

   /**
    * Executes a simple test, i.e. a test without parameters.
    * 
    * @param callee Statement that should be called to measure performance and execute the test
    * @return The result of the test
    * @throws Throwable Any exception that occurs during the test
    */
   protected TestResult executeSimpleTest(final int executions) throws Throwable {
      final TestResult tr = new TestResult(methodName, executions, datacollectors, false);

      if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      try {
         runMainExecution(tr, "execution ", executions, configuration.getRepetitions());
      } catch (final Throwable t) {
         tr.finalizeCollection(t);
         saveData(SaveableTestData.createErrorTestData(methodName, filename, tr, configuration));
         throw t;
      }
      tr.finalizeCollection();
      saveData(SaveableTestData.createFineTestData(methodName, filename, tr, configuration));
      return tr;
   }

   /**
    * Runs the warmup for the tests.
    * 
    * @param callee Statement that should be called to measure performance and execute the test
    * @throws Throwable Any exception that occurs during the test
    */
   private void runWarmup() throws Throwable {
      final TestResult tr = new TestResult(methodName, annotation.warmup(), datacollectors, true);

      if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      try {
         runMainExecution(tr, "warmup execution ", annotation.warmup(), configuration.getRepetitions());
      } catch (final Throwable t) {
         t.printStackTrace();
         tr.finalizeCollection(t);
         throw t;
      }
      tr.finalizeCollection();
   }

   public void setFinished(final boolean isFinished) {
      LOG.debug("Setting finished: " + isFinished + " " + mainRunnable);
      if (mainRunnable != null) {
         mainRunnable.setFinished(isFinished);
      }
   }
}
