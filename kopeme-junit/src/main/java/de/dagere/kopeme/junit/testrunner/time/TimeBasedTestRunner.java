package de.dagere.kopeme.junit.testrunner.time;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.Finishable;
import de.dagere.kopeme.TimeBoundExecution;
import de.dagere.kopeme.annotations.AnnotationDefaults;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.junit.testrunner.PerformanceFail;
import de.dagere.kopeme.junit.testrunner.PerformanceJUnitStatement;
import de.dagere.kopeme.junit.testrunner.PerformanceMethodStatement;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;

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
	 * Gets the PerformanceJUnitStatement for the test execution of the given method.
	 * 
	 * @param currentMethod
	 *            Method that should be tested
	 * @return PerformanceJUnitStatement for testing the method
	 * @throws NoSuchMethodException
	 *             Thrown if the method does not exist
	 * @throws SecurityException
	 *             Thrown if the method is not accessible
	 * @throws IllegalAccessException
	 *             Thrown if the method is not accessible
	 * @throws IllegalArgumentException
	 *             Thrown if the method has arguments
	 * @throws InvocationTargetException
	 *             Thrown if the method is not accessible
	 */
	private PerformanceJUnitStatement getStatement(final FrameworkMethod currentMethod) throws NoSuchMethodException, SecurityException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {

		try {
			final Object testObject = new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
			if (classFinished){
				return null;
			}
			LOG.debug("Statement: " + currentMethod.getName() + " " + classFinished);

			Statement testExceptionTimeoutStatement = methodInvoker(currentMethod, testObject);

			testExceptionTimeoutStatement = possiblyExpectingExceptions(currentMethod, testObject, testExceptionTimeoutStatement);
			// testExceptionTimeoutStatement = withPotentialTimeout(currentMethod, test, testExceptionTimeoutStatement);

			final Method withRulesMethod = BlockJUnit4ClassRunner.class.getDeclaredMethod("withRules", FrameworkMethod.class, Object.class, Statement.class);
			withRulesMethod.setAccessible(true);

			final Statement withRuleStatement = (Statement) withRulesMethod.invoke(this, new Object[] { currentMethod, testObject, testExceptionTimeoutStatement });
			final PerformanceJUnitStatement perfStatement = new PerformanceJUnitStatement(withRuleStatement, testObject);
			final List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
			final List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);
			perfStatement.setBefores(befores);
			perfStatement.setAfters(afters);

			return perfStatement;
		} catch (final Throwable e) {
			return new PerformanceFail(e);
		}
	}

	/**
	 * Creates a PerformanceStatement out of a method
	 * 
	 * @param currentMethod
	 *            Method for which the statement should be created
	 * @return The statement
	 */
	protected Statement createPerformanceStatementFromMethod(final FrameworkMethod currentMethod) {
		try {
			final PerformanceJUnitStatement callee = getStatement(currentMethod);
			
			LOG.trace("Im methodBlock für " + currentMethod.getName());

			this.method = currentMethod;

			if (!classFinished){
				currentMethodStatement = new TimeBasedStatement(callee, filename, klasse, method, logFullData);
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
