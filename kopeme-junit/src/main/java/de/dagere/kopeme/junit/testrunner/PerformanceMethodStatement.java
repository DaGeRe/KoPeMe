package de.dagere.kopeme.junit.testrunner;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.OutputStreamUtil;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.TimeBoundExecution.Type;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;

public class PerformanceMethodStatement extends KoPeMeBasicStatement {

   private static final Logger LOG = LogManager.getLogger(PerformanceMethodStatement.class);

   protected final PerformanceJUnitStatement callee;
   protected final int timeout;
   protected final String className, methodName;
   protected Finishable mainRunnable;
   protected final RunConfiguration configuration;

   public PerformanceMethodStatement(final PerformanceJUnitStatement callee, final String filename, final Class<?> calledClass, final FrameworkMethod method,
         final boolean saveValuesClass) {
      super(null, method.getMethod(), filename);
      this.callee = callee;
//      this.saveFullData = saveFullData ? saveFullData : annotation.logFullData();
      configuration = new RunConfiguration(annotation);
      if (saveValuesClass) {
         configuration.setSaveValues(saveValuesClass);
      }
      timeout = annotation.timeout();
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
               runWarmup(callee);
               final TestResult tr = executeSimpleTest(callee, annotation.iterations());
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
         final TimeBoundExecution tbe = new TimeBoundExecution(mainRunnable, timeout, Type.METHOD, annotation.useKieker());
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
   protected TestResult executeSimpleTest(final PerformanceJUnitStatement callee, final int executions) throws Throwable {
      final TestResult tr = new TestResult(methodName, executions, datacollectors, false);

      if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      try {
         runMainExecution(tr, "execution ", executions, callee, configuration.getRepetitions());
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
   private void runWarmup(final PerformanceJUnitStatement callee) throws Throwable {
      final TestResult tr = new TestResult(methodName, annotation.warmup(), datacollectors, true);

      if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      try {
         runMainExecution(tr, "warmup execution ", annotation.warmup(), callee, configuration.getRepetitions());
      } catch (final Throwable t) {
         t.printStackTrace();
         tr.finalizeCollection(t);
         throw t;
      }
      tr.finalizeCollection();
   }

   /**
    * Runs the main execution of the test, i.e. the execution where performance measures are counted.
    * 
    * @param tr TestResult that should be filled
    * @param callee Statement that should be called to measure performance and execute the test
    * @param simple Weather it is a simple test, i.e. weather there are parameters
    * @throws Throwable Any exception that occurs during the test
    */
   protected void runMainExecution(final TestResult tr, final String warmupString, final int executions, final PerformanceJUnitStatement callee, final int repetitions)
         throws Throwable {
      System.gc();
      final String methodString = className + "." + tr.getTestcase();
      final String fullWarmupStart = "--- Starting " + warmupString + methodString + " {} / {} ---";
      final String fullWarmupStop = "--- Stopping " + warmupString + " {} ---";
      tr.beforeRun();
      int execution = 1;
      try {
         if (annotation.redirectToTemp()) {
            redirectToTempFile();
         } else if (annotation.redirectToNull()) {
            OutputStreamUtil.redirectToNullStream();
         }
         for (execution = 1; execution <= executions; execution++) {
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStart, execution, executions);
            }
            tr.startCollection();
            runAllRepetitions(callee, repetitions);
            tr.stopCollection();
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStop, execution);
            }
            tr.setRealExecutions(execution);
//            if (execution >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
//                  && tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
//               LOG.info("Exiting because of deviation reached");
//               break;
//            }
            checkFinished();
         }
      } finally {
         OutputStreamUtil.resetStreams();
      }
      System.gc();
      Thread.sleep(1);
      LOG.debug("Executions: " + (execution - 1));
      tr.setRealExecutions(execution - 1);
   }

   private void redirectToTempFile() throws IOException, FileNotFoundException {
      File tempFile = Files.createTempFile("kopeme", ".txt").toFile();
      PrintStream stream = new PrintStream(tempFile);
      System.setOut(stream);
      System.setErr(stream);
   }  

   private void checkFinished() throws InterruptedException {
      if (isFinished) {
         LOG.debug("Exiting finished thread: {}.", Thread.currentThread().getName());
         throw new InterruptedException("Test timed out.");
      }
      final boolean interrupted = Thread.interrupted();
//      LOG.trace("Interrupt state: {}", interrupted);
      if (interrupted) {
         LOG.debug("Exiting thread.");
         throw new InterruptedException("Test was interrupted and eventually timed out.");
      }
   }

   private void runAllRepetitions(final PerformanceJUnitStatement callee, final int repetitions) throws Throwable {
      for (int i = 0; i < repetitions; i++) {
         callee.preEvaluate();
         callee.evaluate();
         callee.postEvaluate();
      }
   }

   public void setFinished(final boolean isFinished) {
      LOG.debug("Setting finished: " + isFinished + " " + mainRunnable);
      if (mainRunnable != null) {
         mainRunnable.setFinished(isFinished);
      }
   }
}
