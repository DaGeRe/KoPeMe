package de.dagere.kopeme.junit.testrunner;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


/**
 * A JUnit Statement that could be used for Performance Tests.
 * 
 * @author reichelt
 *
 */
public class PerformanceJUnitStatement extends Statement {

	private final Statement statement;
	private final Object fTarget;
	private final List<FrameworkMethod> befores;
	private final List<FrameworkMethod> afters;

	/**
	 * Initializes PerformanceJUnitStatement.
	 * 
	 * @param statement
	 *            Statement that should be used
	 * @param target
	 *            Object for executing the performance test
	 */
	public PerformanceJUnitStatement(final Statement statement, final Object target, List<FrameworkMethod> befores, List<FrameworkMethod> afters) {
		this.statement = statement;
		fTarget = target;
		this.befores = befores;
		this.afters = afters;
	}

	/**
	 * Called before the measuring evaluation to initialize test.
	 */
	public final void preEvaluate() {
		try {
			for (final FrameworkMethod before : befores) {
				before.invokeExplosively(fTarget);
			}
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called after the measuring evaluation to cleanup test.
	 */
	public final void postEvaluate() {
		try {
			for (final FrameworkMethod after : afters) {
				after.invokeExplosively(fTarget);
			}
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void evaluate() throws Throwable {
		statement.evaluate();
	}
}