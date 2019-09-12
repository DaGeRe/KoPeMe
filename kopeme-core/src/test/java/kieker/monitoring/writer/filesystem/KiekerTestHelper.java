package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.ExecutionException;

import kieker.common.record.controlflow.OperationExecutionRecord;

public class KiekerTestHelper {
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
   	assertEquals(1, listFiles.length); // only the kieker root dir
   	final File kiekerRootDir = listFiles[0];
   	assertTrue("Kieker root dir should be a directory!", kiekerRootDir.isDirectory());
   	final File[] kiekerFiles = kiekerRootDir.listFiles();
   	assertEquals("There should be one kieker file!", 1, kiekerFiles.length);
      return kiekerRootDir;
   }
}
