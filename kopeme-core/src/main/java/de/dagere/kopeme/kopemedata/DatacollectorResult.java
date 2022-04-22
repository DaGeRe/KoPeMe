package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatacollectorResult {
   
   private final String name;

   private List<VMResultChunk> chunks = new LinkedList<>();

   private List<VMResult> results = new LinkedList<>();
   
   public DatacollectorResult(@JsonProperty("name") String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
   
   public List<VMResultChunk> getChunks() {
      return chunks;
   }

   public void setChunks(List<VMResultChunk> chunks) {
      this.chunks = chunks;
   }

   public List<VMResult> getResults() {
      return results;
   }

   public void setResults(List<VMResult> results) {
      this.results = results;
   }
}