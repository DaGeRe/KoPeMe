package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedData;
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedDataNode;

public class TestAggregatedMultifileWriting {

   @Before
   public void setupClass() {
      KiekerTestHelper.emptyFolder(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER);
   }

   @Test
   public void testTenFilesWriting()
         throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
      TestAggregatedTreeWriter.initWriter(0, 3);
      final int methods = 30;
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < methods; j++) {
            final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method" + j + "()");
         }
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);

      final File[] measureFile = KiekerTestHelper.getMeasurementFiles(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER);
      Assert.assertEquals(11, measureFile.length); // newest is empty file

      for (final File file : measureFile) {
         final Map<AggregatedDataNode, AggregatedData> data = AggregatedDataReader.readAggregatedDataFile(file);
         if (!file.getName().equals("measurement-10.csv")) {
            Assert.assertEquals(3, data.size());
         }
      }
   }

   @Test
   public void testTwoFileWriting() throws Exception {
      TestAggregatedTreeWriter.initWriter(0, 10);
      final int methods = 15;
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < methods; j++) {
            final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
            KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void NonExistant.method" + j + "()");
         }
      }
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);

      final File[] measureFile = KiekerTestHelper.getMeasurementFiles(TestChangeableFolderSyncFsWriter.DEFAULT_FOLDER);
      Assert.assertEquals(2, measureFile.length);
   }
}
