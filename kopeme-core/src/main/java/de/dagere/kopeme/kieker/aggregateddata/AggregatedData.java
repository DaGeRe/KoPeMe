package de.dagere.kopeme.kieker.aggregateddata;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.dagere.kopeme.kieker.writer.StatisticConfig;

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
      final StatisticalSummary result = AggregateSummaryStatistics.aggregate(getStatistic().values());
      return result;
   }

   public Map<Long, StatisticalSummary> getStatistic() {
      return statistic;
   }
}