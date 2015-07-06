package de.dagere.kopeme.datacollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import de.dagere.kopeme.Checker;
import de.dagere.kopeme.measuresummarizing.AverageSummerizer;
import de.dagere.kopeme.measuresummarizing.MeasureSummarizer;

/**
 * Saves the Data Collectors, and therefore has access to the current results of the tests. Furthermore, by invoking stopCollection, the historical values are
 * inserted into the DataCollectors
 * 
 * @author dagere
 * 
 */
public class TestResult {
	private static final Logger LOG = LogManager.getLogger(TestResult.class);

	protected Map<String, Long> values;
	protected Map<String, DataCollector> dataCollectors;
	protected List<Map<String, Long>> realValues;
	protected int index;
	protected Checker checker;
	private int realExecutions;
	private final String testcase;
	private final HistoricalTestResults historicalResults;

	private final Map<String, MeasureSummarizer> collectorSummarizerMap;

	/**
	 * Initializes the TestResult with a Testcase-Name and the executionTimes.
	 * 
	 * @param testcase Name of the Testcase
	 * @param executionTimes Count of the planned executions
	 */
	public TestResult(final String testcase, final int executionTimes) {
		values = new HashMap<String, Long>();
		realValues = new ArrayList<Map<String, Long>>(executionTimes + 1);
		index = 0;
		this.testcase = testcase;
		historicalResults = new HistoricalTestResults(testcase);

		collectorSummarizerMap = new HashMap<>();
		dataCollectors = DataCollectorList.STANDARD.getDataCollectors();
	}

	/**
	 * Returns the name of the TestCase for which the result is saved.
	 * 
	 * @return Name of the Testcase
	 */
	public String getTestcase() {
		return testcase;
	}

	/**
	 * Sets the DatacollectorList for collecting Performance-Measures.
	 * 
	 * @param dcl List of Datacollectors
	 */
	public void setCollectors(final DataCollectorList dcl) {
		dataCollectors = new HashMap<String, DataCollector>();
		dataCollectors = dcl.getDataCollectors();
	}

	/**
	 * Adds a DataCollector to the given collectors.
	 * 
	 * @param dc DataCollector that should be added
	 */
	public void addDataCollector(final DataCollector dc) {
		dataCollectors.put(dc.getName(), dc);
	}

	/**
	 * Gets all names of DataCollectors that are used.
	 * 
	 * @return Names of used DataCollectors
	 */
	public Set<String> getKeys() {
		Set<String> keySet = new HashSet<String>();
		for (DataCollector dc : dataCollectors.values()) {
			keySet.add(dc.getName());
		}

		for (int i = 0; i < realValues.size(); i++) {
			if (realValues.get(i) != null) keySet.addAll(realValues.get(i).keySet());
		}
		return keySet;
	}

	/**
	 * Sets the checker, that is checking weather the performance measures are good enough for a stable build.
	 * 
	 * @param c Checker for checking the values
	 */
	public void setChecker(final Checker c) {
		this.checker = c;
	}

	/**
	 * Checks, weather the values are good enough.
	 */
	public void checkValues() {
		if (checker != null) checker.checkValues(this);
	}

	/**
	 * Checks the current list of performance measures are less than the given values.
	 * 
	 * @param assertationvalues Threshold values
	 */
	public void checkValues(final Map<String, Long> assertationvalues) {
		for (Map.Entry<String, Long> entry : assertationvalues.entrySet()) {
			for (DataCollector dc : dataCollectors.values()) {
				LOG.trace("Collector: {} Collector 2:{}", dc.getName(), entry.getKey());
				if (dc.getName().equals(entry.getKey())) {
					LOG.trace("Collector: {} Value: {} Aim: {}", dc.getName(), dc.getValue(), entry.getValue());
					MatcherAssert.assertThat("Kollektor " + dc.getName() + " besitzt Wert " + dc.getValue() + ", Wert sollte aber unter " + entry.getValue()
							+ " liegen.", dc.getValue(), Matchers.lessThan(entry.getValue()));
				}
			}
		}
	}

