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
   private Map<DataCollector, Integer> collectorIndexed;

   public ResultTempWriter() throws IOException {
      tempFile = Files.createTempFile("kopeme", ".tmp").toFile();
      tempFileWriter = new BufferedWriter(new FileWriter(tempFile));
   }

   public void setDataCollectors(Map<String, DataCollector> dataCollectors) {
      int collectorIndex = 0;
      collectorIndexed = new HashMap<>();
      try {
         for (DataCollector collector : dataCollectors.values()) {
            tempFileWriter.write(WrittenResultReader.COLLECTOR_INDEX + collectorIndex + "=" + collector.getName() + "\n");
            collectorIndexed.put(collector, collectorIndex++);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
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

   public void writeValues(DataCollector collectors[]) {
      try {
         for (final DataCollector dc : collectors) {
            int index = collectorIndexed.get(dc);
            tempFileWriter.write(WrittenResultReader.COLLECTOR + index + "=" + dc.getValue() + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void writeValue(String name, long value) {
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
