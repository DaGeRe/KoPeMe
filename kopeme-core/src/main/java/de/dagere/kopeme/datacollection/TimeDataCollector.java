package de.dagere.kopeme.datacollection;

/**
 * Saves time in nanoseconds.
 * 
 * @author dagere
 *
 */
public final class TimeDataCollector extends DataCollector {

   public static long TO_MILLISECONDS = 1000 * 1000;
   
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
	}

	@Override
	public long getValue() {
		return summarizedValue != 0 ? summarizedValue : (stop - start);
	}

	@Override
	public void startOrRestartCollection() {
		summarizedValue += (stop - start);
		System.out.println("Measured: " + summarizedValue);
		startCollection();
	}

}
