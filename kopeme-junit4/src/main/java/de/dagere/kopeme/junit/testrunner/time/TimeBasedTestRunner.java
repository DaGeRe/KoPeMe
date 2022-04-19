package de.dagere.kopeme.junit.testrunner.time;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.annotations.AnnotationDefaults;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.dagere.kopeme.runnables.TestRunnable;
import junit.framework.AssertionFailedError;

/**
 * Runs a Performance Test with JUnit. The method which should be tested has to got the parameter TestResult. This does not work without another runner, e.g. the TheorieRunner. An alternative
 * implementation, e.g. via Rules, which would make it possible to include Theories, is not possible, because one needs to change the signature of test methods to get KoPeMe-Tests running.
 * 
 * This test runner does not measure the time before and after are taking; but time rules take to execute are added to the overall-time of the method-execution.
 * 
 * @author dagere
 * 
 */
public class TimeBasedTestRunner extends PerformanceTestRunnerJUnit {

	private static final PerformanceTestingClass DEFAULTPERFORMANCETESTINGCLASS = AnnotationDefaults.of(PerformanceTestingClass.class);
	private final static Logger LOG = LogManager.getLogger(TimeBasedTestRunner.class);

	/**
	 * Initializes a PerformanceTestRunnerJUnit
	 * 
	 * @param klasse
	 *            Class that should be tested
	 * @throws InitializationError
	 *             Thrown if class can't be initialized
	 */
	public TimeBasedTestRunner(final Class<?> klasse) throws InitializationError {
		super(klasse);
	}

	/**
	 * Creates a PerformanceStatement out of a method
	 * 
	 * @param currentMethod
	 *            Method for which the statement should be created
	 * @return The statement
	 */
	@Override
   protected Statement createPerformanceStatementFromMethod(final FrameworkMethod currentMethod) {
		try {
			final TestRunnable callee = getStatement(currentMethod);
			
			LOG.trace("Im methodBlock für " + currentMethod.getName());

			this.method = currentMethod;

			if (!classFinished){
				currentMethodStatement = new TimeBasedStatement(callee, filename, klasse, method, logFullDataClass);
				return currentMethodStatement;
			}else{
				return new Statement() {
					@Override
					public void evaluate() throws Throwable {
						throw new AssertionFailedError("Test class has already timed out.");
					}
				};
			}
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
