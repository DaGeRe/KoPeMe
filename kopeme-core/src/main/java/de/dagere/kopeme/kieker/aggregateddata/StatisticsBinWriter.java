package de.dagere.kopeme.kieker.aggregateddata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class StatisticsBinWriter {
   private final ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
   private final ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
   private final ByteBuffer doubleBuffer = ByteBuffer.allocate(Double.BYTES);
   
   private BufferedOutputStream currentWriter;
   
   public StatisticsBinWriter(File currentDestination) throws FileNotFoundException {
      FileOutputStream tempFileStream = new FileOutputStream(currentDestination);
      this.currentWriter = new BufferedOutputStream(tempFileStream);
   }
   
   public void writeHeader(final DataNode node) throws IOException {
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
   
   public void writeStatistics(final WritingData value) throws IOException {
      writeLong(value.getCurrentStart());
      writeDouble(value.getCurrentStatistic().getMean());
      writeDouble(value.getCurrentStatistic().getStandardDeviation());
      writeLong(value.getCurrentStatistic().getN());
      writeDouble(value.getCurrentStatistic().getMin());
      writeDouble(value.getCurrentStatistic().getMax());
   }

   
   private void writeInt(int value) throws IOException {
      intBuffer.clear();
      intBuffer.putInt(0, value);
      final byte[] byteArray = intBuffer.array();
      currentWriter.write(byteArray);
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
   
   public void flush() throws IOException {
      currentWriter.flush();
   }

   public void close() throws IOException {
      currentWriter.close();
   }
}
