package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.hamcrest.Matchers;
import org.hamcrest.number.IsNaN;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedData;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedDataNode;

/**
 * Writes Kieker example results for the {@link AggregatedTreeWriter}
 * 
 * @author reichelt
 *
 */
public class TestAggregatedTreeWriter {

   @Before
   public void setupClass() {
      KiekerTestHelper.emptyFolder(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER);
   }

   public static void initWriter(final int warmup, final int entriesPerFile) {
      initWriter(warmup, entriesPerFile, 5, false);
   }
   
   public static void initWriter(final int warmup, final int entriesPerFile, final int interval, final boolean aggregatePartially) {
      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      final String absolutePath = TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER.getAbsolutePath();
      config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
      config.setProperty(AggregatedTreeWriter.CONFIG_PATH, absolutePath);
      config.setProperty(AggregatedTreeWriter.CONFIG_WRITE_INTERVAL, 100);
//      config.setProperty(AggregatedTreeWriter.CONFIG_WARMUP, warmup);
      config.setProperty(AggregatedTreeWriter.CONFIG_AGGREGATE_SPLITTED, aggregatePartially);
      config.setProperty(AggregatedTreeWriter.CONFIG_ENTRIESPERFILE, entriesPerFile);
      Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
      Sample.MONITORING_CONTROLLER.enableMonitoring();
   }

   @Test(expected = RuntimeException.class)
   public void wrongOutlierFactor() throws Exception {
      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
      config.setProperty(AggregatedTreeWriter.CONFIG_OUTLIER, 0.9);
      new AggregatedTreeWriter(config);
   }

   @Test
   public void testSimpleWriting() throws Exception {
      initWriter(0, 100);
      KiekerTestHelper.runFixture(15);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      assertJSONFileContainsMethods(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER, 3); 
   }

   @Test
   public void testWarmup() throws Exception {
      initWriter(5, 100);
      for (int i = 0; i < 3; i++) {
         final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method0()");
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      final Map<AggregatedDataNode, AggregatedData> data = assertJSONFileContainsMethods(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER, 0); 

      Assert.assertEquals(0, data.size());
   }

   /**
    * Attention: While kieker operations do not contain new (e.g. public NonExistant.<init>), Kieker patterns do (e.g. public new NonExistant.<init>)
    * 
    * @throws Exception
    */
   @Test
   public void testConstructor() throws Exception {
      initWriter(0, 100);
      for (int i = 0; i < 3; i++) {
         final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public new NonExistant.<init>()");
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      final Map<AggregatedDataNode, AggregatedData> data = assertJSONFileContainsMethods(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER, 1); 

      final AggregatedDataNode expectedNode = new AggregatedDataNode(-1, -1, "public new NonExistant.<init>()");
      final AggregatedData summaryStatistics = data.get(expectedNode);
      Assert.assertNotNull(summaryStatistics);
      summaryStatistics.getStatistic().forEach((time, statistic) -> {
         Assert.assertThat(statistic.getMean(), Matchers.not(IsNaN.notANumber()));
      });
   }

   static Map<AggregatedDataNode, AggregatedData> assertJSONFileContainsMethods(final File kiekerFolder, final int methods) throws IOException {
      final File currentMeasureFile = assertOneMeasureFile(kiekerFolder);
      System.out.println("File: " + currentMeasureFile.getAbsolutePath());

      final Map<AggregatedDataNode, AggregatedData> data = AggregatedDataReader.readAggregatedDataFile(currentMeasureFile);
      assertEquals(methods, data.keySet().size());

      return data;
   }

   private static File assertOneMeasureFile(final File kiekerFolder) {
      final File[] measureFile = KiekerTestHelper.getMeasurementFiles(kiekerFolder);
      assertEquals(1, measureFile.length);
      final File currentMeasureFile = measureFile[0];
      return currentMeasureFile;
   }

}
