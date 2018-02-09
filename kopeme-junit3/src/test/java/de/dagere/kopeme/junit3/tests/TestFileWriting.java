package de.dagere.kopeme.junit3.tests;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.junit.exampletests.runner.ExampleAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestOnlyTime;

public class TestFileWriting extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}

	public void testOnlyTimeWriting() {
		TestRunner.run(JUnitAdditionTestOnlyTime.class);

		File f = TestUtils.xmlFileForKoPeMeTest(JUnitAdditionTestOnlyTime.class.getName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			for (TestcaseType tct : xdl.getFullData().getTestcases().getTestcase()) {
				List<Datacollector> dcl = tct.getDatacollector();
				Assert.assertEquals(dcl.size(), 1);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void testNormalWriting() {
		TestRunner.run(ExampleAdditionTest.class);

		File f = TestUtils.xmlFileForKoPeMeTest(ExampleAdditionTest.class.getName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			for (TestcaseType tct : xdl.getFullData().getTestcases().getTestcase()) {
				List<Datacollector> dcl = tct.getDatacollector();
				Assert.assertEquals(dcl.size(), 3);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public void testDoubleWriting() {
		TestRunner.run(ExampleAdditionTest.class);
		TestRunner.run(ExampleAdditionTest.class);

		File f = TestUtils.xmlFileForKoPeMeTest(ExampleAdditionTest.class.getName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
	}
}
