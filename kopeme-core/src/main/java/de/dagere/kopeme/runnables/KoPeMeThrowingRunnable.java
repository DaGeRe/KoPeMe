package de.dagere.kopeme.runnables;

/**
 * This is required since some projects exclude JUnit 4
 * @author DaGeRe
 *
 */
public interface KoPeMeThrowingRunnable {
   void run() throws Throwable;
}
