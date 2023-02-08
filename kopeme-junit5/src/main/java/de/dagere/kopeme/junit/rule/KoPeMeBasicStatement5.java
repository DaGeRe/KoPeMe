package de.dagere.kopeme.junit.rule;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.OutputStreamUtil;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import de.dagere.kopeme.runnables.TestRunnable;

/**
 * A statement for running performance tests.
 * 
 * Should once become base class of several TestExecutingStatements - is yet only base class of rule and throughput statement.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeBasicStatement5 {

   private static final Logger LOG = LogManager.getLogger(KoPeMeBasicStatement5.class);

   protected Map<String, Double> maximalRelativeStandardDeviation;
   protected Map<String, Long> assertationvalues;
   protected final String clazzname;
   protected Method method;
   protected TestRunnable runnables;
   protected boolean isFinished = false;
   protected DataCollectorList datacollectors;
   protected PerformanceTest annotation;

   /**
    * Initializes the KoPemeBasicStatement.
    * 
    * @param runnables Runnables that should be run
    * @param method Method that should be executed
    * @param clazzname Name of the
    */
   public KoPeMeBasicStatement5(final TestRunnable runnables, final Method method, final String clazzname) {
      this.runnables = runnables;
      this.clazzname = clazzname;
      this.method = method;

      annotation = method.getAnnotation(PerformanceTest.class);

      if ("EXTENDED".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.EXTENDED;
      } else if ("STANDARD".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.STANDARD;
      } else if ("ONLYTIME".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.ONLYTIME;
      } else if ("ONLYTIME_NOGC".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.ONLYTIME_NOGC;
      } else if ("NONE".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.NONE;
      } else {
         datacollectors = DataCollectorList.ONLYTIME;
         LOG.error("For Datacollectorlist, only STANDARD, ONLYTIME, ONLYTIME_NOGC and NONE are allowed");
      }
      
      maximalRelativeStandardDeviation = new HashMap<>();
      assertationvalues = new HashMap<>();
      for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
         maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
      }

      for (final Assertion a : annotation.assertions()) {
         assertationvalues.put(a.collectorname(), a.maxvalue());
      }

   }

   protected void initializeKieker(final String clazzname, final String methodFileName) {
      try {
         KoPeMeKiekerSupport.INSTANCE.setKiekerWaitTime(annotation.kiekerWaitTime());
         KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), clazzname, methodFileName);
      } catch (final Exception e) {
         System.err.println("kieker has failed!");
         e.printStackTrace();
      }
   }

   /**
    * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
    * 
    * @param tr Test Result that should be checked
    * @return Weather the result is valid
    */
   protected boolean checkCollectorValidity(final TestResult tr) {
      return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
   }

   protected void runMainExecution(final TestResult tr, final String warmupString, final int iterations, final int repetitions) throws Throwable {
      System.gc();
      final String fullWarmupStart = "--- Starting " + warmupString + " {}/" + iterations + " ---";
      final String fullWarmupStop = "--- Stopping " + warmupString + " {}/" + iterations + " ---";
      tr.beforeRun();
      int iteration = 1;
      try {
         if (annotation.redirectToTemp()) {
            redirectToTempFile();
         } else if (annotation.redirectToNull()) {
            OutputStreamUtil.redirectToNullStream();
         }
         LOG.debug("Executing " + iterations + " " + warmupString);
         for (iteration = 1; iteration <= iterations; iteration++) {
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStart, iteration);
            }
            runnables.getBeforeRunnable().run();
            tr.startCollection();
            runAllRepetitions(repetitions);
            tr.stopCollection();
            runnables.getAfterRunnable().run();
            tr.setRealExecutions(iteration - 1);
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStop, iteration);
            }
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
      LOG.debug("Finished iterations: " + (iteration - 1));
      tr.setRealExecutions(iteration - 1);
   }

   private void redirectToTempFile() throws IOException {
      File tempFile = Files.createTempFile("kopeme", ".txt").toFile();
      PrintStream stream = new PrintStream(tempFile);
      System.setOut(stream);
      System.setErr(stream);
   }

   private void runAllRepetitions(final int repetitions) throws Throwable {
      for (int repetition = 0; repetition < repetitions; repetition++) {
         runnables.getTestRunnable().run();
      }
   }

   private void checkFinished() throws InterruptedException {
      if (isFinished) {
         LOG.debug("Exiting finished thread: {}.", Thread.currentThread().getName());
         throw new InterruptedException("Test timed out.");
      }
      final boolean interrupted = Thread.interrupted();
      LOG.trace("Interrupt state: {}", interrupted);
      if (interrupted) {
         LOG.debug("Exiting thread.");
         throw new InterruptedException("Test was interrupted and eventually timed out.");
      }
   }
}