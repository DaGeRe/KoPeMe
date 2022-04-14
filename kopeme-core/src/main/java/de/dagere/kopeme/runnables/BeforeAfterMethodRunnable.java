package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.List;

public class BeforeAfterMethodRunnable implements KoPeMeThrowingRunnable {
   private final List<Method> beforeMethods;
   private final KoPeMeThrowingRunnable runnable;
   private final List<Method> afterMethods;
   private final Object testObject;

   public BeforeAfterMethodRunnable(final List<Method> beforeMethods, final KoPeMeThrowingRunnable runnable, final List<Method> afterMethods, final Object testObject) {
      this.beforeMethods = beforeMethods;
      this.runnable = runnable;
      this.afterMethods = afterMethods;
      this.testObject = testObject;
   }


   @Override
   public void run() throws Throwable {
      for (final Method method : beforeMethods) {
         method.invoke(testObject);
      }
      runnable.run();
      for (final Method method : afterMethods) {
         method.invoke(testObject);
      }
   }
}