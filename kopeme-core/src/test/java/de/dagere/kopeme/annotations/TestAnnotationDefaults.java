package de.dagere.kopeme.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TestAnnotationDefaults {

	@Test
	public void testGetDefaultFromKoPeMeAnnotation() throws Exception {
		PerformanceTest instance = AnnotationDefaults.of(PerformanceTest.class);
		assertEquals(PerformanceTest.class.getMethod("timeout").getDefaultValue(), instance.timeout());
		assertFalse(instance.useKieker());
	}
	
}
