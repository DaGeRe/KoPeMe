package de.dagere.kopeme.kieker.writer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import de.dagere.kopeme.datacollection.tempfile.WrittenResultReaderBin;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedData;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedDataNode;

public class AggregatedDataReaderBin {
   public static void readAggregatedDataFile(final File currentMeasureFile, final Map<AggregatedDataNode, AggregatedData> datas)
         throws IOException {
      try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(currentMeasureFile))) {

         while (reader.available() > 0) {
            final AggregatedDataNode node = readAggregatedDataNode(reader);

            AggregatedData data = datas.get(node);
            if (data == null) {
               data = new AggregatedData(0, new LinkedHashMap<>());
               datas.put(node, data);
            }

            long time = readLong(reader);

            final StatisticalSummary summary = readStatisticalSummary(reader);

            writeSummary(data, time, summary);
         }
      }
   }

   private static void writeSummary(AggregatedData data, long time, final StatisticalSummary summary) {
      StatisticalSummary oldSummary = data.getStatistic().get(time);
      if (oldSummary == null) {
         data.getStatistic().put(time, summary);
      } else {
         List<StatisticalSummary> summaries = new LinkedList<>();
         summaries.add(oldSummary);
         summaries.add(summary);
         StatisticalSummary aggregated = AggregateSummaryStatistics.aggregate(summaries);
         data.getStatistic().put(time, aggregated);
      }
   }

   private static StatisticalSummary readStatisticalSummary(BufferedInputStream reader) throws IOException {
      final double mean = readDouble(reader);
      final double deviation = readDouble(reader);
      final long n = readLong(reader);
      final double min = readDouble(reader);
      final double max = readDouble(reader);
      final double sum = mean * n;
      final StatisticalSummary summary = new StatisticalSummaryValues(mean, deviation * deviation, n, max, min, sum);
      return summary;
   }

   private static AggregatedDataNode readAggregatedDataNode(BufferedInputStream reader) throws IOException {
      String call = WrittenResultReaderBin.readUntilSign(reader, ';');

      int eoi = readInt(reader);
      int ess = readInt(reader);

      System.out.println("Read: " + call + " " + eoi + " " + ess);

      final AggregatedDataNode node = new AggregatedDataNode(eoi, ess, call);
      return node;
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
}
