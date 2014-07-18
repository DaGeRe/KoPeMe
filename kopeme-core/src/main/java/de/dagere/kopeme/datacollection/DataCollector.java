package de.dagere.kopeme.datacollection;

public abstract class DataCollector {
//	protected Map<Date, Long> historicalData;

	public String getName() {
		return this.getClass().getName();
	}
	
	/**
	 * Returns the priority for the DataCollector, i.e. when the DataCollector should be started
	 * in relation to the other DataCollectors. This is important, for example if before the Data
	 * is collected time-intensive cleaning is needed (as for the RAMUsageCollector)
	 * High priority means late starting
	 * @return
	 */
	public abstract int getPriority();

	public abstract void startCollection();

	public abstract void stopCollection();

	public abstract long getValue();

//	public void setHistoricalData(Map<Date, Long> historicalData) {
//		this.historicalData = historicalData;
//	}
//
//	public Map<Date, Long> getHistoricalData() {
//		return historicalData;
//	}
}
