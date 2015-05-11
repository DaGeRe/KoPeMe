package de.dagere.kopeme.exampletests.pure;

import de.dagere.kopeme.annotations.PerformanceTest;

public class TestTimeTest {

	@PerformanceTest(executionTimes = 20, warmupExecutions = 20)
	public void simpleTest() {
		System.out.println("This is a very simple test");
		int i = 10000;
		for (int j = 0; j < 9000; j++) {
			i -= j;
			int[] array = new int[100];
			array[0] = i;
		}
		System.out.println("Test finished");
	}
}

// TODO: So was wie "AdditionalCollectorSet"