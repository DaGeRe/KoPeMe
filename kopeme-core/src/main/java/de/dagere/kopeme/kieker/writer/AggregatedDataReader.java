package de.dagere.kopeme.kieker.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dagere.kopeme.kieker.aggregateddata.AggregatedData;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedDataNode;

public class AggregatedDataReader {
   public static Map<AggregatedDataNode, AggregatedData> getFullDataMap(final File folder) throws JsonParseException, JsonMappingException, IOException {
      if (!folder.isDirectory()) {
         throw new RuntimeException("Expecting folder with CSV-files!");
      }
      final Map<AggregatedDataNode, AggregatedData> resultMap = new HashMap<>();
      for (final File partialDataFile : getFiles(folder)) {
         readAggregatedDataFile(partialDataFile, resultMap);
      }
      return resultMap;
   }

   private static File[] getFiles(final File folder) {
      return folder.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".csv");
         }
      });
   }

   public static void readAggregatedDataFile(final File currentMeasureFile, Map<AggregatedDataNode, AggregatedData> datas) throws JsonParseException, JsonMappingException, IOException {
      try (final BufferedReader reader = new BufferedReader(new FileReader(currentMeasureFile))) {
         String line;
         while ((line = reader.readLine()) != null) {
            final String[] parts = line.split(";");
            final AggregatedDataNode node = readDataNode(parts);

            AggregatedData data = datas.get(node);
            if (data == null) {
               data = new AggregatedData(0, new LinkedHashMap<>());
               datas.put(node, data);
            }

            final long time = Long.parseLong(parts[3]);
            final StatisticalSummary summary = readStatistics(parts);

            data.getStatistic().put(time, summary);
         }
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
