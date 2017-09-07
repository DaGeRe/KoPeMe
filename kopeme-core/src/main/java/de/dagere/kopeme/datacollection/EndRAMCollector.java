package de.dagere.kopeme.datacollection;

public class EndRAMCollector extends DataCollector{

	private long value;

	@Override
	public int getPriority() {
		return MIDDLE_COLLECTOR_PRIORITY; // Middle-High Priority, as the RAMUsageCollector should not
		// measure the things other DataCollectors create
	}

	@Override
	public void startCollection() {

	}

	@Override
	public void stopCollection() {
		value = Runtime.getRuntime().totalMemory();
	}

	@Override
	public long getValue() {
		return value;
	}

}
