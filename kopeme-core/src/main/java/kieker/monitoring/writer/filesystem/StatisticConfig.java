package kieker.monitoring.writer.filesystem;

public class StatisticConfig {
   private final int warmup;
   private final double outlierFactor;

   public StatisticConfig(final int warmup, final double outlierFactor) {
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