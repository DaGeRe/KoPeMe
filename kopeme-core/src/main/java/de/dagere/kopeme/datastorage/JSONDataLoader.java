package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dagere.kopeme.datacollection.tempfile.TempfileReader;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;
import de.dagere.kopeme.datacollection.tempfile.WrittenResultReaderTextFormat;
import de.dagere.kopeme.datastorage.xml.XMLConversionLoader;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;

public class JSONDataLoader implements DataLoader {

   private final File file;

   public JSONDataLoader(File file) {
      this.file = file;
   }

   public Kopemedata getFullData() {
      return loadData(file);
   }

   public DatacollectorResult getData(String collectorName) {
      Kopemedata data = getFullData();
      DatacollectorResult result = JSONDataStorer.findCollector(collectorName, data.getMethods().get(0));
      return result;
   }

   public static Kopemedata loadData(File dataFile, int warmup) {
      final Kopemedata data = loadData(dataFile);
      for (TestMethod testcase : data.getMethods()) {
         for (VMResult result : testcase.getDatacollectorResults().get(0).getResults()) {
            if (result.getFulldata().getFileName() != null) {
               Fulldata replacedFulldata = readFulldata(dataFile, warmup, testcase, result);
               result.setFulldata(replacedFulldata);
            }
         }
      }

      return data;
   }

   public static Kopemedata loadWarmedupData(File dataFile) {
      final Kopemedata data = loadData(dataFile);
      for (TestMethod testcase : data.getMethods()) {
         for (VMResult result : testcase.getDatacollectorResults().get(0).getResults()) {
            if (result.getFulldata().getFileName() != null) {
               Fulldata replacedFulldata = readFulldata(dataFile, (int) (result.getIterations() / 2), testcase, result);
               result.setFulldata(replacedFulldata);
            }
         }
      }

      return data;
   }

   private static Fulldata readFulldata(File jsonFile, int warmup, TestMethod testcase, VMResult result) {
      final File file = new File(result.getFulldata().getFileName());
      final File dataFile = new File(jsonFile.getParentFile(), file.getName());

      Fulldata replacedFulldata = executeReading(testcase.getDatacollectorResults().get(0).getName(), dataFile, warmup);
      return replacedFulldata;
   }

   private static Fulldata executeReading(final String currentDatacollector, final File dataFile, final int warmup) {
      final TempfileReader reader;
      if (dataFile.getName().endsWith(".tmp")) {
         reader = new WrittenResultReaderTextFormat(dataFile);
      } else {
         reader = new WrittenResultReader(dataFile);
      }
      final Set<String> dataCollectors = new HashSet<>();
      dataCollectors.add(currentDatacollector);
      reader.read(null, dataCollectors);

      Fulldata replacedFulldata = reader.createFulldata(warmup, currentDatacollector);
      return replacedFulldata;
   }

   public static Kopemedata loadData(File file2) {
      try {
         if (file2.getName().endsWith(".json")) {
            if (file2.exists()) {
               return KoPeMeConstants.OBJECTMAPPER.readValue(file2, Kopemedata.class);
            } else {
               return new Kopemedata("");
            }
         } else if (file2.getName().endsWith(".xml")) {
            Kopemedata kopemedata = XMLConversionLoader.loadData(file2);
            String pureFileName = file2.getName().substring(0, file2.getName().length() - ".xml".length());
            File jsonFile = new File(file2.getParentFile(), pureFileName + ".json");
            JSONDataStorer.storeData(jsonFile, kopemedata);
            file2.delete();
            return kopemedata;
         } else {
            return null;
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}
