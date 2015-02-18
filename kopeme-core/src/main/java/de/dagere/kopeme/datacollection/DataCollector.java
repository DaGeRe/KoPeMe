package de.dagere.kopeme.datacollection;

public abstract class DataCollector {
	// protected Map<Date, Long> historicalData;

	public String getName() {
		return this.getClass().getName();
	}

	/**
	 * Returns the priority for the DataCollector, i.e. when the DataCollector should be started in relation to the other DataCollectors. This is important, for
	 * example if before the Data is collected time-intensive cleaning is needed (as for the RAMUsageCollector) High priority means late starting
	 * 
	 * @return
	 */
	public abstract int getPriority();

	public abstract void startCollection();

	public abstract void stopCollection();

	public abstract long getValue();

	/**
	 * Starts the Datacollection or restarts it, if it was stopped before.
	 * 
	 * Starts a normal collection by default, which means that no restart is done!
	 */
	public void startOrRestartCollection() {
		startCollection();
	}

}
