package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.kopemedata.VMResultChunk;


public class JSONDataStorer implements DataStorer {

   private static final Logger LOG = LogManager.getLogger(JSONDataStorer.class);
   private final File file;
   private Kopemedata data;
   
   public JSONDataStorer(final File foldername, final String classname, final String methodname) {
      String filename = methodname + ".json";
      file = new File(foldername, filename);
      if (file.exists()) {
         JSONDataLoader loader = new JSONDataLoader(file);
         data = loader.getFullData();
      }else {
         createJSONData(classname);
      }
   }
   
   private void createJSONData(final String classname) {
      data = new Kopemedata(classname);
      storeData();
   }
   
   private void storeData() {
      try {
         new ObjectMapper().writeValue(file, data);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
   public static void storeData(final File file, final Kopemedata currentdata) {
      try {
         new ObjectMapper().writeValue(file, currentdata);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void storeValue(VMResult result, String testcase, String collectorName) {
      final TestMethod test = getOrCreateTestcase(result, testcase);

      final DatacollectorResult dc = getOrCreateDatacollector(collectorName, test);

      if (System.getenv("KOPEME_CHUNKSTARTTIME") != null) {
         final VMResultChunk current = findChunk(dc);
         current.getResults().add(result);
      } else {
         dc.getResults().add(result);
      }
      if (result.getFulldata() != null && result.getFulldata().getFileName() != null) {
         saveFulldata(result);
      }
      result.setCpu(EnvironmentUtil.getCPU());
      result.setMemory(EnvironmentUtil.getMemory());
      storeData();
   }

   private void saveFulldata(VMResult result) {
      File fulldataFile = new File(result.getFulldata().getFileName());
      final File targetFile = new File(file.getParentFile(), fulldataFile.getName());
      try {
         Files.move(fulldataFile.toPath(), targetFile.toPath());
         result.getFulldata().setFileName(targetFile.getName());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private VMResultChunk findChunk(DatacollectorResult dc) {
      final long start = Long.parseLong(System.getenv("KOPEME_CHUNKSTARTTIME"));
      VMResultChunk current = null;
      for (final VMResultChunk chunk : dc.getChunks()) {
         if (chunk.getChunkStartTime() == start) {
            current = chunk;
         }
      }
      if (current == null) {
         current = new VMResultChunk();
         current.setChunkStartTime(start);
         dc.getChunks().add(current);
      }
      return current;
   }

   private DatacollectorResult getOrCreateDatacollector(String collectorName, TestMethod test) {
      DatacollectorResult collectorResult = findCollector(collectorName, test);
      if (collectorResult == null) {
         collectorResult = new DatacollectorResult(collectorName);
         test.getDatacollectorResults().add(collectorResult);
      }
      
      return collectorResult;
   }

   public static DatacollectorResult findCollector(String collectorName, TestMethod test) {
      DatacollectorResult collectorResult = null;
      for (final DatacollectorResult currentCollectorResult : test.getDatacollectorResults()) {
         LOG.trace("Name: {} Collectorname: {}", currentCollectorResult.getName(), collectorName);
         if (currentCollectorResult.getName().equals(collectorName)) {
            collectorResult = currentCollectorResult;
         }
      }
      return collectorResult;
   }

   private TestMethod getOrCreateTestcase(VMResult performanceDataMeasure, String testcase) {
      TestMethod testMethod = null;
      for (TestMethod currentMethod : data.getMethods()) {
         if (currentMethod.getMethod().equals(testcase)) {
            testMethod = currentMethod;
            break;
         }
      }
      if (testMethod == null) {
         testMethod = new TestMethod(testcase);
         data.getMethods().add(testMethod);
      }
      return testMethod;
   }

}
