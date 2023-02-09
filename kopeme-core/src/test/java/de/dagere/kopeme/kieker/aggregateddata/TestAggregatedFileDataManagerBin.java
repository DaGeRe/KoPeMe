package de.dagere.kopeme.kieker.aggregateddata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kieker.writer.AggregatedDataReaderBin;
import de.dagere.kopeme.kieker.writer.StatisticConfig;

public class TestAggregatedFileDataManagerBin {

   @Test
   public void testRegularWriting() throws IOException, InterruptedException {
      File results = new File("target/results");
      results.mkdirs();
      AggregatedFileDataManagerBin fileManager = new AggregatedFileDataManagerBin(new StatisticConfig(-1, -1, 100, 1000), results);

      Map<AggregatedDataNode, AggregatedData> datas = writeAndGetData(results, fileManager);
      
      AggregatedData nodeData = datas.get(new AggregatedDataNode(-1, -1, "TestCall"));
      Assert.assertEquals(18, nodeData.getOverallStatistic().getMean(), 0.01);
      Assert.assertEquals(6, nodeData.getOverallStatistic().getN());
      
      Assert.assertEquals(2, nodeData.getStatistic().size());
   }

   public static Map<AggregatedDataNode, AggregatedData> writeAndGetData(File results, DataWriter fileManager) throws InterruptedException, IOException {
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
      return datas;
   }
}
