package de.dagere.kopeme.junit.rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.terahidro2003.measurement.executor.SjswInterProcessExecutor;
import io.github.terahidro2003.measurement.executor.asprof.AsprofInterProcessExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.OutputStreamUtil;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.RunConfiguration;
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
public abstract class KoPeMeBasicStatement4 extends Statement {

   private static final Logger LOG = LogManager.getLogger(KoPeMeBasicStatement4.class);

   protected Map<String, Double> maximalRelativeStandardDeviation;
   protected Map<String, Long> assertationvalues;
   protected final String clazzname;
   protected Method method;
   protected TestRunnable runnables;
   protected boolean isFinished = false;
   protected DataCollectorList datacollectors;

   protected PerformanceTest annotation;
   protected final RunConfiguration configuration;

   /**
    * Initializes the KoPemeBasicStatement.
    * 
    * @param runnables Runnables that should be run
    * @param method Method that should be executed
    * @param clazzname Name of the
    */
   public KoPeMeBasicStatement4(final TestRunnable runnables, final Method method, final String clazzname, final String methodFileName) {
      this.runnables = runnables;
      this.clazzname = clazzname;
      this.method = method;

      annotation = method.getAnnotation(PerformanceTest.class);

      if ("EXTENDED".equals(annotation.dataCollectors())) {
         datacollectors = DataCollectorList.EXTENDED;
      } else if ("STANDARD".equals(annotation.dataCollectors()) || annotation == null) {
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

      if (annotation != null) {
         configuration = new RunConfiguration(annotation);
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
         configuration = null;
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

   protected void runMainExecution(final TestResult tr, final String warmupString, final int executions, final int repetitions) throws Throwable {
      System.gc();
      final String fullWarmupStart = "--- Starting " + warmupString + " {}/" + executions + " ---";
      final String fullWarmupStop = "--- Stopping " + warmupString + " {}/" + executions + " ---";
      tr.beforeRun(); // this seems unnecessary
      int execution = 1;
      try {
         if (annotation.redirectToTemp()) {
            redirectToTempFile();
         } else if (annotation.redirectToNull()) {
            OutputStreamUtil.redirectToNullStream();
         }
         final File samplingResultsFolder;
         List<File> samplingResultFiles = null;
         SjswInterProcessExecutor measurementProcessor = null;
         if(annotation.useSampling() && !warmupString.contains("warmup")
                 && !annotation.samplingResultsFolder().contains("<NULL>")) {
            LOG.info("KoPeMe with sampling enabled.");
            measurementProcessor = new AsprofInterProcessExecutor();
            samplingResultsFolder = new File(annotation.samplingResultsFolder());
            samplingResultFiles = measurementProcessor.prepareForIterativeMeasurements(samplingResultsFolder, executions);
         }
         LOG.debug("Executing " + executions + " " + warmupString);
         for (execution = 1; execution <= executions; execution++) {
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStart, execution);
            }
            runnables.getBeforeRunnable().run();
            tr.startCollection();
            if(annotation.useSampling() && !warmupString.contains("warmup")
                    && !annotation.samplingResultsFolder().contains("<NULL>") && measurementProcessor != null
                    && samplingResultFiles != null) {
               measurementProcessor.measure(samplingResultFiles.get(execution - 1), annotation.samplingInterval());
               LOG.info("Collecting iteration {} samples" , execution);
            }
            runAllRepetitions(repetitions);
            if(annotation.useSampling() && !warmupString.contains("warmup")
                    && !annotation.samplingResultsFolder().contains("<NULL>") && measurementProcessor != null
                    && samplingResultFiles != null) {
               try {
                  measurementProcessor.stopMeasure();
               } catch (IllegalStateException e) {
                  LOG.error("Tried to stop non-running profiler. Ignoring this measurement");
               }
            }
            tr.stopCollection();
            runnables.getAfterRunnable().run();
            tr.setRealExecutions(execution - 1);
            if (annotation.showStart()) {
               LOG.debug(fullWarmupStop, execution);
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
      LOG.debug("Executions: " + (execution - 1));
      tr.setRealExecutions(execution - 1);
   }
   
   private void redirectToTempFile() throws IOException, FileNotFoundException {
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