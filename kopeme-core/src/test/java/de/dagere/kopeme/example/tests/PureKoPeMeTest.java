package de.dagere.kopeme.example.tests;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

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

	@Test
	public void testExecutionTimeMeasurement() throws Throwable {
		FolderProvider.getInstance().setKopemeDefaultFolder("target/test-classes/.KoPeMe");
		PerformanceTestRunnerKoPeMe.main(new String[] { ExamplePurePerformanceTests.class.getName() });
		long start = System.currentTimeMillis();
		PerformanceTestRunnerKoPeMe.main(new String[] { TestTimeTest.class.getName() });
		long duration = System.currentTimeMillis() - start;
		log.debug("Overall Duration: " + duration);
		String className = TestTimeTest.class.getCanonicalName();
		String folderName = FolderProvider.getInstance().getFolderFor(className);
		String filename = className + ".simpleTest.xml";
		XMLDataLoader xdl = new XMLDataLoader(folderName + File.separator + filename);
		Kopemedata kd = xdl.getFullData();
		List<Datacollector> collector = null;
		for (TestcaseType tct : kd.getTestcases().getTestcase()) {
			if (tct.getName().contains("simpleTest")) {
				collector = tct.getDatacollector();
			}
		}
		Assert.assertNotNull(collector);

		double timeConsumption = 0.0;
		for (Datacollector c : collector) {
			if (c.getName().contains("TimeData")) {
				timeConsumption = Double.parseDouble(c.getResult().get(c.getResult().size() - 1).getValue());
			}
		}
		Assert.assertNotEquals(timeConsumption, 0.0);

		int milisecondTime = (int) ((timeConsumption * 40) / 1000);

		Assert.assertThat((long) milisecondTime, Matchers.lessThan(duration));
	}
}
