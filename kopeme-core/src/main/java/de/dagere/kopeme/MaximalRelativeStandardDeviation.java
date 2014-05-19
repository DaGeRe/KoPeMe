package de.dagere.kopeme;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MaximalRelativeStandardDeviation
{
	String collectorname();
	double maxvalue();
}