	/**
	 * Starts the collection of Data for all Datacollectors.
	 */
	public void startCollection() {
		Collection<DataCollector> dcCollection = dataCollectors.values();
		DataCollector[] sortedCollectors = dcCollection.toArray(new DataCollector[0]);
		Comparator<DataCollector> comparator = new Comparator<DataCollector>() {
			@Override
			public int compare(final DataCollector arg0, final DataCollector arg1) {
				return arg0.getPriority() - arg1.getPriority();
			}
		};
		Arrays.sort(sortedCollectors, comparator);
		for (DataCollector dc : sortedCollectors) {
			LOG.trace("Starte: {}", dc.getName());
			dc.startCollection();
		}
	}

	/**
	 * Starts or restarts the collection for all Datacollectors, e.g. if a TimeDataCollector was started and stoped before, the Time measured now is added to
	 * the original time.
	 */
	public void startOrRestartCollection() {
		Collection<DataCollector> dcCollection = dataCollectors.values();
		DataCollector[] sortedCollectors = dcCollection.toArray(new DataCollector[0]);
		Comparator<DataCollector> comparator = new Comparator<DataCollector>() {
			@Override
			public int compare(final DataCollector arg0, final DataCollector arg1) {
				return arg0.getPriority() - arg1.getPriority();
			}
		};
		Arrays.sort(sortedCollectors, comparator);
		for (DataCollector dc : sortedCollectors) {
			LOG.trace("Starte: {}", dc.getName());
			dc.startOrRestartCollection();
		}
	}

	/**
	 * Stops the collection of data, that are collected via DataCollectors. The collection of self-defined values isn't stopped and historical data are not
	 * loaded, so assertations over self-defined values and historical data is not possible. For this, call finalizeCollection.
	 */
	public void stopCollection() {
		Map<String, Long> runData = new HashMap<String, Long>();
		for (DataCollector dc : dataCollectors.values()) {
			dc.stopCollection();
		}
		for (DataCollector dc : dataCollectors.values()) {
			runData.put(dc.getName(), dc.getValue());
		}
		realValues.add(runData);
		index++;
	}

	/**
	 * Sets the method how the different measures of different runs should be summarized, e.g. as average, median, maximum, ... .
	 * 
	 * @param datacollector The collector for whom the Summarizer should be set
	 * @param ms The summarizer to set
	 */
	public void setMeasureSummarizer(final String datacollector, final MeasureSummarizer ms) {
		this.collectorSummarizerMap.put(datacollector, ms);
	}

	/**
	 * Called when the collection of data is finally finished, i.e. also the collection of self-defined values is finished. By this time, writing into the file
	 * and Assertations over historical data are possible
	 */
	public void finalizeCollection() {
		AverageSummerizer as = new AverageSummerizer();
		for (String collectorName : getKeys()) {
			LOG.trace("Standardabweichung {}: {}", collectorName, getRelativeStandardDeviation(collectorName));
			List<Long> localValues = new LinkedList<Long>();
			for (int i = 0; i < realValues.size() - 1; i++) {
				// log.debug("I: " + i+ " Value: " +
				// realValues.get(i).get(collectorName));
				localValues.add(realValues.get(i).get(collectorName));
			}
			Long result;
			if (collectorSummarizerMap.containsKey(collectorName)) {
				result = collectorSummarizerMap.get(collectorName).getValue(localValues);
			} else {
				result = as.getValue(localValues);
			}
			// values.put(collectorName, result);
		}
	}

	/**
	 * Adds a self-defined value to the currently measured value. This method should be used if you want to measure data youself (e.g. done transactions in a
	 * certain time) and this value should be saved along with the performance measures which where measured by KoPeMe.
	 * 
	 * @param name Name of the measure that should be saved
	 * @param value Value of the measure
	 */
	public void addValue(final String name, final long value) {
		if (dataCollectors.get(name) != null) {
			throw new Error("A self-defined value should not have the name of a DataCollector, name: " + name);
		}
		values.put(name, value);
	}

	/**
	 * Returns the values of measures, that are not collected via DataCollectors.
	 * 
	 * @return Additional Values
	 */
	public Set<String> getAdditionValueKeys() {
		return values.keySet();
	}

