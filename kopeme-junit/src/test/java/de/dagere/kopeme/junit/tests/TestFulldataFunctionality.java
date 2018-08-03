package de.dagere.kopeme.junit.tests;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;

public class TestFulldataFunctionality {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@BeforeClass
	public static void cleanResult() throws IOException {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}
	
	@Test
	public void testNoFullWriting() {
		final JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		jc.run(JUnitAdditionTest.class);
		final File f = TestUtils.xmlFileForKoPeMeTest(JUnitAdditionTest.class.getCanonicalName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			final TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (final Datacollector dc : testcase.getDatacollector()) {
				for (final Result r : dc.getResult()) {
					final Fulldata fd = r.getFulldata();
					if (fd != null) {
						Assert.assertEquals(0, fd.getValue().size());
					}
				}
			}
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

		f.delete();
	}

	@Test
	public void testFullWriting() {
		final JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTestFullData.class);
		final File f = TestUtils.xmlFileForKoPeMeTest(JUnitAdditionTestFullData.class.getCanonicalName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			final TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (final Datacollector dc : testcase.getDatacollector()) {
				for (final Result r : dc.getResult()) {
					final Fulldata fd = r.getFulldata();
					Assert.assertNotNull(fd);
					Assert.assertThat(fd.getValue().size(), Matchers.greaterThan(0));
				}
			}
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

		f.delete();
	}

}
