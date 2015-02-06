package de.dagere.kopeme.junit.testrunner;

/**
 * Same as Fail from JUnit, but extending PerformanceJUnitStatement and therefore beeing usable in a PerformanceRunner
 * 
 * @author reichelt
 *
 */
public class PerformanceFail extends PerformanceJUnitStatement {
	private final Throwable fError;

	public PerformanceFail(Throwable e) {
		super(null, null);
		fError = e;
	}

	@Override
	public void evaluate() throws Throwable {
		throw fError;
	}
}
