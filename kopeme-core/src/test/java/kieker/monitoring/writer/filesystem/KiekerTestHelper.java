package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.TestUtils;
import kieker.common.record.controlflow.OperationExecutionRecord;

public class KiekerTestHelper {

   private static Logger LOG = LogManager.getLogger(KiekerTestHelper.class);

   public static void createAndWriteOperationExecutionRecord(final long tin, final long tout, final String methodSignature, int eoi, int ess) {
      final OperationExecutionRecord e = new OperationExecutionRecord(
            methodSignature,
            OperationExecutionRecord.NO_SESSION_ID,
            OperationExecutionRecord.NO_TRACE_ID,
            tin, tout, "myHost",
            eoi,
            ess);
      Sample.MONITORING_CONTROLLER.newMonitoringRecord(e);
   }

   public static void createAndWriteOperationExecutionRecord(final long tin, final long tout, final String methodSignature) {
      createAndWriteOperationExecutionRecord(tin, 
            tout, 
            methodSignature, 
            OperationExecutionRecord.NO_EOI_ESS,
            OperationExecutionRecord.NO_EOI_ESS);
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
