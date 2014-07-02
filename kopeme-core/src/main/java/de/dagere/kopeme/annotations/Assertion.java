package de.dagere.kopeme.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Assertion
{
	String collectorname();
	long maxvalue();
}