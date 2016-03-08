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
public class TimeBoundedExecution {

	private static final Logger LOG = LogManager.getLogger(TimeBoundedExecution.class);

	private final Thread mainThread;
	private final int timeout;
	private Throwable testError;

	/**
	 * Initializes the execution.
	 * 
	 * @param thread The executed Thread
	 * @param timeout The timeout for canceling the execution
	 */
	public TimeBoundedExecution(final Thread thread, final int timeout) {
		this.mainThread = thread;
		this.timeout = timeout;
	}

	/**
	 * Executes the TimeBoundedExecution.
	 * 
	 * @throws Exception Thrown if an error occurs
	 */
	public final void execute() throws Exception {
		mainThread.start();
		mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread arg0, final Throwable arg1) {
				testError = arg1;
			}
		});
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			LOG.error("Test timed out because of method-timeout!");
			mainThread.interrupt();
		}
		mainThread.join(1000);
		if (mainThread.isAlive()){
			LOG.error("Test timed out and was not able to save his data after 10 seconds - is killed hard now.");
			mainThread.stop();
		}
		if (testError != null) {
			if (testError instanceof Exception) {
				throw (Exception) testError;
			} else if (testError instanceof Error) {
				throw (Error) testError;
			} else {
				LOG.error("Unexpected behaviour");
				testError.printStackTrace();
			}
		}
	}

}
