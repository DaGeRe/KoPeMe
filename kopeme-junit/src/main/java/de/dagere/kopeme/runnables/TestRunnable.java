package de.dagere.kopeme.runnables;

import org.junit.function.ThrowingRunnable;

public interface TestRunnable {

   /**
    * Returns the test Runnable
    * 
    * @return Test-Runnable
    */
   public ThrowingRunnable getTestRunnable();

   /**
    * Returns the runnable, that should be run before the test
    * 
    * @return Before-Runnable
    */
   public ThrowingRunnable getBeforeRunnable();

   /**
    * Returns the runnable, that should be run after the test
    * 
    * @return After-Runnable
    */
   public ThrowingRunnable getAfterRunnable();

}