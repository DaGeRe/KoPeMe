package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.junit.TestExecutorJUnit;

/**
 * Represents an execution of all runs of one test
 * 
 * TODO: Overthing weather directly configure test runs in KoPeMeRule would be more nice
 * 
 * @author dagere
 * 
 */
public class ParameterlessTestExecution extends TestExecutorJUnit {

	static Logger log = LogManager.getLogger(ParameterlessTestExecution.class);

	protected Method method;

	protected Runnable performanceTestThing, beforeExecutions, afterExecutions;

	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;

	public ParameterlessTestExecution(Runnable timeTestThing, Runnable beforeExecutions, Runnable afterExecutions, Method method, String filename) {
		this.performanceTestThing = timeTestThing;
		this.filename = filename;
		this.method = method;

		this.beforeExecutions = beforeExecutions;
		this.afterExecutions = afterExecutions;

		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();
			assertationvalues = new HashMap<>();
			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		} else {
			log.error("No @PerformanceTest-Annotation present!");
		}

		log.trace("Filename: " + filename);
	}

	@Override
	public void evaluate() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), warmupExecutions);
				try {
					tr = executeSimpleTest(tr);
					if (!assertationvalues.isEmpty()) {
						tr.checkValues(assertationvalues);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		mainThread.start();
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			mainThread.interrupt();
		}

		log.info("Test {} beendet", filename);
	}

	private TestResult executeSimpleTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		log.trace("Methodstring: " + methodString);

		Object[] params = {};
		for (int i = 1; i <= warmupExecutions; i++) {
			beforeExecutions.run();
			log.info("--- Starting warmup execution " + methodString + " " + i + "/" + warmupExecutions + " ---");
			performanceTestThing.run();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
			afterExecutions.run();
		}

		tr = new TestResult(method.getName(), executionTimes);

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, params);
		} catch (AssertionFailedError t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, true, false, filename, true);
			throw t;
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, false, true, filename, true);
			throw t;
		}
		tr.finalizeCollection();
		PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);

		tr.checkValues();
		return tr;
	}

	private void runMainExecution(TestResult tr, Object[] params) throws IllegalAccessException, InvocationTargetException {
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + executions + "/" + executionTimes + " ---");
			beforeExecutions.run();
			performanceTestThing.run();
			afterExecutions.run();

			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
