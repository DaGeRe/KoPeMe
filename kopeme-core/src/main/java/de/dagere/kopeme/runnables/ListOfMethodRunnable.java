package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.List;

public class ListOfMethodRunnable implements KoPeMeThrowingRunnable {
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