package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This Rule gives the possibility to test performance with a rule and without a testrunner; this makes it possible to use a different testrunner. Be aware that a rule-execution does measure the time
 * needed for @Before-Executions together with the main execution time, but not the @BeforeClass-Execution.
 * 
 * @author DaGeRe
 *
 */
public class KoPeMeRule implements TestRule {

	private static final Logger LOG = LogManager.getLogger(KoPeMeRule.class);

	private final Object testObject;

	public KoPeMeRule(final Object testObject) {
		this.testObject = testObject;
	}
	
	private KoPeMeStandardRuleStatement koPeMeStandardRuleStatement;

	@Override
	public Statement apply(final Statement stmt, final Description descr) {
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
			final TestRunnables runnables = new TestRunnables(new Runnable() {

				@Override
				public void run() {
					try {
						stmt.evaluate();
					} catch (final Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, testClass, testObject);

			koPeMeStandardRuleStatement = new KoPeMeStandardRuleStatement(runnables, testMethod, testClass.getName());
			return koPeMeStandardRuleStatement; 
		} else {
			return stmt;
		}
	}

	/**
	 * Changes the name of the result - can be used e.g. when a parameterized test is executed and the result should be saved with a differend method name.
	 */
	public void setMethodName(final String name) {
		koPeMeStandardRuleStatement.setMethodName(name);
	}
}
