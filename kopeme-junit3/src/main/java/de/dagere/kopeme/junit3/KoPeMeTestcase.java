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
 * Base class for KoPeMe-JUnit3-Testcases
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeTestcase extends TestCase {
	public KoPeMeTestcase() {

	}

	public KoPeMeTestcase(String name) {
		super(name);
	}

	private static final Logger log = LogManager.getLogger(KoPeMeTestcase.class);

	protected int getWarmupExecutions() {
		return 5;
	}

	protected abstract int getExecutionTimes();

	protected abstract boolean logFullData();

	/**
	 * Returns the time all testcase executions may take *in sum* in ms. -1
	 * means unbounded; Standard is set to 120 s
	 * 
	 * @return
	 */
	protected int getMaximalTime() {
		System.out.println("test2");
		return 2000;
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
		log.debug("Waiting for test-completion for {}", timeoutTime);
		thread.join(timeoutTime);
		log.debug("Test should be finished, is killed now...");
		while (thread.isAlive()) {
			log.debug("Kill Thread..");
			thread.interrupt();
			thread.stop();
		}

		System.out.println("Speichere nach: " + this.getClass().getName());
		PerformanceTestUtils.saveData(getName(), tr, false, false, this.getClass().getName(), fullData);
	}

	private void runTestCase(TestResult tr, Runnable testCase, final int warmupExecutions, final int executionTimes, final boolean fullData)
			throws AssertionFailedError, InvocationTargetException, IllegalAccessException {
		String fullName = this.getClass().getName() + "." + getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("-- Starting warmup execution " + fullName + " " + i + "/" + warmupExecutions + " --");
			testCase.run();
			log.info("-- Stopping warmup execution " + i + "/" + warmupExecutions + " --");
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

	private void runMainExecution(Runnable run, String name, TestResult tr, int executionTimes) throws IllegalAccessException, InvocationTargetException {
		int executions;
		String firstPart = "--- Starting execution " + name + " ";
		String endPart = "/" + executionTimes + " ---";
		for (executions = 1; executions <= executionTimes; executions++) {
			log.debug(firstPart + executions + endPart);
			tr.startCollection();
			run.run();
			tr.stopCollection();
			tr.getValue(TimeDataCollector.class.getName());
			log.debug("--- Stopping execution " + executions + endPart);
		}
		log.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
