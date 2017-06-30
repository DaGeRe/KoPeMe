package de.dagere.kopeme.junit.testrunner;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

public class PerformanceMethodStatement extends KoPeMeBasicStatement implements Finishable {

	private static final Logger LOG = LogManager.getLogger(PerformanceMethodStatement.class);

	private final PerformanceJUnitStatement callee;
	private final int timeout;
	private int warmupExecutions;

	private final String className, methodName;
	private final boolean saveFullData;
	private boolean isFinished = false;
	private Finishable mainRunnable;

	public PerformanceMethodStatement(final PerformanceJUnitStatement callee, final String filename, final Class<?> calledClass, final FrameworkMethod method, final boolean saveFullData) {
		super(null, method.getMethod(), filename);
		this.callee = callee;
		
//		final PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);
//		try {
//			KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), filename, method.getName());
//		} catch (final Exception e) {
//			System.err.println("kieker has failed!");
//			e.printStackTrace();
//		}

		this.saveFullData = saveFullData ? saveFullData : annotation.logFullData();
		warmupExecutions = annotation.warmupExecutions();
		timeout = annotation.timeout();
		this.methodName = method.getName();
		this.className = calledClass.getSimpleName(); // The name of the testcase-class is recorded; if tests of subclasses are called, they belong to the testcase of the superclass anyway
	}

	@Override
	public void evaluate() throws Throwable {

		mainRunnable = new Finishable() {

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
				PerformanceMethodStatement.this.isFinished = isFinished;
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
		final TestResult tr = new TestResult(methodName, annotation.executionTimes(), datacollectors);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			LOG.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, "execution ", annotation.executionTimes(), callee);
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
		final TestResult tr = new TestResult(methodName, annotation.warmupExecutions(), datacollectors);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			LOG.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, "warmup execution ", annotation.warmupExecutions(), callee);
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
	private void runMainExecution(final TestResult tr, final String warmupString, final int executions, final PerformanceJUnitStatement callee) throws Throwable {
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
	}

	@Override
	public void run() {
		// Never called, as the class is called via evaluate - only needs to be implemented to meet the interface
	}

	@Override
	public boolean isFinished() {
		if (mainRunnable != null) {
			return mainRunnable.isFinished();
		} else {
			return false;
		}
	}

	@Override
	public void setFinished(final boolean isFinished) {
		LOG.debug("Setze finished: " + isFinished + " " + mainRunnable);
		if (mainRunnable != null) {
			mainRunnable.setFinished(isFinished);
		}
	}
}