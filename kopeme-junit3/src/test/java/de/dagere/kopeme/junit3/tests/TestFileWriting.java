package de.dagere.kopeme.junit3.tests;

import java.io.File;
import java.util.List;

import org.junit.Assert;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit.exampletests.runner.ExampleAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestOnlyTime;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.TestMethod;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class TestFileWriting extends TestCase {

   @Override
   protected void setUp() throws Exception {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   public void testOnlyTimeWriting() {
      TestRunner.run(JUnitAdditionTestOnlyTime.class);

      File f = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTestOnlyTime.class.getName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

      JSONDataLoader xdl = new JSONDataLoader(f);
      for (TestMethod tct : xdl.getFullData().getMethods()) {
         List<DatacollectorResult> dcl = tct.getDatacollectorResults();
         Assert.assertEquals(dcl.size(), 1);
      }
   }

   public void testNormalWriting() {
      TestRunner.run(ExampleAdditionTest.class);

      File f = TestUtils.jsonFileForKoPeMeTest(ExampleAdditionTest.class.getName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

      JSONDataLoader xdl = new JSONDataLoader(f);
      for (TestMethod tct : xdl.getFullData().getMethods()) {
         List<DatacollectorResult> dcl = tct.getDatacollectorResults();
         Assert.assertEquals(dcl.size(), 3);
      }

   }

   public void testDoubleWriting() {
      TestRunner.run(ExampleAdditionTest.class);
      TestRunner.run(ExampleAdditionTest.class);

      File f = TestUtils.jsonFileForKoPeMeTest(ExampleAdditionTest.class.getName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
   }
}
