package de.dagere.kopeme.junit3.example.tests;

import java.io.File;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result.Fulldata;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;

public class TestFulldataFunctionality extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}
	
	public void testFullWriting() {
		TestRunner.run(JUnitAdditionTestFullData.class);
		File f = TestUtils.xmlfileForKoPeMeRuleTest(JUnitAdditionTestFullData.class.getName(), TestUtils.TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

		XMLDataLoader xdl;
		try {
			xdl = new XMLDataLoader(f);
			TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (Datacollector dc : testcase.getDatacollector()) {
				for (Result r : dc.getResult()) {
					Fulldata fd = r.getFulldata();
					if (fd == null) {
						Assert.fail();
					} else {
						Assert.assertTrue(fd.getValue().size() > 0);
					}
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
