package de.dagere.kopeme;

/**
 * A runnable, that waites for a certain time and kills the other thread afterwards
 * @author reichelt
 *
 */
public class TimeoutWaiter implements Runnable{

	private Thread mainThread;
	private int timeout;
	
	public TimeoutWaiter(Thread waitThread, int timeout){
		mainThread = waitThread;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
		}
		System.out.println("Test timed out after " + timeout + " ms.");
		while (mainThread.isAlive()){
			mainThread.interrupt();
		}
	}
}