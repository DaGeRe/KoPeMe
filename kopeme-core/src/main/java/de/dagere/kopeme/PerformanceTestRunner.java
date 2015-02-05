package de.dagere.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;

/**
 * Represents an execution of all runs of one test
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunner {

	private static Logger log = LogManager.getLogger(PerformanceTestRunner.class);

	protected Class klasse;
	protected Object instanz;
	protected Method method;
	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public PerformanceTestRunner(Class klasse, Object instance, Method method) {
		this.klasse = klasse;
		this.instanz = instance;
		this.method = method;

		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}

		filename = klasse.getName();
		log.info("Executing Performancetest: " + filename);
	}

	public void evaluate() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), warmupExecutions);
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

		TimeBoundedExecution tbe = new TimeBoundedExecution(mainThread, timeout);
		tbe.execute();

		log.info("Test {} beendet", filename);
	}

	private TestResult executeComplexTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		Object[] params = { tr };
		runWarmup(params);
		try {
			if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
				log.warn("Not all Collectors are valid!");
			}
			tr = new TestResult(method.getName(), executionTimes);
			params[0] = tr;
			PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, false, params, tr);
			runMainExecution(pts, tr, params);
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, false, true, filename, true);
			throw t;
		}
		PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);

		tr.checkValues();
		return tr;
	}

	private TestResult executeSimpleTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {

		Object[] params = {};
		runWarmup(params);
		int executions = 0;
		tr = new TestResult(method.getName(), executionTimes);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			log.warn("Not all Collectors are valid!");
		}
		long start = System.currentTimeMillis();
		try {
			PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, true, params, tr);
			runMainExecution(pts, tr, params);
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, false, true, filename, true);
			throw t;
		}
		System.out.println("Zeit: " + (System.currentTimeMillis() - start));
		tr.finalizeCollection();
		PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);
		// TODO: statt true setzen, ob die vollen Daten wirklich geloggt werden
		// sollen

		tr.checkValues();

		return tr;
	}

	private void runWarmup(Object[] params) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
			method.invoke(instanz, params);
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}
	}

	private void runMainExecution(PerformanceKoPeMeStatement pts, TestResult tr, Object[] params) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		// if (maximalRelativeStandardDeviation == 0.0f){
		int executions;

		for (executions = 1; executions <= executionTimes; executions++) {
			log.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
			pts.evaluate();
			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.debug("Entry: {} Aim: {} Value: {}", entry.getKey(), entry.getValue(), tr.getRelativeStandardDeviation(entry.getKey()));
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
