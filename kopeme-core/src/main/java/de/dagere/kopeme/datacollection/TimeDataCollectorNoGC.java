package de.dagere.kopeme.datacollection;

import de.dagere.kopeme.datacollection.DataCollector;

public final class TimeDataCollectorNoGC extends DataCollector {

   private long start;
   private long stop;
   private long summarizedValue = 0;

   /**
    * Initializes the TimeDataCollector.
    */
   public TimeDataCollectorNoGC() {
      System.out.println("No GC Collector");
      start = 0;
      stop = 0;
   }

   @Override
   public int getPriority() {
      return Integer.MAX_VALUE;
   }

   @Override
   public void startCollection() {
      start = System.nanoTime();
   }

   @Override
   public void stopCollection() {
      stop = System.nanoTime();
   }

   @Override
   public long getValue() {
      return summarizedValue != 0 ? summarizedValue : (stop - start);
   }

   @Override
   public void startOrRestartCollection() {
      summarizedValue += (stop - start);
      System.out.println("Measured: " + summarizedValue);
      startCollection();
   }

}
