package de.dagere.kopeme.junit.rule;

import java.lang.reflect.Method;

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

			return new KoPeMeStandardRuleStatement(runnables, testMethod, testClass.getName() + ".yaml");
		} else {
			return stmt;
		}

	}
}
