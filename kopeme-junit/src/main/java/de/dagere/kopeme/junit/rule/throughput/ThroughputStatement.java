package de.dagere.kopeme.junit.rule.throughput;

import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class ThroughputStatement extends KoPeMeBasicStatement {

	private static final Logger log = LogManager.getLogger(ThroughputStatement.class);

	private final int stepsize, maxsize;
	private final IOberserveExecutionTimes oberserver;

	public ThroughputStatement(TestRunnables runnables, Method method, String filename, int stepsize, int maxsize, IOberserveExecutionTimes oberserver) {
		super(runnables, method, filename);
		this.stepsize = stepsize;
		this.maxsize = maxsize;
		this.oberserver = oberserver;
	}

	@Override
	public void evaluate() throws Throwable {
		String methodString = method.getClass().getName() + "." + method.getName();
		runWarmup(methodString);

		while (executionTimes < maxsize) {
			TestResult tr = new TestResult(method.getName(), executionTimes);

			if (!checkCollectorValidity(tr)) {
				log.warn("Not all Collectors are valid!");
			}

			try {
				runMainExecution(tr);
			} catch (AssertionFailedError t) {
				tr.finalizeCollection();
				PerformanceTestUtils.saveData(method.getName(), tr, true, false, filename, true);
				throw t;
			} catch (Throwable t) {
				tr.finalizeCollection();
				PerformanceTestUtils.saveData(method.getName(), tr, false, true, filename, true);
				throw t;
			}
			tr.finalizeCollection();

			executionTimes += stepsize;
			oberserver.setSize(executionTimes);
		}

		// PerformanceTestUtils.saveData(method.getName(), tr, false, false, filename, true);
	}
}
