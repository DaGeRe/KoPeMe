package de.dagere.kopeme.paralleltests;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.dagere.kopeme.Assertion;

@Retention(RetentionPolicy.RUNTIME)
@Target( { METHOD } )
public @interface ParallelPerformanceTest {
	
}
