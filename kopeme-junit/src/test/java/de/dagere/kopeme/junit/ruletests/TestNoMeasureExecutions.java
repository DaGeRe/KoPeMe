package de.dagere.kopeme.junit.ruletests;

import java.io.File;
import java.io.IOException;

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

import de.dagere.kopeme.junit.exampletests.rules.ExampleNonMeasuringBefore;

public class TestNoMeasureExecutions {
	public static Logger log = LogManager.getLogger(TestJUnitRuleExecutions.class);

	@BeforeClass
	public static void cleanResult() {
		try {
			Runtime.getRuntime().exec("rm performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleNonMeasuringBefore.yaml.spendTime.yaml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testBefore() throws JAXBException {
		JUnitCore jc = new JUnitCore();
		Result result = jc.run(ExampleNonMeasuringBefore.class);
		for (Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}
		File f = new File("performanceresults/de.dagere.kopeme.junit.exampletests.rules.ExampleNonMeasuringBefore.yaml.spendTime.yaml");
		Assert.assertThat(f.exists(), Matchers.equalTo(true));
		Integer time = TestBeforeExecution.getTimeResult(f, "spendTime");
		Assert.assertThat(time, Matchers.lessThan(150 * 1000));
	}
}
