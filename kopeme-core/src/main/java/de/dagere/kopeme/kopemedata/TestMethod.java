package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestMethod {
   private final String method;

   private List<DatacollectorResult> datacollectorResults = new LinkedList<>();

   public TestMethod(@JsonProperty("method") String method) {
      this.method = method;
   }

   public String getMethod() {
      return method;
   }

   public List<DatacollectorResult> getDatacollectorResults() {
      return datacollectorResults;
   }

   public void setDatacollectorResults(List<DatacollectorResult> datacollectorResults) {
      this.datacollectorResults = datacollectorResults;
   }

}