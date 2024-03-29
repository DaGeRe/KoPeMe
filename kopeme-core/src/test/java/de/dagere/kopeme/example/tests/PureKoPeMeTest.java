package de.dagere.kopeme.example.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.FolderProvider;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.exampletests.pure.ExamplePurePerformanceTests;
import de.dagere.kopeme.exampletests.pure.TestTimeTest;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerKoPeMe;

public class PureKoPeMeTest {

   private static final Logger log = LogManager.getLogger(PureKoPeMeTest.class);

   @BeforeClass
   public static void setupClass() throws IOException {
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
      final String filename = "simpleTest.json";
      log.info("Suche in: {}", folderName);
      final JSONDataLoader xdl = new JSONDataLoader(new File(folderName, filename));
      final Kopemedata kd = xdl.getFullData();
      List<DatacollectorResult> collectors = null;
      for (final TestMethod tct : kd.getMethods()) {
         if (tct.getMethod().contains("simpleTest")) {
            collectors = tct.getDatacollectorResults();
         }
      }
      Assert.assertNotNull(collectors);

      double timeConsumption = 0.0;
      for (final DatacollectorResult collector : collectors) {
         if (collector.getName().contains("TimeData")) {
            timeConsumption = collector.getResults().get(collector.getResults().size() - 1).getValue();
         }
      }
      Assert.assertNotEquals(timeConsumption, 0.0);

      final long milisecondTime = (long) ((timeConsumption * 40l) / (1000l * 1000l));

      MatcherAssert.assertThat(milisecondTime, Matchers.lessThan(duration));
   }
}
