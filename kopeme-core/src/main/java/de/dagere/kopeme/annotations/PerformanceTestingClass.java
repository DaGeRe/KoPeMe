package de.dagere.kopeme.annotations;
import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceTestingClass{
	/**
	 * Timeout of all test-methods including BeforeClass, Before, ...; normaly 1000000
	 * @return
	 */
	public int overallTimeout() default 1000000;
	
	
	/**
	 * Weather the test should log data of all executions or
	 * only statistical values
	 * @return
	 */
	public boolean logFullData() default false;
	
}