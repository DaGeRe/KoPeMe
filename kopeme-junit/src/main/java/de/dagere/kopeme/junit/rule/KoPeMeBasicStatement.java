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
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

/**
 * A statement for running performance tests.
 * 
 * Should once become base class of several TestExecutingStatements - is yet only base class of rule and throughput statement.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeBasicStatement extends Statement {

	private static final Logger LOG = LogManager.getLogger(KoPeMeBasicStatement.class);

	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected final String filename;
	protected Method method;
	protected TestRunnables runnables;
	protected boolean isFinished;
	protected DataCollectorList datacollectors;

	protected PerformanceTest annotation;

	/**
	 * Initializes the KoPemeBasicStatement.
	 * 
	 * @param runnables
	 *            Runnables that should be run
	 * @param method
	 *            Method that should be executed
	 * @param filename
	 *            Name of the
	 */
	public KoPeMeBasicStatement(final TestRunnables runnables, final Method method, final String filename) {
		super();
		this.runnables = runnables;
		this.filename = filename;
		this.method = method;

		annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation.dataCollectors().equals("EXTENDED")) {
			datacollectors = DataCollectorList.EXTENDED;
		} else if (annotation.dataCollectors().equals("STANDARD")) {
			datacollectors = DataCollectorList.STANDARD;
		} else if (annotation.dataCollectors().equals("ONLYTIME")) {
			datacollectors = DataCollectorList.ONLYTIME;
		} else if (annotation.dataCollectors().equals("NONE")) {
			datacollectors = DataCollectorList.NONE;
		} else {
			datacollectors = DataCollectorList.ONLYTIME;
			LOG.error("For Datacollectorlist, only STANDARD, ONLYTIME AND NONE are allowed");
		}

		if (annotation != null) {
			try {
				KoPeMeKiekerSupport.INSTANCE.useKieker(annotation.useKieker(), filename, method.getName());
			} catch (final Exception e) {
				System.err.println("kieker has failed!");
				e.printStackTrace();
			}
			maximalRelativeStandardDeviation = new HashMap<>();
			assertationvalues = new HashMap<>();
			for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			for (final Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		} else {
			LOG.error("No @PerformanceTest-Annotation present!");
		}
	}

	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr
	 *            Test Result that should be checked
	 * @return Weather the result is valid
	 */
	protected boolean checkCollectorValidity(final TestResult tr) {
		return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
	}

	protected void runMainExecution(final TestResult tr, final String warmupString, final int executions) throws IllegalAccessException, InvocationTargetException, InterruptedException {
		int execution;
		for (execution = 1; execution <= executions; execution++) {

			LOG.debug("--- Starting " + warmupString + execution + "/" + executions + " ---");
			runnables.getBeforeRunnable().run();
			tr.startCollection();
			runnables.getTestRunnable().run();
			tr.stopCollection();
			runnables.getAfterRunnable().run();
			tr.setRealExecutions(execution - 1);
			LOG.debug("--- Stopping execution " + execution + "/" + executions + " ---");
			for (final Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				LOG.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (isFinished) {
				LOG.debug("Exiting finished thread: {}.", Thread.currentThread().getName());
				throw new InterruptedException("Test timed out.");
			}
			if (execution >= annotation.minEarlyStopExecutions() && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
			final boolean interrupted = Thread.interrupted();
			LOG.debug("Interrupt state: {}", interrupted);
			if (interrupted) {
				LOG.debug("Exiting thread.");
				throw new InterruptedException("Test was interrupted and eventually timed out.");
			}
		}
		LOG.debug("Executions: " + (execution - 1));
		tr.setRealExecutions(execution - 1);
		try {
         KoPeMeKiekerSupport.INSTANCE.waitForEnd();
      } catch (final Exception e) {
         e.printStackTrace();
      }
	}
}