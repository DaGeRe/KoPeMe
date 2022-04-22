package de.dagere.kopeme.datastorage.xml;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.Result.Params.Param;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.VMResult;

public class JSON2XML {
   public static Result toXMLResult(VMResult jsonResult) {
      Result xmlResult = new Result();
      
      xmlResult.setValue(jsonResult.getValue());
      xmlResult.setDeviation(jsonResult.getDeviation());
      xmlResult.setMin(jsonResult.getMin());
      xmlResult.setMax(jsonResult.getMax());
      
      xmlResult.setWarmup(jsonResult.getWarmup());
      xmlResult.setIterations(jsonResult.getIterations());
      xmlResult.setRepetitions(jsonResult.getRepetitions());
      
      xmlResult.setCpu(jsonResult.getCpu());
      xmlResult.setMemory(jsonResult.getMemory());
      
      xmlResult.setCputemperature(jsonResult.getCputemperature());
      xmlResult.setDate(jsonResult.getDate());
      xmlResult.setError(jsonResult.isError());
      xmlResult.setFailure(jsonResult.isFailure());
      
      if (jsonResult.getFulldata() != null) {
         if (jsonResult.getFulldata().getFileName() != null) {
            xmlResult.setFulldata(new Fulldata());
            xmlResult.getFulldata().setFileName(jsonResult.getFulldata().getFileName());
         } else if (jsonResult.getFulldata().getValues() != null) {
            xmlResult.setFulldata(new Fulldata());
            for (MeasuredValue jsonValue : jsonResult.getFulldata().getValues()) {
               Value xmlValue = new Value();
               xmlValue.setStart(jsonValue.getStartTime());
               xmlValue.setValue(jsonValue.getValue());
               xmlResult.getFulldata().getValue().add(xmlValue);
            }
         }
      }
      
      if (jsonResult.getParameters() != null) {
         Params params = new Params();
         Param param = new Param();
         param.setKey(KoPeMeConstants.JUNIT_PARAMETERIZED);
         param.setValue(jsonResult.getParameters().get(KoPeMeConstants.JUNIT_PARAMETERIZED));
         params.getParam().add(param);
         xmlResult.setParams(params);
      }
      
      return xmlResult;
   }
}
