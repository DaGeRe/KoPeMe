package kieker.monitoring.writer.filesystem;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

class AggregatedData {

   @JsonIgnore
   final File containedFile;
   private int warmup = 0;

   @JsonIgnore
   private final int maxWarmup;

   @JsonSerialize(using = SummaryStatisticsSerializer.class)
   @JsonDeserialize(using = SummaryStatisticsDeserializer.class)
   private final SummaryStatistics statistic;

   @JsonCreator
   public AggregatedData(final @JsonProperty(value = "warmup") int warmup, final @JsonProperty(value = "statistic") SummaryStatistics statistic) {
      this.containedFile = null;
      this.warmup = warmup;
      this.maxWarmup = -1;
      this.statistic = statistic;
   }

   public AggregatedData(final File containedFile, final int maxWarmup) {
      this.containedFile = containedFile;
      this.maxWarmup = maxWarmup;
      this.statistic = new SummaryStatistics();
   }

   public void addValue(final long value) {
      if (warmup < maxWarmup) {
         warmup++;
      } else {
         statistic.addValue(value);
      }
   }

   public File getContainedFile() {
      return containedFile;
   }

   public int getWarmup() {
      return warmup;
   }

   public SummaryStatistics getStatistic() {
      return statistic;
   }
}