package kieker.monitoring.writer.filesystem.aggregateddata;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import kieker.monitoring.writer.filesystem.StatisticConfig;

public class WritingData extends AggregatedData {

   @JsonIgnore
   protected SummaryStatistics writeStatistic = new SummaryStatistics();

   public WritingData(final File containedFile, final StatisticConfig statisticConfig) {
      super(containedFile, statisticConfig);
   }

   public void addValue(final long value) {
      if (warmup < statisticConfig.getWarmup()) {
         warmup++;
      } else {
         final boolean add = decideAdding(value);
         if (add) {
            writeStatistic.addValue(value);
         }
      }
   }

   private boolean decideAdding(final long value) {
      boolean add = false;
      if (writeStatistic.getN() > statisticConfig.getWarmup() && writeStatistic.getN() > 30) {
         if (value < writeStatistic.getMean() * statisticConfig.getOutlierFactor()) {
            add = true;
         }
      } else {
         add = true;
      }
      return add;
   }

   @JsonSerialize(using = SummaryStatisticsSerializer.class)
   @JsonDeserialize(using = SummaryStatisticsDeserializer.class)
   public StatisticalSummary getStatistic() {
      return writeStatistic;
   }
}