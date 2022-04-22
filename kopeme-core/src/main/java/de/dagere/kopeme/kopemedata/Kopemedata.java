package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.datacollection.TimeDataCollectorNoGC;

public class Kopemedata {
   
   private String clazz;
   private List<TestMethod> methods = new LinkedList<>();

   public Kopemedata(@JsonProperty("clazz") String clazz) {
      this.clazz = clazz;
   }
   
   public String getClazz() {
      return clazz;
   }
   
   public void setClazz(String clazz) {
      this.clazz = clazz;
   }

   public List<TestMethod> getMethods() {
      return methods;
   }

   public void setMethods(List<TestMethod> methods) {
      this.methods = methods;
   }
   
   /**
    * Gets the first result if only one is present - since this happens from time to time (especially for new test cases), this may be useful.
    * @return
    */
   @JsonIgnore
   public VMResult getFirstResult() {
      return methods.get(0).getDatacollectorResults().get(0).getResults().get(0);
   }
   
   @JsonIgnore
   public List<VMResultChunk> getChunks() {
      return methods.get(0).getDatacollectorResults().get(0).getChunks();
   }
   
   @JsonIgnore
   public TestMethod getFirstMethodResult() {
      return methods.get(0);
   }
   
   @JsonIgnore
   public List<VMResult> getFirstDatacollectorContent(){
      return getFirstMethodResult().getDatacollectorResults().get(0).getResults();
   }
   
   @JsonIgnore
   public DatacollectorResult getFirstTimeDataCollector() {
      DatacollectorResult oneRunDatacollector = null;
      
      TestMethod firstMethod = getFirstMethodResult();
      
      for (final DatacollectorResult collector : firstMethod.getDatacollectorResults()) {
         if (collector.getName().equals(TimeDataCollector.class.getName()) || collector.getName().equals(TimeDataCollectorNoGC.class.getName())) {
            oneRunDatacollector = collector;
         }
      }
      if (oneRunDatacollector == null) {
         throw new RuntimeException("Did not find " + TimeDataCollector.class.getName() + " or " + TimeDataCollectorNoGC.class);
      }
      return oneRunDatacollector;
   }
}
