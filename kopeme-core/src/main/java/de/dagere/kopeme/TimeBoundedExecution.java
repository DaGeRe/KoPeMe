package de.dagere.kopeme;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Realizes the interruption of a given thread after a given timeout
 * 
 * @author reichelt
 *
 */
public class TimeBoundedExecution {

	private static final Logger log = LogManager.getLogger(TimeBoundedExecution.class);

	private Thread mainThread;
	private int timeout;
	private Throwable testError;

	public TimeBoundedExecution(Thread thread, int timeout) {
		this.mainThread = thread;
		this.timeout = timeout;
	}

	public void execute() throws Exception {
		mainThread.start();
		mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				testError = arg1;
			}
		});
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			log.error("Test timed out!");
			mainThread.interrupt();
		}
		if (testError != null) {
			if (testError instanceof Exception)
				throw (Exception) testError;
			else if (testError instanceof Error)
				throw (Error) testError;
			else
			{
				log.error("Unexpected behaviour");
				testError.printStackTrace();
			}
		}

	}

}
