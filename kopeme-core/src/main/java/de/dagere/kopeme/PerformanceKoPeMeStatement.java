package de.dagere.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.dagere.kopeme.datacollection.TestResult;

/**
 * Contains a KoPeMe-Teststatement which is a command for invoking a test method and eventually starting and stopping data collection.
 * 
 * @author reichelt
 *
 */
public class PerformanceKoPeMeStatement {

	private final Method fTestMethod;
	private final Object fTarget;
	private final boolean simple;
	private final Object[] params;
	private final TestResult tr;

	/**
	 * Initializes the PerformanceKoPeMeStatement.
	 * 
	 * @param testMethod The method that should be tested
	 * @param target The object that should be used for the test
	 * @param simple Weather data collection should be started (true) or not (false)
	 * @param params The parameters for the invocation
	 * @param tr The Testresult where the data is saved
	 */
	public PerformanceKoPeMeStatement(final Method testMethod, final Object target, final boolean simple, final Object[] params, final TestResult tr) {
		this.fTestMethod = testMethod;
		this.fTarget = target;
		this.simple = simple;
		this.params = params;
		this.tr = tr;
	}

	/**
	 * Evaluates the statement, i.e. executes the tests.
	 * 
	 * @throws IllegalAccessException Thrown if access to the testmethod is illegal
	 * @throws InvocationTargetException Thrown if an exception occurs during invocation
	 */
	public final void evaluate() throws IllegalAccessException, InvocationTargetException {
		if (simple) tr.startCollection();
		fTestMethod.invoke(fTarget, params);
		if (simple) tr.stopCollection();
	}
}
