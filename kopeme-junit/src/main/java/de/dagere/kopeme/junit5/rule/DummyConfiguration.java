package de.dagere.kopeme.junit5.rule;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.engine.config.JupiterConfiguration;

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
}