package de.dagere.kopeme.junit3.example.tests;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestOnlyTime;

public class TestFileWriting extends TestCase {

	public void testOnlyTimeWriting() {
		TestRunner.run(JUnitAdditionTestOnlyTime.class);

		String name = JUnitAdditionTestOnlyTime.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());

		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			for (TestcaseType tct : xdl.getFullData().getTestcases().getTestcase()) {
				List<Datacollector> dcl = tct.getDatacollector();
				Assert.assertEquals(dcl.size(), 1);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// f.delete();
	}

	public void testNormalWriting() {
		TestRunner.run(JUnitAdditionTest.class);

		String name = JUnitAdditionTest.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());

		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			for (TestcaseType tct : xdl.getFullData().getTestcases().getTestcase()) {
				List<Datacollector> dcl = tct.getDatacollector();
				Assert.assertEquals(dcl.size(), 3);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// f.delete();
	}

	public void testDoubleWriting() {
		TestRunner.run(JUnitAdditionTest.class);
		TestRunner.run(JUnitAdditionTest.class);

		String name = JUnitAdditionTest.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		// f.delete();
	}
}
