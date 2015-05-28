package de.dagere.kopeme.junit.rule.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a method that should be run before a test without measuring the performance.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface BeforeNoMeasurement {

}
