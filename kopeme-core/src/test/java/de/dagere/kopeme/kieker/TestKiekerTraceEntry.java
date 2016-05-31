package de.dagere.kopeme.kieker;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestKiekerTraceEntry {

	@Test
	public void testFieldOrder() throws Exception {
		String[] testable = KiekerTraceEntry.getFieldDescription();
		assertFalse(Arrays.asList(testable).contains("FIELDS"));
		assertEquals(10, testable.length);
	}
}
