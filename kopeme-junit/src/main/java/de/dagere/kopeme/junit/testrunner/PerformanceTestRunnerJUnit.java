package de.dagere.kopeme.junit.testrunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private final static Logger log = LogManager.getLogger(PerformanceTestRunnerJUnit.class);

	private Class klasse;

	public PerformanceTestRunnerJUnit(Class<?> klasse) throws InitializationError {
		super(klasse);
		this.klasse = klasse;
	}

	@Override
	public void run(final RunNotifier notifier) {
		long start = System.nanoTime();
		PerformanceTestingClass ptc = (PerformanceTestingClass) klasse.getAnnotation(PerformanceTestingClass.class);
		if (ptc != null) {
			Thread mainThread = new Thread(new Runnable() {

				@Override
				public void run() {
					PerformanceTestRunnerJUnit.super.run(notifier);
				}
			});
			saveFullData = ptc.logFullData();
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

	// @Override
	// protected Statement methodInvoker(FrameworkMethod method, Object test) {
	// return new PerformanceJUnitStatement(method, test);
	// }

	@Override
	protected Statement methodBlock(final FrameworkMethod method) {

		final Statement callee = new Statement() {

			@Override
			public void evaluate() throws Throwable {
				log.debug("Evaluiere..");
				Statement oldStatement = PerformanceTestRunnerJUnit.super.methodBlock(method);
				oldStatement.evaluate();

			}
		};
		log.debug("Im methodBlock für " + method.getName());

		Method m = method.getMethod();
		initValues(m);

		Statement st = new Statement() {
			@Override
			public void evaluate() throws Throwable {
				final Thread mainThread = new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							runWarmup(callee);
							executeSimpleTest(callee);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				TimeBoundedExecution tbe = new TimeBoundedExecution(mainThread, timeout);
				tbe.execute();

			}
		};

		return st;
	}

	protected boolean saveFullData;
	protected Method method;
	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	private void initValues(Method method) {
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

	private TestResult executeSimpleTest(Statement callee) throws Throwable {
		int executions = 0;
		TestResult tr = new TestResult(method.getName(), executionTimes);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			log.warn("Not all Collectors are valid!");
		}
		try {
			executions = runMainExecution(tr, callee, true);
		} catch (Throwable t) {
			tr.finalizeCollection();
			PerformanceTestUtils.saveData(method.getName(), tr, executions, false, true, filename, saveFullData);
			throw t;
		}
		tr.finalizeCollection();
		PerformanceTestUtils.saveData(method.getName(), tr, executions, false, false, filename, saveFullData);

		tr.checkValues();
		return tr;
	}

	private int runMainExecution(TestResult tr, Statement callee, boolean simple) throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		// if (maximalRelativeStandardDeviation == 0.0f){
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
			if (simple)
				tr.startCollection();
			callee.evaluate();
			if (simple)
				tr.stopCollection();
			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			// for (Map.Entry<String, Double> entry :
			// maximalRelativeStandardDeviation
			// .entrySet()) {
			// log.debug("Entry: {} Aim: {} Value: {}", entry.getKey(),
			// entry.getValue(),
			// tr.getRelativeStandardDeviation(entry.getKey()));
			// }
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + executions);
		return executions;
	}

	private void runWarmup(Statement callee) throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
			callee.evaluate();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}
	}
}
