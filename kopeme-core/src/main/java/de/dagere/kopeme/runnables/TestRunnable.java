package de.dagere.kopeme.runnables;


public interface TestRunnable {

   /**
    * Returns the test Runnable
    * 
    * @return Test-Runnable
    */
   public KoPeMeThrowingRunnable getTestRunnable();

   /**
    * Returns the runnable, that should be run before the test
    * 
    * @return Before-Runnable
    */
   public KoPeMeThrowingRunnable getBeforeRunnable();

   /**
    * Returns the runnable, that should be run after the test
    * 
    * @return After-Runnable
    */
   public KoPeMeThrowingRunnable getAfterRunnable();
}