package de.dagere.kopeme.runnables;

import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;

public class SimpleThrowingRunnable implements KoPeMeThrowingRunnable {
   private final TestMethodTestDescriptor descriptor;
   private final JupiterEngineExecutionContext clazzContext;

   public SimpleThrowingRunnable(TestMethodTestDescriptor descriptor, JupiterEngineExecutionContext clazzContext) {
      this.descriptor = descriptor;
      this.clazzContext = clazzContext;
   }

   @Override
   public void run() throws Throwable {
      descriptor.execute(clazzContext, null);

      if (clazzContext.getThrowableCollector().getThrowable() != null) {
         throw clazzContext.getThrowableCollector().getThrowable();
      }
   }
}
