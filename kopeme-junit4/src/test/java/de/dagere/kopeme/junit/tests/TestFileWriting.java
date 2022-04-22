package de.dagere.kopeme.junit.tests;

import java.io.File;
import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitMultiplicationTest;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestClazz;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

/**
 * Tests writing of data
 * 
 * @author reichelt
 *
 */
public class TestFileWriting {

   private static final String TEST_MULTIPLICATION = "testMultiplication";
   private static final String TEST_ADDITION = "testAddition";
   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void initClass() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testNormalWriting() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitAdditionTest.class);
      final String testClass = JUnitAdditionTest.class.getName();
      final File file = TestUtils.jsonFileForKoPeMeTest(testClass, TEST_ADDITION);
      Assert.assertTrue("Datei " + file + " sollte existieren", file.exists());
      file.delete();
   }

   @Test
   public void testDoubleWriting() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitAdditionTest.class);
      jc.run(JUnitAdditionTest.class);
      final File file = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTest.class.getCanonicalName(), TEST_ADDITION);
      Assert.assertTrue("Datei " + file + " sollte existieren", file.exists());
      file.delete();
   }

   @Test
   public void testResults() {
      final JUnitCore jc = new JUnitCore();
      jc.run(JUnitMultiplicationTest.class);
      final File f = TestUtils.jsonFileForKoPeMeTest(JUnitMultiplicationTest.class.getCanonicalName(), TEST_MULTIPLICATION);
      Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

      final Kopemedata kd = new JSONDataLoader(f).getFullData();
      final TestClazz tc = kd.getTestclazzes().get(0);
      Assert.assertEquals(JUnitMultiplicationTest.class.getCanonicalName(), tc.getClazz());

      TestMethod tct = null;
      for (final TestMethod t : tc.getMethods()) {
         if (t.getMethod().equals(TEST_MULTIPLICATION)) {
            tct = t;
            break;
         }
      }
      Assert.assertNotNull(tct);

      DatacollectorResult timeCollector = null;
      for (final DatacollectorResult dc : tct.getDatacollectorResults()) {
         if (dc.getName().equals("de.dagere.kopeme.datacollection.TimeDataCollector")) {
            timeCollector = dc;
            break;
         }
      }
      Assert.assertNotNull(timeCollector);

      for (final VMResult r : timeCollector.getResults()) {
         final int val = (int) r.getValue();
         final int min = (int) r.getMin();
         final int max = (int) r.getMax();
         MatcherAssert.assertThat(val, Matchers.greaterThan(0));
         MatcherAssert.assertThat(max, Matchers.greaterThanOrEqualTo(val));
         MatcherAssert.assertThat(val, Matchers.greaterThanOrEqualTo(min));
         Assert.assertEquals(r.getIterations(), 5);
      }

   }

   @Test
   public void testExceptionWriting() {
      // FIXME why is this empty?
   }
}
