package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestClazz {
   private final String clazz;
   private List<TestMethod> methods = new LinkedList<>();

   public TestClazz(@JsonProperty("clazz") String clazz) {
      this.clazz = clazz;
   }
   
   public String getClazz() {
      return clazz;
   }

   public List<TestMethod> getMethods() {
      return methods;
   }

   public void setMethods(List<TestMethod> methods) {
      this.methods = methods;
   }
   
   
}