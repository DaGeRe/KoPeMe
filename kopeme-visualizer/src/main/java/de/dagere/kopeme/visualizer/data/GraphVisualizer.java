package de.dagere.kopeme.visualizer.data;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.jfree.util.Log;
import org.kohsuke.stapler.DataBoundConstructor;

import de.dagere.kopeme.visualizer.VisualizeAction;

/**
 * Saves the data for one graph, i.e. for one part of a VisualizeAction
 * 
 * @author dagere
 * 
 */
public class GraphVisualizer {

	private static transient Logger LOG = Logger
			.getLogger(VisualizeAction.class.getName());

	private Map<String, Map<Date, Long>> dataMap;
	private int valueCount;
	private boolean visible;
	private String name;

	public String getName() {
		return name;
	}

	public GraphVisualizer() {

	}

	@DataBoundConstructor
	public GraphVisualizer(String name, Map<String, Map<Date, Long>> temp, boolean visible) {
		this.name = name;
		dataMap = temp;
		this.visible = visible;
		LOG.info("Visible: " + visible);
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
			TreeSet<Date> measuredDates = new TreeSet<Date>(new Comparator<Date>() {

				public int compare(Date o1, Date o2) {
					return -o1.compareTo(o2);
				}
			});
			measuredDates.addAll(unOrderedSet);
			Map<Date, Long> newSubMap = new HashMap<Date, Long>();
			int i = 0;
			dateLoop: for (Date d : measuredDates)
			{
				newSubMap.put(d, performanceMeasure.getValue().get(d));
				if (i > valueCount)
					break dateLoop;
				i++;
				System.out.println("Datum: " + d);
			}
			newMap.put(performanceMeasure.getKey(), newSubMap);
		}
		return newMap;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean isVisible) {
		LOG.info("SetVisible: " + isVisible);
		this.visible = isVisible;
	}
}
