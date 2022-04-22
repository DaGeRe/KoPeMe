package de.dagere.kopeme.kopemedata;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Kopemedata {
   private List<TestClazz> testclazzes = new LinkedList<>();

   public List<TestClazz> getTestclazzes() {
      return testclazzes;
   }

   public void setTestclazzes(List<TestClazz> testclazzes) {
      this.testclazzes = testclazzes;
   }
   
   /**
    * Gets the first result if only one is present - since this happens from time to time (especially for new test cases), this may be useful.
    * @return
    */
   @JsonIgnore
   public VMResult getFirstResult() {
      return testclazzes.get(0).getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0);
   }
}
