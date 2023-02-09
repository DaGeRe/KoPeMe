package de.dagere.kopeme.kieker.aggregateddata;

import java.io.File;
import java.io.IOException;

import de.dagere.kopeme.kieker.writer.StatisticConfig;

public class SimpleFileDataManagerBin implements DataWriter {

   private final StatisticConfig config;

   private final File destinationFolder;
   private File currentDestination;
   private StatisticsBinWriter binWriter;

   private int currentEntries = 0;
   private int fileIndex = 0;
   private boolean running = true;

   /**
    * @param aggregatedTreeWriter
    * @throws IOException
    */
   public SimpleFileDataManagerBin(final StatisticConfig config, final File destinationFolder) throws IOException {
      this.config = config;
      this.destinationFolder = destinationFolder;
      currentDestination = new File(destinationFolder, "measurement-0.bin");
      binWriter = new StatisticsBinWriter(currentDestination);

      if (config.getWriteInterval() < 1) {
         throw new RuntimeException("The write interval always needs to be set to 1 or higher!");
      }
   }

   @Override
   public void finish() {
      running = false;
   }

   @Override
   public void run() {
      while (running) {
         try {
            System.out.println("Sleeping: " + config.getWriteInterval());
            Thread.sleep(config.getWriteInterval());
         } catch (final InterruptedException e) {
            System.out.println("Writing is finished...");
         }
         if (running) {
            try {
               writeAll();
            } catch (final IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   private synchronized void writeAll() throws IOException {
      binWriter.flush();
   }

   private void writeLine(DataNode node, WritingData data) throws IOException {
      binWriter.writeHeader(node);
      binWriter.writeStatistics(data);

      currentEntries++;
   }

   private void startNextFile() throws IOException {
      currentEntries = 0;
      fileIndex++;
      binWriter.close();
      currentDestination = new File(destinationFolder, "measurement-" + fileIndex + ".bin");
      binWriter = new StatisticsBinWriter(currentDestination);
   }

   @Override
   public synchronized void write(final DataNode node, final long duration) {
      WritingData data = new WritingData(currentDestination, config);
      data.addValue(duration);
      
      try {
         writeLine(node, data);
         
         if (currentEntries >= config.getEntriesPerFile()) {
            startNextFile();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void close() throws IOException {
      System.out.println("Writing finally...");
      writeAll();
      binWriter.close();
      binWriter = null;
   }
}