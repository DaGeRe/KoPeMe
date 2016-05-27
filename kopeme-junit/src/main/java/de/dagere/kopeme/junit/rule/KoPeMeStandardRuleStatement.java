package de.dagere.kopeme.junit.rule;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.TimeBoundedExecution;
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
		final Finishable finishable = new Finishable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), annotation.warmupExecutions(), datacollectors);
				try {
					tr = executeSimpleTest(tr);
					if (!assertationvalues.isEmpty()) {
						tr.checkValues(assertationvalues);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				} catch (final Throwable e) {
					e.printStackTrace();
				}
			}

			@Override
			public boolean isFinished() {
				return KoPeMeStandardRuleStatement.this.isFinished;
			}

			@Override
			public void setFinished(final boolean isFinished) {
				KoPeMeStandardRuleStatement.this.isFinished = isFinished;
			}
		};
		
		final TimeBoundedExecution tbe = new TimeBoundedExecution(finishable, annotation.timeout());
		tbe.execute();


		log.info("Test {} beendet", filename);
	}

	private TestResult executeSimpleTest(final TestResult tr) throws Throwable {
//		tr = new TestResult(method.getName(), annotation.timeout(), DataCollectorList.STANDARD);

		if (!checkCollectorValidity(tr)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			//Run warmup
			runMainExecution(new TestResult(method.getName(), annotation.timeout(), datacollectors), "warmup execution ", annotation.warmupExecutions());
			runMainExecution(tr, "execution ", annotation.executionTimes());
		} catch (final AssertionFailedError t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createAssertFailedTestData(method.getName(), filename, tr, annotation.warmupExecutions(), true));
			throw t;
		} catch (final Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, annotation.warmupExecutions(), true));
			throw t;
		}
		tr.finalizeCollection();
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, annotation.warmupExecutions(), true));

		return tr;
	}
}
