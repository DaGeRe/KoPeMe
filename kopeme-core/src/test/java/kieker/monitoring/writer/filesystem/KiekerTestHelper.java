package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.dagere.kopeme.TestUtils;
import kieker.common.record.controlflow.OperationExecutionRecord;

public class KiekerTestHelper {
   
   private static Logger LOG = LogManager.getLogger(KiekerTestHelper.class);

   static final ObjectMapper MAPPER = new ObjectMapper();

   static {
      final SimpleModule keyDeserializer = new SimpleModule();
      keyDeserializer.addKeyDeserializer(CallTreeNode.class, new CallTreeNodeDeserializer());
      MAPPER.registerModule(keyDeserializer);
   }
   
   public static Map<CallTreeNode, AggregatedData> readAggregatedDataFile(final File currentMeasureFile) throws JsonParseException, JsonMappingException, IOException {
      final Map<CallTreeNode, AggregatedData> data = KiekerTestHelper.MAPPER.readValue(currentMeasureFile,
            new TypeReference<HashMap<CallTreeNode, AggregatedData>>() {
            });
      return data;
   }

   public static void createAndWriteOperationExecutionRecord(final long tin, final long tout, final String methodSignature) {
      final OperationExecutionRecord e = new OperationExecutionRecord(
            methodSignature,
            OperationExecutionRecord.NO_SESSION_ID,
            OperationExecutionRecord.NO_TRACE_ID,
            tin, tout, "myHost",
            OperationExecutionRecord.NO_EOI_ESS,
            OperationExecutionRecord.NO_EOI_ESS);
      Sample.MONITORING_CONTROLLER.newMonitoringRecord(e);
   }

   public static void runFixture(final int rounds) throws InterruptedException,
         ExecutionException {
      for (int i = 0; i < rounds / 3; i++) {
         final Sample fixture = new Sample();
         final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         fixture.a();
         final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
         KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".a()");
      }
      Thread.sleep(3);// TODO: Remove dirty workaround..
   }

   public static File assertKiekerDir(final File kiekerFolder) {
      final File[] listFiles = kiekerFolder.listFiles();
      LOG.info("Kieker-files: {}", Arrays.toString(listFiles));
      assertEquals("Found not exactly one folder: " + Arrays.toString(listFiles), 1, listFiles.length); // only the kieker root dir
      final File kiekerRootDir = listFiles[0];
      assertTrue("Kieker root dir should be a directory!", kiekerRootDir.isDirectory());
      return kiekerRootDir;
   }

   public static File[] getMeasurementFiles(final File kiekerFolder) {
      final File kiekerRootDir = KiekerTestHelper.assertKiekerDir(kiekerFolder);
      final File[] measureFile = kiekerRootDir.listFiles(new FileFilter() {

         @Override
         public boolean accept(final File pathname) {
            return !pathname.getName().equals("kieker.map");
         }
      });
      return measureFile;
   }

   public static void emptyFolder(final File folder) {
      TestUtils.deleteRecursively(folder);
      folder.mkdirs();
   }
}
