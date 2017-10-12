package de.dagere.kopeme.junit.testrunner.time;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.junit.testrunner.PerformanceJUnitStatement;
import de.dagere.kopeme.junit.testrunner.PerformanceMethodStatement;

/**
 * Statement for executing a timebased test
 * @author reichelt
 *
 */
public class TimeBasedStatement extends PerformanceMethodStatement {

	private static final Logger LOG = LogManager.getLogger(TimeBasedStatement.class);

	private static final long NANOTOMIKRO = 1000;

	private final long duration;

	public TimeBasedStatement(final PerformanceJUnitStatement callee, final String filename, final Class<?> calledClass, final FrameworkMethod method, final boolean saveFullData) {
		super(callee, filename, calledClass, method, saveFullData);
		duration = annotation.duration() * NANOTOMIKRO;
	}
	
	@Override
	public void evaluate() throws Throwable {
		mainRunnable = new Finishable() {

			@Override
			public void run() {
				try {
					final int executions = calibrateMeasurement(className, method.getName() + " warmup", new TestResult(method.getName(), 1, DataCollectorList.ONLYTIME), duration, repetitions, callee);
					final TestResult tr = executeSimpleTest(callee, executions);
					tr.checkValues();
					if (!assertationvalues.isEmpty()) {
						LOG.info("Checking: " + assertationvalues.size());
						tr.checkValues(assertationvalues);
					}
				} catch (final Exception e) {
					if (e instanceof RuntimeException) {
						e.printStackTrace();
						throw (RuntimeException) e;
					}
					if (e instanceof InterruptedException) {
						throw new RuntimeException(e);
					}
					LOG.error("Catched Exception: {}", e.getLocalizedMessage());
					e.printStackTrace();
				} catch (final Throwable t) {
					if (t instanceof Error)
						throw (Error) t;
					LOG.error("Unknown Type: " + t.getClass() + " " + t.getLocalizedMessage());
				}
			}

			@Override
			public void setFinished(final boolean isFinished) {
				TimeBasedStatement.this.isFinished = isFinished;
			}

			@Override
			public boolean isFinished() {
				return isFinished;
			}
		};
		if (!isFinished){
			final TimeBoundExecution tbe = new TimeBoundExecution(mainRunnable, timeout, "method");
			tbe.execute();
		}
		LOG.debug("Timebounded execution finished");
	}

	private int calibrateMeasurement(final String executionTypName, final String name, final TestResult tr, final long maximumDuration, final int repetitions, final PerformanceJUnitStatement callee) {
		int executions = 1;
		final long calibrationStart = System.nanoTime();
		try {
			final long emptyDuration = runMainExecutionTimed(tr, executionTypName, 0, callee, 1);
			final long basicDuration = runMainExecutionTimed(tr, executionTypName, 1, callee, 1);
			long calibration = basicDuration;
			final List<Long> calibrationValues = new LinkedList<>();

			while (calibration < maximumDuration / 2) {
				final long value = runMainExecutionTimed(tr, executionTypName, 1, callee, 1);
				calibration += value;
				LOG.debug("Adding: {}", calibration / NANOTOMIKRO, value / NANOTOMIKRO, maximumDuration/ NANOTOMIKRO);
				calibrationValues.add(value);
			}
			final DescriptiveStatistics statistics = new DescriptiveStatistics();
			calibrationValues.forEach(value -> statistics.addValue(value));
			
			LOG.debug("Mean: " + statistics.getMean() / 1000 + " " + statistics.getPercentile(20) / 1000 + " Calibration time: " + calibration / 1000);

			final long halfTime = maximumDuration / 2;
			
			final double estimatedExecutionDuration = Math.abs(statistics.getMean() - emptyDuration);
			LOG.debug("Estimated Execution Duration: {} Half-Time: {}", estimatedExecutionDuration / NANOTOMIKRO, halfTime / NANOTOMIKRO);
			executions = (int) (halfTime / (emptyDuration + estimatedExecutionDuration));
			LOG.debug("Executions: {}", executions, (maximumDuration / statistics.getMean()));
			final long calibrationEnd = System.nanoTime();
			LOG.debug("Duration of calibration: {}", (calibrationEnd - calibrationStart) / 1000);
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		
		return executions;
	}


	/**
	 * Runs the main execution of the test, i.e. the execution where performance measures are counted.
	 * 
	 * @param tr
	 *            TestResult that should be filled
	 * @param callee
	 *            Statement that should be called to measure performance and execute the test
	 * @param simple
	 *            Weather it is a simple test, i.e. weather there are parameters
	 * @throws Throwable
	 *             Any exception that occurs during the test
	 */
	private long runMainExecutionTimed(final TestResult tr, final String warmupString, final int executions, final PerformanceJUnitStatement callee, final int repetitions) throws Throwable {
		final long beginTime = System.nanoTime();
		runMainExecution(tr, warmupString, executions, callee, repetitions);
		final long endTime = System.nanoTime();
		return (endTime - beginTime) / NANOTOMIKRO;
	}

}
