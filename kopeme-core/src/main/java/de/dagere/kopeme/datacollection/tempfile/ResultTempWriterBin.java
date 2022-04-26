package de.dagere.kopeme.datacollection.tempfile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.DataCollector;

public class ResultTempWriterBin {

   private static final Logger LOG = LogManager.getLogger(ResultTempWriterBin.class);

   private final File tempFile;
   private final BufferedOutputStream tempFileWriter;

   public ResultTempWriterBin(final boolean warmup) throws IOException {
      tempFile = Files.createTempFile(warmup ? "kopeme-warmup-" : "kopeme-", ".bin").toFile();
      FileOutputStream tempFileStream = new FileOutputStream(tempFile);
      tempFileWriter = new BufferedOutputStream(tempFileStream);
   }

   public void setDataCollectors(final DataCollector[] collectors) {
      try {
         for (int index = 0; index < collectors.length; index++) {
            DataCollector dc = collectors[index];
//            tempFileWriter.write(WrittenResultReader.COLLECTOR_INDEX.getBytes());
            tempFileWriter.write('=');
            tempFileWriter.write(dc.getName().getBytes());
            tempFileWriter.write('\n');
         }
         tempFileWriter.write('\n'); // Mark end of collector information
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public File getTempFile() {
      return tempFile;
   }

   public final void executionStart(final long currentTimeMillis) {
      try {
         longBuffer.clear();
         longBuffer.putLong(0, System.currentTimeMillis());
         final byte[] byteArray = longBuffer.array();
         tempFileWriter.write(byteArray);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private final ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
   
   public final void writeValues(final DataCollector collectors[]) {
      try {
         for (int index = 0; index < collectors.length; index++) {
            DataCollector dc = collectors[index];
            longBuffer.clear();
            
            longBuffer.putLong(0, dc.getValue());
            final byte[] byteArray = longBuffer.array();
            tempFileWriter.write(byteArray);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public final void finalizeCollection() {
      try {
         tempFileWriter.flush();
         tempFileWriter.close();
         LOG.info("Flushing to " + tempFile.getAbsolutePath() + " finished");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
