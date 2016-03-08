package de.dagere.kopeme.annotations;


import org.junit.Assert;
import org.junit.Test;

public class TestAnnotationDefaults {

	@Test
	public void testGetDefaultFromKoPeMeAnnotation() throws Exception {
		final PerformanceTest instance = AnnotationDefaults.of(PerformanceTest.class);
		Assert.assertEquals(PerformanceTest.class.getMethod("timeout").getDefaultValue(), instance.timeout());
		Assert.assertTrue(instance.useKieker());
	}
	
}
