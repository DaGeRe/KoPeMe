package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

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

   @BeforeClass
   public static void setupClass() {
      emptyFolder(DEFAULT_FOLDER);

      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      final String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
      config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
      config.setProperty(AggregatedTreeWriter.CONFIG_PATH, absolutePath);
      config.setProperty(AggregatedTreeWriter.CONFIG_WRITEINTERVAL, "5");
      Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
      Sample.MONITORING_CONTROLLER.enableMonitoring();
   }

   private static void emptyFolder(final File folder) {
      TestUtils.deleteRecursively(folder);
      folder.mkdirs();
   }

   @Test
   public void testSimpleWriting() throws Exception {
      KiekerTestHelper.runFixture(15);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      assertJSONFileContainsMethods(DEFAULT_FOLDER, 3); // TODO due to the meta data entry, which are written to every folder
   }

   @Test
   public void testBigWriting() throws Exception {
      KiekerTestHelper.runFixture(1);
      for (int j = 0; j < 10000; j++) {
         final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method" + j + "()");
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
      assertJSONFileContainsMethods(DEFAULT_FOLDER, 3); // TODO due to the meta data entry, which are written to every folder
   }

   private void assertJSONFileContainsMethods(final File kiekerFolder, final int methods) throws IOException {
      final File currentMeasureFile = assertOneMeasureFile(kiekerFolder);
      System.out.println("File: " + currentMeasureFile.getAbsolutePath());

      final Map<CallTreeNode, SummaryStatistics> data = new ObjectMapper().readValue(currentMeasureFile, Map.class);

      assertEquals(3, data.keySet().size());
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
