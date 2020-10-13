package de.dagere.kopeme.junit5.rule;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.jupiter.engine.extension.MutableExtensionRegistry;
import org.junit.jupiter.engine.support.JupiterThrowableCollectorFactory;
import org.junit.platform.engine.UniqueId;

import de.dagere.kopeme.junit.rule.KoPeMeStandardRuleStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class KoPeMeExtension implements BeforeEachCallback {

   @Override
   public void beforeEach(ExtensionContext context) throws Exception {
      final Object instance = context.getTestInstance().get();
      Method method = context.getTestMethod().get();

      final JupiterConfiguration configuration = getDummyConfiguration();
      TestMethodTestDescriptor descriptor = new TestMethodTestDescriptor(UniqueId.forEngine(JupiterEngineDescriptor.ENGINE_ID), instance.getClass(), method, configuration);

      final JupiterEngineExecutionContext jupiterContext = prepareJUnit5(context, instance, configuration, descriptor);
      try {
         final ThrowingRunnable throwingRunnable = new ThrowingRunnable() {
            
            @Override
            public void run() throws Throwable {
               descriptor.execute(jupiterContext, null);
               
            } };
         final TestRunnables runnables = new TestRunnables(throwingRunnable, instance.getClass(), instance);
         final KoPeMeStandardRuleStatement statement = new KoPeMeStandardRuleStatement(runnables, method, instance.getClass().getName());
         statement.evaluate();
      } catch (Throwable t) {
         t.printStackTrace();
      }
   }

   private JupiterEngineExecutionContext prepareJUnit5(ExtensionContext context, final Object instance, final JupiterConfiguration configuration,
         TestMethodTestDescriptor descriptor) {
      MutableExtensionRegistry extensionRegistry = MutableExtensionRegistry.createRegistryWithDefaultExtensions(configuration);
      
      final JupiterEngineExecutionContext context2 = new JupiterEngineExecutionContext(null, configuration)
            .extend()
            .withExtensionRegistry(extensionRegistry)
            .withExtensionContext(context)
            .withThrowableCollector(JupiterThrowableCollectorFactory.createThrowableCollector())
            .build();
      
      ClassBasedTestDescriptor classDescriptor = new ClassTestDescriptor(UniqueId.forEngine(JupiterEngineDescriptor.ENGINE_ID), instance.getClass(), configuration);
      classDescriptor.prepare(context2);

      descriptor.prepare(context2);
      return context2;
   }

   private JupiterConfiguration getDummyConfiguration() {
      final JupiterConfiguration configuration = new JupiterConfiguration() {

         @Override
         public boolean isParallelExecutionEnabled() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public boolean isExtensionAutoDetectionEnabled() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public Optional<String> getRawConfigurationParameter(String key) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public Predicate<ExecutionCondition> getExecutionConditionFilter() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public Lifecycle getDefaultTestInstanceLifecycle() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public ExecutionMode getDefaultExecutionMode() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public DisplayNameGenerator getDefaultDisplayNameGenerator() {
            // TODO Auto-generated method stub
            return new DisplayNameGenerator() {

               @Override
               public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
                  return nestedClass.getName();
               }

               @Override
               public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
                  // TODO Auto-generated method stub
                  return testClass.getName() + "#" + testMethod.getName();
               }

               @Override
               public String generateDisplayNameForClass(Class<?> testClass) {
                  // TODO Auto-generated method stub
                  return testClass.getName();
               }
            };
         }

         @Override
         public ExecutionMode getDefaultClassesExecutionMode() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public <T> Optional<T> getRawConfigurationParameter(String key, Function<String, T> transformer) {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public Optional<MethodOrderer> getDefaultTestMethodOrderer() {
            // TODO Auto-generated method stub
            return null;
         }
      };
      return configuration;
   }
}
