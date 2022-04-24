package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.datacollection.TimeDataCollectorNoGC;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

public class TestDataSaving {

   final File file = new File("target/test.json");

   @Test
   public void testSavingJAXB() throws Exception {
      Kopemedata data = createData();

      JSONDataStorer.storeData(file, data);
      VMResult addResult = createResult(16.5);
      data.getFirstDatacollectorContent().add(addResult);
      JSONDataStorer.storeData(file, data);

      testLoadedResults();
   }

   private Kopemedata createData() {
      Kopemedata data = new Kopemedata("de.Test");
      final TestMethod testcase = new TestMethod("test");
      data.getMethods().add(testcase);
      final DatacollectorResult datacollector = new DatacollectorResult(TimeDataCollectorNoGC.class.getCanonicalName());
      testcase.getDatacollectorResults().add(datacollector);
      VMResult result = createResult(15.5);
      datacollector.getResults().add(result);
      return data;
   }

   private VMResult createResult(final double value2) {
      final VMResult result = new VMResult();
      result.setFulldata(new Fulldata());
      result.setValue(value2);
      result.setMin(5D);
      result.setMax(15D);
      result.setDeviation(3.5);
      result.setWarmup(15);
      result.setRepetitions(16);
      result.setIterations(17);
      result.setJavaVersion("1.8");
      result.getVmRunConfiguration().setShowStart(false);
      result.getVmRunConfiguration().setRedirectToNull(true);
      result.getVmRunConfiguration().setRedirectToTemp(false);
      result.getVmRunConfiguration().setUseKieker(false);
      for (int i = 0; i < 5; i++) {
         final MeasuredValue value = new MeasuredValue();
         value.setStartTime(Long.valueOf(i));
         value.setValue(i);
         result.getFulldata().getValues().add(value);
      }
      return result;
   }

   private void testLoadedResults() {
      Kopemedata data2 = JSONDataLoader.loadData(file);
      final List<VMResult> loadedResults = data2.getFirstDatacollectorContent();
      final VMResult result2 = loadedResults.get(0);
      assertCorrectResult(result2, 15.5);
      final VMResult result21 = loadedResults.get(1);
      assertCorrectResult(result21, 16.5);

      Assert.assertNotNull(result2.getJavaVersion());
      Assert.assertNotNull(result2.getVmRunConfiguration().isRedirectToNull());
      Assert.assertNotNull(result2.getVmRunConfiguration().isRedirectToTemp());
      Assert.assertNotNull(result2.getVmRunConfiguration().isShowStart());

      Assert.assertEquals(result2.getJavaVersion(), result21.getJavaVersion());
      Assert.assertEquals(result2.getVmRunConfiguration().isRedirectToNull(), result21.getVmRunConfiguration().isRedirectToNull());
      Assert.assertEquals(result2.getVmRunConfiguration().isRedirectToTemp(), result21.getVmRunConfiguration().isRedirectToTemp());
      Assert.assertEquals(result2.getVmRunConfiguration().isShowStart(), result21.getVmRunConfiguration().isShowStart());
   }

   private void assertCorrectResult(final VMResult result2, final double actual) {
      Assert.assertEquals(result2.getValue(), actual, 0.01);
      Assert.assertEquals(result2.getMin(), 5L, 0.01);
      Assert.assertEquals(result2.getMax(), 15L, 0.01);
      Assert.assertEquals(result2.getDeviation(), 3.5, 0.01);
      Assert.assertEquals(result2.getWarmup(), 15, 0.01);
      Assert.assertEquals(result2.getRepetitions(), 16, 0.01);
      Assert.assertEquals(result2.getIterations(), 17, 0.01);
      for (int i = 0; i < 5; i++) {
         MeasuredValue value2 = result2.getFulldata().getValues().get(i);
         Assert.assertEquals(i, value2.getValue());
         Assert.assertEquals(Long.valueOf(i), (Long) value2.getStartTime());
      }
   }
}
