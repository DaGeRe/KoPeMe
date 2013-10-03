package de.kopeme.datacollection;
/**
 * Saves Time in milliseconds
 * @author dagere
 *
 */
public class TimeDataCollector extends DataCollector {

	private long start;
	private long stop;
	
//	@Override
//	public String getName() {
//		return "TimeDataCollector";
//	}

	@Override
	public void startCollection() {
		start = System.nanoTime();
	}

	@Override
	public void stopCollection() {
		stop = System.nanoTime();
	}

	@Override
	public long getValue() {
		return (stop - start)/1000000;
	}

}
