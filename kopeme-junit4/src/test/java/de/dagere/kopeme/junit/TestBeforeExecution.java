package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassMeasurement;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeTestRule;
import de.dagere.kopeme.junit.exampletests.rules.ExampleNoBeforeTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleNonMeasuringBefore;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import jakarta.xml.bind.JAXBException;

/**
 * Test for checking the behaviour of before and after for all runners (rule and junit runner).
 * 
 * @author Dan Häberlein
 *
 */
@RunWith(Parameterized.class)
public class TestBeforeExecution {

   public static final long TO_MILLISECONDS = 1000 * 1000;

   private static final String TEST_NAME = "spendTime";

   @Parameters(name = "{0}")
   public static Iterable<Object[]> parameters() {
      return Arrays.asList(new Object[][] {
            { ExampleBeforeTestRule.class, TEST_NAME },
            { ExampleBeforeClassTest.class, TEST_NAME },
            { ExampleNoBeforeTest.class, TEST_NAME },
            { ExampleNonMeasuringBefore.class, TEST_NAME },
            { ExampleBeforeClassMeasurement.class, TEST_NAME }
      });
   }

   @Parameter(0)
   public Class<?> junitTestClass;

   @Parameter(1)
   public String testname;

   public static Logger LOG = LogManager.getLogger(TestJUnitRuleExecutions.class);

   @BeforeClass
   public static void cleanResult() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testBefore() throws JAXBException {
      final JUnitCore jc = new JUnitCore();
      final Result result = jc.run(junitTestClass);
      for (final Failure failure : result.getFailures()) {
         System.out.println("A failure occured");
         System.out.println(failure.toString());
      }
      final String canonicalName = junitTestClass.getCanonicalName();
      final File resultFile = TestUtils.jsonFileForKoPeMeTest(canonicalName, testname);
      LOG.debug("Searching: {} Existing: {}", resultFile.getAbsolutePath(), resultFile.exists());
      MatcherAssert.assertThat(resultFile.exists(), Matchers.equalTo(true));
      final double time = getTimeResult(resultFile, testname);
      /*
       * Executiontimes vary between 100 and 130 ms. Because Thread.sleep is sometimes slightly inaccurate, there is a tolerance. Since threads will tend to oversleep rather than
       * undersleep, there is more room up.
       */
      if (!System.getProperty("os.name").startsWith("Mac")) {
         MatcherAssert.assertThat("Test error in " + canonicalName, time, Matchers.lessThan(150d * TO_MILLISECONDS));
      }
      MatcherAssert.assertThat("Test error in " + canonicalName, time, Matchers.greaterThan(99d * TO_MILLISECONDS));
   }

   public static double getTimeResult(final File measurementFile, final String methodName) throws JAXBException {
      final DatacollectorResult collectorData = new JSONDataLoader(measurementFile).getData(TimeDataCollector.class.getCanonicalName());
      Assert.assertNotNull(collectorData);
      final double time = collectorData.getResults().get(0).getValue();
      return time;
   }

}
