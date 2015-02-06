package de.dagere.kopeme.junit.ruletests;

import java.io.File;
import java.io.IOException;
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
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleNoBeforeTest;

public class TestBeforeExecution {
	public static Logger log = LogManager.getLogger(TestJUnitRuleExecutions.class);

	@BeforeClass
	public static void cleanResult() {
		try {
			Runtime.getRuntime().exec("rm performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeTest.yaml.spendTime.yaml");
			Runtime.getRuntime().exec("rm performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassTest.yaml.spendTime.yaml");
			Runtime.getRuntime().exec("rm performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleNoBeforeTest.yaml.spendTime.yaml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testBefore() throws JAXBException {
		JUnitCore jc = new JUnitCore();
		Result result = jc.run(ExampleBeforeTest.class);
		for (Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}
		File f = new File("performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeTest.yaml.spendTime.yaml");
		Assert.assertThat(f.exists(), Matchers.equalTo(true));
		Integer time = getTimeResult(f);
		Assert.assertThat(time, Matchers.lessThan(250 * 1000));
		// TestcaseType kd.getTestcases().getTestca

	}

	private Integer getTimeResult(File f) throws JAXBException {
		Map<String, Map<Date, Long>> collectorData = new XMLDataLoader(f).getData(TimeDataCollector.class.getCanonicalName());
		Map<Date, Long> data = collectorData.get("spendTime");
		Assert.assertNotNull(data);
		Integer time = data.entrySet().iterator().next().getValue().intValue();
		return time;
	}

	@Test
	public void testBeforeClass() throws JAXBException {
		JUnitCore jc = new JUnitCore();
		Result result = jc.run(ExampleBeforeClassTest.class);
		for (Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}

		File f = new File("performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleBeforeClassTest.yaml.spendTime.yaml");
		Assert.assertThat(f.exists(), Matchers.equalTo(true));
		Integer time = getTimeResult(f);
		Assert.assertThat(time, Matchers.lessThan(150 * 1000));
	}

	@Test
	public void testNoBefore() throws JAXBException {
		JUnitCore jc = new JUnitCore();
		Result result = jc.run(ExampleNoBeforeTest.class);

		for (Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}

		File f = new File("performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleNoBeforeTest.yaml.spendTime.yaml");
		Assert.assertThat(f.exists(), Matchers.equalTo(true));
		Integer time = getTimeResult(f);
		Assert.assertThat(time, Matchers.lessThan(150 * 1000));
	}
}
