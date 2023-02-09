package de.dagere.kopeme.kieker.aggregateddata;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kieker.writer.StatisticConfig;

public class TestSimpleFileDataManagerBin {

   @Test
   public void testRegularWriting() throws IOException, InterruptedException {
      File results = new File("target/results");
      results.mkdirs();
      SimpleFileDataManagerBin fileManager = new SimpleFileDataManagerBin(new StatisticConfig(-1, -1, 100, 1000), results);

      Map<AggregatedDataNode, AggregatedData> datas = TestAggregatedFileDataManagerBin.writeAndGetData(results, fileManager);
      
      
      AggregatedData nodeData = datas.get(new AggregatedDataNode(-1, -1, "TestCall"));
      
      Assert.assertEquals(18, nodeData.getOverallStatistic().getMean(), 0.01);
      Assert.assertEquals(6, nodeData.getOverallStatistic().getN());
   }
}
