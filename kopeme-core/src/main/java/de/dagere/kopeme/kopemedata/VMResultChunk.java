package de.dagere.kopeme.kopemedata;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Saves one chunk, i.e. one belonging together set of data. This might be data from the executions of two versions of a benchmark on the same computer.
 * 
 * @author DaGeRe
 *
 */
public class VMResultChunk {
   private long chunkStartTime;

   private List<VMResult> results = new LinkedList<>();

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
   
   @JsonIgnore
   public Set<String> getCommits(){
      Set<String> commits = new HashSet<>();
      for (VMResult result : results) {
         commits.add(result.getCommit());
      }
      return commits;
   }
}