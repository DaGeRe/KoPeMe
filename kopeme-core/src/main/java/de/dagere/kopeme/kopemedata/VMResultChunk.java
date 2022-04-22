package de.dagere.kopeme.kopemedata;

import java.util.List;

/**
 * Saves one chunk, i.e. one belonging together set of data. This might be data from the executions of two versions of a benchmark on the same computer.
 * 
 * @author DaGeRe
 *
 */
public class VMResultChunk {
   private long chunkStartTime;

   private List<VMResult> results;

   public long getChunkStartTime() {
      return chunkStartTime;
   }

   public void setChunkStartTime(long chunkStartTime) {
      this.chunkStartTime = chunkStartTime;
   }

   public List<VMResult> getResults() {
      return results;
   }

   public void setResults(List<VMResult> results) {
      this.results = results;
   }
}