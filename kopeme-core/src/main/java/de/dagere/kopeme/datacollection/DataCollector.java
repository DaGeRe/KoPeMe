package de.dagere.kopeme.datacollection;

/**
 * Super class of all classes enabling test data collection, for example test time, RAM usage, ...
 * 
 * @author reichelt
 *
 */
public abstract class DataCollector {

	protected static final int MIDDLE_COLLECTOR_PRIORITY = 10;
	protected static final int LOW_DATACOLLECTOR_PRIORITY = 5;

	/**
	 * Returns the name of the DataCollector.
	 * 
	 * @return Name of the DataCollector
	 */
	public final String getName() {
		return this.getClass().getName();
	}

	/**
	 * Returns the priority for the DataCollector, i.e. when the DataCollector should be started in relation to the other DataCollectors. This is important, for
	 * example if before the Data is collected time-intensive cleaning is needed (as for the RAMUsageCollector) High priority means late starting
	 * 
	 * @return Priority of the DataCollector
	 */
	public abstract int getPriority();

	/**
	 * Starts the data collection.
	 */
	public abstract void startCollection();

	/**
	 * Stops the data collection.
	 */
	public abstract void stopCollection();

	/**
	 * Returns the value of the collected data.
	 * 
	 * @return Value of the collected data.
	 */
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
