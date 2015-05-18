package de.dagere.kopeme.datastorage;

/**
 * Data storage object for performance data
 * 
 * @author reichelt
 *
 */
public class PerformanceDataMeasure {
	public final String testcase;
	public final String collectorname;
	public final long value;
	public final double deviation;
	public final int executionTimes;
	public final long min, max;
	public final double first10percentile;
	public final boolean failure, error;

	public PerformanceDataMeasure(String testcase, String collectorname, long value, double deviation, int executionTimes, long min, long max,
			double first10percentile) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
		this.first10percentile = first10percentile;
		error = false;
		failure = false;
	}

	public PerformanceDataMeasure(String testcase, String collectorname, long value, double deviation, int executionTimes, long min, long max, boolean failure,
			boolean error) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
		first10percentile = 10.0;
		this.failure = failure;
		this.error = error;
	}

	public String getTestcase() {
		return testcase;
	}

	public String getCollectorname() {
		return collectorname;
	}

	public long getValue() {
		return value;
	}

	public double getDeviation() {
		return deviation;
	}

	public int getExecutionTimes() {
		return executionTimes;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public double getFirst10percentile() {
		return first10percentile;
	}

	public boolean isFailure() {
		return failure;
	}

	public boolean isError() {
		return error;
	}
}