package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.function.ThrowingRunnable;

public class ListOfMethodRunnable implements ThrowingRunnable {
   private final List<Method> currentMethods;
   private final Object testObject;

   public ListOfMethodRunnable(final List<Method> beforeMethods, final Object testObject) {
      this.currentMethods = beforeMethods;
      this.testObject = testObject;
   }

   @Override
   public void run() throws Throwable {
      for (final Method method : currentMethods) {
         method.invoke(testObject);
      }
   }
}