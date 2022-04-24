package de.dagere.kopeme.datastorage;

import de.dagere.kopeme.annotations.AnnotationChecker;
import de.dagere.kopeme.annotations.PerformanceTest;

public class RunConfiguration {
   private final int warmupExecutions, repetitions;
   private final boolean showStart, redirectToTemp, redirectToNull, useKieker;
   private boolean saveValues, executeBeforeClassInMeasurement;

   public RunConfiguration(final int warmupExecutions, final int repetitions, final boolean showStart, final boolean redirectToTemp,
         final boolean redirectToNull, final boolean saveValues, final boolean executeBeforeClassInMeasurement, boolean useKieker) {
      this.warmupExecutions = warmupExecutions;
      this.repetitions = repetitions;
      this.showStart = showStart;
      this.redirectToTemp = redirectToTemp;
      this.redirectToNull = redirectToNull;
      this.saveValues = saveValues;
      this.executeBeforeClassInMeasurement = executeBeforeClassInMeasurement;
      this.useKieker = useKieker;
   }

   public RunConfiguration(final PerformanceTest annotation) {
      warmupExecutions = annotation.warmup();
      repetitions = annotation.repetitions();
      showStart = annotation.showStart();
      redirectToTemp = annotation.redirectToTemp();
      redirectToNull = annotation.redirectToNull();
      saveValues = annotation.logFullData();
      executeBeforeClassInMeasurement = annotation.executeBeforeClassInMeasurement();
      this.useKieker = annotation.useKieker();
      
      AnnotationChecker.check(annotation);
   }

   public int getWarmupExecutions() {
      return warmupExecutions;
   }

   public boolean isExecuteBeforeClassInMeasurement() {
      return executeBeforeClassInMeasurement;
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

   public void setSaveValues(final boolean saveValues) {
      this.saveValues = saveValues;
   }
   
   public boolean isUseKieker() {
      return useKieker;
   }
}
