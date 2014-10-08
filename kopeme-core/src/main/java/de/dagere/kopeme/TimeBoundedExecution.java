package de.dagere.kopeme;

import java.lang.Thread.UncaughtExceptionHandler;

public class TimeBoundedExecution {
	private Thread mainThread;
	private int timeout;
	private Throwable testError;

	public TimeBoundedExecution(Thread thread, int timeout) {
		this.mainThread = thread;
		this.timeout = timeout;
	}

	public void execute() throws Throwable {
		mainThread.start();
		mainThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				testError = arg1;
			}
		});
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			mainThread.interrupt();
		}
		if (testError != null)
			throw testError;
	}

}
