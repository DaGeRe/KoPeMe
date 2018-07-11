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
	public final int executions, warmup, repetitions;
	public final long min, max;
	public final double first10percentile;
	public final boolean failure, error;

	/**
	 * Initializes the PerformanceDataMeasure.
	 * 
	 * @param testcase Name of the testcase
	 * @param collectorname Name of the datacollector
	 * @param value Measured value
	 * @param deviation deviation of the values
	 * @param executionTimes execution times
	 * @param min minimum value
	 * @param max maximum value
	 * @param first10percentile First 10-Percentil of the measured values
	 */
	public PerformanceDataMeasure(final String testcase, final String collectorname, 
	      final long value, 
	      final double deviation,
			final int executionTimes,
			final int warmupExecutions,
			int repetitions,
			final long min,
			final long max,
			final double first10percentile) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executions = executionTimes;
		this.warmup = warmupExecutions;
		this.repetitions = repetitions;
		this.min = min;
		this.max = max;
		this.first10percentile = first10percentile;
		error = false;
		failure = false;
	}

	/**
	 * Initializes the PerformanceDataMeasure.
	 * 
	 * @param testcase Name of the testcase
	 * @param collectorname Name of the datacollector
	 * @param value Measured value
	 * @param deviation deviation of the values
	 * @param executionTimes execution times
	 * @param min minimum value
	 * @param max maximum value
	 * @param failure Weather an failure occured
	 * @param error Weather an error occured
	 */
	public PerformanceDataMeasure(final String testcase, final String collectorname, final long value, final double deviation,
			final int executionTimes,
			final int warmupExecutions,
			int repetitions,
			final long min,
			final long max, final boolean failure,
			final boolean error) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executions = executionTimes;
		this.warmup = warmupExecutions;
		this.repetitions = repetitions;
		this.min = min;
		this.max = max;
		first10percentile = 10.0;
		this.failure = failure;
		this.error = error;
	}

	/**
	 * @return the testcase
	 */
	public String getTestcase() {
		return testcase;
	}

	/**
	 * @return the collectorname
	 */
	public String getCollectorname() {
		return collectorname;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @return the deviation
	 */
	public double getDeviation() {
		return deviation;
	}

	/**
	 * @return the executionTimes
	 */
	public int getExecutionTimes() {
		return executions;
	}

	public int getWarmupExecutions() {
		return warmup;
	}

	/**
	 * @return the min
	 */
	public long getMin() {
		return min;
	}

	/**
	 * @return the max
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @return the first10percentile
	 */
	public double getFirst10percentile() {
		return first10percentile;
	}

	/**
	 * @return the failure
	 */
	public boolean isFailure() {
		return failure;
	}

	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
}