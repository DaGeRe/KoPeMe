package de.dagere.kopeme.datastorage;

public class PerformanceDataMeasure {
	public String testcase;
	public String collectorname;
	public long value;
	public double deviation;
	public int executionTimes;
	public long min;
	public long max;
	public boolean failure, error;

	public PerformanceDataMeasure(String testcase, String collectorname,
			long value, double deviation, int executionTimes, long min, long max) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
	}
	
	public PerformanceDataMeasure(String testcase, String collectorname,
			long value, double deviation, int executionTimes, long min, long max,
			boolean failure, boolean error) {
		this.testcase = testcase;
		this.collectorname = collectorname;
		this.value = value;
		this.deviation = deviation;
		this.executionTimes = executionTimes;
		this.min = min;
		this.max = max;
		this.failure = failure;
		this.error = error;
	}
}