package de.dagere.kopeme.junit5.extension;

import static java.util.Collections.emptySet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.Filterable;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.descriptor.TestTemplateInvocationTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.jupiter.engine.extension.MutableExtensionRegistry;
import org.junit.jupiter.engine.support.JupiterThrowableCollectorFactory;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datastorage.RunConfiguration;
import de.dagere.kopeme.junit.rule.BeforeAfterMethodFinderJUnit5;
import de.dagere.kopeme.junit.rule.KoPeMeExtensionStatement;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.runnables.KoPeMeThrowingRunnable;
import de.dagere.kopeme.runnables.PreparableTestRunnables;
import de.dagere.kopeme.runnables.SimpleThrowingRunnable;
import de.dagere.kopeme.runnables.TestRunnable;
import de.dagere.kopeme.runnables.TestRunnables;

public class KoPeMeJUnit5Starter {

   private final ExtensionContext context;
   private final UniqueId currentId = UniqueId.forEngine(JupiterEngineDescriptor.ENGINE_ID);
   private final Object outerInstance;
   private final Method method;
   private final JupiterConfiguration configuration;
   private LinkedHashMap<String, String> params = null;

   public KoPeMeJUnit5Starter(final ExtensionContext context) {
      this.context = context;
      outerInstance = context.getTestInstance().get();
      method = context.getTestMethod().get();
      configuration = getDummyConfiguration();
   }
   
   public void start() {
      TestMethodTestDescriptor descriptor = new TestMethodTestDescriptor(currentId, outerInstance.getClass(), method, new Supplier<List<Class<?>>>() {
         
         @Override
         public List<Class<?>> get() {
            return Arrays.asList(KoPeMeJUnit5Starter.class);
         }
      }, configuration);

      boolean reinitialize = needsReinitialization();
      if (reinitialize) {
         executeMethodReinitializationTest(descriptor);
      } else {
         executeTest(descriptor);
      }
   }

   /**
    * Some test cases require reinitialization, e.g. if InjectMocks is used (cause if the field in an injected mock is already set, it will not be set again).
    * These cases are defined here (and should be extended if necessary).
    */
   private boolean needsReinitialization() {
      boolean reinitialize = false;
      for (Field field : outerInstance.getClass().getDeclaredFields()) {
         for (Annotation annotation : field.getAnnotations()) {
            if (annotation.toString().startsWith("@org.mockito.InjectMocks")) {
               reinitialize = true;
            }
         }
      }
      return reinitialize;
   }

   private void executeMethodReinitializationTest(TestMethodTestDescriptor descriptor) {
      final JupiterEngineExecutionContext jupiterContext = prepareJUnit5Class(descriptor);
      final RunConfiguration runConfiguration = new RunConfiguration(method.getAnnotation(PerformanceTest.class));

      final PreparableTestRunnables runnables = new PreparableTestRunnables(runConfiguration, outerInstance.getClass(), descriptor, jupiterContext);

      try {
         final KoPeMeExtensionStatement statement = new KoPeMeExtensionStatement(runnables, method, outerInstance.getClass().getName(), params);
         executeStatement(jupiterContext, statement);
      } catch (Throwable t) {
         throw new RuntimeException("Test caused exception", t);
      }
   }

   private void executeTest(TestMethodTestDescriptor descriptor) {
      final JupiterEngineExecutionContext clazzContext = prepareJUnit5Method(descriptor);
      try {
         final KoPeMeThrowingRunnable throwingRunnable = new SimpleThrowingRunnable(descriptor, clazzContext);
         final Object ownCreatedInstance = clazzContext.getExtensionContext().getTestInstance().get();

         final RunConfiguration runConfiguration = new RunConfiguration(method.getAnnotation(PerformanceTest.class));
         List<Method> beforeClassMethods = BeforeAfterMethodFinderJUnit5.getBeforeWithMeasurements(outerInstance.getClass());
         List<Method> afterClassMethods = BeforeAfterMethodFinderJUnit5.getAfterWithMeasurements(outerInstance.getClass());
         final TestRunnable runnables = new TestRunnables(runConfiguration, throwingRunnable, outerInstance.getClass(), ownCreatedInstance,
               beforeClassMethods, afterClassMethods);
         final KoPeMeExtensionStatement statement = new KoPeMeExtensionStatement(runnables, method, outerInstance.getClass().getName(), params);
         executeStatement(clazzContext, statement);
      } catch (Throwable t) {
         throw new RuntimeException("Test caused exception", t);
      }
   }

   private void executeStatement(final JupiterEngineExecutionContext clazzContext, final KoPeMeExtensionStatement statement) throws Throwable {
      statement.evaluate();
      if (statement.getThrowable() != null) {
         throw statement.getThrowable();
      }
      ThrowableCollector collector = clazzContext.getThrowableCollector();
      if (!collector.isEmpty()) {
         throw new RuntimeException("Test caused exception", collector.getThrowable());
      }
   }

   private JupiterEngineExecutionContext prepareJUnit5Class(final TestMethodTestDescriptor descriptor) {
      MutableExtensionRegistry extensionRegistry = MutableExtensionRegistry.createRegistryWithDefaultExtensions(configuration);

      final JupiterEngineExecutionContext kopemeContext = new JupiterEngineExecutionContext(null, configuration)
            .extend()
            .withExtensionRegistry(extensionRegistry)
            .withExtensionContext(context)
            .withThrowableCollector(JupiterThrowableCollectorFactory.createThrowableCollector())
            .build();

      ClassBasedTestDescriptor classDescriptor = new ClassTestDescriptor(currentId, outerInstance.getClass(), configuration);
      JupiterEngineExecutionContext clazzContext = classDescriptor.prepare(kopemeContext);

      clazzContext = eventuallyAddParameterContext(descriptor, clazzContext);
      return clazzContext;
   }

   private JupiterEngineExecutionContext prepareJUnit5Method(final TestMethodTestDescriptor descriptor) {
      JupiterEngineExecutionContext clazzContext = prepareJUnit5Class(descriptor);
      JupiterEngineExecutionContext methodContext = descriptor.prepare(clazzContext);

      return methodContext;
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

                  if (testDescriptor instanceof TestTemplateInvocationTestDescriptor) {
                     TestTemplateInvocationTestDescriptor testTemplateDescriptor = (TestTemplateInvocationTestDescriptor) testDescriptor;
                     clazzContext = testTemplateDescriptor.prepare(clazzContext);

                     int index = getIndex(testTemplateDescriptor);
                     createParams(index);
                  }
               } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                  e.printStackTrace();
               }
            }
         }
      }
      return clazzContext;
   }

   private void createParams(int index) {
      params = new LinkedHashMap<String, String>();
      params.put(KoPeMeConstants.JUNIT_PARAMETERIZED, Integer.toString(index));
   }

   private static final Pattern PATTERN = Pattern.compile("^[^\\d]*(\\d+)");

   public static int getIndex(TestTemplateInvocationTestDescriptor testTemplateDescriptor) {
      String displayName = testTemplateDescriptor.getDisplayName();
      return getIndexFromName(displayName);
   }

   public static int getIndexFromName(String displayName) {
      String index = "0";
      Matcher matcher = PATTERN.matcher(displayName);
      if (matcher.lookingAt()) {
         index = matcher.group(1);
      }
      return Integer.parseInt(index);
   }

   private JupiterConfiguration getDummyConfiguration() {
      final JupiterConfiguration configuration = new DummyConfiguration(context);
      return configuration;
   }
}
