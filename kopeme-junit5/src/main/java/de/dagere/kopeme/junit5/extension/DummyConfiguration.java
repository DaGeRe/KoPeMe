package de.dagere.kopeme.junit5.extension;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestInstantiationAwareExtension.ExtensionContextScope;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDirFactory;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.platform.engine.reporting.OutputDirectoryProvider;

final class DummyConfiguration implements JupiterConfiguration {
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
   public Optional<String> getRawConfigurationParameter(final String key) {
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
         public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {
            return nestedClass.getName();
         }

         @Override
         public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {
            // TODO Auto-generated method stub
            return testClass.getName() + "#" + testMethod.getName();
         }

         @Override
         public String generateDisplayNameForClass(final Class<?> testClass) {
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
   public <T> Optional<T> getRawConfigurationParameter(final String key, final Function<String, T> transformer) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Optional<MethodOrderer> getDefaultTestMethodOrderer() {
      return Optional.of(new MethodOrderer.DisplayName());
   }

   @Override
   public Optional<ClassOrderer> getDefaultTestClassOrderer() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public CleanupMode getDefaultTempDirCleanupMode() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Supplier<TempDirFactory> getDefaultTempDirFactorySupplier() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Predicate<Class<? extends Extension>> getFilterForAutoDetectedExtensions() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isThreadDumpOnTimeoutEnabled() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public ExtensionContextScope getDefaultTestInstantiationExtensionContextScope() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public OutputDirectoryProvider getOutputDirectoryProvider() {
      // TODO Auto-generated method stub
      return null;
   }
}