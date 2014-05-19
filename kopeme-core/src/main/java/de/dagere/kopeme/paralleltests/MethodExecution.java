package de.dagere.kopeme.paralleltests;

import de.dagere.kopeme.datacollection.TestResult;

public interface MethodExecution {
	public void executeMethod(TestResult tr);
	public int getCallCount();
}
