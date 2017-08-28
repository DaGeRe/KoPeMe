package de.dagere.kopeme.junit3;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.AnnotationDefaults;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import junit.framework.TestCase;

public class TimeBasedTestcase extends TestCase {
	private static final int NANOTOMIKRO = 1000;


	private static final Logger LOG = LogManager.getLogger(TimeBasedTestcase.class);

	private final PerformanceTest annoTestcase = AnnotationDefaults.of(PerformanceTest.class);
	private final PerformanceTestingClass annoTestClass = AnnotationDefaults.of(PerformanceTestingClass.class);

	/**
	 * Returns the expected duration of the test in milliseconds
	 * @return
	 */
	public long getDuration(){
		return 10000;
	}
	
	@Override
	public void runBare() throws InterruptedException {
		LOG.debug("Running TimeBasedTestcase");

		final long durationInMilliseconds = getDuration();
		final long maximumDuration = durationInMilliseconds * 1000 * NANOTOMIKRO; // Default maximum test duration: 1000 ms = 1 second
		final int executions = calibrateMeasurement(maximumDuration);
		runMeasurement(maximumDuration, executions);

		LOG.debug("KoPeMe-Test {} finished", getName());
	}

	private void runMeasurement(final long maximumDuration, final int executions) {
		final List<Long> values = new LinkedList<>();
		long finalTime = 0;
		while (finalTime < maximumDuration / 2) {
			final long value = measureNTimes(executions);
			values.add(value);
			finalTime += value;
		}

		final DescriptiveStatistics statistics = new DescriptiveStatistics();
		values.forEach(value -> statistics.addValue(value));
		LOG.debug("Durations: {}", values);
		LOG.debug("Average: {} ns / Execution", statistics.getMean() / executions);
	}

	private int calibrateMeasurement(final long maximumDuration) {
		final long basicDuration =  measureNTimes(1);
		long calibration = basicDuration;
		final List<Long> calibrationValues = new LinkedList<>();

		while (calibration < maximumDuration / 2) {
			final long value = measureNTimes(1);
			calibration += value;
//			 LOG.debug("Adding: {}", calibration / NANOTOMIKRO, value / NANOTOMIKRO, maximumDuration);
			calibrationValues.add(value);
		}

		final DescriptiveStatistics statistics = new DescriptiveStatistics();
		calibrationValues.forEach(value -> statistics.addValue(value));

		LOG.debug("Mean: " + statistics.getMean() / NANOTOMIKRO + " " + statistics.getPercentile(20) / NANOTOMIKRO + " Calibration time: " + calibration / NANOTOMIKRO);

		final int executions = (int) (((maximumDuration / 2) / statistics.getMean()) / 10);
		LOG.debug("Executions: {}", executions, (maximumDuration / statistics.getMean()));
		return executions;
	}

	private long measureNTimes(final int n) {
		final long start = System.nanoTime();
		try {
			for (int i = 0; i < n; i++) {
				TimeBasedTestcase.super.runTest();
			}
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		final long end = System.nanoTime();
		return end - start;
	}
}
