package de.dagere.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.CPUUsageCollector;
import de.dagere.kopeme.datacollection.DataCollector;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.XMLDataStorer;
import de.dagere.kopeme.datastorage.YAMLDataStorer;

/**
 * Represents an execution of all runs of one test
 * 
 * @author dagere
 * 
 */
public class TestExecution {

	private static Logger log = LogManager.getLogger(TestExecution.class);

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

		filename = klasse.getName();
		log.info("Filename: " + filename);
	}

	public void runTest() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), filename,
						warmupExecutions);
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

		mainThread.start();
		mainThread.join(timeout);
		if (mainThread.isAlive()) {
			mainThread.interrupt();
		}

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
		log.info("Checking DataCollector validity...");
		boolean valid = true;
		for (String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		String keys = "";
		for (String key : tr.getKeys()) {
			keys += key + " ";
		}
		for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for maximale relative standard deviation: "
						+ collectorName + " Available Keys: " + keys);
				for (String key : tr.getKeys()) {
					System.out.println(key + " - " + collectorName + ": "
							+ key.equals(collectorName));
				}
			}
		}
		log.info("... " + valid);
		return valid;
	}

	private TestResult executeComplexTest(TestResult tr)
			throws IllegalAccessException, InvocationTargetException {
		int executions = 0;
		try {
			Object[] params = { tr };
			String methodString = method.getClass().getName() + "."
					+ method.getName();
			log.info("Methodstring: " + methodString + " Warmup-Executions: "
					+ warmupExecutions);
			for (int i = 1; i <= warmupExecutions; i++) {
				log.info("--- Starting warmup execution " + methodString + " "
						+ i + "/" + warmupExecutions + " ---");
				method.invoke(instanz, params);
				log.info("--- Stopping warmup execution " + i + "/"
						+ warmupExecutions + " ---");
			}

			if (!checkCollectorValidity(tr)) {
				log.warn("Not all Collectors are valid!");
			}

			tr = new TestResult(method.getName(), filename, executionTimes);
			params[0] = tr;
			executions = runMainExecution(tr, params, false);

			tr.finalizeCollection();
		} catch (Throwable t) {
			saveData(method.getName(), tr, executions);
			throw t;
		}
		saveData(method.getName(), tr, executions);

		tr.checkValues();
		return tr;
	}

	private TestResult executeSimpleTest(TestResult tr)
			throws IllegalAccessException, InvocationTargetException {
		int executions = 0;
		String methodString = method.getClass().getName() + "."
				+ method.getName();
		log.info("Methodstring: " + methodString);

		Object[] params = {};
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + methodString + i + "/"
					+ warmupExecutions + " ---");
			method.invoke(instanz, params);
			log.info("--- Stopping warmup execution " + i + "/"
					+ warmupExecutions + " ---");
		}

		tr = new TestResult(method.getName(), filename, executionTimes);

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			executions = runMainExecution(tr, params, true);
		} catch (Throwable t) {
			tr.finalizeCollection();
			saveData(method.getName(), tr, executions);
			throw t;
		}
		tr.finalizeCollection();
		saveData(method.getName(), tr, executions);

		tr.checkValues();
		return tr;
	}

	private int runMainExecution(TestResult tr, Object[] params, boolean simple)
			throws IllegalAccessException, InvocationTargetException {

		// if (maximalRelativeStandardDeviation == 0.0f){
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + executions + "/"
					+ executionTimes + " ---");
			if (simple)
				tr.startCollection();
			method.invoke(instanz, params);
			if (simple)
				tr.stopCollection();

			log.debug("--- Stopping execution " + executions + "/"
					+ executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation
					.entrySet()) {
				log.debug("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= minEarlyStopExecutions
					&& !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + executions);
		return executions;
	}

	/**
	 * Saves the measured data
	 */
	public void saveData(String testcasename, TestResult tr, int executions) {
		XMLDataStorer xds = new XMLDataStorer(filename);
		for (String s : tr.getKeys()) {
			double relativeStandardDeviation = tr
					.getRelativeStandardDeviation(s);
			long value = tr.getValue(s);
			long min = tr.getMinumumCurrentValue(s);
			long max = tr.getMaximumCurrentValue(s);
			xds.storeValue(testcasename, s, value, relativeStandardDeviation,
					executions, min, max);
			// xds.storeValue(s, getValue(s));
			log.info("{}: {}, (rel. Standardabweichung: {})", s, value,
					relativeStandardDeviation);
		}
		xds.storeData();
	}
}
