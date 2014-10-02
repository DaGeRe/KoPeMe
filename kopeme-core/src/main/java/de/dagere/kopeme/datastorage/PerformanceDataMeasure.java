package de.dagere.kopeme.datastorage;

public class PerformanceDataMeasure {
	public String testcase;
	public String collectorname;
	public long value;
	public double deviation;
	public int executionTimes;
	public long min, max;
	public double first10percentile;
	public boolean failure, error;
	public int temperature;

	public PerformanceDataMeasure(String testcase, String collectorname, long value, double deviation, int executionTimes, long min, long max,
			double first10percentile, int temperature) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
		this.temperature = temperature;
		this.first10percentile = first10percentile;
	}

	public PerformanceDataMeasure(String testcase, String collectorname, long value, double deviation, int executionTimes, long min, long max, boolean failure,
			boolean error, int temperature) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
		this.failure = failure;
		this.error = error;
		this.temperature = temperature;
	}
}