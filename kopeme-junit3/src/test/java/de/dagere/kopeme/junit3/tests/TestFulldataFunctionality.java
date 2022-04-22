package de.dagere.kopeme.junit3.tests;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class TestFulldataFunctionality extends TestCase {

   @Override
   protected void setUp() throws Exception {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   public void testFullWriting() {
      final SummaryStatistics st = new SummaryStatistics();
      st.addValue(5.0);// If this is not added, for some unexplainable classpath reason, mvn release:prepare fails in the tests (while mvn test succeeds)
      System.out.println(st.getMean());
      TestRunner.run(JUnitAdditionTestFullData.class);
      final File file = TestUtils.jsonFileForKoPeMeTest(JUnitAdditionTestFullData.class.getName(), TestUtils.TEST_ADDITION);
      Assert.assertTrue("File " + file + " should exist.", file.exists());

      final JSONDataLoader xdl = new JSONDataLoader(file);
      final TestMethod testcase = xdl.getFullData().getTestclazzes().get(0).getMethods().get(0);
      for (final DatacollectorResult dc : testcase.getDatacollectorResults()) {
         for (final VMResult r : dc.getResults()) {
            Assert.assertEquals(2, r.getRepetitions());
            final Fulldata fd = r.getFulldata();
            if (fd == null) {
               Assert.fail();
            } else {
               Assert.assertTrue(fd.getValues().size() > 0);
            }
         }
      }
   }

}
