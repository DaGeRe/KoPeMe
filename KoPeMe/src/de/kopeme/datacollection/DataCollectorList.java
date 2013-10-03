package de.kopeme.datacollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains enumerations of DataCollectors, which could be used. Here is a good
 * extension point: if one has additional DataCollectors in an project, and one 
 * wants to use this frequently, one could extend this class and add new DataCollectorLists.
 * 
 * @author dagere
 * 
 */
public class DataCollectorList {
	/**
	 * The list, containing the standard-Collectors, i.e. collectors for Time, Hard disk usage and cpu usage
	 */
	public static final DataCollectorList STANDARD;
	/**
	 * The list, containing only a collector for time usage
	 */
	public static final DataCollectorList ONLYTIME;
	/**
	 * The list, containing no collector; one could use this if one wants only to use self-defined collectors.
	 */
	public static final DataCollectorList NONE;
	
	private Set<Class> collectors;

	static {
		STANDARD = new DataCollectorList();
		STANDARD.addDataCollector(TimeDataCollector.class);
		STANDARD.addDataCollector(HarddiskWriteCollector.class);
		STANDARD.addDataCollector(HarddiskReadCollector.class);
		STANDARD.addDataCollector(CPUUsageCollector.class);
		STANDARD.addDataCollector(RAMUsageCollector.class);
		
		ONLYTIME = new DataCollectorList();
		ONLYTIME.addDataCollector(TimeDataCollector.class);
		
		NONE = new DataCollectorList();
	}

	protected DataCollectorList() {
		collectors = new HashSet<Class>();
	}

	protected void addDataCollector(Class collectorName) {
		collectors.add(collectorName);
	}

	/**
	 * Returns new DataCollectors, which are saved in the current DataCollectorList. Every
	 * time this method is called there are new DataCollectors instanciated.
	 * @return DataCollectors, which are saved in the current DataCollectorList.
	 */
	public Map<String, DataCollector> getDataCollectors() {
		Map<String, DataCollector> collectorsRet = new HashMap<String, DataCollector>();
		for ( Class<DataCollector> c : collectors )
		{
			DataCollector dc;
			try {
				dc = c.newInstance();
				collectorsRet.put(dc.getName(), dc);
			} catch (InstantiationException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
		}
		return collectorsRet;
	}
}
