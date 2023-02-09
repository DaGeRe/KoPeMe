package de.dagere.kopeme.kieker.aggregateddata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kieker.writer.AggregatedDataReaderBin;
import de.dagere.kopeme.kieker.writer.StatisticConfig;

public class TestSimpleFileDataManagerBin {

   @Test
   public void testRegularWriting() throws IOException, InterruptedException {
      File results = new File("target/results");
      results.mkdirs();
      SimpleFileDataManagerBin fileManager = new SimpleFileDataManagerBin(new StatisticConfig(-1, -1, 100, 1000), results);

      Thread thread = new Thread(fileManager);
      thread.start();

      fileManager.write(new DataNode("TestCall"), 10);
      fileManager.write(new DataNode("TestCall"), 15);
      fileManager.write(new DataNode("TestCall"), 20);

      Thread.sleep(200);
      
      fileManager.write(new DataNode("TestCall"), 20);
      fileManager.write(new DataNode("TestCall"), 21);
      fileManager.write(new DataNode("TestCall"), 22);

      fileManager.finish();
      fileManager.close();

      Thread.sleep(100);

      File expectedResultFile = new File(results, "measurement-0.bin");

      HashMap<AggregatedDataNode, AggregatedData> datas = new HashMap<>();
      AggregatedDataReaderBin.readAggregatedDataFile(expectedResultFile, datas);
      
      AggregatedData nodeData = datas.get(new AggregatedDataNode(-1, -1, "TestCall"));
      
      System.out.println(nodeData.getStatistic());
      
      Assert.assertEquals(18, nodeData.getOverallStatistic().getMean(), 0.01);
      Assert.assertEquals(6, nodeData.getOverallStatistic().getN());
   }
}
