package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;

public class TestDataSaving {

   final File file = new File("target/test.xml");

   @Test
   public void testSavingJAXB() throws Exception {
      Kopemedata data = createData();

      XMLDataStorer.storeData(file, data);
      Result addResult = createResult(16.5);
      data.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult().add(addResult);
      XMLDataStorer.storeData(file, data);

      testLoadedResults();
   }

   private Kopemedata createData() {
      Kopemedata data = new Kopemedata();
      final TestcaseType testcase = new TestcaseType();
      data.setTestcases(new Testcases());
      data.getTestcases().getTestcase().add(testcase);
      final Datacollector datacollector = new Datacollector();
      testcase.getDatacollector().add(datacollector);
      Result result = createResult(15.5);
      datacollector.getResult().add(result);
      return data;
   }

   private Result createResult(final double value2) {
      final Result result = new Result();
      result.setFulldata(new Fulldata());
      result.setValue(value2);
      result.setMin(5D);
      result.setMax(15D);
      result.setDeviation(3.5);
      result.setWarmup(15);
      result.setRepetitions(16);
      result.setIterations(17);
      result.setJavaVersion("1.8");
      result.setShowStart(false);
      result.setRedirectToNull(true);
      result.setRedirectToTemp(false);
      result.setUseKieker(false);
      for (int i = 0; i < 5; i++) {
         final Value value = new Value();
         value.setStart(Long.valueOf(i));
         value.setValue(i);
         result.getFulldata().getValue().add(value);
      }
      return result;
   }

   private void testLoadedResults() throws JAXBException {
      Kopemedata data2 = XMLDataLoader.loadData(file);
      final List<Result> loadedResults = data2.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult();
      final Result result2 = loadedResults.get(0);
      assertCorrectResult(result2, 15.5);
      final Result result21 = loadedResults.get(1);
      assertCorrectResult(result21, 16.5);

      Assert.assertNotNull(result2.getJavaVersion());
      Assert.assertNotNull(result2.isRedirectToNull());
      Assert.assertNotNull(result2.isRedirectToTemp());
      Assert.assertNotNull(result2.isShowStart());

      Assert.assertEquals(result2.getJavaVersion(), result21.getJavaVersion());
      Assert.assertEquals(result2.isRedirectToNull(), result21.isRedirectToNull());
      Assert.assertEquals(result2.isRedirectToTemp(), result21.isRedirectToTemp());
      Assert.assertEquals(result2.isShowStart(), result21.isShowStart());
   }

   private void assertCorrectResult(final Result result2, final double actual) {
      Assert.assertEquals(result2.getValue(), actual, 0.01);
      Assert.assertEquals(result2.getMin(), 5L, 0.01);
      Assert.assertEquals(result2.getMax(), 15L, 0.01);
      Assert.assertEquals(result2.getDeviation(), 3.5, 0.01);
      Assert.assertEquals(result2.getWarmup(), 15, 0.01);
      Assert.assertEquals(result2.getRepetitions(), 16, 0.01);
      Assert.assertEquals(result2.getIterations(), 17, 0.01);
      for (int i = 0; i < 5; i++) {
         Value value2 = result2.getFulldata().getValue().get(i);
         Assert.assertEquals(i, value2.getValue());
         Assert.assertEquals(Long.valueOf(i), value2.getStart());
      }
   }
}
