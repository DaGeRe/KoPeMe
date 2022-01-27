package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.AfterWithMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;

public class BeforeAfterMethodFinder {

   public static List<Method> getBeforeWithMeasurements(final Class<?> testClazz){
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         BeforeWithMeasurement annotationValue = classMethod.getAnnotation(BeforeWithMeasurement.class);
         if (annotationValue != null) {
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
         if (classMethod.getAnnotation(BeforeClass.class) != null
               || classMethod.getAnnotation(BeforeAll.class) != null) {
            classMethod.setAccessible(true);
            addToPriority(beforePriorityMethods, classMethod, Integer.MAX_VALUE);
         }
      }
      
      List<Method> orderedMethodsMethod = buildOrderedList(beforePriorityMethods);
      return orderedMethodsMethod;
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

   public static List<Method> getAfterWithMeasurements(final Class<?> testClazz) {
      Map<Integer, List<Method>> beforePriorityMethods = new TreeMap<>();
      for (final Method classMethod : testClazz.getDeclaredMethods()) {
         AfterWithMeasurement annotationValue = classMethod.getAnnotation(AfterWithMeasurement.class);
         if (annotationValue != null) {
            classMethod.setAccessible(true);
            int priority = annotationValue.priority();
            addToPriority(beforePriorityMethods, classMethod, priority);
         }
         if (classMethod.getAnnotation(AfterClass.class) != null 
               || classMethod.getAnnotation(AfterEach.class) != null) {
            classMethod.setAccessible(true);
            addToPriority(beforePriorityMethods, classMethod, Integer.MAX_VALUE);
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

   private static List<Method> buildOrderedList(final Map<Integer, List<Method>> beforePriorityMethods) {
      List<Method> orderedMethodsMethod = new LinkedList<Method>();
      for (List<Method> priorityMethods : beforePriorityMethods.values()) {
         orderedMethodsMethod.addAll(0, priorityMethods);
      }
      return orderedMethodsMethod;
   }

   private static void addToPriority(final Map<Integer, List<Method>> beforePriorityMethods, final Method classMethod, final int priority) {
      List<Method> currentPriorityList = beforePriorityMethods.get(priority);
      if (currentPriorityList == null) {
         currentPriorityList = new LinkedList<>();
         beforePriorityMethods.put(priority, currentPriorityList);
      }
      currentPriorityList.add(classMethod);
   }
}
