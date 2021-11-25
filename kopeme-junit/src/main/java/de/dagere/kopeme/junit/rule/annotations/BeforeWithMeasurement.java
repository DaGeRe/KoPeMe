package de.dagere.kopeme.junit.rule.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a method that should be run before a test *with* measurement
 * 
 * For JUnit 4, the regular @BeforeClass method does the same; therefore this is not usable with JUnit4. 
 * For JUnit 5, no extension point allows to use @BeforeAll in an equivalent manner; 
 * therefore, @BeforeWithMeasurement needs to be replaced if the @BeforeAll should be executed in every measurement repetition
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface BeforeWithMeasurement {

}
