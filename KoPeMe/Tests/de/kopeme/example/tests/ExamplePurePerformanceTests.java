package de.kopeme.example.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import de.kopeme.Assertion;
import de.kopeme.Checker;
import de.kopeme.PerformanceTest;
import de.kopeme.datacollection.CPUUsageCollector;
import de.kopeme.datacollection.DataCollectorList;
import de.kopeme.datacollection.TestResult;
import de.kopeme.datacollection.TimeDataCollector;

public class ExamplePurePerformanceTests {

//	@PerformanceTest(executionTimes=5, warmupExecutions=2,
//		assertions={@Assertion(collectorname="de.kopeme.datacollection.TimeDataCollector", maxvalue=1750)} )
	public void simpleTest(){
		System.out.println("This is a very simple test");
		int i = 10000;
		for (int j = 0; j < 9000; j++ )
		{
			i -= j;
			int[] array = new int[100];
		}
		System.out.println("Test finished");
	}
	
	@PerformanceTest(maximalRelativeStandardDeviation=0.10f, warmupExecutions=2, executionTimes=1000,
		assertions={@Assertion(collectorname="de.kopeme.datacollection.TimeDataCollector", maxvalue=1750)} )
	public void simpleDeviationTest(){
		System.out.println("This is a very simple test");
		int i = 10000;
		for (int j = 0; j < 19000; j++ )
		{
			i -= j;
			int[] array = new int[100];
		}
		System.out.println("Test finished");
	}
	
//	@PerformanceTest(executionTimes=5, warmupExecutions=2 )
	public void complexTest(TestResult tr){
		tr.startCollection();
		int i = 10000;
		for (int j = 0; j < 9000; j++ )
		{
			i -= j;
			int[] array = new int[100];
		}

		tr.stopCollection();

		tr.addValue("Count", (int)(1000 + Math.random()*100));

		tr.setChecker(new Checker() {

			@Override
			public void checkValues(TestResult tr) {
				String CPUUSage = CPUUsageCollector.class.getName();
				MatcherAssert.assertThat(tr.getValue(CPUUSage),
						Matchers.greaterThan(10L));
				MatcherAssert.assertThat(tr.getValue(CPUUSage),
						Matchers.greaterThan((long) (tr.getLastRunsAverage(
								CPUUSage, 5) * 0.80)));
				MatcherAssert.assertThat(tr.getValue(TimeDataCollector.class
						.getName()), Matchers.lessThan((long) (tr
						.getLastRunsAverage(TimeDataCollector.class.getName(),
								5) * 1.30)));
			}
		});
	}
	
//	@PerformanceTest(executionTimes=5, warmupExecutions=2,
//			assertions={@Assertion(collectorname="de.kopeme.datacollection.TimeDataCollector", maxvalue=1750)} )
	public void testMoebelkauf(final TestResult tr) {
		
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

		tr.addValue("Anzahl", (int)(1000 + Math.random()*100));

		tr.setChecker(new Checker() {

			@Override
			public void checkValues(TestResult tr) {
				String CPUUSage = CPUUsageCollector.class.getName();
				MatcherAssert.assertThat(tr.getValue(CPUUSage),
						Matchers.greaterThan(10L));
				MatcherAssert.assertThat(tr.getValue(CPUUSage),
						Matchers.greaterThan((long) (tr.getLastRunsAverage(
								CPUUSage, 5) * 0.80)));
				MatcherAssert.assertThat(tr.getValue(TimeDataCollector.class
						.getName()), Matchers.lessThan((long) (tr
						.getLastRunsAverage(TimeDataCollector.class.getName(),
								5) * 1.30)));
			}
		});
	}

//	@PerformanceTest
//	public void testMoebelkauf2(final TestResult tr) {
//
//	}
}

// TODO: So was wie "AdditionalCollectorSet"