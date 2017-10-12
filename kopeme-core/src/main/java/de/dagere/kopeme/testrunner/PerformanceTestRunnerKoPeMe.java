package de.dagere.kopeme.testrunner;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestRunner;
import de.dagere.kopeme.annotations.PerformanceTest;

/**
 * Runs a performance test via the pure test Runner, which does not need any
 * additional librarys.
 * 
 * @author dagere
 *
 */
public final class PerformanceTestRunnerKoPeMe {

	private static final Logger LOG = LogManager.getFormatterLogger(PerformanceTestRunnerKoPeMe.class);

	/**
	 * Class should not be initialized.
	 */
	private PerformanceTestRunnerKoPeMe() {
	}

	/**
	 * Starts testing a given class.
	 * 
	 * @param args
	 *            Only the classname
	 * @throws Throwable
	 *             Any possible exception during testing the class
	 */
	public static void main(final String[] args) throws Throwable {
		if (args.length == 0) {
			LOG.error("Der PerformanceTestRunner muss mit einem Klassennamen als Parameter ausgeführt werden.");
			System.exit(1);
		}
		String klassenName = args[0];

		try {
			Class c = Class.forName(klassenName);
			runTestsWithClass(c);
		} catch (ClassNotFoundException e) {
			LOG.error("Die gewünschte Klasse " + klassenName + " wurde unglücklicherweise nicht gefunden.");
			// e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Runs all tests for the given class.
	 * 
	 * @param clazz
	 *            Class-Object, for which the tests should be run
	 * @throws Throwable
	 *             Any possible exception during the run
	 */
	public static void runTestsWithClass(final Class<?> clazz) throws Throwable {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (instance == null) {
			LOG.error("Klasseninstanziierung nicht möglich");
			return;
		}
		boolean failed = false;
		List<AssertionError> errors = new LinkedList<AssertionError>();
		for (Method method : clazz.getMethods()) {
			try {
				if (method.isAnnotationPresent(PerformanceTest.class)) {
					PerformanceTestRunner te = new PerformanceTestRunner(clazz, instance, method);
					te.evaluate();
				}
			} catch (AssertionError ae) {
				failed = true;
				errors.add(ae);
			}
		}
		if (failed) {
			for (AssertionError ae : errors) {
				LOG.error("Exception: " + ae.getLocalizedMessage());
				ae.printStackTrace();
			}
		}
	}
}
