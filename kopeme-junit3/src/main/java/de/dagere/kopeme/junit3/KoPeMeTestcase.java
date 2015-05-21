package de.dagere.kopeme.junit3;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;

/**
 * Base class for KoPeMe-JUnit3-Testcases.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeTestcase extends TestCase {
	/**
	 * Initializes the testcase
	 */
	public KoPeMeTestcase() {

	}

	/**
	 * Initializes the testcase with its name.
	 * 
	 * @param name
	 */
	public KoPeMeTestcase(final String name) {
		super(name);
	}

	private static final Logger LOG = LogManager.getLogger(KoPeMeTestcase.class);

	/**
	 * Returns the count of warmup executions, default is 5.
	 * 
	 * @return Warmup executions
	 */
	protected int getWarmupExecutions() {
		return 5;
	}

	/**
	 * Returns the count of real executions
	 * 
	 * @return real executions
	 */
	protected abstract int getExecutionTimes();

	/**
	 * Returns weather full data should be logged
	 * 
	 * @return Weather full data should be logged
	 */
	protected abstract boolean logFullData();

	/**
	 * Returns the time all testcase executions may take *in sum* in ms. -1 means unbounded; Standard is set to 120 s.
	 * 
	 * @return Maximal time of all test executions
	 */
	protected int getMaximalTime() {
		return 120000;
	}

	/**
	 * Gets the list of datacollectors for the current execution.
	 * 
	 * @return List of Datacollectors
	 */
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.STANDARD;
	}

	@Override
	protected void runTest() throws Throwable {
		final Runnable testCase = new Runnable() {

			@Override
			public void run() {
				try {
					KoPeMeTestcase.super.runTest();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		final int warmupExecutions = getWarmupExecutions(), executionTimes = getExecutionTimes();
		final boolean fullData = logFullData();
		final int timeoutTime = getMaximalTime();

		final TestResult tr = new TestResult(this.getClass().getName(), executionTimes);
		tr.setCollectors(getDataCollectors());
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					runTestCase(testCase, tr, warmupExecutions, executionTimes, fullData);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AssertionFailedError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tr.finalizeCollection();
			}
		});

		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof OutOfMemoryError) {
					while (t.isAlive())
						t.interrupt();
				}
				e.printStackTrace();
				fail();
			}
		});

		thread.start();
		LOG.debug("Waiting for test-completion for {}", timeoutTime);
		thread.join(timeoutTime);
		LOG.debug("Test should be finished...");
		while (thread.isAlive()) {
			LOG.debug("Thread not finished, is kill now..");
			thread.interrupt();
			thread.stop();
		}

		System.out.println("Speichere nach: " + this.getClass().getName());
		PerformanceTestUtils.saveData(getName(), tr, false, false, this.getClass().getName(), fullData);
	}

	/**
	 * Runs the whole testcase.
	 * 
	 * @param testCase Runnable that should be run
	 * @param tr Where the results should be saved
	 * @param warmupExecutions How many warmup executions should be done
	 * @param executionTimes How many normal executions should be done
	 * @param fullData Weather to log full data
	 * @throws AssertionFailedError Thrown if an assertion failed, i.e. the test is an failure
	 * @throws IllegalAccessException Thrown if an access error occurs
	 * @throws InvocationTargetException Thrown if an access error occurs
	 */
	private void runTestCase(final Runnable testCase, final TestResult tr, final int warmupExecutions, final int executionTimes, final boolean fullData)
			throws AssertionFailedError, InvocationTargetException, IllegalAccessException {
		String fullName = this.getClass().getName() + "." + getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			LOG.info("-- Starting warmup execution " + fullName + " " + i + "/" + warmupExecutions + " --");
			testCase.run();
			LOG.info("-- Stopping warmup execution " + i + "/" + warmupExecutions + " --");
		}

		try {
			runMainExecution(testCase, fullName, tr, executionTimes);
		} catch (AssertionFailedError t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(this.getClass().getName(), tr, true, false, getName(), fullData);
			throw t;
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(this.getClass().getName(), tr, false, true, getName(), fullData);
			throw t;
		}
	}

	/**
	 * Runs the main execution of the test, i.e. the execution where performance measures are counted.
	 * 
	 * @param testCase Runnable that should be run
	 * @param name Name of the test
	 * @param tr Where the results should be saved
	 * @param executionTimes How often the test should be executed
	 * @throws IllegalAccessException Thrown if an access error occurs
	 * @throws InvocationTargetException Thrown if an access error occurs
	 */
	private void runMainExecution(final Runnable testCase, final String name, final TestResult tr, final int executionTimes) throws IllegalAccessException,
			InvocationTargetException {
		int executions;
		String firstPart = "--- Starting execution " + name + " ";
		String endPart = "/" + executionTimes + " ---";
		for (executions = 1; executions <= executionTimes; executions++) {
			LOG.debug(firstPart + executions + endPart);
			tr.startCollection();
			testCase.run();
			tr.stopCollection();
			tr.getValue(TimeDataCollector.class.getName());
			LOG.debug("--- Stopping execution " + executions + endPart);
		}
		LOG.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
