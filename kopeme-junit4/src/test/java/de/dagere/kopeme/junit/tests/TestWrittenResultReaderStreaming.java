package de.dagere.kopeme.junit.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datacollection.DataCollector;
import de.dagere.kopeme.datacollection.tempfile.ResultTempWriter;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;

public class TestWrittenResultReaderStreaming {

   private final static class FakeDataCollector extends DataCollector{
      
      private final String fakeName;
      private int state;
      
      public FakeDataCollector(String fakeName, int state) {
         this.fakeName = fakeName;
         this.state = state;
      }
      
      @Override
      public void stopCollection() {
      }
      
      @Override
      public void startCollection() {
      }
      
      @Override
      public long getValue() {
         int value = state++;
         return value;
      }
      
      @Override
      public int getPriority() {
         return 0;
      }
      
      @Override
      public String getName() {
         return fakeName;
      }
   }
   
   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void cleanResult() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testNoFullWriting() throws IOException {
      
      ResultTempWriter writer = new ResultTempWriter(false);
      
      
      DataCollector[] collectors = new DataCollector[3];
      collectors[0] = new FakeDataCollector("myFirstFake", 1000);
      collectors[1] = new FakeDataCollector("mySecondFake", 10000);
      collectors[2] = new FakeDataCollector("myThirdFake", 100000);
      
      writer.setDataCollectors(collectors);
      
      for (int i = 0; i < 1000; i++) {
         writer.executionStart(1000 + i);
         writer.writeValues(collectors);
      }
      writer.finalizeCollection();
      
      WrittenResultReader reader = new WrittenResultReader(writer.getTempFile());
      
      Set<String> keys = new HashSet<>(Arrays.asList("myFirstFake", "mySecondFake", "myThirdFake"));
      reader.readStreaming(keys);
      
      Assert.assertEquals(1499.5, reader.getCollectorSummary("myFirstFake").getMean(), 0.01);
      Assert.assertEquals(10499.5, reader.getCollectorSummary("mySecondFake").getMean(), 0.01);
      Assert.assertEquals(100499.5, reader.getCollectorSummary("myThirdFake").getMean(), 0.01);
      
      writer.getTempFile().delete();
   }
}
