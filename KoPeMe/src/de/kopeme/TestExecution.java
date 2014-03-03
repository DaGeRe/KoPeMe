package de.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.kopeme.datacollection.CPUUsageCollector;
import de.kopeme.datacollection.DataCollector;
import de.kopeme.datacollection.HarddiskWriteCollector;
import de.kopeme.datacollection.TestResult;
import de.kopeme.datacollection.TimeDataCollector;
import de.kopeme.datastorage.YAMLDataStorer;

/**
 * Represents an execution of all runs of one test
 * 
 * @author dagere
 * 
 */
public class TestExecution {
	protected Class klasse;
	protected Object instanz;
	protected Method method;
	protected int executionTimes, warmupExecutions;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public TestExecution(Class klasse, Object instance, Method method) {
		this.klasse = klasse;
		this.instanz = instance;
		this.method = method;

		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}

		filename = klasse.getName() + "." + method.getName();

		// System.out.println("Run " + klasse);
	}

	public void runTest() {
		try {

			TestResult tr = new TestResult(filename, warmupExecutions);
			if (method.getParameterTypes().length == 1) {
				tr = executeComplexTest(tr);
			} else {
				tr = executeSimpleTest(tr);
			}

			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}

			System.out.println("Beendet");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
				"Testmethoden von KoPeMe müssen genau einen Parameter vom Typ TestResult enthalten.");
		} catch (IllegalAccessException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
	}

	private TestResult executeComplexTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		Object[] params = { tr };

		for (int i = 1; i <= warmupExecutions; i++) {
			System.out.println("--- Starting warmup execution " + i + "/" + warmupExecutions + " ---");
			method.invoke(instanz, params);
			System.out.println("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}

		tr = new TestResult(filename, executionTimes);
		params[0] = tr;
		runMainExecution(tr, params, false);

		tr.finalizeCollection();

		tr.checkValues();
		return tr;
	}

	private TestResult executeSimpleTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		Object[] params = {};

		for (int i = 1; i <= warmupExecutions; i++) {
			System.out.println("--- Starting warmup execution " + i + "/" + warmupExecutions + " ---");
			method.invoke(instanz, params);
			System.out.println("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}

		tr = new TestResult(filename, executionTimes);

		runMainExecution(tr, params, true);

		tr.finalizeCollection();

		tr.checkValues();
		return tr;
	}

	private void runMainExecution(TestResult tr, Object[] params, boolean simple) throws IllegalAccessException,
		InvocationTargetException {

		// if (maximalRelativeStandardDeviation == 0.0f){
		for (int i = 1; i <= executionTimes; i++) {

			System.out.println("--- Starting execution " + i + "/" + executionTimes + " ---");
			if (simple)
				tr.startCollection();
			method.invoke(instanz, params);
			if (simple)
				tr.stopCollection();
			System.out.println("--- Stopping execution " + i + "/" + executionTimes + " ---");
			if (!maximalRelativeStandardDeviation.isEmpty() &&
				tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)){
				break;
			}
		}
		// }
		// else{
		// System.out.println("--- Starting execution until standard deviation is below "
		// + maximalRelativeStandardDeviation + " ---");
		// int i = 1;
		// while
		// (!tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)
		// && i < executionTimes){
		// System.out.println("--- Starting execution " + i + " ---");
		// if (simple) tr.startCollection();
		// method.invoke(instanz, params);
		// if (simple) tr.stopCollection();
		// System.out.println("--- Stopping execution " + i + " ---");
		// i++;
		// }
		// }

	}
}
