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

	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.STANDARD;
	}

	protected void runTest() throws Throwable {
		Runnable testCase = new Runnable() {

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

		int executions = 0;

		Object[] params = {};
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + this.getClass().getName() + i + "/" + warmupExecutions + " ---");
			testCase.run();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}

		TestResult tr = new TestResult(this.getClass().getName(), executionTimes);
		tr.setCollectors(getDataCollectors());
		try {
			executions = runMainExecution(testCase, this.getClass().getName(), tr, executionTimes);
		} catch (AssertionFailedError t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(this.getClass().getName(), tr, executions, true, false, getName(), fullData);
			throw t;
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(this.getClass().getName(), tr, executions, false, true, getName(), fullData);
			throw t;
		}
		tr.finalizeCollection();
		System.out.println("Speichere nach: " + this.getClass().getName());
		PerformanceTestUtils.saveData(getName(), tr, executions, false, false, this.getClass().getName(), fullData);
	}

	private int runMainExecution(Runnable run, String name, TestResult tr, int executionTimes) throws IllegalAccessException, InvocationTargetException {
		// if (maximalRelativeStandardDeviation == 0.0f){
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
		return executions;
	}
}
