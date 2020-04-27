package de.dagere.kopeme.datacollection.tempfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import de.dagere.kopeme.datacollection.DataCollector;

public class ResultTempWriter {
   
   private File tempFile;
   private BufferedWriter tempFileWriter;
   
   public ResultTempWriter() throws IOException {
      tempFile = Files.createTempFile("kopeme", ".tmp").toFile();
      tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
   }
   
   public File getTempFile() {
      return tempFile;
   }

   public void executionStart(long currentTimeMillis) {
      try {
         tempFileWriter.write(WrittenResultReader.EXECUTIONSTART + System.currentTimeMillis() + "\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void writeValues(Map<String, DataCollector> dataCollectors) {
      for (final DataCollector dc : dataCollectors.values()) {
         try {
            tempFileWriter.write(WrittenResultReader.COLLECTOR + dc.getName() + "=" + dc.getValue() + "\n");
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   public void writeValue(String name, long value){
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
