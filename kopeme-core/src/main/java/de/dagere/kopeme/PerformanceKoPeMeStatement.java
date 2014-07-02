package de.dagere.kopeme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import de.dagere.kopeme.datacollection.TestResult;

public class PerformanceKoPeMeStatement {
	
	private final Method fTestMethod;
	private Object fTarget;
	private boolean simple;
	private Object[] params;
	private TestResult tr;
	
	public PerformanceKoPeMeStatement(Method testMethod, Object target, boolean simple, Object[] params, TestResult tr) {
		fTestMethod = testMethod;
		fTarget = target;
		this.simple = simple;
		this.params = params;
		this.tr = tr;
	}
	
	public void evaluate() throws IllegalAccessException, InvocationTargetException{
		if (simple)
			tr.startCollection();
		fTestMethod.invoke(fTarget, params);
		if (simple)
			tr.stopCollection();
	}
}
