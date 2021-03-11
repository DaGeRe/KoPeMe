package de.dagere.kopeme;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.TimeBoundExecution.Type;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.datastorage.SaveableTestData;

/**
 * Represents an execution of all runs of one test.
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunner {

   private static Logger LOG = LogManager.getLogger(PerformanceTestRunner.class);

   protected final Class<?> klasse;
   protected final Object instanz;
   protected final Method method;
   protected int executionTimes, warmupExecutions, repetitions, minEarlyStopExecutions, timeout;
   protected Map<String, Double> maximalRelativeStandardDeviation;
   protected Map<String, Long> assertationvalues;
   protected String filename;
   private final boolean useKieker;
   private boolean isFinished = false;
   private final RunConfiguration configuration;

   /**
    * Initializes the PerformanceTestRunner.
    * 
    * @param klasse Class whose tests should be run
    * @param instance Instance of the class, whose tests should be run
    * @param method Test method that should be run
    */
   public PerformanceTestRunner(final Class<?> klasse, final Object instance, final Method method) {
      this.klasse = klasse;
      this.instanz = instance;
      this.method = method;

      final PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

      if (annotation != null) {
         useKieker = annotation.useKieker();
         executionTimes = annotation.iterations();
         warmupExecutions = annotation.warmup();
         repetitions = annotation.repetitions();
         minEarlyStopExecutions = annotation.minEarlyStopExecutions();
         timeout = annotation.timeout();
         maximalRelativeStandardDeviation = new HashMap<>();

         for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
            maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
         }

         assertationvalues = new HashMap<>();
         for (final Assertion a : annotation.assertions()) {
            assertationvalues.put(a.collectorname(), a.maxvalue());
         }
         configuration = new RunConfiguration(annotation);
      } else {
         configuration = new RunConfiguration(5, 1, false, false, false, true, false);
         useKieker = false;
      }

      filename = klasse.getName();
      LOG.info("Executing Performancetest: " + filename);
   }

   /**
    * Runs the performance test.
    * 
    * @throws Throwable Any error that occurs during the test
    */
   public void evaluate() throws Throwable {
      final Finishable finishable = new Finishable() {
         @Override
         public void run() {
            TestResult tr = null;
            try {
               if (method.getParameterTypes().length == 1) {
                  tr = executeComplexTest();
               } else {
                  tr = executeSimpleTest();
               }
               if (!assertationvalues.isEmpty()) {
                  tr.checkValues(assertationvalues);
               }
            } catch (IllegalAccessException | InvocationTargetException e) {
               e.printStackTrace();
            }
         }

         @Override
         public boolean isFinished() {
            return PerformanceTestRunner.this.isFinished;
         }

         @Override
         public void setFinished(final boolean isFinished) {
            PerformanceTestRunner.this.isFinished = isFinished;
         }

      };

      final TimeBoundExecution tbe = new TimeBoundExecution(finishable, timeout, Type.METHOD, useKieker);
      tbe.execute();

      LOG.trace("Test {} beendet", filename);
   }

   /**
    * Executes a complex test, i.e. a test which has TestResult as a parameter.
    * 
    * @return New TestResult
    * @throws IllegalAccessException Thrown if an error during method access occurs
    * @throws InvocationTargetException Thrown if an error during method access occurs
    */
   private TestResult executeComplexTest() throws IllegalAccessException, InvocationTargetException {
      final TestResult tr = new TestResult(method.getName(), warmupExecutions, DataCollectorList.NONE, true);
      final Object[] params = { tr };
      runWarmup(params);

      try {
         if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
            LOG.warn("Not all Collectors are valid!");
         }
         TestResult finalResult = new TestResult(method.getName(), executionTimes, DataCollectorList.STANDARD, false);
         params[0] = finalResult;
         tr.beforeRun();
         final PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, false, params, finalResult);
         runMainExecution(pts, finalResult);
      } catch (final Throwable t) {
         tr.finalizeCollection(t);
         saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, configuration));
         throw t;
      }
      saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, configuration));
      tr.checkValues();
      return tr;
   }

   /**
    * Executes a simple test, i.e. a test without parameters.
    * 
    * @return The result of the test
    * @throws IllegalAccessException Thrown if an error during method access occurs
    * @throws InvocationTargetException Thrown if an error during method access occurs
    */
   private TestResult executeSimpleTest() throws IllegalAccessException, InvocationTargetException {
      TestResult tr = new TestResult(method.getName(), warmupExecutions, DataCollectorList.STANDARD, false);
      final Object[] params = {};
      runWarmup(params);
      tr.deleteTempFile();
      tr = new TestResult(method.getName(), executionTimes, DataCollectorList.STANDARD, true);

      if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
         LOG.warn("Not all Collectors are valid!");
      }
      final long start = System.currentTimeMillis();
      try {
         tr.beforeRun();
         final PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, true, params, tr);
         runMainExecution(pts, tr);
      } catch (final Throwable t) {
         tr.finalizeCollection(t);
         saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, configuration));
         throw t;
      }
      LOG.trace("Zeit: " + (System.currentTimeMillis() - start));
      tr.finalizeCollection();
      saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, configuration));
      tr.checkValues();
      return tr;
   }

   /**
    * Runs the warmup-executions of a test.
    * 
    * @param params The params for the method executions
    * @throws IllegalAccessException Thrown if an error during method access occurs
    * @throws InvocationTargetException Thrown if an error during method access occurs
    */
   private void runWarmup(final Object[] params) throws IllegalAccessException, InvocationTargetException {
      final String methodString = method.getClass().getName() + "." + method.getName();
      for (int i = 1; i <= warmupExecutions; i++) {
         LOG.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
         method.invoke(instanz, params);
         LOG.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
      }
   }

   /**
    * Runs the main Executions of a test.
    * 
    * @param pts The Statement that should be run
    * @param tr The testresult that should save the results and eventually cancel the executions early
    * @throws IllegalAccessException Thrown if an error during method access occurs
    * @throws InvocationTargetException Thrown if an error during method access occurs
    */
   private void runMainExecution(final PerformanceKoPeMeStatement pts, final TestResult tr) throws IllegalAccessException, InvocationTargetException {
      final String methodString = method.getClass().getName() + "." + method.getName();
      int executions;
      for (executions = 1; executions <= executionTimes; executions++) {
         LOG.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
         for (int reptition = 0; reptition < repetitions; reptition++) {
            pts.evaluate();
         }
         LOG.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
         for (final Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
            LOG.debug("Entry: {} Aim: {}", entry.getKey(), entry.getValue());
         }
         tr.setRealExecutions(executions);
//         if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
//               && tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
//            break;
//         }
      }
      LOG.debug("Executions: " + executions);
      tr.setRealExecutions(executions);
   }
}
