package de.dagere.kopeme.junit5.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.descriptor.TestTemplateInvocationTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.jupiter.engine.extension.MutableExtensionRegistry;
import org.junit.jupiter.engine.support.JupiterThrowableCollectorFactory;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.junit.rule.KoPeMeRule;
import de.dagere.kopeme.junit.rule.KoPeMeStandardRuleStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class KoPeMeJUnit5Starter {

   private final ExtensionContext context;
   private final UniqueId currentId = UniqueId.forEngine(JupiterEngineDescriptor.ENGINE_ID);
   private final Object instance;
   private final Method method;
   private final JupiterConfiguration configuration;
   private Params params = null;
   private boolean enabled = true;

   public KoPeMeJUnit5Starter(final ExtensionContext context) {
      this.context = context;
      instance = context.getTestInstance().get();
      method = context.getTestMethod().get();
      configuration = getDummyConfiguration();
   }

   public void start() throws Exception {
      TestMethodTestDescriptor descriptor = new TestMethodTestDescriptor(currentId, instance.getClass(), method, configuration);

      final JupiterEngineExecutionContext jupiterContext = prepareJUnit5(descriptor);
      
      if (enabled) {
         executeTest(descriptor, jupiterContext);
      }else {
         System.out.println("Test has been disabled by chosenIndex");
      }
   }

   private void executeTest(TestMethodTestDescriptor descriptor, final JupiterEngineExecutionContext jupiterContext) {
      try {
         final ThrowingRunnable throwingRunnable = new ThrowingRunnable() {

            @Override
            public void run() throws Throwable {
               JupiterEngineExecutionContext methodContext = descriptor.prepare(jupiterContext);

               descriptor.execute(methodContext, null);
               methodContext.close();
               if (!methodContext.getThrowableCollector().isEmpty()) {
                  Method addMethod = ThrowableCollector.class.getDeclaredMethod("add", Throwable.class);
                  addMethod.setAccessible(true);
                  addMethod.invoke(jupiterContext.getThrowableCollector(), methodContext.getThrowableCollector().getThrowable());
               }
            }
         };
         final RunConfiguration runConfiguration = new RunConfiguration(method.getAnnotation(PerformanceTest.class));
         final TestRunnables runnables = new TestRunnables(runConfiguration, throwingRunnable, instance.getClass(), instance);
         final KoPeMeStandardRuleStatement statement = new KoPeMeStandardRuleStatement(runnables, method, instance.getClass().getName(), params);
         statement.evaluate();
         ThrowableCollector collector = jupiterContext.getThrowableCollector();
         if (!collector.isEmpty()) {
            throw new RuntimeException("Test caused exception", collector.getThrowable());
         }
      } catch (Throwable t) {
         throw new RuntimeException("Test caused exception", t);
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

      clazzContext = eventuallyAddParameterContext(descriptor, clazzContext);

      return clazzContext;
   }

   private static Method getTestDescriptorMethod;
   
   static {
      try {
         Class<?> abstractExtensionContextClass = Class.forName("org.junit.jupiter.engine.descriptor.AbstractExtensionContext");
         getTestDescriptorMethod = abstractExtensionContextClass.getDeclaredMethod("getTestDescriptor");
         getTestDescriptorMethod.setAccessible(true);
      } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException e) {
         e.printStackTrace();
      }
      
   }
   
   private JupiterEngineExecutionContext eventuallyAddParameterContext(final TestMethodTestDescriptor descriptor, JupiterEngineExecutionContext clazzContext) {
      if (descriptor.getSource().isPresent()) {
         TestSource testSource = descriptor.getSource().get();
         if (testSource instanceof MethodSource) {
            MethodSource source = (MethodSource) testSource;
            String parameters = source.getMethodParameterTypes();
            if (parameters.length() != 0) {
               try {
                  TestDescriptor testDescriptor = (TestDescriptor) getTestDescriptorMethod.invoke(context);

                  TestTemplateInvocationTestDescriptor testTemplateDescriptor = (TestTemplateInvocationTestDescriptor) testDescriptor;
                  clazzContext = testTemplateDescriptor.prepare(clazzContext);

                  int index = getIndex(testTemplateDescriptor);
                  createParams(index);
                  
               } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                  e.printStackTrace();
               }
            }
         }
      }
      return clazzContext;
   }

   private void createParams(int index) {
      Result.Params.Param param = new Result.Params.Param();
      param.setKey(KoPeMeRule.JUNIT_PARAMETERIZED);
      param.setValue(Integer.toString(index));
      params = new Params();
      params.getParam().add(param);
   }

   private int getIndex(TestTemplateInvocationTestDescriptor testTemplateDescriptor) {
      String displayName = testTemplateDescriptor.getDisplayName();
      String index = displayName.substring(1, displayName.indexOf(" ") - 1);
      return Integer.parseInt(index);
   }

   private JupiterConfiguration getDummyConfiguration() {
      final JupiterConfiguration configuration = new DummyConfiguration();
      return configuration;
   }
}
