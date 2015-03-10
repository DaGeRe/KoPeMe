package de.dagere.kopeme.junit.testrunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.TimeBoundedExecution;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.datacollection.TestResult;

/**
 * Runs a Performance Test with JUnit. The method which should be tested has to got the parameter TestResult. This does not work without another runner, e.g.
 * the TheorieRunner. An alternative implementation, e.g. via Rules, which would make it possible to include Theories, is not possible, because one needs to
 * change the signature of test methods to get KoPeMe-Tests running.
 * 
 * This test runner does not measure the time before and after are taking; but time rules take to execute are added to the overall-time of the method-execution
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunnerJUnit extends BlockJUnit4ClassRunner {

	private final static Logger log = LogManager.getLogger(PerformanceTestRunnerJUnit.class);

	private Class klasse;
	protected boolean saveFullData;
	protected FrameworkMethod method;
	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	public PerformanceTestRunnerJUnit(Class<?> klasse) throws InitializationError {
		super(klasse);
		this.klasse = klasse;
	}

	@Override
	public void run(final RunNotifier notifier) {
		long start = System.nanoTime();
		PerformanceTestingClass ptc = (PerformanceTestingClass) klasse.getAnnotation(PerformanceTestingClass.class);
		if (ptc != null) {
			final RunNotifier parallelNotifier = new RunNotifier();
			Thread mainThread = new Thread(new Runnable() {
				@Override
				public void run() {
					PerformanceTestRunnerJUnit.super.run(parallelNotifier);
				}
			});
			saveFullData = ptc.logFullData();
			log.info("Ausführung: " + klasse.getName() + " Class-Timeout: " + ptc.overallTimeout());
			mainThread.start();

			try {
				mainThread.join(ptc.overallTimeout());
				if (mainThread.isAlive()) {
					log.debug("Call interrupt because of class-timeout");
					mainThread.interrupt();
					log.debug("Firing..");
					// notifier.fireTestFailure(new Failure(getDescription(), new Exception("Test timed out because of class timeout")));
					EachTestNotifier testNotifier = new EachTestNotifier(notifier,
							getDescription());
					testNotifier.addFailure(new TimeoutException("Test timed out because of class timeout"));
				} else {
					// parallelNotifier.
				}

			} catch (InterruptedException e) {
				log.debug("Zeit: " + (System.nanoTime() - start) / 10E5);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			super.run(notifier);
		}
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		PerformanceTest a = method.getAnnotation(PerformanceTest.class);

		if (a != null)
			super.runChild(method, notifier);
		else {
			Description testBeschreibung = Description.createTestDescription(this.getTestClass().getJavaClass(), method.getName());
			notifier.fireTestIgnored(testBeschreibung);
		}
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors) {
		for (FrameworkMethod each : computeTestMethods()) {
			if (each.getMethod().getParameterTypes().length > 1) {
				errors.add(new Exception("Method " + each.getName() + " is supposed to have one or zero parameters, who's type is TestResult"));
			} else {
				if (each.getMethod().getParameterTypes().length == 1 && each.getMethod().getParameterTypes()[0] != TestResult.class) {
					errors.add(new Exception("Method " + each.getName() + " has wrong parameter Type: " + each.getMethod().getParameterTypes()[0]));
				}
			}
		}
	}

	private PerformanceJUnitStatement getStatement(final FrameworkMethod currentMethod) throws NoSuchMethodException, SecurityException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		Object test;
		try {
			test = new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new PerformanceFail(e);
		}
		log.debug("Statement: " + currentMethod.getName() + " " + method);

		Statement statement = methodInvoker(currentMethod, test);

		statement = possiblyExpectingExceptions(currentMethod, test, statement);
		statement = withPotentialTimeout(currentMethod, test, statement);
		Method method2 = BlockJUnit4ClassRunner.class.getDeclaredMethod("withRules", FrameworkMethod.class, Object.class, Statement.class);
		method2.setAccessible(true);

		statement = (Statement) method2.invoke(this, new Object[] { currentMethod, test, statement });
		PerformanceJUnitStatement perfStatement = new PerformanceJUnitStatement(statement, test);
		List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);
		List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);
		perfStatement.setBefores(befores);
		perfStatement.setAfters(afters);
		return perfStatement;
	}

	@Override
	protected Statement methodBlock(final FrameworkMethod currentMethod) {
		try {
			final PerformanceJUnitStatement callee = getStatement(currentMethod);

			log.trace("Im methodBlock für " + currentMethod.getName());

			initValues(currentMethod);

			final Statement st = new Statement() {
				@Override
				public void evaluate() throws Throwable {
					final Thread mainThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								runWarmup(callee);
								TestResult tr = executeSimpleTest(callee);
								tr.checkValues();
								if (!assertationvalues.isEmpty()) {
									log.info("Checking: " + assertationvalues.size());
									tr.checkValues(assertationvalues);
								}
							} catch (Exception e) {
								if (e instanceof RuntimeException) {
									throw (RuntimeException) e;
								}
								log.error("Catched Exception: {}", e.getLocalizedMessage());
								e.printStackTrace();
							} catch (Throwable t) {
								if (t instanceof Error)
									throw (Error) t;
								log.error("Unknown Type: " + t.getClass() + " " + t.getLocalizedMessage());
							}
						}
					});
					TimeBoundedExecution tbe = new TimeBoundedExecution(mainThread, timeout);
					tbe.execute();
					log.debug("Timebounded execution finished");
				}
			};
			return st;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private void initValues(FrameworkMethod method) {
		this.method = method;
		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);
		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}
		filename = klasse.getName();
	}

	private TestResult executeSimpleTest(PerformanceJUnitStatement callee) throws Throwable {
		TestResult tr = new TestResult(method.getName(), executionTimes);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			runMainExecution(tr, callee, true);
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, false, true, filename, saveFullData);
			throw t;
		}
		tr.finalizeCollection();
		PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, saveFullData);
		return tr;
	}

	private void runMainExecution(TestResult tr, PerformanceJUnitStatement callee, boolean simple) throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		// if (maximalRelativeStandardDeviation == 0.0f){
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			callee.preEvaluate();
			log.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
			if (simple)
				tr.startCollection();
			callee.evaluate();
			if (simple)
				tr.stopCollection();
			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			callee.postEvaluate();
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}

	private void runWarmup(PerformanceJUnitStatement callee) throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			callee.preEvaluate();
			log.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
			callee.evaluate();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
			callee.postEvaluate();
		}
	}
}