	/**
	 * Gets the current value of the measurement.
	 * 
	 * @param name Name of the measure
	 * @return Value of the measure
	 */
	public long getValue(final String name) {
		return values.get(name) != null ? values.get(name) : dataCollectors.get(name).getValue();
	}

	/**
	 * Returns the relative standard deviation for the given DataCollector.
	 * 
	 * @param datacollector Name of the DataCollector
	 * @return Relative standard deviation
	 */
	public double getRelativeStandardDeviation(final String datacollector) {
		long[] currentValues = new long[realValues.size()];
		for (int i = 0; i < realValues.size(); i++) {
			Map<String, Long> map = realValues.get(i);
			currentValues[i] = map.get(datacollector);
		}
		if (datacollector.equals("de.kopeme.datacollection.CPUUsageCollector") || datacollector.equals("de.kopeme.datacollection.TimeDataCollector")) {
			LOG.trace(Arrays.toString(currentValues));
		}
		SummaryStatistics st = new SummaryStatistics();
		for (Long l : currentValues) {
			st.addValue(l);
		}

		LOG.trace("Mittel: {} Standardabweichung: {}", st.getMean(), st.getStandardDeviation());
		return st.getStandardDeviation() / st.getMean();
	}

	/**
	 * Checks weather the given real deviations are below the maximale relative standard deviations that are given.
	 * 
	 * @param deviations maximale relative standard deviations
	 * @return Weather the test can be stopped
	 */
	public boolean isRelativeStandardDeviationBelow(final Map<String, Double> deviations) {
		if (realValues.size() < 5) return false;
		boolean isRelativeDeviationBelowValue = true;
		for (String collectorName : getKeys()) {
			Double aimStdDeviation = deviations.get(collectorName);
			if (aimStdDeviation != null) {
				double stdDeviation = getRelativeStandardDeviation(collectorName);
				LOG.debug("Standardabweichung {}: {} Ziel-Standardabweichung: {}", collectorName, stdDeviation, aimStdDeviation);
				if (stdDeviation > aimStdDeviation) {
					isRelativeDeviationBelowValue = false;
					break;
				}
			}
		}

		return isRelativeDeviationBelowValue;
	}

	/**
	 * Gets current minimum value for the measured values.
	 * 
	 * @param key Name of the performance measure
	 * @return Minimum of the currently measured values
	 */
	public long getMinumumCurrentValue(final String key) {
		long min = Long.MAX_VALUE;
		for (int i = 0; i < realValues.size(); i++) {
			if (realValues.get(i).get(key) < min)
				min = realValues.get(i).get(key);
		}
		LOG.trace("Minimum ermittelt: " + min);
		return min;
	}

	/**
	 * Gets current maximum value for the measured values.
	 * 
	 * @param key Name of the performance measure
	 * @return Maximum of the currently measured values
	 */
	public long getMaximumCurrentValue(final String key) {
		long max = 0;
		for (int i = 0; i < realValues.size(); i++) {
			if (realValues.get(i).get(key) > max) max = realValues.get(i).get(key);
		}
		LOG.trace("Maximum ermittelt: " + max);
		return max;
	}

	/**
	 * Returns all measured value for a measure name.
	 * 
	 * @param key Name of the measure
	 * @return Values measured
	 */
	public List<Long> getValues(final String key) {
		List<Long> currentValues = new LinkedList<>();
		for (int i = 0; i < realValues.size(); i++) {
			currentValues.add(realValues.get(i).get(key));
		}
		return currentValues;
	}

	/**
	 * Returns count of real executions.
	 * 
	 * @return Count of real Executions
	 */
	public int getRealExecutions() {
		return realExecutions;
	}

	/**
	 * Sets count of real executions.
	 * 
	 * @param realExecutions Count of real executions
	 */
	public void setRealExecutions(final int realExecutions) {
		this.realExecutions = realExecutions;
	}

	/**
	 * Gets the historical test results, e.g. the results in past runs.
	 * 
	 * @return
	 */
	public HistoricalTestResults getHistoricalResults() {
		return historicalResults;
	}
}
