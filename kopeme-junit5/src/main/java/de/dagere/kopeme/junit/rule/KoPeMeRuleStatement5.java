package de.dagere.kopeme.junit.rule;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.TimeBoundExecution.Type;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.ParamNameHelper;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.annotations.ParameterChecker;
import de.dagere.kopeme.runnables.TestRunnable;

/**
 * Represents an execution of all runs of one test
 * 
 * TODO: Overthink weather directly configure test runs in KoPeMeRule would be more nice
 * 
 * @author dagere
 * 
 */
public class KoPeMeRuleStatement5 extends KoPeMeBasicStatement5 {

   private static final Logger LOG = LogManager.getLogger(KoPeMeRuleStatement5.class);

   private final TestResult finalResult;
   private final LinkedHashMap<String, String> params;

   public KoPeMeRuleStatement5(final TestRunnable runnables, final Method method, final String filename, final LinkedHashMap<String, String> params) {
      super(runnables,
            method,
            filename,
            (params != null) ? method.getName() + "(" + ParamNameHelper.paramsToString(params) + ")" : method.getName());
      finalResult = new TestResult(method.getName(), annotation.warmup(), datacollectors, false, params);
      this.params = params;
      
      if (!ParameterChecker.parameterIndexInvalid(annotation, params)) {
         String methodFileName = (params != null) ? method.getName() + "(" + ParamNameHelper.paramsToString(params) + ")" : method.getName();
         initializeKieker(clazzname, methodFileName);
      }
   }

   public void evaluate() throws Throwable {
      boolean parameterIndexInvalid = ParameterChecker.parameterIndexInvalid(annotation, params);
      if (parameterIndexInvalid) {
         return;
      }
      
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
            return KoPeMeRuleStatement5.this.isFinished;
         }

         @Override
         public void setFinished(final boolean isFinished) {
            KoPeMeRuleStatement5.this.isFinished = isFinished;
         }
      };

      final TimeBoundExecution tbe = new TimeBoundExecution(finishable, annotation.timeout(), Type.METHOD, annotation.useKieker());
      tbe.execute();
      LOG.info("Test {} beendet", clazzname);
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
      } catch (final Throwable t) {
         t.printStackTrace();
         finalResult.finalizeCollection(t);
         saveData(SaveableTestData.createErrorTestData(finalResult.getMethodName(), clazzname, finalResult, configuration));
         throw t;
      }
      finalResult.finalizeCollection();
      saveData(SaveableTestData.createFineTestData(finalResult.getMethodName(), clazzname, finalResult, configuration));
   }

   private void runWarmup() throws Throwable {
      if (annotation.warmup() > 0) {
         final TestResult deletableResult = new TestResult(method.getName(), annotation.warmup(), datacollectors, true);
         runMainExecution(deletableResult, "warmup execution ", annotation.warmup(), annotation.repetitions());
         deletableResult.deleteTempFile();
      }
   }
}
