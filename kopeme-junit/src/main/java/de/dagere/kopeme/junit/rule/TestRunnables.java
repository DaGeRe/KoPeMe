package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit.rule.annotations.AfterNoMeasurement;
import de.dagere.kopeme.junit.rule.annotations.BeforeNoMeasurement;

public class TestRunnables {

	private static final Logger log = LogManager.getLogger(TestRunnables.class);

	private final Runnable testRunnable, beforeRunnable, afterRunnable;

	public TestRunnables(Runnable testRunnable, Class testClass, final Object testObject) {
		super();
		this.testRunnable = testRunnable;
		final List<Method> beforeMethods = new LinkedList<>();
		final List<Method> afterMethods = new LinkedList<>();
		log.debug("Klasse: {}", testClass);
		for (Method classMethod : testClass.getMethods()) {
			log.debug("Prüfe: {}", classMethod);
			if (classMethod.getAnnotation(BeforeNoMeasurement.class) != null) {
				if (classMethod.getParameterTypes().length > 0) {
					throw new RuntimeException("BeforeNoMeasurement-methods must not have arguments");
				}
				beforeMethods.add(classMethod);
			}
			if (classMethod.getAnnotation(AfterNoMeasurement.class) != null) {
				if (classMethod.getParameterTypes().length > 0) {
					throw new RuntimeException("AfterNoMeasurement-methods must not have arguments");
				}
				afterMethods.add(classMethod);
			}
		}

		beforeRunnable = new Runnable() {

			@Override
			public void run() {
				for (Method m : beforeMethods) {
					try {
						m.invoke(testObject);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};

		afterRunnable = new Runnable() {

			@Override
			public void run() {
				for (Method m : afterMethods) {
					try {
						m.invoke(testObject);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};
	}

	public Runnable getTestRunnable() {
		return testRunnable;
	}

	public Runnable getBeforeRunnable() {
		return beforeRunnable;
	}

	public Runnable getAfterRunnable() {
		return afterRunnable;
	}
}
