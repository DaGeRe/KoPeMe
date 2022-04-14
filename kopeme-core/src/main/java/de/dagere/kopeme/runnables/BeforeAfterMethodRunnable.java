package de.dagere.kopeme.runnables;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.function.ThrowingRunnable;

public class BeforeAfterMethodRunnable implements ThrowingRunnable {
   private final List<Method> beforeMethods;
   private final ThrowingRunnable runnable;
   private final List<Method> afterMethods;
   private final Object testObject;

   public BeforeAfterMethodRunnable(final List<Method> beforeMethods, final ThrowingRunnable runnable, final List<Method> afterMethods, final Object testObject) {
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