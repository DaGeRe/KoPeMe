package de.dagere.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.CPUUsageCollector;
import de.dagere.kopeme.datacollection.DataCollector;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.YAMLDataStorer;

/**
 * Represents an execution of all runs of one test
 * 
 * @author dagere
 * 
 */
public class TestExecution {

	private Logger log = LogManager.getLogger(TestExecution.class);

	protected Class klasse;
	protected Object instanz;
	protected Method method;
	protected int executionTimes, warmupExecutions, minEarlyStopExecutions,
			timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public TestExecution(Class klasse, Object instance, Method method) {
		this.klasse = klasse;
		this.instanz = instance;
		this.method = method;

		PerformanceTest annotation = method
				.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (MaximalRelativeStandardDeviation maxDev : annotation
					.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(),
						maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}

		filename = klasse.getName() + "." + method.getName();
	}

	public void runTest() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {

			@Override
			public void run() {
				TestResult tr = new TestResult(filename, warmupExecutions);
				try {
					if (method.getParameterTypes().length == 1) {
						tr = executeComplexTest(tr);

					} else {
						tr = executeSimpleTest(tr);
					}
					if (!assertationvalues.isEmpty()) {
						tr.checkValues(assertationvalues);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		Thread waitForTimeoutThread = new Thread(new TimeoutWaiter(this, mainThread, timeout));

		waitForTimeoutThread.start();
		mainThread.start();
		
		mainThread.join();

		log.info("Test {} beendet", filename);
	}
	
	/**
	 * Tests weather the collectors given in the assertions and the maximale
	 * relative standard deviations are correct
	 * 
	 * @param tr
	 * @return
	 */
	private boolean checkCollectorValidity(TestResult tr) {
		log.info("Checking DataCollector validity");
		boolean valid = true;
		for (String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for maximale relative standard deviation: "
						+ collectorName);
			}
		}
		return valid;
	}

	private TestResult executeComplexTest(TestResult tr)
			throws IllegalAccessException, InvocationTargetException {
		Object[] params = { tr };

		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + i + "/"
					+ warmupExecutions + " ---");
			method.invoke(instanz, params);
			log.info("--- Stopping warmup execution " + i + "/"
					+ warmupExecutions + " ---");
		}

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}

		tr = new TestResult(filename, executionTimes);
		params[0] = tr;
		runMainExecution(tr, params, false);

		tr.finalizeCollection();

		tr.checkValues();
		return tr;
	}

	private TestResult executeSimpleTest(TestResult tr)
			throws IllegalAccessException, InvocationTargetException {
		Object[] params = {};

		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + i + "/"
					+ warmupExecutions + " ---");
			method.invoke(instanz, params);
			log.info("--- Stopping warmup execution " + i + "/"
					+ warmupExecutions + " ---");
		}

		tr = new TestResult(filename, executionTimes);

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}

		runMainExecution(tr, params, true);
		tr.finalizeCollection();

		tr.checkValues();
		return tr;
	}

	private void runMainExecution(TestResult tr, Object[] params, boolean simple)
			throws IllegalAccessException, InvocationTargetException {

		// if (maximalRelativeStandardDeviation == 0.0f){
		for (int i = 1; i <= executionTimes; i++) {

			log.debug("--- Starting execution " + i + "/" + executionTimes
					+ " ---");
			if (simple)
				tr.startCollection();
			method.invoke(instanz, params);
			if (simple)
				tr.stopCollection();

			log.debug("--- Stopping execution " + i + "/" + executionTimes
					+ " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation
					.entrySet()) {
				log.debug("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (i >= minEarlyStopExecutions
					&& !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}

	}
}
