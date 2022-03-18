package de.dagere.kopeme.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a marker interface that indicates that the annotated method should not be used by KoPeMe or Peass.
 * @author DaGeRe
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface KoPeMeIgnore {

}
