package de.dagere.kopeme.junit5.exampletests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

@ExtendWith(KoPeMeExtension.class)
public class ExampleExtensionTestThrowingOOM {

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
	public void testNormal() {
		System.out.println("Normal Execution");
		//throw new OutOfMemoryError("OOM Error");
		String[] array = new String[100000 * 100000];
	}
}
