package de.dagere.kopeme.kopemedata;

public class ResultConfiguration {
   private boolean showStart;
   private boolean executeBeforeClassInMeasurement;
   private boolean redirectToTemp;
   private boolean redirectToNull;
   private boolean useKieker;

   public boolean isShowStart() {
      return showStart;
   }

   public void setShowStart(boolean showStart) {
      this.showStart = showStart;
   }

   public boolean isExecuteBeforeClassInMeasurement() {
      return executeBeforeClassInMeasurement;
   }

   public void setExecuteBeforeClassInMeasurement(boolean executeBeforeClassInMeasurement) {
      this.executeBeforeClassInMeasurement = executeBeforeClassInMeasurement;
   }

   public boolean isRedirectToTemp() {
      return redirectToTemp;
   }

   public void setRedirectToTemp(boolean redirectToTemp) {
      this.redirectToTemp = redirectToTemp;
   }

   public boolean isRedirectToNull() {
      return redirectToNull;
   }

   public void setRedirectToNull(boolean redirectToNull) {
      this.redirectToNull = redirectToNull;
   }

   public boolean isUseKieker() {
      return useKieker;
   }

   public void setUseKieker(boolean useKieker) {
      this.useKieker = useKieker;
   }

}