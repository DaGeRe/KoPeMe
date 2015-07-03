package de.dagere.kopeme.exampletests.pure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import de.dagere.kopeme.Checker;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.CPUUsageCollector;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;

public class ExamplePurePerformanceTests {

	// @PerformanceTest(executionTimes=5, warmupExecutions=2,
	// assertions={@Assertion(collectorname="de.kopeme.datacollection.TimeDataCollector",
	// maxvalue=1750)} )
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

	@PerformanceTest(warmupExecutions = 3, executionTimes = 10, assertions = { @Assertion(collectorname = "de.kopeme.datacollection.TimeDataCollector", maxvalue = 1750l) }, minEarlyStopExecutions = 15, deviations = {
			@MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 0.1),
			@MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.RAMUsageCollector", maxvalue = 0.1),
			@MaximalRelativeStandardDeviation(collectorname = "de.dagere.kopeme.datacollection.CPUUsageCollector", maxvalue = 0.4) })
	public void simpleDeviationTest() {
		System.out.println("This is a very simple test");
		int i = 10000;
		List<int[]> list = new LinkedList<>();
		for (int j = 0; j < 100000; j++) {
			i -= j;
			int[] array = new int[100];
			list.add(array);
		}
		System.out.println("Test finished: " + i);
	}

	// @PerformanceTest(warmupExecutions = 3, executionTimes = 20, assertions =
	// { @Assertion(collectorname =
	// "de.kopeme.datacollection.TimeDataCollector", maxvalue = 1750) },
	// minEarlyStopExecutions = 15, deviations = {
	// @MaximalRelativeStandardDeviation(collectorname =
	// "de.kopeme.datacollection.TimeDataCollector", maxvalue = 0.1),
	// @MaximalRelativeStandardDeviation(collectorname =
	// "de.kopeme.datacollection.RAMUsageCollector", maxvalue = 0.1),
	// @MaximalRelativeStandardDeviation(collectorname =
	// "de.kopeme.datacollection.CPUUsageCollector", maxvalue = 0.4) })
	// public void complexTest(TestResult tr) {
	// tr.setMeasureSummarizer("de.kopeme.datacollection.RAMUsageCollector",
	// new MedianSummarizer());
	// tr.startCollection();
	// int i = 10000;
	// List<int[]> list = new LinkedList<>();
	// for (int j = 0; j < 100000; j++) {
	// i -= j;
	// int[] array = new int[100];
	// list.add(array);
	// array[0] = i;
	// }
	//
	// tr.stopCollection();
	//
	// tr.addValue("Count", (int) (1000 + Math.random() * 100));
	//
	// tr.setChecker(new Checker() {
	//
	// @Override
	// public void checkValues(TestResult tr) {
	// System.out.println("Prüfe komplexe Performanz");
	// String CPUUSage = CPUUsageCollector.class.getName();
	// MatcherAssert.assertThat(tr.getValue(CPUUSage),
	// Matchers.greaterThan(10L));
	// MatcherAssert.assertThat(tr.getValue(CPUUSage),
	// Matchers.greaterThan((long) (tr.getLastRunsAverage(
	// CPUUSage, 5) * 0.60)));
	// MatcherAssert.assertThat(tr.getValue(TimeDataCollector.class
	// .getName()), Matchers.lessThan((long) (tr
	// .getLastRunsAverage(TimeDataCollector.class.getName(),
	// 5) * 1.30)));
	// }
	// });
	// }

	// @PerformanceTest(executionTimes=5, warmupExecutions=2,
	// assertions={@Assertion(collectorname="de.kopeme.datacollection.TimeDataCollector",
	// maxvalue=1750)} )
	public void AtestMoebelkauf(final TestResult tr) {

		tr.startCollection();
		int anzahl = 1000 + (int) (Math.random() * 10);
		anzahl = 500;
		for (int i = 0; i < anzahl; i++) {
			try {
				File f = new File("asd" + i + ".dat");
				FileOutputStream fos;
				fos = new FileOutputStream(f);
				for (int j = 0; j < 1024; j++)
					fos.write(i);
				fos.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
		}

		tr.stopCollection();

		tr.addValue("Anzahl", (int) (1000 + Math.random() * 100));

		tr.setChecker(new Checker() {

			@Override
			public void checkValues(final TestResult tr) {
				String CPUUSage = CPUUsageCollector.class.getName();
				MatcherAssert.assertThat(tr.getValue(CPUUSage), Matchers.greaterThan(10L));
				MatcherAssert.assertThat(tr.getValue(CPUUSage), Matchers.greaterThan((long) (tr.getHistoricalResults().getLastRunsAverage(CPUUSage, 5) * 0.80)));
				MatcherAssert.assertThat(tr.getValue(TimeDataCollector.class.getName()),
						Matchers.lessThan((long) (tr.getHistoricalResults().getLastRunsAverage(TimeDataCollector.class.getName(), 5) * 1.30)));
			}
		});
	}

}

// TODO: So was wie "AdditionalCollectorSet"