package de.dagere.kopeme.kieker.writer;

public class StatisticConfig {
   private int warmup;
   private final double outlierFactor;
   private final int writeInterval;
   private final int entriesPerFile;

   public StatisticConfig(final int warmup, final double outlierFactor, int writeInterval, int entriesPerFile) {
      if (outlierFactor < 1.0 && outlierFactor != -1.0) {
         throw new RuntimeException("Outlier factors needs to be above 1.0");
      }
      this.warmup = warmup;
      this.outlierFactor = outlierFactor;
      this.writeInterval = writeInterval;
      this.entriesPerFile = entriesPerFile;

   }

   public int getWarmup() {
      return warmup;
   }

   public double getOutlierFactor() {
      return outlierFactor;
   }
   
   public int getWriteInterval() {
      return writeInterval;
   }
   
   public int getEntriesPerFile() {
      return entriesPerFile;
   }

   public void setWarmup(int warmup) {
      this.warmup = warmup;
   }
}