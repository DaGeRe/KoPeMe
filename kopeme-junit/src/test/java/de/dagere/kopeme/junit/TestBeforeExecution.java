package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeTestRule;
import de.dagere.kopeme.junit.exampletests.rules.ExampleNoBeforeTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleNonMeasuringBefore;
import de.dagere.kopeme.junit.exampletests.runner.ExampleBeforeTestRunner;

/**
 * Test for checking the behaviour of before and after for all runners (rule and junit runner).
 * 
 * @author Dan Häberlein
 *
 */
@RunWith(Parameterized.class)
public class TestBeforeExecution {
	
	private static final String TEST_NAME = "spendTime";
	
	@Parameters(name = "{0}")
	public static Iterable<Object[]> parameters(){
		return Arrays.asList(new Object[][]{
			{ExampleBeforeTestRule.class, TEST_NAME},
			{ExampleBeforeClassTest.class, TEST_NAME},
			{ExampleNoBeforeTest.class, TEST_NAME},
			{ExampleNonMeasuringBefore.class, TEST_NAME}
		});
	}
//	,
//	{ExampleBeforeTestRunner.class, "testMethod"}
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
		for (final Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}
		final String canonicalName = junitTestClass.getCanonicalName();
		final File resultFile = TestUtils.xmlFileForKoPeMeTest(canonicalName, testname);
		LOG.debug("Suche: {} Existiert: {}", resultFile.getAbsolutePath(), resultFile.exists());
		Assert.assertThat(resultFile.exists(), Matchers.equalTo(true));
		final Integer time = getTimeResult(resultFile, testname);
		Assert.assertThat("Testfehler in " + canonicalName, time, Matchers.lessThan(150 * 1000));
		Assert.assertThat("Testfehler in " + canonicalName, time, Matchers.greaterThan(100 * 1000));
	}
	
	public static Integer getTimeResult(final File f, final String methodName) throws JAXBException {
		final Map<String, Map<Date, Long>> collectorData = new XMLDataLoader(f).getData(TimeDataCollector.class.getCanonicalName());
		final Map<Date, Long> data = collectorData.get(methodName);
		Assert.assertNotNull(data);
		final Integer time = data.entrySet().iterator().next().getValue().intValue();
		return time;
	}

}
