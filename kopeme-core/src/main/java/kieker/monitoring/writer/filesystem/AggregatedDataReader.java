package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedData;
import kieker.monitoring.writer.filesystem.aggregateddata.CallTreeNode;
import kieker.monitoring.writer.filesystem.aggregateddata.FileDataManager;

public class AggregatedDataReader {
   public static Map<CallTreeNode, AggregatedData> getFullDataMap(final File folder) throws JsonParseException, JsonMappingException, IOException{
      if (!folder.isDirectory()) {
         throw new RuntimeException("Expecting folder with JSON-files!");
      }
      final Map<CallTreeNode, AggregatedData> resultMap = new HashMap<>();
      for (final File partialDataFile : getFiles(folder)) {
         final Map<CallTreeNode, AggregatedData> partialData = readAggregatedDataFile(partialDataFile);
         resultMap.putAll(partialData);
      }
      return resultMap;
   }

   private static File[] getFiles(final File folder) {
      return folder.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".json");
         }
      });
   }
   
   public static Map<CallTreeNode, AggregatedData> readAggregatedDataFile(final File currentMeasureFile) throws JsonParseException, JsonMappingException, IOException {
      final Map<CallTreeNode, AggregatedData> data = FileDataManager.MAPPER.readValue(currentMeasureFile,
            new TypeReference<HashMap<CallTreeNode, AggregatedData>>() {
            });
      return data;
   }
}
