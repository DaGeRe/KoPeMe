package de.dagere.kopeme.datacollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains enumerations of DataCollectors, which could be used. Here is a good extension point: if one has additional DataCollectors in an project, and one
 * wants to use this frequently, one could extend this class and add new DataCollectorLists.
 * 
 * @author dagere
 * 
 */
public class DataCollectorList {
	
	/**
	 * The list, containing additional data collectors to standard, e.g. the data collector that is collecting the reserved RAM of the current vm.
	 */
	public static final DataCollectorList EXTENDED;
	
	/**
	 * The list, containing the standard-Collectors, i.e. collectors for Time, Hard disk usage and cpu usage.
	 */
	public static final DataCollectorList STANDARD;
	/**
	 * The list, containing only a collector for time usage.
	 */
	public static final DataCollectorList ONLYTIME;
	/**
	 * The list, containing no collector; one could use this if one wants only to use self-defined collectors.
	 */
	public static final DataCollectorList NONE;

	private final Set<Class<DataCollector>> collectors;

	static {
		STANDARD = new DataCollectorList();
		STANDARD.addDataCollector(TimeDataCollector.class);
		STANDARD.addDataCollector(CPUUsageCollector.class);
		STANDARD.addDataCollector(RAMUsageCollector.class);
		
		EXTENDED = new DataCollectorList();
		EXTENDED.addDataCollector(TimeDataCollector.class);
		EXTENDED.addDataCollector(CPUUsageCollector.class);
		EXTENDED.addDataCollector(RAMUsageCollector.class);
		EXTENDED.addDataCollector(EndRAMCollector.class);

		ONLYTIME = new DataCollectorList();
		ONLYTIME.addDataCollector(TimeDataCollector.class);

		NONE = new DataCollectorList();
	}

	/**
	 * Initializes a DataCollectorList with empty list.
	 */
	protected DataCollectorList() {
		collectors = new HashSet<>();
	}

	/**
	 * Adds a DataCollector to the given list.
	 * 
	 * @param collectorName The collector that should be added
	 */
	protected final void addDataCollector(final Class collectorName) {
		if (!DataCollector.class.isAssignableFrom(collectorName)) {
			throw new RuntimeException("Class must be subclass of DataCollector!");
		}
		collectors.add(collectorName);
	}

	/**
	 * Returns new DataCollectors, which are saved in the current DataCollectorList. Every time this method is called there are new DataCollectors instanciated.
	 * 
	 * @return DataCollectors, which are saved in the current DataCollectorList.
	 */
	public final Map<String, DataCollector> getDataCollectors() {
		final Map<String, DataCollector> collectorsRet = new HashMap<>();
		for (final Class<DataCollector> c : collectors) {
			DataCollector dc;
			try {
				dc = c.newInstance();
				collectorsRet.put(dc.getName(), dc);
			} catch (final InstantiationException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return collectorsRet;
	}
}
