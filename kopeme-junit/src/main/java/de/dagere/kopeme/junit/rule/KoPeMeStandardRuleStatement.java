package de.dagere.kopeme.junit.rule;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;

/**
 * Represents an execution of all runs of one test
 * 
 * TODO: Overthink weather directly configure test runs in KoPeMeRule would be more nice
 * 
 * @author dagere
 * 
 */
public class KoPeMeStandardRuleStatement extends KoPeMeBasicStatement {

	static Logger log = LogManager.getLogger(KoPeMeStandardRuleStatement.class);

	public KoPeMeStandardRuleStatement(final TestRunnables runnables, final Method method, final String filename) {
		super(runnables, method, filename);
	}

	@Override
	public void evaluate() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), annotation.warmupExecutions());
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
		mainThread.join(annotation.timeout());
		if (mainThread.isAlive()) {
			mainThread.interrupt();
		}

		log.info("Test {} beendet", filename);
	}

	private TestResult executeSimpleTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		runWarmup(methodString);

		tr = new TestResult(method.getName(), annotation.timeout());

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr);
		} catch (AssertionFailedError t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createAssertFailedTestData(method.getName(), filename, tr, true));
			throw t;
		} catch (Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, true));
			throw t;
		}
		tr.finalizeCollection();
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, true));

		return tr;
	}
}
