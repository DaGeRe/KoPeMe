package de.dagere.kopeme.example.tests;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.FolderProvider;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.exampletests.pure.ExamplePurePerformanceTests;
import de.dagere.kopeme.exampletests.pure.TestTimeTest;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerKoPeMe;

public class PureKoPeMeTest {

	private static final Logger log = LogManager.getLogger(PureKoPeMeTest.class);

	@BeforeClass
	public static void setupClass(){
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}
	
	@Test
	public void testPureKoPeMeExecution() throws Throwable {
		final String params[] = new String[] { ExamplePurePerformanceTests.class.getName() };
		PerformanceTestRunnerKoPeMe.main(params);
	}
	
	@Test
	public void testExecutionTimeMeasurement() throws Throwable {
		final long start = System.currentTimeMillis();
		PerformanceTestRunnerKoPeMe.main(new String[] { TestTimeTest.class.getName() });
		final long duration = System.currentTimeMillis() - start;
		log.debug("Overall Duration: " + duration);
		final String className = TestTimeTest.class.getCanonicalName();
		final String folderName = FolderProvider.getInstance().getFolderFor(className);
		final String filename = "simpleTest.xml";
		log.info("Suche in: {}", folderName);
		final XMLDataLoader xdl = new XMLDataLoader(new File(folderName, filename));
		final Kopemedata kd = xdl.getFullData();
		List<Datacollector> collectors = null; 
		for (final TestcaseType tct : kd.getTestcases().getTestcase()) {
			if (tct.getName().contains("simpleTest")) {
				collectors = tct.getDatacollector();
			}
		}
		Assert.assertNotNull(collectors);

		double timeConsumption = 0.0;
		for (final Datacollector collector : collectors) {
			if (collector.getName().contains("TimeData")) {
				timeConsumption = collector.getResult().get(collector.getResult().size() - 1).getValue();
			}
		}
		Assert.assertNotEquals(timeConsumption, 0.0);

		final int milisecondTime = (int) ((timeConsumption * 40) / 1000);

		Assert.assertThat((long) milisecondTime, Matchers.lessThan(duration));
	}
}
