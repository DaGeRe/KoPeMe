package de.dagere.kopeme.datastorage.xml;

import java.io.File;
import java.util.LinkedHashMap;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params.Param;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Chunk;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.kopemedata.VMResultChunk;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class XMLConversionLoader {
   public static de.dagere.kopeme.kopemedata.Kopemedata loadData(final File dataFile) {
      try {
         Unmarshaller unmarshaller = XMLDataLoader.jc.createUnmarshaller();
         final Kopemedata xmlData = (Kopemedata) unmarshaller.unmarshal(dataFile);

         de.dagere.kopeme.kopemedata.Kopemedata jsonData = new de.dagere.kopeme.kopemedata.Kopemedata(xmlData.getTestcases().getClazz());

         for (TestcaseType xmlTestmethod : xmlData.getTestcases().getTestcase()) {
            TestMethod jsonMethod = new TestMethod(xmlTestmethod.getName());
            jsonData.getMethods().add(jsonMethod);
            
            for (Datacollector xmlCollector : xmlTestmethod.getDatacollector()) {
               DatacollectorResult jsonCollector = new DatacollectorResult(xmlCollector.getName());
               jsonMethod.getDatacollectorResults().add(jsonCollector);
               
               for (Result xmlResult : xmlCollector.getResult()) {
                  VMResult jsonVMResult = convertXML2JSONResult(xmlResult);
                  
                  jsonCollector.getResults().add(jsonVMResult);
               }
               
               for (Chunk xmlChunk : xmlCollector.getChunk()) {
                  VMResultChunk jsonChunk = new VMResultChunk();
                  if (xmlChunk.getChunkStartTime() != null) {
                     jsonChunk.setChunkStartTime(xmlChunk.getChunkStartTime());
                  }
                  
                  jsonCollector.getChunks().add(jsonChunk);
                  for (Result xmlResult : xmlChunk.getResult()) {
                     VMResult jsonVMResult = convertXML2JSONResult(xmlResult);
                     jsonChunk.getResults().add(jsonVMResult);
                  }
               }
            }
         }
         
         return jsonData;
      } catch (JAXBException e) {
         throw new RuntimeException(e);
      }

   }

   private static VMResult convertXML2JSONResult(Result xmlResult) {
      VMResult jsonVMResult = new VMResult();
      jsonVMResult.setValue(xmlResult.getValue());
      jsonVMResult.setDeviation(xmlResult.getDeviation());
      
      if (xmlResult.getParams() != null) {
         jsonVMResult.setParameters(new LinkedHashMap<>());
         for (Param param : xmlResult.getParams().getParam()) {
            jsonVMResult.getParameters().put(param.getKey(), param.getValue());
         }
      }
      return jsonVMResult;
   }
}
