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

   public static final String EXECUTIONSTART = "\n" + WrittenResultReader.EXECUTIONSTART;
   public static final String COLLECTOR = "\n" + WrittenResultReader.COLLECTOR;

   private final File tempFile;
   private final BufferedWriter tempFileWriter;

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

   public final void executionStart(final long currentTimeMillis) {
      try {
         // tempFileWriter.write(EXECUTIONSTART);
         tempFileWriter.write(Long.toString(System.currentTimeMillis()));
         tempFileWriter.write('\n');
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public final void writeValues(final DataCollector collectors[]) {
      try {
         for (int index = 0; index < collectors.length; index++) {
            DataCollector dc = collectors[index];
            // tempFileWriter.write(COLLECTOR);
            tempFileWriter.write(Integer.toString(index));
            tempFileWriter.write('=');
            tempFileWriter.write(Long.toString(dc.getValue()));
            tempFileWriter.write('\n');
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public final void finalizeCollection() {
      try {
         tempFileWriter.flush();
         tempFileWriter.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
