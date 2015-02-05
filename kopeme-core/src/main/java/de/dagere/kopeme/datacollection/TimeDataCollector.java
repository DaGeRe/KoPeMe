package de.dagere.kopeme.datacollection;

/**
 * Saves Time in milliseconds
 * 
 * @author dagere
 *
 */
public class TimeDataCollector extends DataCollector {

	private long start;
	private long stop;

	public TimeDataCollector() {
		start = 0;
		stop = 0;
	}

	public int getPriority() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void startCollection() {
		System.gc();
		start = System.nanoTime();
	}

	@Override
	public void stopCollection() {
		stop = System.nanoTime();
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public long getValue() {
		// System.out.println("Stop: " + stop + " Start: " + start);
		return (stop - start) / 1000;
		// Divisionen: 1 - Nano, 1E3 - Mikro, 1E6 - Milli
	}

}
