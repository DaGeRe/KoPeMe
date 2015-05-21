package de.dagere.kopeme.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation tells a &#064;PerformanceTest the threshoulds for measured values of some collectors.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Assertion {
	/**
	 * Name of the collector, for whom the assertion should be, e.g. de.dagere.kopeme.datacollection.TimeDataCollector.
	 * 
	 * @return Name of the collector
	 */
	String collectorname();

	/**
	 * Maximum Value the measure of the collector should have.
	 * 
	 * @return Maximum value
	 */
	long maxvalue();
}