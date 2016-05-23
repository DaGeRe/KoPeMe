package de.dagere.kopeme.junit.testrunner;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundedExecution;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

public class PerformanceMethodStatement extends Statement {

	private static final Logger LOG = LogManager.getLogger(PerformanceMethodStatement.class);

	private final PerformanceJUnitStatement callee;
	private final Map<String, Long> assertationvalues;
	private final Map<String, Double> maximalRelativeStandardDeviation;
	private final int timeout, minEarlyStopExecutions;
	private int warmupExecutions;

	private final int executionTimes;
	private final DataCollectorList datacollectors;
	private final String methodName, filename;
	private final boolean saveFullData;
	private boolean isFinished = false;

	public PerformanceMethodStatement(final PerformanceJUnitStatement callee, final String filename, final FrameworkMethod method, final boolean saveFullData) {
		super();
		this.callee = callee;
		final PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);
		try {
			KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), filename, method.getName());
		} catch (final Exception e) {
			System.err.println("kieker has failed!");
			e.printStackTrace();
		}
		if (annotation.dataCollectors().equals("STANDARD")) {
			datacollectors = DataCollectorList.STANDARD;
		} else if (annotation.dataCollectors().equals("ONLYTIME")) {
			datacollectors = DataCollectorList.ONLYTIME;
		} else if (annotation.dataCollectors().equals("NONE")) {
			datacollectors = DataCollectorList.NONE;
		} else {
			datacollectors = DataCollectorList.ONLYTIME;
			LOG.error("For Datacollectorlist, only STANDARD, ONLYTIME AND NONE ARE ALLOWED");
		}

		this.saveFullData = saveFullData;
		executionTimes = annotation.executionTimes();
		warmupExecutions = annotation.warmupExecutions();
		minEarlyStopExecutions = annotation.minEarlyStopExecutions();
		timeout = annotation.timeout();
		maximalRelativeStandardDeviation = new HashMap<>();

		for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
			maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
		}
		this.filename = filename;
		this.methodName = method.getName();

		assertationvalues = new HashMap<>();
		for (final Assertion a : annotation.assertions()) {
			assertationvalues.put(a.collectorname(), a.maxvalue());
		}
	}

	@Override
	public void evaluate() throws Throwable {

		final Finishable mainRunnable = new Finishable() {
			
			@Override
			public void run() {
				try {
					runWarmup(callee);
					final TestResult tr = executeSimpleTest(callee);
					tr.checkValues();
					if (!assertationvalues.isEmpty()) {
						LOG.info("Checking: " + assertationvalues.size());
						tr.checkValues(assertationvalues);
					}
				} catch (final Exception e) {
					if (e instanceof RuntimeException) {
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
				PerformanceMethodStatement.this.isFinished = isFinished;
			}
			
			@Override
			public boolean isFinished() {
				return isFinished;
			}
		};
			
		final TimeBoundedExecution tbe = new TimeBoundedExecution(mainRunnable, timeout);
		tbe.execute();
		LOG.debug("Timebounded execution finished");
	}

	/**
	 * Executes a simple test, i.e. a test without parameters.
	 * 
	 * @param callee
	 *            Statement that should be called to measure performance and execute the test
	 * @return The result of the test
	 * @throws Throwable
	 *             Any exception that occurs during the test
	 */
	private TestResult executeSimpleTest(final PerformanceJUnitStatement callee) throws Throwable {
		final TestResult tr = new TestResult(methodName, executionTimes, datacollectors);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			LOG.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, callee, true, "execution ", executionTimes);
		} catch (final Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(methodName, filename, tr, warmupExecutions, saveFullData));
			throw t;
		}
		tr.finalizeCollection();
		saveData(SaveableTestData.createFineTestData(methodName, filename, tr, warmupExecutions, saveFullData));
		return tr;
	}

	/**
	 * Runs the warmup for the tests.
	 * 
	 * @param callee
	 *            Statement that should be called to measure performance and execute the test
	 * @throws Throwable
	 *             Any exception that occurs during the test
	 */
	private void runWarmup(final PerformanceJUnitStatement callee) throws Throwable {
		final TestResult tr = new TestResult(methodName, executionTimes, datacollectors);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			LOG.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, callee, true, "warmup execution ", warmupExecutions);
			warmupExecutions = tr.getRealExecutions();
		} catch (final Throwable t) {
			tr.finalizeCollection();
			throw t;
		}
		tr.finalizeCollection();
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
	private void runMainExecution(final TestResult tr, final PerformanceJUnitStatement callee, final boolean simple, final String warmupString, final int executions) throws Throwable {
		final String methodString = tr.getTestcase();
		int execution;
		for (execution = 1; execution <= executions; execution++) {

			callee.preEvaluate();
			LOG.debug("--- Starting " + warmupString + methodString + " " + execution + "/" + executionTimes + " ---");
			if (simple)
				tr.startCollection();
			callee.evaluate();
			if (simple)
				tr.stopCollection();
			LOG.debug("--- Stopping " + warmupString + +execution + "/" + executionTimes + " ---");
			callee.postEvaluate();
			tr.setRealExecutions(execution);
			if (execution >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				LOG.info("Exiting because of deviation reached");
				break;
			}
			final boolean interrupted = Thread.interrupted();
			LOG.debug("Interrupt state: {}", interrupted);
			if (isFinished || interrupted) {
				throw new InterruptedException();
			}
			Thread.sleep(1); // To let other threads "breath"
		}
		LOG.debug("Executions: " + execution);
		tr.setRealExecutions(execution);
	}
}
