package de.dagere.kopeme.datastorage.xml;

import java.io.File;
import java.util.LinkedHashMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.Result.Params.Param;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Chunk;
import de.dagere.kopeme.generated.Versioninfo;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.kopemedata.VMResultChunk;

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
      
      jsonVMResult.setMin(xmlResult.getMin());
      jsonVMResult.setMax(xmlResult.getMax());
      
      jsonVMResult.setIterations(xmlResult.getIterations());
      jsonVMResult.setWarmup(xmlResult.getWarmup());
      jsonVMResult.setRepetitions(xmlResult.getRepetitions());
      
      Versioninfo version = xmlResult.getVersion();
      if (version != null) {
         jsonVMResult.setCommit(version.getGitversion());
      }
      
      transformConfiguration(xmlResult, jsonVMResult);
      
      if (xmlResult.getParams() != null) {
         jsonVMResult.setParameters(new LinkedHashMap<>());
         for (Param param : xmlResult.getParams().getParam()) {
            jsonVMResult.getParameters().put(param.getKey(), param.getValue());
         }
      }
      
      jsonVMResult.setJavaVersion(xmlResult.getJavaVersion());
      jsonVMResult.setCpu(xmlResult.getCpu());
      jsonVMResult.setMemory(xmlResult.getMemory());
      jsonVMResult.setDate(xmlResult.getDate());
      jsonVMResult.setCpuTemperature(xmlResult.getCputemperature());
      jsonVMResult.setFailure(xmlResult.isFailure() != null && xmlResult.isFailure() == false ? false : true);
      jsonVMResult.setError(xmlResult.isError() != null && xmlResult.isError() == false ? false : true);
      
      transformFulldata(xmlResult, jsonVMResult);
      
      return jsonVMResult;
   }

   private static void transformConfiguration(Result xmlResult, VMResult jsonVMResult) {
      jsonVMResult.getVmRunConfiguration().setShowStart(xmlResult.isShowStart());
      jsonVMResult.getVmRunConfiguration().setExecuteBeforeClassInMeasurement(xmlResult.isExecuteBeforeClassInMeasurement());
      jsonVMResult.getVmRunConfiguration().setRedirectToTemp(xmlResult.isRedirectToTemp());
      jsonVMResult.getVmRunConfiguration().setRedirectToNull(xmlResult.isRedirectToNull());
      jsonVMResult.getVmRunConfiguration().setUseKieker(xmlResult.isUseKieker());
   }

   private static void transformFulldata(Result xmlResult, VMResult jsonVMResult) {
      if (xmlResult.getFulldata() != null) {
         Fulldata fulldata = new Fulldata();
         jsonVMResult.setFulldata(fulldata);
         
         for (Value value : xmlResult.getFulldata().getValue()) {
            MeasuredValue measuredValue = new MeasuredValue();
            measuredValue.setStartTime(value.getStart());
            measuredValue.setValue(value.getValue());
            fulldata.getValues().add(measuredValue);
         }
      }
   }
}
