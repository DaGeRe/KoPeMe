package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.AfterWithMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;

public class BeforeAfterMethodFinder {

   public static void checkNoBeforeWithMeasurement(final Class<?> testClazz) {
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         BeforeWithMeasurement beforeAnnotation = classMethod.getAnnotation(BeforeWithMeasurement.class);
         if (beforeAnnotation != null) {
            throw new RuntimeException("No @BeforeWithMeasurement is allowed if executeBeforeClassInMeasurement is disabled");
         }
         
         AfterWithMeasurement afterAnnotation = classMethod.getAnnotation(AfterWithMeasurement.class);
         
         if (afterAnnotation != null) {
            throw new RuntimeException("No @AfterWithMeasurement is allowed if executeBeforeClassInMeasurement is disabled");
         }
      }
   }

   public static List<Method> getBeforeNoMeasurements(final Class<?> testClazz) {
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         BeforeNoMeasurement annotationValue = classMethod.getAnnotation(BeforeNoMeasurement.class);
         if (annotationValue != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("BeforeNoMeasurement-methods must not have arguments");
            }
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
      }

      List<Method> orderedMethodsMethod = buildOrderedList(beforePriorityMethods);
      return orderedMethodsMethod;
   }

   public static List<Method> getAfterNoMeasurements(final Class<?> testClazz) {
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         AfterNoMeasurement annotationValue = classMethod.getAnnotation(AfterNoMeasurement.class);
         if (annotationValue != null) {
            if (classMethod.getParameterTypes().length > 0) {
               throw new RuntimeException("AfterNoMeasurement-methods must not have arguments");
            }
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
      }

      List<Method> orderedMethodsMethod = buildOrderedList(beforePriorityMethods);
      return orderedMethodsMethod;
   }

   protected static List<Method> buildOrderedList(final Map<Integer, List<Method>> beforePriorityMethods) {
      List<Method> orderedMethodsMethod = new LinkedList<Method>();
      for (List<Method> priorityMethods : beforePriorityMethods.values()) {
         orderedMethodsMethod.addAll(0, priorityMethods);
      }
      return orderedMethodsMethod;
   }

   protected static void addToPriority(final Map<Integer, List<Method>> beforePriorityMethods, final Method classMethod, final int priority) {
      List<Method> currentPriorityList = beforePriorityMethods.get(priority);
      if (currentPriorityList == null) {
         currentPriorityList = new LinkedList<>();
         beforePriorityMethods.put(priority, currentPriorityList);
      }
      currentPriorityList.add(classMethod);
   }
}
