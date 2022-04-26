package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
         if (partialDataFile.getName().endsWith(".csv")) {
            AggregatedDataReaderCSV.readAggregatedDataFile(partialDataFile, resultMap);
         } else if (partialDataFile.getName().endsWith(".bin")) {
            AggregatedDataReaderBin.readAggregatedDataFile(partialDataFile, resultMap);
         }
         
      }
      return resultMap;
   }

   private static File[] getFiles(final File folder) {
      return folder.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".csv") || pathname.getName().endsWith(".bin");
         }
      });
   }

   
}
