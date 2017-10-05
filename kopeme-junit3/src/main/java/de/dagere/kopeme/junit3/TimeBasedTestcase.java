package de.dagere.kopeme.junit3;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;

/**
 * Only measures time for testcases of arbitrary length in a given time. Therefore, the testcases are executed the first half of the time on order to get an prediction of the duration, and in the
 * second half of the time for measurement.
 * 
 * @author reichelt
 *
 */
public abstract class TimeBasedTestcase extends KoPeMeTestcase {

	/**
	 * Goal-Executions per Test. The test is repeated so often, that exactly this count of measurements is derived.
	 */
	private static final int NANOTOMIKRO = 1000;

	private static final Logger LOG = LogManager.getLogger(TimeBasedTestcase.class);

	/**
	 * Initializes the testcase.
	 */
	public TimeBasedTestcase() {
	}

	/**
	 * Initializes the testcase with its name.
	 * 
	 * @param name
	 *            Name of the testcase
	 */
	public TimeBasedTestcase(final String name) {
		super(name);
	}

	@Override
	public int getRepetitions() {
		return 200; // Default 200 repetitions - can still be changed
	}

	/**
	 * Defines expected test duration in milliseconds.
	 * 
	 * @return Test Duration in Milliseconds
	 */
	public abstract long getDuration();

	@Override
	public void runBare() throws InterruptedException {
		LOG.trace("Running TimeBasedTestcase");

		final long durationInMilliseconds = getDuration();
		LOG.debug("Duration: " + durationInMilliseconds);
		final String testClassName = this.getClass().getName();
		final long maximumDuration = durationInMilliseconds * 1000 * NANOTOMIKRO; // Default maximum test duration: 1000 ms = 1 second
		final int executionTimes = calibrateMeasurement("warmup", testClassName, new TestResult(testClassName, 1, DataCollectorList.ONLYTIME), maximumDuration);

		final TestResult tr = new TestResult(testClassName, executionTimes, DataCollectorList.ONLYTIME);

		final String fullName = this.getClass().getName() + "." + getName();
		try {
			runMainExecution("main", fullName, tr, executionTimes);
		} catch (final Throwable e) {
			e.printStackTrace();
		}

		LOG.debug("KoPeMe-Test {} finished", getName());
		PerformanceTestUtils.saveData(SaveableTestData.createFineTestData(getName(), getClass().getName(), tr, executionTimes, true));
	}

	private int calibrateMeasurement(final String executionTypName, final String name, final TestResult tr, final long maximumDuration) {
		try {
			final long calibrationStart = System.nanoTime();
			final long emptyDuration = measureNTimes(executionTypName, name, tr, 0);
			final long basicDuration = measureNTimes(executionTypName, name, tr, 1);
			long calibration = basicDuration;
			final List<Long> calibrationValues = new LinkedList<>();

			while (calibration < maximumDuration / 2) {
				final long value = measureNTimes(executionTypName, name, tr, 1);
				calibration += value;
				// LOG.debug("Adding: {}", calibration / NANOTOMIKRO, value / NANOTOMIKRO, maximumDuration);
				calibrationValues.add(value);
			}

			final DescriptiveStatistics statistics = new DescriptiveStatistics();
			calibrationValues.forEach(value -> statistics.addValue(value));

			LOG.debug("Mean: " + statistics.getMean() / NANOTOMIKRO + " " + statistics.getPercentile(20) / NANOTOMIKRO + " Calibration time: " + calibration / NANOTOMIKRO);
			LOG.debug("Empty: {} Per-Execution-Duration: {}", emptyDuration / NANOTOMIKRO, Math.abs(statistics.getMean() - emptyDuration) / NANOTOMIKRO);

			final long halfTime = maximumDuration / 2;
			// final int executions = (int) ((halfTime / statistics.getMean()) / getRepetitions());
			final double estimatedExecutionDuration = Math.abs(statistics.getMean() - emptyDuration);
			LOG.debug("Estimated Execution Duration: {} Half-Time: {}", estimatedExecutionDuration / NANOTOMIKRO, halfTime / NANOTOMIKRO);
			final int executions = (int) (halfTime / (emptyDuration + estimatedExecutionDuration));
			LOG.debug("Executions: {}", executions, (maximumDuration / statistics.getMean()));
			final long calibrationEnd = System.nanoTime();
			LOG.debug("Duration of calibration: {}", (calibrationEnd - calibrationStart) / NANOTOMIKRO);
			return executions;
		} catch (final Throwable e) {
			e.printStackTrace(); 
			return 1;// When an test throws an exception, due to whatever reason, its functional behaviour should be fixed first; therefore, it is only run once
		}
		
	}

	private long measureNTimes(final String executionTypName, final String name, final TestResult tr, final int n) throws Throwable {
		final long overheadStart = System.nanoTime();
		runMainExecution(executionTypName, name, tr, n);
		final long overheadEnd = System.nanoTime();
		return overheadEnd - overheadStart;

		// final String firstPart = "--- Starting " + executionTypName + " execution " + name + " ";
		// final String endPart = "/" + n + " ---";
		//
		// try {
		// LOG.debug(firstPart + n + endPart);
		// setUp();
		// tr.startCollection();
		// TimeBasedTestcase.super.runTest();
		// tr.stopCollection();
		// tearDown();
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
		//
		// long retval = tr.getValue(TimeDataCollector.class.getName());
		// tr.setRealExecutions(n);
		// LOG.debug("--- Stopping " + executionTypName + " execution " + n + endPart);
		// if (Thread.interrupted()) {
		// return retval;
		// } else {
		// LOG.trace("Not interrupted!");
		// }
		// long overheadEnd = System.nanoTime();
		// return overheadEnd - overheadStart;
		// final long start = System.nanoTime();
		// try {
		// for (int i = 0; i < n; i++) {
		// TimeBasedTestcase.super.runTest();
		// }
		// } catch (final Throwable e) {
		// e.printStackTrace();
		// }
		// final long end = System.nanoTime();
		// return end - start;
	}
}
