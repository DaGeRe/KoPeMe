package de.dagere.kopeme.junit3;

import java.lang.reflect.InvocationTargetException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;

public abstract class KoPeMeTestcase extends TestCase {
	public KoPeMeTestcase() {

	}

	public KoPeMeTestcase(String name) {
		super(name);
	}

	private static final Logger log = LogManager.getLogger(KoPeMeTestcase.class);

	protected abstract int getWarmupExecutions();

	protected abstract int getExecutionTimes();

	protected abstract boolean logFullData();

	/**
	 * Returns the time all testcase executions may take *in sum*. -1 means
	 * unbounded
	 * 
	 * @return
	 */
	protected int getMaximalTime() {
		return 10000;
	}

	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.STANDARD;
	}

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
					runTestCase(tr, testCase, warmupExecutions, executionTimes, fullData);
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

		thread.start();

		thread.join(timeoutTime);
		while (thread.isAlive()) {
			thread.interrupt();
		}

		System.out.println("Speichere nach: " + this.getClass().getName());
		PerformanceTestUtils.saveData(getName(), tr, false, false, this.getClass().getName(), fullData);
	}

	private void runTestCase(TestResult tr, Runnable testCase, final int warmupExecutions, final int executionTimes, final boolean fullData)
			throws AssertionFailedError, InvocationTargetException, IllegalAccessException {
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + this.getClass().getName() + i + "/" + warmupExecutions + " ---");
			testCase.run();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}

		try {
			runMainExecution(testCase, this.getClass().getName(), tr, executionTimes);
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

	private void runMainExecution(Runnable run, String name, TestResult tr, int executionTimes) throws IllegalAccessException, InvocationTargetException {
		int executions;

		for (executions = 1; executions <= executionTimes; executions++) {
			log.debug("--- Starting execution " + name + " " + executions + "/" + executionTimes + " ---");
			tr.startCollection();
			run.run();
			tr.stopCollection();
			tr.getValue(TimeDataCollector.class.getName());
			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
		}
		log.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
