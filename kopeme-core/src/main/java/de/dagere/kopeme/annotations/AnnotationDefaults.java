package de.dagere.kopeme.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Class used to get an instance of an annotation type (e.g.
 * {@link PerformanceTest}). Recommendation of a stackoverflow article to create
 * annotation instances used to get the default of an annotation.
 * 
 * @see <a href="http://stackoverflow.com/questions/266903/create-annotation-instance-with-defaults-in-java">http://stackoverflow.com/questions/266903/create-annotation-instance-with-defaults-in-java</a>
 * @author dhaeb
 * 
 */
public class AnnotationDefaults implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A of(final Class<A> annotation) {
		return (A) Proxy.newProxyInstance(annotation.getClassLoader(),new Class[] { annotation }, new AnnotationDefaults());
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return method.getDefaultValue();
	}
}
