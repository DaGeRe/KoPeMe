package de.dagere.kopeme.testrunner;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.TestExecution;
import de.dagere.kopeme.paralleltests.ParallelPerformanceTest;
import de.dagere.kopeme.paralleltests.ParallelTestExecution;

public class PerformanceExecutionStatement extends Statement {

	private final FrameworkMethod fTestMethod;
	private Object fTarget;

	public PerformanceExecutionStatement(FrameworkMethod testMethod, Object target) {
		fTestMethod = testMethod;
		fTarget = target;
	}

	@Override
	public void evaluate() throws Throwable {
		TestExecution te;
		Class<? extends Object> clazz = fTarget.getClass();
		Method method = fTestMethod.getMethod();
		if (fTestMethod.getAnnotation(ParallelPerformanceTest.class) != null) {
			te = new ParallelTestExecution(clazz, fTarget, method);
		} else {
			System.out.println("PerformanceExecutionStatement.evaluate(1.2): " + System.currentTimeMillis());
			te = new TestExecution(clazz, fTarget, method);
		}
		te.evaluate();
	}
}