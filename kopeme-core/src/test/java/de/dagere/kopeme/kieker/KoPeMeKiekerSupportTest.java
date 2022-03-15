package de.dagere.kopeme.kieker;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.controller.WriterController;
import kieker.monitoring.writer.MonitoringWriterThread;

public class KoPeMeKiekerSupportTest {

   private static final int TO_MILLISECONDS = 1000000;

   @Test
   public void testStartAndEnd() throws Exception {
      KoPeMeKiekerSupport.INSTANCE.useKieker(true, "MyTest", "test");
      IMonitoringController instance = MonitoringController.getInstance();
      System.out.println(System.identityHashCode(instance));

      Assert.assertTrue(instance.isMonitoringEnabled());
      KoPeMeKiekerSupport.INSTANCE.waitForEnd();

      Assert.assertFalse(instance.isMonitoringEnabled());
   }

   @Test
   public void testWaitTime() throws Exception {
      KoPeMeKiekerSupport.INSTANCE.setKiekerWaitTime(3);
      
      // This causes an exception, but we need to stop the thread which writes to hdd before we can try whether waiting time is used correctly
      MonitoringWriterThread thread = getMonitoringWriter();
      thread.stop();

      long startTime = System.nanoTime();
      BlockingQueue<IMonitoringRecord> queue = KoPeMeKiekerSupport.INSTANCE.getWriterQueue();
      queue.add(new OperationExecutionRecord("dummy", "1", 0, 0, 0, null, 0, 0));

      KoPeMeKiekerSupport.INSTANCE.waitForEnd();
      long endTime = System.nanoTime();
      long duration = (endTime - startTime) / TO_MILLISECONDS;
      
      MatcherAssert.assertThat(duration, Matchers.lessThan(4000l));
      MatcherAssert.assertThat(duration, Matchers.greaterThan(2000l));
   }

   public MonitoringWriterThread getMonitoringWriter() throws Exception {
      final Field writerControllerField = MonitoringController.class.getDeclaredField("writerController");
      writerControllerField.setAccessible(true);
      final WriterController writerController = (WriterController) writerControllerField.get(MonitoringController.getInstance());

      return KoPeMeKiekerSupport.getMonitoringWriterThread(writerController);
   }
}
