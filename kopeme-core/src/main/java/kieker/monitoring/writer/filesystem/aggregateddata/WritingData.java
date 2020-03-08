package kieker.monitoring.writer.filesystem.aggregateddata;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kieker.monitoring.writer.filesystem.StatisticConfig;

public class WritingData extends AggregatedData {

   @JsonIgnore
   protected Map<Long, StatisticalSummary> writeStatistic = new LinkedHashMap<>();

   private SummaryStatistics newestStatistic = new SummaryStatistics();
   private long newestTime;
   private SummaryStatistics overallStatistics = new SummaryStatistics();

   public WritingData(final File containedFile, final StatisticConfig statisticConfig) {
      super(containedFile, statisticConfig);
      nextLine();
   }

   public void persistStatistic() {
      synchronized (writeStatistic) {
         nextLine();
      }
   }

   public void addValue(final long value) {
      if (warmup < statisticConfig.getWarmup()) {
         warmup++;
      } else {
         final boolean add = decideAdding(value);
         if (add) {
            synchronized (writeStatistic) {
               overallStatistics.addValue(value);
               newestStatistic.addValue(value);
            }
         }
      }
   }

   private void nextLine() {
      getAvailableTimeslot();
      writeStatistic.put(newestTime, newestStatistic);
   }

   private void getAvailableTimeslot() {
      newestTime = System.currentTimeMillis();
      while (writeStatistic.get(newestTime) != null) {
         newestTime++;
      }
   }

   private boolean decideAdding(final long value) {
      boolean add = false;
      if (overallStatistics.getN() > statisticConfig.getWarmup() && overallStatistics.getN() > 30) {
         if (statisticConfig.getOutlierFactor() == -1 || value < overallStatistics.getMean() * statisticConfig.getOutlierFactor()) {
            add = true;
         }
      } else {
         add = true;
      }
      return add;
   }

   @JsonIgnore
   public SummaryStatistics getOverallStatistic() {
      return overallStatistics;
   }

   public Map<Long, StatisticalSummary> getStatistic() {
      return writeStatistic;
   }

   public StatisticalSummary getCurrentStatistic() {
      return newestStatistic;
   }

   public long getCurrentStart() {
      return newestTime;
   }
}
