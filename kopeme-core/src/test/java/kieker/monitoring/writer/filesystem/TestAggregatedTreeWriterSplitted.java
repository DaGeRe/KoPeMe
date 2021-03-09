package kieker.monitoring.writer.filesystem;

import java.io.IOException;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedData;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedDataNode;

/**
 * Writes Kieker example results for the {@link AggregatedTreeWriter}
 * 
 * @author reichelt
 *
 */
public class TestAggregatedTreeWriterSplitted {

   @Before
   public void setupClass() throws IOException {
      KiekerTestHelper.emptyFolder(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER);
   }


   @Test
   public void testSplittedWriting() throws Exception {
      TestAggregatedTreeWriter.initWriter(0, 100, 100, true);
      KiekerTestHelper.runFixture(15 * 3);
      Thread.sleep(105);
      KiekerTestHelper.runFixture(15 * 3);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      final Map<AggregatedDataNode, AggregatedData> data = TestAggregatedTreeWriter.assertJSONFileContainsMethods(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER, 3); 
      
      for (final Map.Entry<AggregatedDataNode, AggregatedData> method : data.entrySet()) {
         System.out.println(method.getKey() + " " + method.getValue().getStatistic());
         final long measurements = method.getValue().getStatistic().values().stream().mapToLong(statistic -> statistic.getN()).sum();
         Assert.assertEquals(30, measurements);
         Assert.assertThat(method.getValue().getStatistic().size(), Matchers.greaterThanOrEqualTo(2));
      }
   }
}
