package de.dagere.kopeme.datacollection;

/**
 * Saves Time in milliseconds.
 * 
 * @author dagere
 *
 */
public final class TimeDataCollector extends DataCollector {

	private static final int MIKRO = 1000;
	private long start;
	private long stop;
	private long summarizedValue = 0;

	/**
	 * Initializes the TimeDataCollector.
	 */
	public TimeDataCollector() {
		start = 0;
		stop = 0;
	}

	@Override
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
			Thread.currentThread().interrupt();
			// It is ok, if the collection is interrupted
			// The interrupt status should stay the same
			// throw new RuntimeException(e);
		}
	}

	@Override
	public long getValue() {
		return summarizedValue != 0 ? summarizedValue : (stop - start) / MIKRO;
		// Divisionen: 1 - Nano, 1E3 - Mikro, 1E6 - Milli
	}

	@Override
	public void startOrRestartCollection() {
		summarizedValue += (stop - start) / MIKRO;
		System.out.println("Measured: " + summarizedValue);
		startCollection();
	}

}
