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

	private final FinishableThread mainThread;
	private final String type;
	private final int timeout;
	private Throwable testError;

	/**
	 * Initializes the execution.
	 * 
	 * @param thread
	 *            The executed Thread
	 * @param timeout
	 *            The timeout for canceling the execution
	 */
	public TimeBoundExecution(final FinishableThread thread, final int timeout, final String type) {
		this.mainThread = thread;
		this.timeout = timeout;
		this.type = type;
	}

	public TimeBoundExecution(final Finishable finishable, final int timeout, final String type) {
		String threadName;
		synchronized (LOG) {
			threadName = "timebound-" + (id++);
		}
		this.mainThread = new FinishableThread(finishable, threadName);
		this.timeout = timeout;
		this.type = type;
	}

	/**
	 * Initializes a timebounded execution where the object is not set to finish. This indicates that whenever an interrupt state is caught, the process will run even if the time bounded execution
	 * should finish.
	 * 
	 * @param finishable
	 * @param timeout
	 */
	public TimeBoundExecution(final Runnable finishable, final int timeout, final String type) {
		this.mainThread = new FinishableThread(new Finishable() {

			@Override
			public void run() {
				finishable.run();
			}

			@Override
			public void setFinished(final boolean isFinished) {
				LOG.debug("Warning: Thread can not be finished");
			}

			@Override
			public boolean isFinished() {
				return false;
			}
		});
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
		mainThread.start();
		mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread arg0, final Throwable arg1) {
				LOG.error("Uncaught exception in {}: {}", arg0.getName(), arg1.getClass());
				testError = arg1;
			}
		});
		LOG.debug("Warte: " + timeout);
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			mainThread.setFinished(true);
			LOG.error("Test " + type + " " + mainThread.getName() + " timed out!");
			for (int i = 0; i < 5; i++) {
				mainThread.interrupt();
				// asure, that the test does not catch the interrupt state itself
				Thread.sleep(5);
			}
		}
		else {
			finished = true;
		}
		mainThread.join(1000); // TODO If this time is shortened, test
		if (mainThread.isAlive()) {
			LOG.error("Test timed out and was not able to save his data after 10 seconds - is killed hard now.");
			mainThread.stop();
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
