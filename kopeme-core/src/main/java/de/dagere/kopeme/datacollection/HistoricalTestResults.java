package de.dagere.kopeme.datacollection;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages loading and returning results of past runs.
 * 
 * @author reichelt
 *
 */
public class HistoricalTestResults {

	final String testcase;
	protected Map<String, Map<Date, Long>> historicalDataMap;

	public HistoricalTestResults(final String testcase) {
		this.testcase = testcase;
		historicalDataMap = new HashMap<String, Map<Date, Long>>();
	}

	/**
	 * Gets the maximum value of the measurement of all runs.
	 * 
	 * @param measurement Name of the measure
	 * @return Maximum Value of the measure
	 */
	public long getMaximumValue(final String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData.size() > 0) {
			long max = Long.MIN_VALUE;
			for (Long value : historicalData.values()) {
				max = (value > max ? value : max);
			}
			return max;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the minimum value of the measurement of all runs.
	 * 
	 * @param measurement Name of the measure
	 * @return Minimum value of the measure
	 */
	public long getMinumumValue(final String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData.size() > 0) {
			long min = Long.MAX_VALUE;
			for (Long value : historicalData.values()) {
				min = (value < min ? value : min);
			}
			return min;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the average value of the performance-measure over all runs.
	 * 
	 * @param measurement Name of the measure
	 * @return Average value of the measure
	 */
	public long getAverageValue(final String measurement) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData == null) return 0L;
		if (historicalData.size() > 0) {
			long sum = 0;
			for (Number value : historicalData.values()) {
				sum += value.longValue();
			}
			return sum / historicalData.size();
		} else {
			return 0;
		}
	}

	/**
	 * Gets a List of Dates of the last runs.
	 * 
	 * @param measurement Name of the measure
	 * @param runs Count of runs
	 * @return List of dates
	 */
	private List<Date> getLastRuns(final String measurement, final int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		if (historicalData == null) return new LinkedList<Date>();

		List<Date> dateList = new LinkedList<Date>(historicalData.keySet());
		Collections.sort(dateList);

		int start = 0;
		if (dateList.size() > runs) {
			start = dateList.size() - runs;
		}
		return dateList.subList(start, dateList.size());
	}

	/**
	 * Gets the average value of the performance-measure over the last runs runs.
	 * 
	 * @param measurement measurment, for which the value should be calculated
	 * @param runs count of runs
	 * @return Average value
	 */
	public long getLastRunsAverage(final String measurement, final int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);
		List<Date> lastRunList = getLastRuns(measurement, runs);

		long sum = 0;
		for (Date d : lastRunList) {
			Number num = historicalData.get(d);
			sum += (num.longValue());
		}
		return lastRunList.size() != 0 ? sum / lastRunList.size() : 0;
	}

	/**
	 * Gets the maximum value of the performance-measure over the last runs runs.
	 * 
	 * @param measurement measurment, for which the value should be calculated
	 * @param runs count of runs
	 * @return Last runs maximum
	 */
	public long getLastRunsMaximum(final String measurement, final int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);

		long max = Long.MIN_VALUE;
		for (Number num : historicalData.values()) {
			if (max < num.longValue()) max = num.longValue();
		}
		return max;
	}

	/**
	 * Gets the minimum value of the performance-measure over the last runs runs.
	 * 
	 * @param measurement measurment, for which the value should be calculated
	 * @param runs count of runs
	 * @return Last runs minimum
	 */
	public long getLastRunsMinimum(final String measurement, final int runs) {
		Map<Date, Long> historicalData = historicalDataMap.get(measurement);

		long min = Long.MAX_VALUE;
		for (Number num : historicalData.values()) {
			if (min < num.longValue()) min = num.longValue();
		}
		return min;
	}

}
