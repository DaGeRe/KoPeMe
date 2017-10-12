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
	private List<FrameworkMethod> befores;
	private List<FrameworkMethod> afters;

	/**
	 * Initializes PerformanceJUnitStatement.
	 * 
	 * @param statement
	 *            Statement that should be used
	 * @param target
	 *            Object for executing the performance test
	 */
	public PerformanceJUnitStatement(final Statement statement, final Object target) {
		this.statement = statement;
		fTarget = target;
	}

	/**
	 * Called before the measuring evaluation to initialize test.
	 */
	public void preEvaluate() {
		try {
			for (final FrameworkMethod before : befores) {
				before.invokeExplosively(fTarget);
			}
		} catch (final Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Called after the measuring evaluation to cleanup test.
	 */
	public void postEvaluate() {
		try {
			for (final FrameworkMethod after : afters) {
				after.invokeExplosively(fTarget);
			}
		} catch (final Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void evaluate() throws Throwable {
		statement.evaluate();
	}

	/**
	 * Sets Before-Methods that should be executed.
	 * 
	 * @param befores
	 *            Before-Methods
	 */
	public void setBefores(final List<FrameworkMethod> befores) {
		this.befores = befores;
	}

	/**
	 * Sets Before-Methods that should be executed.
	 * 
	 * @param afters
	 *            After-Methods
	 */
	public void setAfters(final List<FrameworkMethod> afters) {
		this.afters = afters;

	}
}