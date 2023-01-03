package de.dagere.kopeme.junit5.exampletests.mockito.mocked;

public class Station {

   public static String getStation() {
       return "inStation";
   }

   public String getNonStaticStation() {
      return "inStation";
   }
}