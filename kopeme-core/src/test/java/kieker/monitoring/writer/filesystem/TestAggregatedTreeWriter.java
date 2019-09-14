package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.hamcrest.number.IsNaN;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;

/**
 * Writes Kieker example results for the {@link AggregatedTreeWriter}
 * 
 * @author reichelt
 *
 */
public class TestAggregatedTreeWriter {

   static final File TEST_FOLDER = new File("target/test-classes/kieker_testresults");

   @Before
   public void setupClass() {
      KiekerTestHelper.emptyFolder(TEST_FOLDER);
   }

   public static void initWriter(final int warmup, final int entriesPerFile) {
      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      final String absolutePath = TEST_FOLDER.getAbsolutePath();
      config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
      config.setProperty(AggregatedTreeWriter.CONFIG_PATH, absolutePath);
      config.setProperty(AggregatedTreeWriter.CONFIG_WRITEINTERVAL, 5);
      config.setProperty(AggregatedTreeWriter.CONFIG_WARMUP, warmup);
      config.setProperty(AggregatedTreeWriter.CONFIG_ENTRIESPERFILE, entriesPerFile);
      Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
      Sample.MONITORING_CONTROLLER.enableMonitoring();
   }

   @Test
   public void testSimpleWriting() throws Exception {
      initWriter(0, 100);
      KiekerTestHelper.runFixture(15);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      assertJSONFileContainsMethods(TEST_FOLDER, 3); // TODO due to the meta data entry, which are written to every folder
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
      final Map<CallTreeNode, AggregatedData> data = assertJSONFileContainsMethods(TEST_FOLDER, 1); // TODO due to the meta data entry, which are written to every folder

      final CallTreeNode expectedNode = new CallTreeNode(-1, -1, "public void NonExistant.method0()");
      final AggregatedData summaryStatistics = data.get(expectedNode);
      Assert.assertNotNull(summaryStatistics);
      Assert.assertThat(summaryStatistics.getStatistic().getMean(), IsNaN.notANumber());
   }

   private Map<CallTreeNode, AggregatedData> assertJSONFileContainsMethods(final File kiekerFolder, final int methods) throws IOException {
      final File currentMeasureFile = assertOneMeasureFile(kiekerFolder);
      System.out.println("File: " + currentMeasureFile.getAbsolutePath());

      
      final Map<CallTreeNode, AggregatedData> data = KiekerTestHelper.readAggregatedDataFile(currentMeasureFile);
      assertEquals(methods, data.keySet().size());

      return data;
   }

   private File assertOneMeasureFile(final File kiekerFolder) {
      final File[] measureFile = KiekerTestHelper.getMeasurementFiles(kiekerFolder);
      assertEquals(1, measureFile.length);
      final File currentMeasureFile = measureFile[0];
      return currentMeasureFile;
   }

}
