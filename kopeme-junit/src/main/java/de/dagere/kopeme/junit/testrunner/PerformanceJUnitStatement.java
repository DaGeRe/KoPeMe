package de.dagere.kopeme.junit.testrunner;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class PerformanceJUnitStatement extends Statement {

	private final Statement statement;
	private final Object fTarget;
	private List<FrameworkMethod> befores;
	private List<FrameworkMethod> afters;

	public PerformanceJUnitStatement(Statement statement, Object target) {
		this.statement = statement;
		fTarget = target;
	}

	public void preEvaluate() {
		try {
			for (FrameworkMethod before : befores) {
				before.invokeExplosively(fTarget);
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postEvaluate() {
		try {
			for (FrameworkMethod after : afters) {
				after.invokeExplosively(fTarget);
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void evaluate() throws Throwable {
		statement.evaluate();
	}

	public void setBefores(List<FrameworkMethod> befores) {
		this.befores = befores;
	}

	public void setAfters(List<FrameworkMethod> afters) {
		this.afters = afters;

	}
}