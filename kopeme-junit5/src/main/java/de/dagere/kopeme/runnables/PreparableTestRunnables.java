package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

import de.dagere.kopeme.datastorage.RunConfiguration;

public class PreparableTestRunnables implements TestRunnable {
   
   private final List<Method> beforeMethods;
   private final List<Method> afterMethods;
   private final RunConfiguration config;
   private final Class<?> testClass;
   private final TestMethodTestDescriptor descriptor;
   private final JupiterEngineExecutionContext jupiterContext;
   
   private Object instance;
   private ThrowingRunnable innerRunnable;

   public PreparableTestRunnables(RunConfiguration config, Class<?> testClass, TestMethodTestDescriptor descriptor, JupiterEngineExecutionContext jupiterContext) {
      this.config = config;
      this.testClass = testClass;
      beforeMethods = BeforeAfterMethodFinder.getBeforeNoMeasurements(testClass);
      afterMethods = BeforeAfterMethodFinder.getAfterNoMeasurements(testClass);

      this.descriptor = descriptor;
      this.jupiterContext = jupiterContext;
   }

   public void prepare() {
      final JupiterEngineExecutionContext methodContext = descriptor.prepare(jupiterContext);
      instance = methodContext.getExtensionContext().getTestInstance().get();

      innerRunnable = new ThrowingRunnable() {

         @Override
         public void run() throws Throwable {
            descriptor.execute(methodContext, null);
            methodContext.close();
            if (!methodContext.getThrowableCollector().isEmpty()) {
               Method addMethod = ThrowableCollector.class.getDeclaredMethod("add", Throwable.class);
               addMethod.setAccessible(true);
               addMethod.invoke(jupiterContext.getThrowableCollector(), methodContext.getThrowableCollector().getThrowable());
            }
         }
      };

   }

   @Override
   public ThrowingRunnable getTestRunnable() {
      final ThrowingRunnable runnable;
      if (config.isExecuteBeforeClassInMeasurement()) {
         List<Method> beforeClassMethod = BeforeAfterMethodFinder.getBeforeWithMeasurements(testClass);
         List<Method> afterClassMethod = BeforeAfterMethodFinder.getAfterWithMeasurements(testClass);

         runnable = new BeforeAfterMethodRunnable(beforeClassMethod, innerRunnable, afterClassMethod, instance);
      } else {
         runnable = innerRunnable;
      }
      return runnable;
   }

   @Override
   public ThrowingRunnable getBeforeRunnable() {
      prepare();
      ThrowingRunnable beforeRunnable = new ListOfMethodRunnable(beforeMethods, instance);
      return beforeRunnable;
   }

   @Override
   public ThrowingRunnable getAfterRunnable() {
      ThrowingRunnable afterRunnable = new ListOfMethodRunnable(afterMethods, instance);
      return afterRunnable;
   }

}
