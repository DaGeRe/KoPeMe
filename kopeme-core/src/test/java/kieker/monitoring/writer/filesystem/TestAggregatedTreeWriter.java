package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.number.IsNaN;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.dagere.kopeme.TestUtils;
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

   private static final File DEFAULT_FOLDER = new File("target/test-classes/kieker_testresults");

   @Before
   public void setupClass() {
      emptyFolder(DEFAULT_FOLDER);
   }

   private void initWriter(final int warmup) {
      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      final String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
      config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
      config.setProperty(AggregatedTreeWriter.CONFIG_PATH, absolutePath);
      config.setProperty(AggregatedTreeWriter.CONFIG_WRITEINTERVAL, 5);
      config.setProperty(AggregatedTreeWriter.CONFIG_WARMUP, warmup);
      Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
      Sample.MONITORING_CONTROLLER.enableMonitoring();
   }

   private static void emptyFolder(final File folder) {
      TestUtils.deleteRecursively(folder);
      folder.mkdirs();
   }

   @Test
   public void testSimpleWriting() throws Exception {
      initWriter(0);
      KiekerTestHelper.runFixture(15);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      assertJSONFileContainsMethods(DEFAULT_FOLDER, 3); // TODO due to the meta data entry, which are written to every folder
   }

   @Test
   public void testWarmup() throws Exception {
      initWriter(5);
      for (int i = 0; i < 3; i++) {
         final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method0()");
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      final Map<CallTreeNode, AggregatedData> data = assertJSONFileContainsMethods(DEFAULT_FOLDER, 1); // TODO due to the meta data entry, which are written to every folder

      final CallTreeNode expectedNode = new CallTreeNode(-1, -1, "public void NonExistant.method0()");
      final AggregatedData summaryStatistics = data.get(expectedNode);
      Assert.assertNotNull(summaryStatistics);
      Assert.assertThat(summaryStatistics.getStatistic().getMean(), IsNaN.notANumber());
   }

   @Test
   public void testBigWriting() throws Exception {
      initWriter(0);
      final int methods = 5;
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < methods; j++) {
            final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method" + j + "()");
         }
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      final Map<CallTreeNode, AggregatedData> data = assertJSONFileContainsMethods(DEFAULT_FOLDER, methods); // TODO due to the meta data entry, which are written to every folder

      final CallTreeNode expectedNode = new CallTreeNode(-1, -1, "public void NonExistant.method1()");
      data.keySet().forEach(value -> System.out.println(value.getCall() + " " + value.getClass() + " " + value.hashCode()));
      final AggregatedData summaryStatistics = data.get(expectedNode);
      System.out.println("Keys: " + data.keySet().size());
      Assert.assertNotNull(summaryStatistics);
      // Assert.assertThat(summaryStatistics.getStatistic().getMean(), IsNaN.notANumber());
   }

   private Map<CallTreeNode, AggregatedData> assertJSONFileContainsMethods(final File kiekerFolder, final int methods) throws IOException {
      final File currentMeasureFile = assertOneMeasureFile(kiekerFolder);
      System.out.println("File: " + currentMeasureFile.getAbsolutePath());

      final ObjectMapper MAPPER = new ObjectMapper();
      final SimpleModule keyDeserializer = new SimpleModule();
      keyDeserializer.addKeyDeserializer(CallTreeNode.class, new CallTreeNodeDeserializer());
      MAPPER.registerModule(keyDeserializer);
      final Map<CallTreeNode, AggregatedData> data = MAPPER.readValue(currentMeasureFile,
            new TypeReference<HashMap<CallTreeNode, AggregatedData>>() {
            });
      assertEquals(methods, data.keySet().size());

      return data;
   }

   private File assertOneMeasureFile(final File kiekerFolder) {
      final File kiekerRootDir = KiekerTestHelper.assertKiekerDir(kiekerFolder);
      final File[] measureFile = kiekerRootDir.listFiles(new FileFilter() {

         @Override
         public boolean accept(final File pathname) {
            return !pathname.getName().equals("kieker.map");
         }
      });
      assertEquals(1, measureFile.length);
      final File currentMeasureFile = measureFile[0];
      return currentMeasureFile;
   }

}
