package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This Rule gives the possibility to test performance with a rule and without a testrunner; this makes it possible to use a different testrunner. Be aware that
 * a rule-execution does measure the time needed for @Before-Executions together with the main execution time, but not the @BeforeClass-Execution.
 * 
 * @author DaGeRe
 *
 */
public class KoPeMeRule implements TestRule {

	private static final Logger log = LogManager.getLogger(KoPeMeRule.class);

	private Object testObject;

	public KoPeMeRule(Object testObject) {
		this.testObject = testObject;
	}

	@Override
	public Statement apply(final Statement stmt, Description descr) {
		if (descr.isTest()) {
			Method testMethod = null;
			Class<?> testClass = null;
			try {
				testClass = Class.forName(descr.getClassName());
				testMethod = testClass.getMethod(descr.getMethodName());
			} catch (ClassNotFoundException | NoSuchMethodException
					| SecurityException e) {
				e.printStackTrace();
			}

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

			Runnable beforeRunnable = new Runnable() {

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

			Runnable afterRunnable = new Runnable() {

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

			Runnable testRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						stmt.evaluate();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			};
			return new ParameterlessTestExecution(testRunnable, beforeRunnable, afterRunnable, testMethod, testClass.getName() + ".yaml");
		} else {
			return stmt;
		}

	}
}
