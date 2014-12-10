package de.dagere.kopeme.junit.testrunner;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestRunner;
import de.dagere.kopeme.paralleltests.ParallelPerformanceTest;
import de.dagere.kopeme.paralleltests.ParallelTestExecution;

public class PerformanceJUnitStatement extends Statement {

	private final FrameworkMethod fTestMethod;
	private Object fTarget;

	public PerformanceJUnitStatement(FrameworkMethod testMethod, Object target) {
		fTestMethod = testMethod;
		fTarget = target;
	}

	@Override
	public void evaluate() throws Throwable {
		PerformanceTestRunner te;
		Class<? extends Object> clazz = fTarget.getClass();
		Method method = fTestMethod.getMethod();
		if (fTestMethod.getAnnotation(ParallelPerformanceTest.class) != null) {
			te = new ParallelTestExecution(clazz, fTarget, method);
		} else {
			te = new PerformanceTestRunner(clazz, fTarget, method);
		}
		te.evaluate();
	}
}