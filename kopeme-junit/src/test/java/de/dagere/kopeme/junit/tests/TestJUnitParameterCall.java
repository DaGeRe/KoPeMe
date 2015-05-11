package de.dagere.kopeme.junit.tests;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.junit.exampletests.runner.ExampleJUnitTests;

public class TestJUnitParameterCall {

	@Test
	public void testParameterCall() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleJUnitTests.class);
	}
}
