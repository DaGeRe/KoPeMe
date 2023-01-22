package de.dagere.kopeme;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

/**
 * Finishes a Finishable after a certain time and finishes Kieker monitoring if enabled.
 * 
 * @author reichelt
 *
 */
public class TimeBoundExecution {

   public static enum Type {
      CLASS, METHOD;
   }

   public static int id = 0;

   private static final Logger LOG = LogManager.getLogger(TimeBoundExecution.class);

   ThreadGroup experimentThreadGroup;
   private final FinishableThread experimentThread;
   private final Type type;
   private final long timeout;
   private boolean needToStopHart = false;
   private final boolean useKieker;
   private Throwable testError;

   private int interruptTries = 10;
   private int waitTimeBetweenInterrupts = 10;

   public TimeBoundExecution(final Finishable finishable, final long timeout, final Type type, final boolean useKieker) {
      String threadName;
      synchronized (LOG) {
         threadName = "timebound-" + (id++);
      }
      experimentThreadGroup = new ThreadGroup("kopeme-experiment");
      this.experimentThread = new FinishableThread(experimentThreadGroup, finishable, threadName);
      this.timeout = timeout;
      this.type = type;
      this.useKieker = useKieker;
   }

   /**
    * Executes the TimeBoundedExecution.
    * 
    * @throws Exception Thrown if an error occurs
    */
   public final boolean execute() {
      experimentThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(final Thread t, final Throwable e) {
            if (e instanceof OutOfMemoryError) {
               LOG.debug("Out of memory - can not reuse VM for measurement");
               e.printStackTrace();
               t.interrupt();
               System.exit(1);
            }
            testError = e;
         }
      });

      boolean finished = tryExecution();

      if (needToStopHart == true && type != Type.CLASS) {
         LOG.error("Would normally stop " + type + " hard; omitted.");
         //// System.exit(1);
      }
      finishKieker();

      if (testError != null) {
         rethrowError();
      }

      return finished;
   }

   private void finishKieker() {
      if (useKieker) {
         try {
            KoPeMeKiekerSupport.INSTANCE.waitForEnd();
         } catch (final Exception e) {
            e.printStackTrace();
            testError = e;
         }
      }
   }

   private boolean tryExecution() {
      boolean finished = false;
      try {
         experimentThread.start();
         LOG.debug("Waiting: {}", timeout);
         experimentThread.join(timeout);
         experimentThread.setFinished(true);
         if (experimentThread.isAlive()) {
            LOG.trace("Waiting for 100 ms, whether test stops alone");
            Thread.sleep(100);
         }
         LOG.debug("KoPeMe-Test {}. Kieker: {} Threads: {}", type, useKieker, experimentThreadGroup.activeCount());

         if (experimentThreadGroup.activeCount() != 0 && type == Type.METHOD) {
            handleChildThreads();
         } else if (type == Type.CLASS && experimentThread.isAlive()) {
            LOG.info("Class timed out.");
         } else {
            finished = true;
         }
      } catch (final InterruptedException e1) {
         e1.printStackTrace();
         testError = e1;
      }
      return finished;
   }

   private void rethrowError() throws Error {
      LOG.debug("Test error != null");
      if (testError instanceof TimeoutException) {
         throw new RuntimeException(testError);
      } else if (testError instanceof RuntimeException) {
         throw (RuntimeException) testError;
      } else if (testError instanceof Error) {
         throw (Error) testError;
      } else {
         LOG.error("Unexpected behaviour");
         testError.printStackTrace();
      }
   }

   private void handleChildThreads() throws InterruptedException {
      if (experimentThread.isAlive() && useKieker) {
         KoPeMeKiekerSupport.INSTANCE.waitForEnd();
      }
      final Thread[] stillActiveThreads = new Thread[experimentThreadGroup.activeCount()];
      experimentThreadGroup.enumerate(stillActiveThreads);
      LOG.debug("Finishing {} remaining thread(s)", stillActiveThreads.length);
      for (final Thread thread : stillActiveThreads) {
         waitForThreadEnd(100, thread);
      }
      LOG.debug("Threads still active: {}", experimentThreadGroup.activeCount());
      if (experimentThreadGroup.activeCount() != 0) {
         LOG.error("Finishing all Threads was not successfull, still {} Threads active - finishing VM", experimentThreadGroup.activeCount());
         needToStopHart = true;
         // testError = new TimeoutException("Test timed out because subthreads could not be finished: " + experimentThreadGroup.activeCount());
      }
   }

   private void waitForThreadEnd(final long timeoutTime, final Thread thread) throws InterruptedException {
      thread.join(timeoutTime);
      Thread.sleep(waitTimeBetweenInterrupts);
      LOG.trace("Test should be finished...");
      if (thread.isAlive()) {
         int count = tryNTimes(thread);
         if (count == interruptTries) {
            LOG.debug("Experiment thread does not respond, so the JVM needs to be shutdown now: " + thread.getName());
            needToStopHart = true;
         }
      }
   }

   private int tryNTimes(final Thread thread) throws InterruptedException {
      int count = 0;
      while (thread.isAlive() && count < interruptTries) {
         LOG.debug("Thread " + type + " (" + thread.getName() + ") not finished, is kill now..");
         thread.interrupt();
         Thread.sleep(waitTimeBetweenInterrupts);
         count++;
      }
      return count;
   }

   public void setInterruptTries(final int interruptTries) {
      this.interruptTries = interruptTries;
   }

   public void setWaitTimeBetweenInterrupts(final int waitTimeBetweenInterrupts) {
      this.waitTimeBetweenInterrupts = waitTimeBetweenInterrupts;
   }

}
