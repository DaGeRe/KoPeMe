package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

/**
 * A statement for running performance tests.
 * 
 * Should once become base class of several TestExecutingStatements - is yet only base class of rule and throughput statement.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeBasicStatement extends Statement {

   private static final Logger LOG = LogManager.getLogger(KoPeMeBasicStatement.class);

   protected Map<String, Double> maximalRelativeStandardDeviation;
   protected Map<String, Long> assertationvalues;
   protected final String filename;
   protected Method method;
   protected TestRunnables runnables;
   protected boolean isFinished = false;
   protected DataCollectorList datacollectors;

   protected PerformanceTest annotation;

   /**
    * Initializes the KoPemeBasicStatement.
    * 
    * @param runnables
    *           Runnables that should be run
    * @param method
    *           Method that should be executed
    * @param filename
    *           Name of the
    */
   public KoPeMeBasicStatement(final TestRunnables runnables, final Method method, final String filename) {
      super();
      this.runnables = runnables;
      this.filename = filename;
      this.method = method;

      annotation = method.getAnnotation(PerformanceTest.class);

      if ("EXTENDED".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.EXTENDED;
      } else if ("STANDARD".equals(annotation.dataCollectors()) || annotation == null) {
         datacollectors = DataCollectorList.STANDARD;
      } else if ("ONLYTIME".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.ONLYTIME;
      } else if ("NONE".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.NONE;
      } else {
         datacollectors = DataCollectorList.ONLYTIME;
         LOG.error("For Datacollectorlist, only STANDARD, ONLYTIME AND NONE are allowed");
      }

      if (annotation != null) {
         try {
            KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), filename, method.getName());
         } catch (final Exception e) {
            System.err.println("kieker has failed!");
            e.printStackTrace();
         }
         maximalRelativeStandardDeviation = new HashMap<>();
         assertationvalues = new HashMap<>();
         for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
            maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
         }

         for (final Assertion a : annotation.assertions()) {
            assertationvalues.put(a.collectorname(), a.maxvalue());
         }
      } else {
         LOG.error("No @PerformanceTest-Annotation present!");
      }
   }

   /**
    * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
    * 
    * @param tr
    *           Test Result that should be checked
    * @return Weather the result is valid
    */
   protected boolean checkCollectorValidity(final TestResult tr) {
      return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
   }

   protected void runMainExecution(final TestResult tr, final String warmupString, final int executions, final int repetitions) throws Throwable {
      int execution;
      tr.beforeRun();
      final String fullWarmupStart = "--- Starting " + warmupString + " {} / {} ---";
      final String fullWarmupStop = "--- Stopping " + warmupString + " {} ---";
      for (execution = 1; execution <= executions; execution++) {
         LOG.debug(fullWarmupStart, execution, executions);
         runnables.getBeforeRunnable().run();
         tr.startCollection();
         for (int repetition = 0; repetition < repetitions; repetition++) {
            runnables.getTestRunnable().run();
         }
         tr.stopCollection();
         runnables.getAfterRunnable().run();
         tr.setRealExecutions(execution - 1);
         LOG.debug(fullWarmupStop, execution);
         for (final Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
            LOG.trace("Entry: {} {}", entry.getKey(), entry.getValue());
         }
         if (execution >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
               && tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
            break;
         }
         if (isFinished) {
            LOG.debug("Exiting finished thread: {}.", Thread.currentThread().getName());
            throw new InterruptedException("Test timed out.");
         }
         final boolean interrupted = Thread.interrupted();
         LOG.debug("Interrupt state: {}", interrupted);
         if (interrupted) {
            LOG.debug("Exiting thread.");
            throw new InterruptedException("Test was interrupted and eventually timed out.");
         }
      }
      LOG.debug("Executions: " + (execution - 1));
      tr.setRealExecutions(execution - 1);
   }
}