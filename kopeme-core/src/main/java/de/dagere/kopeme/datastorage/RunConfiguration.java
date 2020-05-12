package de.dagere.kopeme.datastorage;

import de.dagere.kopeme.annotations.PerformanceTest;

public class RunConfiguration {
   private final int warmupExecutions, repetitions;
   private final boolean showStart, redirectToTemp, redirectToNull;
   private boolean saveValues;
   
   public RunConfiguration(int warmupExecutions, int repetitions, boolean showStart, boolean redirectToTemp, boolean redirectToNull, boolean saveValues) {
      this.warmupExecutions = warmupExecutions;
      this.repetitions = repetitions;
      this.showStart = showStart;
      this.redirectToTemp = redirectToTemp;
      this.redirectToNull = redirectToNull;
      this.saveValues = saveValues;
   }

   public RunConfiguration(PerformanceTest annotation) {
      warmupExecutions = annotation.warmupExecutions();
      repetitions = annotation.repetitions();
      showStart = annotation.showStart();
      redirectToTemp = annotation.redirectToTemp();
      redirectToNull = annotation.redirectToNull();
      saveValues = annotation.logFullData();
   }

   public int getWarmupExecutions() {
      return warmupExecutions;
   }

   public int getRepetitions() {
      return repetitions;
   }

   public boolean isShowStart() {
      return showStart;
   }

   public boolean isRedirectToTemp() {
      return redirectToTemp;
   }

   public boolean isRedirectToNull() {
      return redirectToNull;
   }

   public boolean isSaveValues() {
      return saveValues;
   }
   
   public void setSaveValues(boolean saveValues) {
      this.saveValues = saveValues;
   }
}
