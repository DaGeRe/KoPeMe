package kieker.monitoring.writer.filesystem;

public class StatisticConfig {
   private final int warmup;
   private final double outlierFactor;

   public StatisticConfig(final int warmup, final double outlierFactor) {
      if (outlierFactor < 1.0) {
         throw new RuntimeException("Outlier factors needs to be above 1.0");
      }
      this.warmup = warmup;
      this.outlierFactor = outlierFactor;

   }

   public int getWarmup() {
      return warmup;
   }

   public double getOutlierFactor() {
      return outlierFactor;
   }
}