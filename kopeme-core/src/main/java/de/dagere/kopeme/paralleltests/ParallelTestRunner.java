package de.dagere.kopeme.paralleltests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.dagere.kopeme.PerformanceTestRunner;
import de.dagere.kopeme.datacollection.ParallelTestResult;
import de.dagere.kopeme.datacollection.TestResult;

/**
 * Test runner for parallel tests.
 * 
 * @author reichelt
 *
 */
public class ParallelTestRunner extends PerformanceTestRunner {

	/**
	 * Initializes the parallel test runner.
	 * 
	 * @param klasse Class that should be tested
	 * @param instance Instance that should be tested
	 * @param method Method that should be tested
	 */
	public ParallelTestRunner(final Class<?> klasse, final Object instance, final Method method) {
		super(klasse, instance, method);

	}

	/**
	 * Creates a thread for parallel exexution
	 * 
	 * @param me
	 * @param tr
	 * @return
	 */
	private Thread createThread(final MethodExecution me, final TestResult tr)
	{
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				me.executeMethod(tr);// Wird am selben Objekt ausgeführt... muss aber ja Thread-sicher sein?!
			}
		});
		return t;
	}

	/**
	 * Execute parallel tests and waits for completion.
	 * 
	 * @param methodexecutions Test executions
	 * @param tr Test result that should be saved
	 */
	public void executeOnce(final List<MethodExecution> methodexecutions, final ParallelTestResult tr) {
		for (final MethodExecution me : methodexecutions)
		{
			Thread threads[] = new Thread[me.getCallCount()];
			for (int i = 0; i < me.getCallCount(); i++)
			{
				threads[i] = new Thread(new Runnable() {
					@Override
					public void run() {
						me.executeMethod(tr);// Wird am selben Objekt ausgeführt... muss aber ja Thread-sicher sein?!
					}
				});
			}
			for (int i = 0; i < me.getCallCount(); i++)
			{
				threads[i].start();
			}
			for (int i = 0; i < me.getCallCount(); i++)
			{
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void evaluate() {
		try {
			ParallelTestResult tr = new ParallelTestResult(method.getName(), warmupExecutions);
			Object[] params = { tr };

			method.invoke(instanz, params);

			List<MethodExecution> mes = tr.getParallelTests();

			tr.startCollection();
			executeOnce(mes, tr);// TODO Warmup, Parallel..
			tr.stopCollection();

			tr.finalizeCollection();
			tr.checkValues();

			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
