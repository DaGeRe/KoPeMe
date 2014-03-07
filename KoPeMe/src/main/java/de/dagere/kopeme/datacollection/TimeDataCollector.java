package de.dagere.kopeme.datacollection;
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

	public int getPriority(){
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
		return (stop - start)/1000000;
	}

}
