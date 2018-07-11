package de.dagere.kopeme;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;

/**
 * Represents an execution of all runs of one test.
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunner {

	private static Logger LOG = LogManager.getLogger(PerformanceTestRunner.class);

	protected final Class klasse;
	protected final Object instanz;
	protected final Method method;
	protected int executionTimes, warmupExecutions, repetitions, minEarlyStopExecutions, timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	/**
	 * Initializes the PerformanceTestRunner.
	 * 
	 * @param klasse Class whose tests should be run
	 * @param instance Instance of the class, whose tests should be run
	 * @param method Test method that should be run
	 */
	public PerformanceTestRunner(final Class klasse, final Object instance, final Method method) {
		this.klasse = klasse;
		this.instanz = instance;
		this.method = method;

		final PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			repetitions = annotation.repetitions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (final MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (final Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}

		filename = klasse.getName();
		LOG.info("Executing Performancetest: " + filename);
	}

	/**
	 * Runs the performance test.
	 * 
	 * @throws Throwable Any error that occurs during the test
	 */
	public void evaluate() throws Throwable {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				TestResult tr = null;
				try {
					if (method.getParameterTypes().length == 1) {
						tr = executeComplexTest();
					} else {
						tr = executeSimpleTest();
					}
					if (!assertationvalues.isEmpty()) {
						tr.checkValues(assertationvalues);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		final TimeBoundExecution tbe = new TimeBoundExecution(runnable, timeout, "method");
		tbe.execute();

		LOG.trace("Test {} beendet", filename);
	}

	/**
	 * Executes a complex test, i.e. a test which has TestResult as a parameter.
	 * 
	 * @return New TestResult
	 * @throws IllegalAccessException Thrown if an error during method access occurs
	 * @throws InvocationTargetException Thrown if an error during method access occurs
	 */
	private TestResult executeComplexTest() throws IllegalAccessException, InvocationTargetException {
		final TestResult tr = new TestResult(method.getName(), warmupExecutions, DataCollectorList.NONE);
		final Object[] params = { tr };
		runWarmup(params);
		TestResult newResult = null;
		try {
			if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
				LOG.warn("Not all Collectors are valid!");
			}
			newResult = new TestResult(method.getName(), executionTimes, DataCollectorList.STANDARD);
			params[0] = newResult;
			final PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, false, params, newResult);
			runMainExecution(pts, newResult);
		} catch (final Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, warmupExecutions, repetitions, true));
			throw t;
		}
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, warmupExecutions, repetitions, true));
		tr.checkValues();
		return tr;
	}

	/**
	 * Executes a simple test, i.e. a test without parameters.
	 * 
	 * @return The result of the test
	 * @throws IllegalAccessException Thrown if an error during method access occurs
	 * @throws InvocationTargetException Thrown if an error during method access occurs
	 */
	private TestResult executeSimpleTest() throws IllegalAccessException, InvocationTargetException {
		TestResult tr = new TestResult(method.getName(), warmupExecutions, DataCollectorList.NONE);
		final Object[] params = {};
		runWarmup(params);
		tr = new TestResult(method.getName(), executionTimes, DataCollectorList.STANDARD);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			LOG.warn("Not all Collectors are valid!");
		}
		final long start = System.currentTimeMillis();
		try {
			final PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, true, params, tr);
			runMainExecution(pts, tr);
		} catch (final Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, warmupExecutions, repetitions, true));
			throw t;
		}
		LOG.trace("Zeit: " + (System.currentTimeMillis() - start));
		tr.finalizeCollection();
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, warmupExecutions, repetitions, true));
		// TODO: statt true setzen, ob die vollen Daten wirklich geloggt werden sollen
		tr.checkValues();
		return tr;
	}

	/**
	 * Runs the warmup-executions of a test.
	 * 
	 * @param params The params for the method executions
	 * @throws IllegalAccessException Thrown if an error during method access occurs
	 * @throws InvocationTargetException Thrown if an error during method access occurs
	 */
	private void runWarmup(final Object[] params) throws IllegalAccessException, InvocationTargetException {
		final String methodString = method.getClass().getName() + "." + method.getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			LOG.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
			method.invoke(instanz, params);
			LOG.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}
	}

	/**
	 * Runs the main Executions of a test.
	 * 
	 * @param pts The Statement that should be run
	 * @param tr The testresult that should save the results and eventually cancel the executions early
	 * @throws IllegalAccessException Thrown if an error during method access occurs
	 * @throws InvocationTargetException Thrown if an error during method access occurs
	 */
	private void runMainExecution(final PerformanceKoPeMeStatement pts, final TestResult tr) throws IllegalAccessException, InvocationTargetException {
		final String methodString = method.getClass().getName() + "." + method.getName();
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {
			LOG.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
			for (int reptition = 0; reptition < repetitions; reptition++) {
				pts.evaluate();
			}
			LOG.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (final Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				LOG.debug("Entry: {} Aim: {} Value: {}", entry.getKey(), entry.getValue(), tr.getRelativeStandardDeviation(entry.getKey()));
			}
			tr.setRealExecutions(executions);
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}

		}
		LOG.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
