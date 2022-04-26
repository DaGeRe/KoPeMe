package de.dagere.kopeme.kieker.aggregateddata;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.dagere.kopeme.kieker.writer.AggregatedTreeWriter;

public class FileDataManagerBinary implements Runnable, Closeable {

   private final AggregatedTreeWriter aggregatedTreeWriter;

   private final File destinationFolder;
   private File currentDestination;
   private BufferedOutputStream currentWriter;
   private final Map<DataNode, WritingData> nodeMap = new ConcurrentHashMap<>();

   private int currentEntries = 0;
   private int fileIndex = 0;
   private boolean running = true;

   /**
    * @param aggregatedTreeWriter
    * @throws IOException
    */
   public FileDataManagerBinary(final AggregatedTreeWriter aggregatedTreeWriter) throws IOException {
      this.aggregatedTreeWriter = aggregatedTreeWriter;
      this.destinationFolder = aggregatedTreeWriter.getResultFolder();
      currentDestination = new File(destinationFolder, "measurement-0.bin");
      FileOutputStream tempFileStream = new FileOutputStream(currentDestination);
      currentWriter = new BufferedOutputStream(tempFileStream);
   }

   public void finish() {
      running = false;
   }

   @Override
   public void run() {
      while (running) {
         try {
            System.out.println("Sleeping: " + aggregatedTreeWriter.getWriteInterval());
            Thread.sleep(aggregatedTreeWriter.getWriteInterval());
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
      for (final Map.Entry<DataNode, WritingData> value : nodeMap.entrySet()) {
         writeLine(value);

         if (currentEntries >= aggregatedTreeWriter.getEntriesPerFile()) {
            startNextFile();
         }
      }
      currentWriter.flush();
   }

   private void writeLine(final Map.Entry<DataNode, WritingData> value) throws IOException {
      if (value.getValue().getCurrentStatistic() != null &&
            !Double.isNaN(value.getValue().getCurrentStatistic().getMean())
            && value.getValue().getCurrentStatistic().getN() != 0) {
         writeHeader(value.getKey());
         writeStatistics(value.getValue());
         // currentWriter.write('\n');
         currentEntries++;
         value.getValue().persistStatistic();
      }
   }

   private void startNextFile() throws IOException {
      currentEntries = 0;
      fileIndex++;
      currentWriter.close();
      currentDestination = new File(destinationFolder, "measurement-" + fileIndex + ".bin");
      FileOutputStream tempFileStream = new FileOutputStream(currentDestination);
      currentWriter = new BufferedOutputStream(tempFileStream);
   }

   private final ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
   private final ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
   private final ByteBuffer doubleBuffer = ByteBuffer.allocate(Double.BYTES);

   private void writeHeader(final DataNode node) throws IOException {
      currentWriter.write(node.getCall().getBytes());
      currentWriter.write(';');

      int eoi, ess;
      if (node instanceof AggregatedDataNode) {
         eoi = ((AggregatedDataNode) node).getEoi();
         ess = ((AggregatedDataNode) node).getEss();
      } else {
         eoi = -1;
         ess = -1;
      }
      
      writeInt(eoi);
      writeInt(ess);
   }

   private void writeInt(int value) throws IOException {
      intBuffer.clear();
      intBuffer.putInt(0, value);
      final byte[] byteArray = intBuffer.array();
      currentWriter.write(byteArray);
   }

   private void writeStatistics(final WritingData value) throws IOException {
      writeLong(value.getCurrentStart());
      writeDouble(value.getCurrentStatistic().getMean());
      writeDouble(value.getCurrentStatistic().getStandardDeviation());
      writeLong(value.getCurrentStatistic().getN());
      writeDouble(value.getCurrentStatistic().getMin());
      writeDouble(value.getCurrentStatistic().getMax());
   }

   private void writeLong(long value) throws IOException {
      longBuffer.clear();
      longBuffer.putLong(0, value);
      final byte[] byteArray = longBuffer.array();
      currentWriter.write(byteArray);
   }

   private void writeDouble(double value) throws IOException {
      doubleBuffer.clear();
      doubleBuffer.putDouble(0, value);
      final byte[] byteArray = doubleBuffer.array();
      currentWriter.write(byteArray);
   }

   public synchronized void write(final DataNode node, final long duration) {
      final WritingData data = getData(node);
      data.addValue(duration);
   }

   private WritingData getData(final DataNode node) {
      WritingData data = nodeMap.get(node);
      if (data == null) {
         data = new WritingData(currentDestination, aggregatedTreeWriter.getStatisticConfig());
         nodeMap.put(node, data);
      }
      return data;

   }

   @Override
   public void close() throws IOException {
      System.out.println("Writing finally...");
      writeAll();
      currentWriter.close();
      currentWriter = null;
   }
}