package de.dagere.kopeme;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to define which maximal relative standard deviation should be reached after a test is finished.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MaximalRelativeStandardDeviation
{
	/**
	 * Name of the collector, for which the maximale relative standard deviation is given.
	 * 
	 * @return Name of the data collector
	 */
	String collectorname();

	/**
	 * Maximum value.
	 * 
	 * @return value
	 */
	double maxvalue();
}