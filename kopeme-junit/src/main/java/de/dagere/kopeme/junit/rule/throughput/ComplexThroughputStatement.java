package de.dagere.kopeme.junit.rule.throughput;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class ComplexThroughputStatement extends KoPeMeBasicStatement {

	private static final Logger log = LogManager.getLogger(ComplexThroughputStatement.class);

	private final int stepsize, maxsize;
	private int currentsize;
	private final IOberserveExecutionTimes oberserver;

	public ComplexThroughputStatement(final TestRunnables runnables, final Method method, final String filename, final int startsize, final int stepsize, final int maxsize,
			final IOberserveExecutionTimes oberserver) {
		super(runnables, method, filename);
		this.stepsize = stepsize;
		this.maxsize = maxsize;
		this.oberserver = oberserver;
		this.currentsize = startsize;
	}

	@Override
	public void evaluate() throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		runWarmup(methodString);

		while (currentsize <= maxsize) {
			TestResult tr = new TestResult(method.getName(), annotation.executionTimes());

			if (!checkCollectorValidity(tr)) {
				log.warn("Not all Collectors are valid!");
			}

			try {
				runMainExecution(tr);
			} catch (AssertionFailedError t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createAssertFailedTestData(method.getName(), filename, tr, 0, true));
				throw t;
			} catch (Throwable t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, 0, true));
				throw t;
			}
			tr.finalizeCollection();
			tr.addValue("size", currentsize);
			saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, 0, true));
			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}

			currentsize += stepsize;
			oberserver.setSize(currentsize);
		}
	}

	@Override
	protected void runMainExecution(final TestResult tr) throws IllegalAccessException, InvocationTargetException {
		int executions;
		int executionTimes = annotation.executionTimes();
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + executions + "/" + executionTimes + " ---");
			runnables.getBeforeRunnable().run();
			tr.startCollection();
			runnables.getTestRunnable().run();
			tr.stopCollection();
			runnables.getAfterRunnable().run();

			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + (executions - 1));
		tr.setRealExecutions(executions - 1);
	}
}
