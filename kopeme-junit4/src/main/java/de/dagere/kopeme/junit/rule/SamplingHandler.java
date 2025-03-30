package de.dagere.kopeme.junit.rule;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import io.github.terahidro2003.measurement.executor.SjswInterProcessExecutor;
import io.github.terahidro2003.measurement.executor.asprof.AsprofInterProcessExecutor;

public class SamplingHandler {
   
   private static final Logger LOG = LogManager.getLogger(SamplingHandler.class);
   
   private final PerformanceTest annotation;
   private List<File> samplingResultFiles;
   private SjswInterProcessExecutor measurementProcessor;
   private final String warmupString;

   public SamplingHandler(final PerformanceTest annotation, final String warmupString, final int executions) {
      this.annotation = annotation;
      this.warmupString = warmupString;
      
      if(annotation.useSampling() && !warmupString.contains("warmup")
              && !annotation.samplingResultsFolder().contains("<NULL>")) {
         LOG.info("KoPeMe with sampling enabled.");
         measurementProcessor = new AsprofInterProcessExecutor();
         final File samplingResultsFolder = new File(annotation.samplingResultsFolder());
         samplingResultFiles = measurementProcessor.prepareForIterativeMeasurements(samplingResultsFolder, executions);
      }
   }
   
   public void handleSamplingStart(final TestResult tr, final int execution) {
      if(annotation.useSampling() && !warmupString.contains("warmup")
              && !annotation.samplingResultsFolder().contains("<NULL>") && measurementProcessor != null
              && samplingResultFiles != null) {
         measurementProcessor.measure(samplingResultFiles.get(execution - 1), annotation.samplingInterval(), tr.getTestcase());
         LOG.info("Collecting iteration {} samples" , execution);
      }
   }

   public void handleSamplingEnd() {
      if(annotation.useSampling() && !warmupString.contains("warmup")
              && !annotation.samplingResultsFolder().contains("<NULL>") && measurementProcessor != null
              && samplingResultFiles != null) {
         try {
            measurementProcessor.stopMeasure();
         } catch (IllegalStateException e) {
            LOG.error("Tried to stop non-running profiler. Ignoring this measurement", e);
         }
      }
   }
}
