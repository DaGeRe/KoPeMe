package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.kopeme.datastorage.xml.XMLConversionLoader;
import de.dagere.kopeme.datastorage.xml.XMLDataLoader;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;

public class JSONDataLoader implements DataLoader {

   private final File file;

   public JSONDataLoader(File file) {
      this.file = file;
   }

   @Override
   public Map<String, Map<Date, Long>> getData() {
      // TODO Auto-generated method stub
      return null;
   }

   public Kopemedata getFullData() {
      try {
         return new ObjectMapper().readValue(file, Kopemedata.class);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public DatacollectorResult getData(String collectorName) {
      Kopemedata data = getFullData();
      DatacollectorResult result = JSONDataStorer.findCollector(collectorName, data.getMethods().get(0));
      return result;
   }

   public static Kopemedata loadData(File file2) {
      try {
         if (file2.getName().endsWith(".json")) {
            return new ObjectMapper().readValue(file2, Kopemedata.class);
         } else if (file2.getName().endsWith(".xml")) {
            return XMLConversionLoader.loadData(file2);
         } else {
            return null;
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}
