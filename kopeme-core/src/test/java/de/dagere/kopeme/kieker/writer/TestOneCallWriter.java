package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.dagere.kopeme.kieker.record.OneCallRecord;
import de.dagere.kopeme.kieker.writer.onecall.OneCallReader;
import de.dagere.kopeme.kieker.writer.onecall.OneCallWriter;
import kieker.common.configuration.Configuration;

public class TestOneCallWriter {
   
   @TempDir
   File tempDir;
   
   @Test
   public void testBasicWriting() throws IOException {
      Configuration configuration = new Configuration();
      configuration.setProperty(OneCallWriter.CONFIG_ENTRIESPERFILE, "5");
      configuration.setProperty(OneCallWriter.CONFIG_PATH, tempDir.getAbsolutePath());
      OneCallWriter writer = new OneCallWriter(configuration);
      writer.onStarting();
      
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodB"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodC"));
      
      writer.onTerminating();
      
      Set<String> methods = OneCallReader.getCalledMethods(tempDir.listFiles()[0]);
      MatcherAssert.assertThat(methods, IsIterableContainingInAnyOrder.containsInAnyOrder("Class#methodA", "Class#methodB", "Class#methodC"));
   }
   
   @Test
   public void testTwoFilesWriting() throws IOException {
      Configuration configuration = new Configuration();
      configuration.setProperty(OneCallWriter.CONFIG_ENTRIESPERFILE, "5");
      configuration.setProperty(OneCallWriter.CONFIG_PATH, tempDir.getAbsolutePath());
      OneCallWriter writer = new OneCallWriter(configuration);
      writer.onStarting();
      
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodA"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodB"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodC"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodD"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodE"));
      writer.writeMonitoringRecord(new OneCallRecord("Class#methodF"));
      
      writer.onTerminating();
      
      Set<String> methods = OneCallReader.getCalledMethods(tempDir.listFiles()[0]);
      MatcherAssert.assertThat(methods, IsIterableContainingInAnyOrder.containsInAnyOrder("Class#methodA", "Class#methodB", "Class#methodC",
            "Class#methodD", "Class#methodE", "Class#methodF"));
   }
}
