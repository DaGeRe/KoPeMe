package de.dagere.kopeme.junit.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollectorNoGC;
import de.dagere.kopeme.datacollection.tempfile.TempfileReader;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;
import de.dagere.kopeme.datastorage.EnvironmentUtil;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullDataBig;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

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
      final File f = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTest.class.getCanonicalName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
      JSONDataLoader xdl = new JSONDataLoader(f);

      final TestMethod testcase = xdl.getFullData().getMethods().get(0);
      for (final DatacollectorResult dc : testcase.getDatacollectorResults()) {
         for (final VMResult r : dc.getResults()) {
            System.out.println(r.getCpu());
            System.out.println(EnvironmentUtil.getCPU());
            Assert.assertEquals(r.getCpu(), EnvironmentUtil.getCPU());
            Assert.assertEquals(r.getMemory(), EnvironmentUtil.getMemory());
            final Fulldata fd = r.getFulldata();
            if (fd != null) {
               Assert.assertEquals(0, fd.getValues().size());
            }
         }
      }

      f.delete();
   }

   @Test
   public void testFullWriting() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitAdditionTestFullData.class);
      final File f = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTestFullData.class.getCanonicalName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
      JSONDataLoader xdl = new JSONDataLoader(f);
      final TestMethod testcase = xdl.getFullData().getMethods().get(0);
      for (final DatacollectorResult dc : testcase.getDatacollectorResults()) {
         for (final VMResult r : dc.getResults()) {
            final Fulldata fd = r.getFulldata();
            Assert.assertNotNull(fd);
            Assert.assertEquals(900, fd.getValues().size());
         }
      }

      f.delete();
   }

   @Test
   public void testFullWritingSeparateFile() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitAdditionTestFullDataBig.class);
      final File expectedKoPemeJSON = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTestFullDataBig.class.getCanonicalName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + expectedKoPemeJSON + " sollte existieren", expectedKoPemeJSON.exists());
      JSONDataLoader xdl = new JSONDataLoader(expectedKoPemeJSON);
      final TestMethod testcase = xdl.getFullData().getMethods().get(0);
      for (final DatacollectorResult dc : testcase.getDatacollectorResults()) {
         for (final VMResult r : dc.getResults()) {
            final Fulldata fd = r.getFulldata();
            Assert.assertNotNull(fd);
            final File fulldataFile = new File(expectedKoPemeJSON.getParentFile(), fd.getFileName());
            Assert.assertTrue(fulldataFile.exists());
            TempfileReader reader = new WrittenResultReader(fulldataFile);
            final Set<String> collectors = new HashSet<>();
            collectors.add(TimeDataCollectorNoGC.class.getCanonicalName());
            reader.read(null, collectors);
            Assert.assertEquals(TestResult.BOUNDARY_SAVE_FILE * 2, reader.getRealValues().size());
         }
      }

      expectedKoPemeJSON.delete();
   }

}
