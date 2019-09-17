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
import kieker.monitoring.writer.filesystem.aggregateddata.AggregatedDataNode;
import kieker.monitoring.writer.filesystem.aggregateddata.FileDataManager;

public class AggregatedDataReader {
   public static Map<AggregatedDataNode, AggregatedData> getFullDataMap(final File folder) throws JsonParseException, JsonMappingException, IOException{
      if (!folder.isDirectory()) {
         throw new RuntimeException("Expecting folder with JSON-files!");
      }
      final Map<AggregatedDataNode, AggregatedData> resultMap = new HashMap<>();
      for (final File partialDataFile : getFiles(folder)) {
         final Map<AggregatedDataNode, AggregatedData> partialData = readAggregatedDataFile(partialDataFile);
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
   
   public static Map<AggregatedDataNode, AggregatedData> readAggregatedDataFile(final File currentMeasureFile) throws JsonParseException, JsonMappingException, IOException {
      final Map<AggregatedDataNode, AggregatedData> data = FileDataManager.MAPPER.readValue(currentMeasureFile,
            new TypeReference<HashMap<AggregatedDataNode, AggregatedData>>() {
            });
      return data;
   }
   
   public static void main(final String[] args) throws JsonParseException, JsonMappingException, IOException {
      final File currentMeasureFile = new File("/home/reichelt/.KoPeMe/de.test/demo-project/de.test.CalleeTest/1568624964903/onlyCallMethod1/kieker-20190916-090924-5526425321393-UTC--/measurement-0.json");
      final Map<AggregatedDataNode, AggregatedData> readAggregatedDataFile = readAggregatedDataFile(currentMeasureFile);
      readAggregatedDataFile.forEach((key, value) -> System.out.println(key + " " + value.getStatistic().getMean()));
   }
}
