package de.dagere.kopeme.kopemedata;

import java.util.List;

class DatacollectorResult {
   private List<VMResultChunk> chunks;

   private List<VMResult> results;

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