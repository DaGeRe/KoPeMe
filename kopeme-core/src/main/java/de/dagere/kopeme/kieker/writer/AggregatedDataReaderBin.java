package de.dagere.kopeme.kieker.writer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedData;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedDataNode;

public class AggregatedDataReaderBin {
   public static void readAggregatedDataFile(final File currentMeasureFile, final Map<AggregatedDataNode, AggregatedData> datas)
         throws JsonParseException, JsonMappingException, IOException {
      try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(currentMeasureFile))) {

         System.out.println("test");
         while (reader.available() > 0) {
            String call = WrittenResultReader.readUntilSign(reader, ';');
            
            int eoi = readInt(reader);
            int ess = readInt(reader);
            
            System.out.println("Read: " + call + " " + eoi + " " + ess);
            
            final AggregatedDataNode node = new AggregatedDataNode(eoi, ess, call);
            
            AggregatedData data = datas.get(node);
            if (data == null) {
               data = new AggregatedData(0, new LinkedHashMap<>());
               datas.put(node, data);
            }
            
            long time = readLong(reader);
            
            final double mean = readDouble(reader);
            final double deviation = readDouble(reader);
            final long n = readLong(reader);
            final double min = readDouble(reader);
            final double max = readDouble(reader);
            final double sum = mean * n;
            final StatisticalSummary summary = new StatisticalSummaryValues(mean, deviation * deviation, n, max, min, sum);
            
            data.getStatistic().put(time, summary);
         }
      }
   }

   private static final byte[] longBytes = new byte[Long.BYTES];
   private static final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

   private static long readLong(BufferedInputStream reader) throws IOException {
      // This is very inefficient, but since we are in the reading part, this is acceptable...
      synchronized (buffer) {
         buffer.clear();
         reader.read(longBytes);
         buffer.put(longBytes);
         buffer.flip();
         long value = buffer.getLong();
         return value;
      }
   }
   
   private static final byte[] intBytes = new byte[Integer.BYTES];
   private static final ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);

   private static int readInt(BufferedInputStream reader) throws IOException {
      // This is very inefficient, but since we are in the reading part, this is acceptable...
      synchronized (intBuffer) {
         intBuffer.clear();
         reader.read(intBytes);
         intBuffer.put(intBytes);
         intBuffer.flip();
         int value = intBuffer.getInt();
         return value;
      }
   }
   
   private static final byte[] doubleBytes = new byte[Double.BYTES];
   private static final ByteBuffer doubleBuffer = ByteBuffer.allocate(Double.BYTES);

   private static double readDouble(BufferedInputStream reader) throws IOException {
      // This is very inefficient, but since we are in the reading part, this is acceptable...
      synchronized (doubleBuffer) {
         doubleBuffer.clear();
         reader.read(doubleBytes);
         doubleBuffer.put(doubleBytes);
         doubleBuffer.flip();
         double value = doubleBuffer.getDouble();
         return value;
      }
   }

   private static AggregatedDataNode readDataNode(final String[] parts) {
      final String call = parts[0];
      final int eoi = Integer.parseInt(parts[1]);
      final int ess = Integer.parseInt(parts[2]);
      final AggregatedDataNode node = new AggregatedDataNode(eoi, ess, call);
      return node;
   }

   private static StatisticalSummary readStatistics(final String[] parts) {
      final double mean = Double.parseDouble(parts[4]);
      final double deviation = Double.parseDouble(parts[5]);
      final long n = Long.parseLong(parts[6]);
      final double min = Double.parseDouble(parts[7]);
      final double max = Double.parseDouble(parts[8]);
      final double sum = mean * n;
      final StatisticalSummary summary = new StatisticalSummaryValues(mean, deviation * deviation, n, max, min, sum);
      return summary;
   }
}
