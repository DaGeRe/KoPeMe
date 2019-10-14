package kieker.monitoring.writer.filesystem.aggregateddata;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class StatisticUtil {
   public static void mergePartStatistic(final SummaryStatistics result, final StatisticalSummary splittedStatistic) {
      final long n = splittedStatistic.getN();
      final long k = n / 2;
      final double s = splittedStatistic.getStandardDeviation() * Math.sqrt(((double) n - 1) / n);
      if (n % 2 == 0) {
         buildStatisticEven(result, splittedStatistic, n, k, s);
      } else {
         buildStatisticUneven(result, splittedStatistic, n, k, s);
      }
   }
   
   public static double getMean(final List<StatisticalSummary> statistics) {
      final SummaryStatistics result = new SummaryStatistics();
      for (final StatisticalSummary splittedStatistic : statistics) {
         mergePartStatistic(result, splittedStatistic);
      }
      return result.getMean();
   }

   private static void buildStatisticUneven(final SummaryStatistics result, final StatisticalSummary splittedStatistic, final long n, final long k, final double s) {
      final double x_a = splittedStatistic.getMean() + Math.sqrt(((double) n) / (n - 1)) * s;
      final double x_b = splittedStatistic.getMean() - Math.sqrt(((double) n) / (n - 1)) * s;
      for (int i = 0; i < k; i++) {
         result.addValue(x_a);
      }
      for (long i = k; i < k * 2; i++) {
         result.addValue(x_b);
      }
      result.addValue(splittedStatistic.getMean());
   }

   private static void buildStatisticEven(final SummaryStatistics result, final StatisticalSummary splittedStatistic, final long n, final long k, final double s) {
      final double x_a = splittedStatistic.getMean() - s;
      final double x_b = splittedStatistic.getMean() + s;
      for (int i = 0; i < k; i++) {
         result.addValue(x_a);
      }
      for (long i = k; i < n; i++) {
         result.addValue(x_b);
      }
   }
}
