package de.kopeme.datacollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import de.kopeme.Checker;
import de.kopeme.datastorage.YAMLDataStorer;
import de.kopeme.measuresummarizing.AverageSummerizer;
import de.kopeme.measuresummarizing.MeasureSummarizer;
import de.kopeme.paralleltests.MethodExecution;

/**
 * Saves the Data Collectors, and therefore has access to the current results of
 * the tests. Furthermore, by invoking stopCollection, the historical values are
 * inserted into the DataCollectors
 * 
 * @author dagere
 * 
 */
public class TestResult {
	Logger log = LogManager.getLogger(TestResult.class);
	
	protected Map<String, Long> values;
	protected Map<String, DataCollector> dataCollectors;
	protected Map<String, Map<Date, Long>> historicalDataMap;
	protected List<Map<String, Long>> realValues;
	protected int index;
	protected String filename;
	protected Checker checker;
	private List<MethodExecution> methods;
	
	private MeasureSummarizer ms;

	public TestResult(String filename, int executionTimes) {
		values = new HashMap<String, Long>();
		this.filename = filename;
		realValues = new ArrayList<Map<String, Long>>(executionTimes + 1);
		methods = new LinkedList<MethodExecution>();
		index = 0;
		
		ms = new AverageSummerizer();
		dataCollectors = DataCollectorList.STANDARD.getDataCollectors();
		// realValues = new HashMap<String, Long>[executionTimes];
	}

	public void setCollectors(DataCollectorList dcl) {
		dataCollectors = new HashMap<String, DataCollector>();
		dataCollectors = dcl.getDataCollectors();
	}

	public void addDataCollector(DataCollector dc) {
		dataCollectors.put(dc.getName(), dc);
	}

	public Set<String> getKeys() {
		Set<String> s = new HashSet<String>();
		for (DataCollector dc : dataCollectors.values())
			s.add(dc.getName());
		for (int i = 0; i < realValues.size(); i++) {
			if (realValues.get(i) != null)
				s.addAll(realValues.get(i).keySet());
		}
		// s.addAll(values.keySet());
		return s;
	}

	public void setChecker(Checker c) {
		this.checker = c;
	}

	public void checkValues() {
		if (checker != null)
			checker.checkValues(this);
	}

	public void checkValues(Map<String, Long> assertationvalues) {
		for (Map.Entry<String, Long> entry : assertationvalues.entrySet()) {
			for (DataCollector dc : dataCollectors.values()) {
				if (dc.getName().equals(entry.getKey())) {
					// System.out.println("prüfe " + dc.getName() + " Soll: " +
					// entry.getValue() + " Ist: " + dc.getValue());
					MatcherAssert.assertThat("Kollektor " + dc.getName()
							+ " besitzt Wert " + dc.getValue()
							+ ", Wert sollte aber unter " + entry.getValue()
							+ " liegen.", dc.getValue(),
							Matchers.lessThan(entry.getValue()));
				}
			}
		}
	}

	public void startCollection() {
//		if (dataCollectors == null) {
//			throw new Error(
//					"Sie sollten TestResult.setCollectors aufrufen, bevor Sie anfangen, Daten zu sammeln.");
//		}
		new Runnable() {
			
			@Override
			public void run() {
				// TODO Automatisch generierter Methodenstub
				
			}
		};
		Comparator<DataCollector> collector = new Comparator<DataCollector>() {

			@Override
			public int compare(DataCollector arg0, DataCollector arg1) {
				// TODO Automatisch generierter Methodenstub
				return arg0.getPriority() - arg1.getPriority();
			}
		};
		Collection<DataCollector> dcCollection = dataCollectors.values();
		DataCollector[] sortedCollectors = (DataCollector[]) dcCollection.toArray(new DataCollector[0]);
		Comparator<DataCollector> comparator = new Comparator<DataCollector>(){
			@Override
			public int compare(DataCollector arg0, DataCollector arg1) {
				return arg0.getPriority() - arg1.getPriority();
			}
		};
		Arrays.sort(sortedCollectors, comparator);
//		.
		for (DataCollector dc : sortedCollectors){
			System.out.println("Starte: " + dc.getName());
			dc.startCollection();
		}
			
	}

	/**
	 * Stops the collection of data, that are collected via DataCollectors. The
	 * collection of self-defined values isn't stopped and historical data are
	 * not loaded, so assertations over self-defined values and historical data
	 * is not possible. For this, call finalizeCollection
	 */
	public void stopCollection() {
		Map<String, Long> runData = new HashMap<String, Long>();
		for (DataCollector dc : dataCollectors.values()) {
			dc.stopCollection();
			runData.put(dc.getName(), dc.getValue());
		}
		log.debug("Index: " + index);
		realValues.add(runData);
		index++;
	}
	
	/**
	 * Sets the method how the different measures of different runs
	 * should be summarized, e.g. as average, median, maximum, ...
	 * @param ms
	 */
	public void setMeasureSummarizer(MeasureSummarizer ms)
	{
		this.ms = ms;
	}

