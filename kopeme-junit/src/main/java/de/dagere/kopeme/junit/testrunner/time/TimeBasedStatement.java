package de.dagere.kopeme.junit.testrunner.time;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.testrunner.PerformanceJUnitStatement;
import de.dagere.kopeme.junit.testrunner.PerformanceMethodStatement;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

/**
 * Statement for executing a timebased test
 * @author reichelt
 *
 */
public class TimeBasedStatement extends PerformanceMethodStatement implements Finishable {

	private static final Logger LOG = LogManager.getLogger(TimeBasedStatement.class);

	private static final long NANOTOMIKRO = 1000;

	private int repetitions;
	private final long duration;

	public TimeBasedStatement(PerformanceJUnitStatement callee, String filename, Class<?> calledClass, FrameworkMethod method, boolean saveFullData) {
		super(callee, filename, calledClass, method, saveFullData);
		duration = annotation.duration() * NANOTOMIKRO;
		repetitions = annotation.repetitions();
	}
	
	@Override
	public void evaluate() throws Throwable {
		mainRunnable = new Finishable() {

			@Override
			public void run() {
				try {
					int executions = calibrateMeasurement(className, method.getName() + " warmup", new TestResult(method.getName(), 1, DataCollectorList.ONLYTIME), duration, repetitions, callee);
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

	private int calibrateMeasurement(final String executionTypName, final String name, final TestResult tr, final long maximumDuration, int repetitions, PerformanceJUnitStatement callee) {
		int executions = 1;
		final long calibrationStart = System.nanoTime();
		try {
			long basicDuration = runMainExecution2(tr, executionTypName, 1, callee);
			long calibration = basicDuration;
			final List<Long> calibrationValues = new LinkedList<>();

			while (calibration < maximumDuration / 2) {
				final long value = runMainExecution2(tr, executionTypName, 1, callee);
				calibration += value;
				LOG.debug("Adding: {}", calibration / NANOTOMIKRO, value / NANOTOMIKRO, maximumDuration/ NANOTOMIKRO);
				calibrationValues.add(value);
			}
			final DescriptiveStatistics statistics = new DescriptiveStatistics();
			calibrationValues.forEach(value -> statistics.addValue(value));
			
			LOG.debug("Mean: " + statistics.getMean() / 1000 + " " + statistics.getPercentile(20) / 1000 + " Calibration time: " + calibration / 1000);

			long halfTime = maximumDuration / 2;
			
			executions = (int) ((halfTime / statistics.getMean()) / repetitions);
			LOG.debug("Executions: {}", executions, (maximumDuration / statistics.getMean()));
			long calibrationEnd = System.nanoTime();
			LOG.debug("Duration of calibration: {}", (calibrationEnd - calibrationStart) / 1000);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
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
	private long runMainExecution2(final TestResult tr, final String warmupString, final int executions, final PerformanceJUnitStatement callee) throws Throwable {
		long beginTime = System.nanoTime();
		final String methodString = className + "." + tr.getTestcase();
		int execution;
		for (execution = 1; execution <= executions; execution++) {

			callee.preEvaluate();
			LOG.debug("--- Starting " + warmupString + methodString + " " + execution + "/" + executions + " ---");
			tr.startCollection();
			callee.evaluate();
			tr.stopCollection();
			LOG.debug("--- Stopping " + warmupString + +execution + "/" + executions + " ---");
			callee.postEvaluate();
			tr.setRealExecutions(execution);
			if (execution >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				LOG.info("Exiting because of deviation reached");
				break;
			}
			if (isFinished){
				LOG.debug("Exiting finished thread: {}." , Thread.currentThread().getName());
				throw new InterruptedException("Test timed out.");
			}
			final boolean interrupted = Thread.interrupted();
			LOG.debug("Interrupt state: {}", interrupted);
			if (interrupted) {
				LOG.debug("Exiting thread.");
				throw new InterruptedException("Test was interrupted and eventually timed out.");
			}
			Thread.sleep(1); // To let other threads "breath"
		}
		LOG.debug("Executions: " + execution);
		tr.setRealExecutions(execution);
		long endTime = System.nanoTime();
		return (endTime - beginTime) / NANOTOMIKRO;
	}

}