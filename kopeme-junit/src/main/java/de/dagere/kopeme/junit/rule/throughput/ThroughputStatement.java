package de.dagere.kopeme.junit.rule.throughput;

import java.lang.reflect.Method;

import de.dagere.kopeme.junit.rule.KoPeMeBasicStatement;
import de.dagere.kopeme.junit.rule.TestRunnables;

public class ThroughputStatement extends KoPeMeBasicStatement {

	public ThroughputStatement(TestRunnables runnables, Method method, String filename) {
		super(runnables, method, filename);
	}

	@Override
	public void evaluate() throws Throwable {
		throw new RuntimeException("Not implemented yet");
	}

}
