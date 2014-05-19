package de.dagere.kopeme.paralleltests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.dagere.kopeme.TestExecution;
import de.dagere.kopeme.datacollection.TestResult;

public class ParallelTestExecution extends TestExecution {

	public ParallelTestExecution(Class<?> klasse, Object instance, Method method) {
		super(klasse, instance, method);

		ParallelPerformanceTest annotation = method
				.getAnnotation(ParallelPerformanceTest.class);
	}
	
	public Thread createThread(final MethodExecution me, final TestResult tr)
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				me.executeMethod(tr);//Wird am selben Objekt ausgeführt... muss aber ja Thread-sicher sein?!
			}
		});
		return t;
	}
	
	public void executeOnce(List<MethodExecution> mes, TestResult tr){
		for (MethodExecution me : mes)
		{
			Thread threads[] = new Thread[me.getCallCount()];
			for (int i = 0; i < me.getCallCount(); i++)
			{
				threads[i] = createThread(me, tr);
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
					// TODO Automatisch generierter Erfassungsblock
					e.printStackTrace();
				}
			}
		}
	}

	public void runTest() {
		try {
			TestResult tr = new TestResult(method.getName(), filename, warmupExecutions);
			Object[] params = { tr };

			method.invoke(instanz, params);
			
			List<MethodExecution> mes = tr.getParallelTests();
			
			tr.startCollection();
			executeOnce(mes, tr);//TODO Warmup, Parallel..
			tr.stopCollection();
			
			tr.finalizeCollection();
			tr.checkValues();

			if (!assertationvalues.isEmpty()) {
				tr.checkValues(assertationvalues);
			}
		} catch (IllegalArgumentException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
	}

}
