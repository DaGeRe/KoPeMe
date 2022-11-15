package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import de.dagere.kopeme.junit.rule.annotations.AfterWithMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.runnables.BeforeAfterMethodFinder;

public class BeforeAfterMethodFinderJUnit5 extends BeforeAfterMethodFinder {
   public static List<Method> getBeforeWithMeasurements(final Class<?> testClazz) {
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         BeforeWithMeasurement annotationValue = classMethod.getAnnotation(BeforeWithMeasurement.class);
         if (annotationValue != null) {
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
         if (classMethod.getAnnotation(BeforeEach.class) != null) {
            classMethod.setAccessible(true);
            addToPriority(beforePriorityMethods, classMethod, Integer.MAX_VALUE);
         }
      }

      List<Method> orderedMethodsMethod = BeforeAfterMethodFinder.buildOrderedList(beforePriorityMethods);
      return orderedMethodsMethod;
   }

   public static List<Method> getAfterWithMeasurements(final Class<?> testClazz) {
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         AfterWithMeasurement annotationValue = classMethod.getAnnotation(AfterWithMeasurement.class);
         if (annotationValue != null) {
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
         if (classMethod.getAnnotation(AfterEach.class) != null) {
            classMethod.setAccessible(true);
            addToPriority(beforePriorityMethods, classMethod, Integer.MAX_VALUE);
         }
      }

      List<Method> orderedMethodsMethod = BeforeAfterMethodFinder.buildOrderedList(beforePriorityMethods);
      return orderedMethodsMethod;
   }
}
