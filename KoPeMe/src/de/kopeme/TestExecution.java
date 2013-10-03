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
 * Represents an execution of all runs of
 * one test
 * @author dagere
 *
 */
public class TestExecution {
	protected Class klasse;
	protected Object instanz;
	protected Method method;
	protected int executionTimes, warmupExecutions;
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

			assertationvalues = new HashMap<String, Long>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}
		
		filename = klasse.getName() + "::" + method.getName();
	}

	public void runTest() {
		try {

			TestResult tr = new TestResult(filename, warmupExecutions);
			Object[] params = { tr };

			for (int i = 1; i <= warmupExecutions; i++) {
				System.out.println("--- Starting warmup execution " + i + "/"
						+ warmupExecutions + " ---");
				method.invoke(instanz, params);
				System.out.println("--- Stopping warmup execution " + i + "/"
						+ warmupExecutions + " ---");
			}

			tr = new TestResult(filename, executionTimes);
			params[0] = tr;
			for (int i = 1; i <= executionTimes; i++) {
				System.out.println("--- Starting execution " + i + "/"
						+ executionTimes + " ---");
				method.invoke(instanz, params);
				System.out.println("--- Stopping execution " + i + "/"
						+ executionTimes + " ---");
			}

			tr.finalizeCollection();

			tr.checkValues();

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
}
