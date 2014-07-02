package de.dagere.kopeme;

public class TimeBoundedExecution {
	private Thread mainThread;
	private int timeout;

	public TimeBoundedExecution(Thread t, int timeout) {
		this.mainThread = t;
		this.timeout = timeout;
	}

	public void execute() throws InterruptedException {
		mainThread.start();
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			mainThread.interrupt();
		}
	}
}
