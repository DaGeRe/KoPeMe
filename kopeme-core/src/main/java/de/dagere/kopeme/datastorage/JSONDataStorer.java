package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;


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
      data = new Kopemedata();
      data.getTestclazzes().get(0).setClazz(classname);
      storeData();
   }
   
   private void storeData() {
      try {
         new ObjectMapper().writeValue(file, data);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void storeValue(VMResult performanceDataMeasure, String testcase, String collectorName) {
      // TODO Auto-generated method stub
      
   }

}
