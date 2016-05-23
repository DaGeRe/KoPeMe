package de.dagere.kopeme.junit.rule.throughput;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class ThroughputStatement extends KoPeMeBasicStatement {

	private static final Logger log = LogManager.getLogger(ThroughputStatement.class);

	private final int stepsize, maxsize;

	public ThroughputStatement(final TestRunnables runnables, final Method method, final String filename, final int stepsize, final int maxsize) {
		super(runnables, method, filename);
		this.stepsize = stepsize;
		this.maxsize = maxsize;
	}

	@Override
	public void evaluate() throws Throwable {
		final String methodString = method.getClass().getName() + "." + method.getName();
		runMainExecution(new TestResult(method.getName(), annotation.warmupExecutions(), DataCollectorList.STANDARD), "warmup execution ", annotation.warmupExecutions());

		int executionTimes = annotation.executionTimes();
		while (executionTimes <= maxsize) {
			final TestResult tr = new TestResult(method.getName(), executionTimes, DataCollectorList.STANDARD);

			if (!checkCollectorValidity(tr)) {
				log.warn("Not all Collectors are valid!");
			}

			try {
				runMainExecution(tr, "execution ", annotation.executionTimes());
			} catch (final AssertionFailedError t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createAssertFailedTestData(method.getName(), filename, tr, 0, true));
				throw t;
			} catch (final Throwable t) {
				tr.finalizeCollection();
				saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, 0, true));
				throw t;
			}
			tr.finalizeCollection();
			saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, 0, true));
			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}

			executionTimes += stepsize;
		}

		// PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);
	}

//	protected void runMainExecution(final TestResult tr) throws IllegalAccessException, InvocationTargetException {
//		int executions;
//		final int executionTimes = annotation.executionTimes();
//		for (executions = 1; executions <= executionTimes; executions++) {
//
//			log.debug("--- Starting execution " + executions + "/" + executionTimes + " ---");
//			runnables.getBeforeRunnable().run();
//			tr.startOrRestartCollection();
//			runnables.getTestRunnable().run();
//			tr.stopCollection();
//			runnables.getAfterRunnable().run();
//
//			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
//			for (final Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
//				log.trace("Entry: {} {}", entry.getKey(), entry.getValue());
//			}
//			if (executions >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
//					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
//				break;
//			}
//		}
//		log.debug("Executions: " + (executions - 1));
//		tr.setRealExecutions(executions - 1);
//	}
}
