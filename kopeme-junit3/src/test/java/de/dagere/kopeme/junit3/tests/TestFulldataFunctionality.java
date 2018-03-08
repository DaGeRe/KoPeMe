package de.dagere.kopeme.junit3.tests;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result.Fulldata;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;
import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class TestFulldataFunctionality extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}
	
	public void testFullWriting() {
		// If this is not added, a strange classpath error occurs
		new SummaryStatistics().addValue(0.0);
		TestRunner.run(JUnitAdditionTestFullData.class);
		final File file = TestUtils.xmlFileForKoPeMeTest(JUnitAdditionTestFullData.class.getName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("File " + file + " should exist.", file.exists());

		try {
			final XMLDataLoader xdl = new XMLDataLoader(file);
			final TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (final Datacollector dc : testcase.getDatacollector()) {
				for (final Result r : dc.getResult()) {
					final Fulldata fd = r.getFulldata();
					if (fd == null) {
						Assert.fail();
					} else {
						Assert.assertTrue(fd.getValue().size() > 0);
					}
				}
			}
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

	}

}
