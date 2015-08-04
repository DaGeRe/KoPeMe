package de.dagere.kopeme.junit.rule.throughput;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class ThroughputStatement extends KoPeMeBasicStatement {

	private static final Logger log = LogManager.getLogger(ThroughputStatement.class);

	private final int stepsize, maxsize;

	public ThroughputStatement(TestRunnables runnables, Method method, String filename, int stepsize, int maxsize) {
		super(runnables, method, filename);
		this.stepsize = stepsize;
		this.maxsize = maxsize;
	}

	@Override
	public void evaluate() throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		runWarmup(methodString);

		while (executionTimes <= maxsize) {
			TestResult tr = new TestResult(method.getName(), executionTimes);

			if (!checkCollectorValidity(tr)) {
				log.warn("Not all Collectors are valid!");
			}

			try {
				runMainExecution(tr);
			} catch (AssertionFailedError t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createAssertFailedTestData(method.getName(), filename, tr, true));
				throw t;
			} catch (Throwable t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, true));
				throw t;
			}
			tr.finalizeCollection();
			saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, true));
			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}

			executionTimes += stepsize;
		}

		// PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);
	}

	protected void runMainExecution(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + executions + "/" + executionTimes + " ---");
			runnables.getBeforeRunnable().run();
			tr.startOrRestartCollection();
			runnables.getTestRunnable().run();
			tr.stopCollection();
			runnables.getAfterRunnable().run();

			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + (executions - 1));
		tr.setRealExecutions(executions - 1);
	}
}
