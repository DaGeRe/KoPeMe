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
	private static final int INTERRUPT_TRIES = 10;

	private static final Logger LOG = LogManager.getLogger(TimeBasedTestcase.class);

	private final PerformanceTest annoTestcase = AnnotationDefaults.of(PerformanceTest.class);
	private final PerformanceTestingClass annoTestClass = AnnotationDefaults.of(PerformanceTestingClass.class);

	@Override
	public void runBare() throws InterruptedException {
		LOG.debug("Running TimeBasedTestcase");

		final long maximumDuration = 1000 * 1000 * 1000; // Default maximum test duration: 1000 ms = 1 second

		final long basicDuration =  measureNTimes(1);
		long calibration = basicDuration;
		final List<Long> calibrationValues = new LinkedList<>();

		while (calibration < maximumDuration / 2) {
			final long value = measureNTimes(1);
			calibration += value;
			calibrationValues.add(calibration);
		}

		final DescriptiveStatistics statistics = new DescriptiveStatistics();
		calibrationValues.forEach(value -> statistics.addValue(value / 1000000));

		LOG.debug("Mean: " + statistics.getMean() + " " + statistics.getPercentile(20));

		final List<Long> values = new LinkedList<>();

		long finalTime = 0;
		while (finalTime < maximumDuration / 2) {
			final long value = measureNTimes(1);
			values.add(value);
			finalTime += value;
		}

		// LOG.debug("Durations: {}", values);

		LOG.debug("KoPeMe-Test {} finished", getName());
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
