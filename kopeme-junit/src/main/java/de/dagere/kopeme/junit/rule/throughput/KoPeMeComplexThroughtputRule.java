package de.dagere.kopeme.junit.rule.throughput;

import java.lang.reflect.Method;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.junit.rule.TestRunnables;

public class KoPeMeComplexThroughtputRule implements TestRule, IOberserveExecutionTimes {

	private final int maxsize, stepsize;
	private int currentsize;

	private Object testObject;

	public KoPeMeComplexThroughtputRule(int startvalue, int stepsize, int maxsize, Object testObject) {
		this.stepsize = stepsize;
		this.maxsize = maxsize;
		currentsize = startvalue;
		this.testObject = testObject;
	}

	public int getCurrentSize() {
		return currentsize;
	}

	@Override
	public Statement apply(final Statement stmt, Description descr) {
		if (descr.isTest()) {
			Method testMethod = null;
			Class<?> testClass = null;
			try {
				// testClass = Class.forName(descr.getClassName());
				testClass = testObject.getClass();
				testMethod = testClass.getMethod(descr.getMethodName());
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			TestRunnables runnables = new TestRunnables(new Runnable() {

				@Override
				public void run() {
					try {
						stmt.evaluate();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, testClass, testObject);

			return new ComplexThroughputStatement(runnables, testMethod, testClass.getName() + ".yaml", currentsize, stepsize, maxsize, this);
		} else {
			return stmt;
		}
	}

	@Override
	public void setSize(int size) {
		currentsize = size;
	}

}