package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

public class Fulldata {
   private String fileName;
   private List<MeasuredValue> values = new LinkedList<>();

   public String getFileName() {
      return fileName;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public List<MeasuredValue> getValues() {
      return values;
   }

   public void setValues(List<MeasuredValue> values) {
      this.values = values;
   }
}
