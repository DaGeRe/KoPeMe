package de.dagere.kopeme.kopemedata;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResultConfiguration {
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean showStart;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean executeBeforeClassInMeasurement;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean redirectToTemp;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean redirectToNull;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private Boolean useKieker;

   public Boolean isShowStart() {
      return showStart;
   }

   public void setShowStart(Boolean showStart) {
      this.showStart = showStart;
   }

   public Boolean isExecuteBeforeClassInMeasurement() {
      return executeBeforeClassInMeasurement;
   }

   public void setExecuteBeforeClassInMeasurement(Boolean executeBeforeClassInMeasurement) {
      this.executeBeforeClassInMeasurement = executeBeforeClassInMeasurement;
   }

   public Boolean isRedirectToTemp() {
      return redirectToTemp;
   }

   public void setRedirectToTemp(Boolean redirectToTemp) {
      this.redirectToTemp = redirectToTemp;
   }

   public Boolean isRedirectToNull() {
      return redirectToNull;
   }

   public void setRedirectToNull(Boolean redirectToNull) {
      this.redirectToNull = redirectToNull;
   }

   public Boolean isUseKieker() {
      return useKieker;
   }

   public void setUseKieker(Boolean useKieker) {
      this.useKieker = useKieker;
   }

}