	/**
	 * Called when the collection of data is finally finished, i.e. also the
	 * collection of self-defined values is finished. By this time, writing into
	 * the file and Assertations over historical data are possible
	 */
	public void finalizeCollection() {
		for (String collectorName : getKeys()) {
			log.debug("Standardabweichung: " + getRelativeStandardDeviation(collectorName));
			List<Long> localValues = new LinkedList<Long>();
			for (int i = 0; i < realValues.size()-1; i++) {
				log.debug("I: " + i+ " Value: " + realValues.get(i).get(collectorName));
				localValues.add(realValues.get(i).get(collectorName));
			}
			Long result = ms.getValue(localValues);
			values.put(collectorName, result);
		}

		historicalDataMap = new HashMap<String, Map<Date, Long>>();
		YAMLDataStorer xds = new YAMLDataStorer(filename);
		for (String s : getKeys()) {
			Map<Date, Long> historicalData = xds.getHistoricalData(s);
			// System.out.println("String: " + s);

			historicalDataMap.put(s, historicalData);
			xds.storeValue(s, getValue(s));
			log.info("Speichere für "+s+": " + getValue(s)+ " Relative Standardabweichung: " + getRelativeStandardDeviation(s));
		}
		xds.storeData();
	}

	/**
	 * Adds a self-defined value
	 * 
	 * @param name
	 * @param value
	 */
	public void addValue(String name, long value) {
		if (dataCollectors.get(name) != null)
			throw new Error(
					"A self-defined value should not have the name of a DataCollector, name: "
							+ name);
		values.put(name, value);
	}

	/**
	 * Gets the current value of the measurement
	 * 
	 * @param name
	 * @return
	 */
	public long getValue(String name) {
		// System.out.println("Name: " + name);
		return values.get(name) != null ? values.get(name) : dataCollectors
				.get(name).getValue();
	}

	/**
	 * Gets the maximum value of the measurement of all runs
	 * 
	 * @param measurement
	 * @return
	 */
	public long getMaximumValue(String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData.size() > 0) {
			long max = Long.MIN_VALUE;
			for (Long value : historicalData.values())
				max = (value > max ? value : max);
			return max;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the minimum value of the measurement of all runs
	 * 
	 * @param measurement
	 * @return
	 */
	public long getMinumumValue(String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData.size() > 0) {
			long min = Long.MAX_VALUE;
			for (Long value : historicalData.values())
				min = (value < min ? value : min);
			return min;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the average value of the performance-measure over all runs
	 * 
	 * @param measurement
	 * @return
	 */
	public long getAverageValue(String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData == null)
			return 0l;
		if (historicalData.size() > 0) {
			long sum = 0;
			for (Number value : historicalData.values())
				sum += value.longValue();
			return sum / historicalData.size();
		} else {
			return 0;
		}
	}

	/**
	 * Gets a List of Dates of the last runs
	 * 
	 * @param measurement
	 * @param runs
	 * @return
	 */
	private List<Date> getLastRuns(String measurement, int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData == null)
			return new LinkedList<Date>();

		List<Date> dateList = new LinkedList<Date>(historicalData.keySet());
		Collections.sort(dateList);

		int start = 0;
		if (dateList.size() > runs) {
			start = dateList.size() - runs;
		}
		return dateList.subList(start, dateList.size());
	}

	/**
	 * Gets the average value of the performance-measure over the last runs runs
	 * 
	 * @param measurement
	 *            measurment, for which the value should be calculated
	 * @param runs
	 *            count of runs
	 * @return
	 */
	public long getLastRunsAverage(String measurement, int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		List<Date> lastRunList = getLastRuns(measurement, runs);

		long sum = 0;
		for (Date d : lastRunList) {
			Number num = historicalData.get(d);
			sum += (num.longValue());
		}
		return lastRunList.size() != 0 ? sum / lastRunList.size() : 0;
	}

	public long getLastRunsMaximum(String measurement, int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		List<Date> lastRunList = getLastRuns(measurement, runs);

		long max = Long.MIN_VALUE;
		for (Date d : lastRunList) {
			Number num = historicalData.get(d);
			if (max > num.longValue())
				max = num.longValue();
		}
		return max;
	}

	public long getLastRunsMinimum(String measurement, int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		List<Date> lastRunList = getLastRuns(measurement, runs);

		long min = Long.MAX_VALUE;
		for (Date d : lastRunList) {
			Number num = historicalData.get(d);
			if (min < num.longValue())
				min = num.longValue();
		}
		return min;
	}

	public void addParallelTest(MethodExecution methodExecution) {
		methods.add(methodExecution);
	}

	public List<MethodExecution> getParallelTests() {
		return methods;
	}
	
	private double getRelativeStandardDeviation(String datacollector){
		long []values = new long[realValues.size()];
		for (int i=0; i < realValues.size(); i++){
			values[i] = realValues.get(i).get(datacollector);
		}
		if (datacollector.equals("de.kopeme.datacollection.CPUUsageCollector") || datacollector.equals("de.kopeme.datacollection.TimeDataCollector")){
			System.out.println(Arrays.toString(values));
		}
		SummaryStatistics st = new SummaryStatistics();
		for (Long l : values){
			st.addValue(l);
			
		}
		
		System.out.println("Mittel: " + st.getMean() + " Standardabweichung: " + st.getStandardDeviation());
		return st.getStandardDeviation() / st.getMean();
	}

	public boolean isRelativeStandardDeviationBelow(Map<String, Double> deviations) {
		if (realValues.size() < 5)
			return false;
		boolean isRelativeDeviationBelowValue = true;
		for (String collectorName : getKeys()){
			Double aimStdDeviation = deviations.get(collectorName);
			if (aimStdDeviation != null){
				double stdDeviation = getRelativeStandardDeviation(collectorName);
				log.info("Standardabweichung: " + stdDeviation + " Ziel-Standardabweichung: " + aimStdDeviation);
				if (stdDeviation > aimStdDeviation){
					isRelativeDeviationBelowValue = false; break;
				}
			}
		}
		
		return isRelativeDeviationBelowValue;
	}
}
