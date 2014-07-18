package de.dagere.kopeme.visualizer.data;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jfree.util.Log;

/**
 * Saves the data for one graph, i.e. for one part of a VisualizeAction
 * 
 * @author dagere
 * 
 */
public class GraphVisualizer {
	
	private Map<String, Map<Date, Long>> dataMap;
	private Set<String> viewable;
	boolean useMultipleAxis;
	private int valueCount;

	public boolean isUseMultipleAxis() {
		return useMultipleAxis;
	}

	public void setUseMultipleAxis(boolean useMultipleAxis) {
		this.useMultipleAxis = useMultipleAxis;
	}

	public GraphVisualizer(Map<String, Map<Date, Long>> temp,
			boolean useMultipleAxis) {
		dataMap = temp;
		viewable = new HashSet<String>();
		for (String s : temp.keySet()) {
			viewable.add(s);
		}
		this.useMultipleAxis = useMultipleAxis;
	}

	public boolean isViewable(String s) {
		return viewable.contains(s);
	}

	public String[] getViewable() {
		return viewable.toArray(new String[0]);
	}

	public String[] getMeasurements() {
		return dataMap.keySet().toArray(new String[0]);
	}

	public Map<String, Map<Date, Long>> getDatamap() {
		if (valueCount <= 0)
			return dataMap;
		Map<String, Map<Date, Long>> newMap = new HashMap<String, Map<Date, Long>>();
		for (Map.Entry<String, Map<Date, Long>> performanceMeasure : dataMap.entrySet()) {
			Log.info("Füge Key hinzu: " + performanceMeasure.getKey());
			Set<Date> unOrderedSet = performanceMeasure.getValue().keySet();
			TreeSet<Date> measuredDates = new TreeSet<Date>( new Comparator<Date>() {

				public int compare(Date o1, Date o2) {
					return -o1.compareTo(o2);
				}
			});
			measuredDates.addAll(unOrderedSet);
			Map<Date, Long> newSubMap = new HashMap<Date, Long>();
			int i = 0;
			dateLoop: for ( Date d : measuredDates)
			{
				newSubMap.put(d, performanceMeasure.getValue().get(d));
				if ( i > valueCount )
					break dateLoop;
				i++;
				System.out.println("Datum: " + d);
			}
			newMap.put(performanceMeasure.getKey(), newSubMap);
		}
		return newMap;
	}

	/**
	 * Sets weather the values of a measure are viewable or not viewable
	 * @param s
	 * @param isViewable
	 */
	public void setViewable(String s, boolean isViewable) {
		if (isViewable)
			viewable.add(s);
		else
			viewable.remove(s);
	}

	/**
	 * Returns how much values are displayed in the graph
	 * @return
	 */
	public int getValueCount() {
		return valueCount;
	}

	/**
	 * Sets how much values are displayed in the graph
	 * @param valueCount
	 */
	public void setValueCount(int valueCount) {
		this.valueCount = valueCount;
	}
}
