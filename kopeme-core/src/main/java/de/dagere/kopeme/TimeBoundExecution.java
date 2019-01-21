package de.dagere.kopeme;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Realizes the interruption of a given thread after a given timeout.
 * 
 * @author reichelt
 *
 */
public class TimeBoundExecution {

	public static int id = 0;

	private static final Logger LOG = LogManager.getLogger(TimeBoundExecution.class);

	ThreadGroup experimentThreadGroup;
	private final FinishableThread experimentThread;
	private final String type;
	private final int timeout;
	private Throwable testError;

	public TimeBoundExecution(final Finishable finishable, final int timeout, final String type) {
		String threadName;
		synchronized (LOG) {
			threadName = "timebound-" + (id++);
		}
		experimentThreadGroup = new ThreadGroup("kopeme-experiment");
		this.experimentThread = new FinishableThread(experimentThreadGroup, finishable, threadName);
		this.timeout = timeout;
		this.type = type;
	}

	/**
	 * Executes the TimeBoundedExecution.
	 * 
	 * @throws Exception
	 *             Thrown if an error occurs
	 */
	public final boolean execute() throws Exception {
		boolean finished = false;
		experimentThread.start();
		experimentThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(final Thread t, final Throwable e) {
            if (e instanceof OutOfMemoryError) {
               t.interrupt();
            }
            e.printStackTrace();
            LOG.debug("Out of memory - can not reuse VM for measurement");
            System.exit(1);
         }
      });
		LOG.debug("Warte: " + timeout);
		experimentThread.join(timeout);
		if (experimentThread.isAlive()) {
		   experimentThread.setFinished(true);
			LOG.error("Test " + type + " " + experimentThread.getName() + " timed out!");
			Thread.sleep(5);
			for (int i = 0; i < 5; i++) {
			   experimentThread.interrupt();
				// asure, that the test does not catch the interrupt state itself
				Thread.sleep(5);
			}
		}
		else {
			finished = true;
		}
		experimentThread.join(1000); // TODO If this time is shortened, test
		if (experimentThread.isAlive()) {
			LOG.error("Test timed out and was not able to save his data after 10 seconds - is killed hard now.");
			experimentThread.stop();
		}
		if (testError != null) {
			LOG.trace("Test error != null");
			if (testError instanceof Exception) {
				throw (Exception) testError;
			} else if (testError instanceof Error) {
				throw (Error) testError;
			} else {
				LOG.error("Unexpected behaviour");
				testError.printStackTrace();
			}
		}
		return finished;
	}

}
