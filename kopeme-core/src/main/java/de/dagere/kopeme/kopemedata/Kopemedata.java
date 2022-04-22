package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

public class Kopemedata {
   List<TestClazz> testclazzes = new LinkedList<>();

   public List<TestClazz> getTestclazzes() {
      return testclazzes;
   }

   public void setTestclazzes(List<TestClazz> testclazzes) {
      this.testclazzes = testclazzes;
   }
}
