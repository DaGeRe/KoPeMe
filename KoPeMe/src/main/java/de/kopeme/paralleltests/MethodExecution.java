package de.kopeme.paralleltests;

import de.kopeme.datacollection.TestResult;

public interface MethodExecution {
	public void executeMethod(TestResult tr);
	public int getCallCount();
}
