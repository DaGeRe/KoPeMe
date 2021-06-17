package de.dagere.kopeme.junit5.rule;

import java.lang.reflect.Method;

import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.jupiter.engine.extension.MutableExtensionRegistry;
import org.junit.jupiter.engine.support.JupiterThrowableCollectorFactory;
import org.junit.platform.engine.UniqueId;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.junit.rule.KoPeMeStandardRuleStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class KoPeMeJUnit5Starter {

   private final ExtensionContext context;
   private final UniqueId currentId = UniqueId.forEngine(JupiterEngineDescriptor.ENGINE_ID);
   private final Object instance;
   private final Method method;
   private final JupiterConfiguration configuration;

   public KoPeMeJUnit5Starter(final ExtensionContext context) {
      this.context = context;
      instance = context.getTestInstance().get();
      method = context.getTestMethod().get();
      configuration = getDummyConfiguration();
   }

   public void start() throws Exception {
      TestMethodTestDescriptor descriptor = new TestMethodTestDescriptor(currentId, instance.getClass(), method, configuration);

      final JupiterEngineExecutionContext jupiterContext = prepareJUnit5(descriptor);
      try {
         final ThrowingRunnable throwingRunnable = new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {
               descriptor.execute(jupiterContext, null);
            }
         };
         final RunConfiguration runConfiguration = new RunConfiguration(method.getAnnotation(PerformanceTest.class));
         final TestRunnables runnables = new TestRunnables(runConfiguration, throwingRunnable, instance.getClass(), instance);
         final KoPeMeStandardRuleStatement statement = new KoPeMeStandardRuleStatement(runnables, method, instance.getClass().getName());
         statement.evaluate();
      } catch (Throwable t) {
         t.printStackTrace();
      }
   }

   private JupiterEngineExecutionContext prepareJUnit5(final TestMethodTestDescriptor descriptor) {
      MutableExtensionRegistry extensionRegistry = MutableExtensionRegistry.createRegistryWithDefaultExtensions(configuration);

      final JupiterEngineExecutionContext kopemeContext = new JupiterEngineExecutionContext(null, configuration)
            .extend()
            .withExtensionRegistry(extensionRegistry)
            .withExtensionContext(context)
            .withThrowableCollector(JupiterThrowableCollectorFactory.createThrowableCollector())
            .build();

      ClassBasedTestDescriptor classDescriptor = new ClassTestDescriptor(currentId, instance.getClass(), configuration);
      JupiterEngineExecutionContext clazzContext = classDescriptor.prepare(kopemeContext);
      JupiterEngineExecutionContext methodContext = descriptor.prepare(clazzContext);
      return methodContext;
   }

   private JupiterConfiguration getDummyConfiguration() {
      final JupiterConfiguration configuration = new DummyConfiguration();
      return configuration;
   }
}
