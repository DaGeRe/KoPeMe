package de.dagere.kopeme.junit.exampletests.rules;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class SimpleNotUsingRuleTest {

	@Rule
	public TestRule rule = new TestRule() {

		@Override
		public Statement apply(Statement base, Description description) {
			System.out.println("Changing Statement by TestRule");
			return base;
		}
	};

	@Rule
	public MethodRule rule2 = new MethodRule() {

		@Override
		public Statement apply(Statement base, FrameworkMethod method, Object target) {
			System.out.println("Exec!");
			return base;
		}
	};

	@BeforeClass
	public static void beforeTest() throws InterruptedException {
		System.out.println("Before");
		Thread.sleep(100);
	}

	@Test
	public void spendTime() throws InterruptedException {
		System.out.println("SpendTime");
		Thread.sleep(100);
	}
}
