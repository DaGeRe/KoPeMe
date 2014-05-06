package de.dagere.kopeme.testrunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.Theories.TheoryAnchor;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import de.dagere.kopeme.TestExecution;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.paralleltests.ParallelPerformanceTest;
import de.dagere.kopeme.paralleltests.ParallelTestExecution;

/**
 * Runs a Performance Test with JUnit. The method which should be tested has to
 * got the parameter TestResult. This does not work without another runner, e.g.
 * the TheorieRunner. An alternative implementation, e.g. via Rules, which would
 * make it possible to include Theories, is not possible, because one needs to
 * change the signature of test methods to get KoPeMe-Tests running.
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunnerJUnit extends BlockJUnit4ClassRunner {

	private Class klasse;

	public PerformanceTestRunnerJUnit(Class<?> klasse) throws InitializationError {
		super(klasse);
		this.klasse = klasse;
	}
	
	@Override
	public void run(final RunNotifier notifier) {
		System.out.println("PerformanceTestRunnerJUnit.run: " + System.currentTimeMillis());
		long start = System.nanoTime();
		PerformanceTestingClass ptc = (PerformanceTestingClass) klasse.getAnnotation(PerformanceTestingClass.class);
		if (ptc != null){
			Thread mainThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					PerformanceTestRunnerJUnit.super.run(notifier);
				}
			});
			System.out.println("Timeout: " + ptc.overallTimeout());
			mainThread.start();
			
			try {
				mainThread.join(ptc.overallTimeout());
				mainThread.interrupt();
			} catch (InterruptedException e) {
				System.out.println("Zeit: " + (System.nanoTime() - start) / 10E5);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("PerformanceTestRunnerJUnit.run(2): " + System.currentTimeMillis());
			super.run(notifier);
		}
		System.out.println("PerformanceTestRunnerJUnit.run(E): " + System.currentTimeMillis());
		
	}
	
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		System.out.println("PerformanceTestRunnerJUnit.runChild: " + System.currentTimeMillis());
		PerformanceTest a = method.getAnnotation(PerformanceTest.class);
		
		if (a != null)
			super.runChild(method, notifier);
		else {
			Description testBeschreibung = Description.createTestDescription(this.getTestClass().getJavaClass(),
				method.getName());
			notifier.fireTestIgnored(testBeschreibung);
		}
		System.out.println("PerformanceTestRunnerJUnit.runChild(2): " + System.currentTimeMillis());
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors) {
		for (FrameworkMethod each : computeTestMethods()) {
			if (each.getMethod().getParameterTypes().length > 1) {
				errors.add(new Exception("Method " + each.getName()
					+ " is supposed to have one or zero parameters, who's type is TestResult"));
			} else {
				if (each.getMethod().getParameterTypes().length == 1 && 
					each.getMethod().getParameterTypes()[0] != TestResult.class) {
					errors.add(new Exception("Method " + each.getName() + " has wrong parameter Type: "
						+ each.getMethod().getParameterTypes()[0]));
				}
			}
		}
	}

	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new PerformanceExecutionStatement(method, test);
	}

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
			te.runTest();
		}
	}
}
