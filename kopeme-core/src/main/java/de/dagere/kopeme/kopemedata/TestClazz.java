package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

public class TestClazz {
   private String clazz;
   private List<TestMethod> methods = new LinkedList<>();

   public String getClazz() {
      return clazz;
   }

   public void setClazz(String clazz) {
      this.clazz = clazz;
   }

   public List<TestMethod> getMethods() {
      return methods;
   }

   public void setMethods(List<TestMethod> methods) {
      this.methods = methods;
   }
   
   
}