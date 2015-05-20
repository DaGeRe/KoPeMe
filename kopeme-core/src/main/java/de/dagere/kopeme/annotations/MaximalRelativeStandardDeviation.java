package de.dagere.kopeme.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to define which maximal relative standard deviation should be reached after a test is finished. The test is finished earlier - before
 * the normal executionTimes are executed - if this maximale relative standard deviation is reached. This saves testing time and ensures, that the measured
 * value is reliable as the standard deviation of the measures is low.
 * 
 * Example:
 * 
 * <pre>
 * @PerformanceTest(warmupExecutions = 3, executionTimes = 100, 
 *     assertions = { .. }, 
 *     minEarlyStopExecutions = 15, 
 *     deviations = {
 * 			@MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 0.1)
 * </pre>
 * 
 * would run the test 3 times for warmump, than 15 times for measurement. After this 15 times, the remaining 85 executions are only as executed as long as the
 * maximale relative standard deviation is more than 0.1. If it drops below this deviation, the test is finished.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MaximalRelativeStandardDeviation {
	/**
	 * Name of the collector, for which the maximale relative standard deviation is given.
	 * 
	 * @return Name of the data collector
	 */
	String collectorname();

	/**
	 * Maximum value, that should be checked.
	 * 
	 * @return value Maximum value
	 */
	double maxvalue();
}