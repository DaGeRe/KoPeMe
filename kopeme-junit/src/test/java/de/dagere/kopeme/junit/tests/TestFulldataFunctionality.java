package de.dagere.kopeme.junit.tests;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result.Fulldata;
import de.dagere.kopeme.junit.exampletests.runner.ExampleBeforeTestRunner;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;
import de.dagere.kopeme.junit.ruletests.TestBeforeExecution;

public class TestFulldataFunctionality {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testNoFullWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);

		jc.run(JUnitAdditionTest.class);

		String name = PerformanceTestUtils.PERFORMANCEFOLDER + File.separator + JUnitAdditionTest.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (Datacollector dc : testcase.getDatacollector()) {
				for (Result r : dc.getResult()) {
					Fulldata fd = r.getFulldata();
					if (fd == null) {
						Assert.assertTrue(true);
					} else {
						Assert.assertEquals(0, fd.getValue().size());
					}
				}
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f.delete();
	}

	@Test
	public void testFullWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTestFullData.class);

		// jc.run(JUnitAdditionTestFullData.class);

		String name = PerformanceTestUtils.PERFORMANCEFOLDER + "/" + JUnitAdditionTestFullData.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (Datacollector dc : testcase.getDatacollector()) {
				for (Result r : dc.getResult()) {
					Fulldata fd = r.getFulldata();
					Assert.assertNotNull(fd);
					Assert.assertThat(fd.getValue().size(), Matchers.greaterThan(0));
				}
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f.delete();
	}

	@Test
	public void testBefore() throws JAXBException {
		JUnitCore jc = new JUnitCore();
		org.junit.runner.Result result = jc.run(ExampleBeforeTestRunner.class);
		for (Failure failure : result.getFailures())
		{
			System.out.println(failure.toString());
		}
		File f = new File("performanceresults/de.dagere.kopeme.junit.exampletests.runner.ExampleBeforeTestRunner.testMethod.yaml");
		Assert.assertThat(f.exists(), Matchers.equalTo(true));
		Integer time = TestBeforeExecution.getTimeResult(f, "testMethod");
		Assert.assertThat(time, Matchers.lessThan(150 * 1000));
		// TestcaseType kd.getTestcases().getTestca

	}

}
