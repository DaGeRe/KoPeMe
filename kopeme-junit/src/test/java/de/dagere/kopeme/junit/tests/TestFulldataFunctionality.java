package de.dagere.kopeme.junit.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datacollection.TimeDataCollectorNoGC;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullDataBig;

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
		try {
		   XMLDataLoader xdl = new XMLDataLoader(f);
			final TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
			for (final Datacollector dc : testcase.getDatacollector()) {
				for (final Result r : dc.getResult()) {
					final Fulldata fd = r.getFulldata();
					Assert.assertNotNull(fd);
					Assert.assertEquals(900, fd.getValue().size());
				}
			}
		} catch (final JAXBException e) {
			e.printStackTrace();
		}

		f.delete();
	}
	
	@Test
   public void testFullWritingSeparateFile() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitAdditionTestFullDataBig.class);
      final File expectedKoPemeXML = TestUtils.xmlFileForKoPeMeTest(JUnitAdditionTestFullDataBig.class.getCanonicalName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + expectedKoPemeXML + " sollte existieren", expectedKoPemeXML.exists());
      try {
         XMLDataLoader xdl = new XMLDataLoader(expectedKoPemeXML);
         final TestcaseType testcase = xdl.getFullData().getTestcases().getTestcase().get(0);
         for (final Datacollector dc : testcase.getDatacollector()) {
            for (final Result r : dc.getResult()) {
               final Fulldata fd = r.getFulldata();
               Assert.assertNotNull(fd);
               final File fulldataFile = new File(expectedKoPemeXML.getParentFile(), fd.getFileName());
               Assert.assertTrue(fulldataFile.exists());
               WrittenResultReader reader = new WrittenResultReader(fulldataFile);
               final Set<String> collectors = new HashSet<>();
               collectors.add(TimeDataCollectorNoGC.class.getCanonicalName());
               reader.read(null, collectors);
               Assert.assertEquals(2000, reader.getRealValues().size());
            }
         }
      } catch (final JAXBException e) {
         e.printStackTrace();
      }

      expectedKoPemeXML.delete();
   }


}
