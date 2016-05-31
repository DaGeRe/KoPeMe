package de.dagere.kopeme.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation of a <code>public class</code> tells that this class contains performance test. It mainly enables to set the overall timeout for all tests of
 * a class. With this overall timeout, it is possible to prevent the test from freezing if some aspects takes to long.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceTestingClass {
	/**
	 * Optionally specifies the timeout of all test-methods including BeforeClass, Before, ...; default is 60 * 60 * 1000 ( = 1 hour).
	 * 
	 * @return
	 */
	int overallTimeout() default 60 * 60 * 1000;

	/**
	 * Optionally specifies eather the test should log data of all executions or only statistical values.
	 * 
	 * @return
	 */
	boolean logFullData() default false;
}