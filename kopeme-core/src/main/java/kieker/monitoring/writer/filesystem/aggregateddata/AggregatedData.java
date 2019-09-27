package kieker.monitoring.writer.filesystem.aggregateddata;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import kieker.monitoring.writer.filesystem.StatisticConfig;

public class AggregatedData {

   @JsonIgnore
   protected final File containedFile;
   protected int warmup = 0;

   @JsonIgnore
   protected final StatisticConfig statisticConfig;

   protected Map<Long, StatisticalSummary> statistic;

   @JsonCreator
   public AggregatedData(final @JsonProperty(value = "warmup") int warmup,
         final @JsonProperty(value = "statistic") Map<Long, StatisticalSummary> statistic) {
      this.containedFile = null;
      this.warmup = 0;
      this.statistic = statistic;
      statisticConfig = null;
   }

   protected AggregatedData(final File containedFile, final StatisticConfig statisticConfig) {
      this.containedFile = containedFile;
      this.statisticConfig = statisticConfig;
      this.statistic = new LinkedHashMap<>();
   }

   public File getContainedFile() {
      return containedFile;
   }

   public int getWarmup() {
      return warmup;
   }

   @JsonIgnore
   public StatisticalSummary getOverallStatistic() {
      final SummaryStatistics result = new SummaryStatistics();
      for (final StatisticalSummary splittedStatistic : getStatistic().values()) {
         StatisticUtil.mergePartStatistic(result, splittedStatistic);
      }
      return result;
   }

   @JsonSerialize(contentUsing = SummaryStatisticsSerializer.class)
   @JsonDeserialize(contentUsing = SummaryStatisticsDeserializer.class)
   public Map<Long, StatisticalSummary> getStatistic() {
      return statistic;
   }
}