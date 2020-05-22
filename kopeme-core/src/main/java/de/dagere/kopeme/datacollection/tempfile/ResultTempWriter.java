package de.dagere.kopeme.datacollection.tempfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import de.dagere.kopeme.datacollection.DataCollector;

public class ResultTempWriter {

   private File tempFile;
   private BufferedWriter tempFileWriter;

   public ResultTempWriter() throws IOException {
      tempFile = Files.createTempFile("kopeme", ".tmp").toFile();
      tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
   }

   public void setDataCollectors(final DataCollector collectors[]) {
      try {
         for (int index = 0; index < collectors.length; index++) {
            DataCollector dc = collectors[index];
            tempFileWriter.write(WrittenResultReader.COLLECTOR_INDEX + index + "=" + dc.getName() + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public File getTempFile() {
      return tempFile;
   }

   public void executionStart(final long currentTimeMillis) {
      try {
         tempFileWriter.write(WrittenResultReader.EXECUTIONSTART + System.currentTimeMillis() + "\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void writeValues(final DataCollector collectors[]) {
      try {
         for (int index = 0; index < collectors.length; index++) {
            DataCollector dc = collectors[index];
            tempFileWriter.write(WrittenResultReader.COLLECTOR + index + "=" + dc.getValue() + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void writeValue(final String name, final long value) {
      try {
         tempFileWriter.write(WrittenResultReader.FINAL_VALUE + name + "=" + value + "\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void finalizeCollection() {
      try {
         tempFileWriter.flush();
         tempFileWriter.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
