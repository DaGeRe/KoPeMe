package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

/**
 * A statement for running performance tests.
 * 
 * Should once become base class of several TestExecutingStatements - isn't yet.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeBasicStatement extends Statement {

	private static final Logger LOG = LogManager.getLogger(KoPeMeBasicStatement.class);

	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;
	protected Method method;
	protected TestRunnables runnables;

	protected PerformanceTest annotation;

	/**
	 * Initializes the KoPemeBasicStatement.
	 * 
	 * @param runnables Runnables that should be run
	 * @param method Method that should be executed
	 * @param filename Name of the
	 */
	public KoPeMeBasicStatement(final TestRunnables runnables, final Method method, final String filename) {
		super();
		this.runnables = runnables;
		this.filename = filename;
		this.method = method;

		annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			try {
				KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), filename, method.getName());
			} catch (Exception e) {
				System.err.println("kieker has failed!");
				e.printStackTrace();
			}
			maximalRelativeStandardDeviation = new HashMap<>();
			assertationvalues = new HashMap<>();
			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		} else {
			LOG.error("No @PerformanceTest-Annotation present!");
		}
	}

	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr Test Result that should be checked
	 * @return Weather the result is valid
	 */
	protected boolean checkCollectorValidity(final TestResult tr) {
		return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
	}

	protected void runMainExecution(final TestResult tr) throws IllegalAccessException, InvocationTargetException {
		int executions;
		for (executions = 1; executions <= annotation.executionTimes(); executions++) {

			LOG.debug("--- Starting execution " + executions + "/" + annotation.executionTimes() + " ---");
			runnables.getBeforeRunnable().run();
			tr.startCollection();
			runnables.getTestRunnable().run();
			tr.stopCollection();
			runnables.getAfterRunnable().run();
			tr.setRealExecutions(executions - 1);
			LOG.debug("--- Stopping execution " + executions + "/" + annotation.executionTimes() + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				LOG.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		LOG.debug("Executions: " + (executions - 1));
		tr.setRealExecutions(executions - 1);
	}

	protected void runWarmup(final String methodString) {
		for (int i = 1; i <= annotation.warmupExecutions(); i++) {
			runnables.getBeforeRunnable().run();
			LOG.info("--- Starting warmup execution " + methodString + " " + i + "/" + annotation.warmupExecutions() + " ---");
			runnables.getTestRunnable().run();
			LOG.info("--- Stopping warmup execution " + i + "/" + annotation.warmupExecutions() + " ---");
			runnables.getAfterRunnable().run();
		}
	}
}