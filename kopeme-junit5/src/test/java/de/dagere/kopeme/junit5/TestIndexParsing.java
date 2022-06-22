package de.dagere.kopeme.junit5;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.junit5.extension.KoPeMeJUnit5Starter;

public class TestIndexParsing {
   
   @Test
   public void testIndexParsing() {
      int index = KoPeMeJUnit5Starter.getIndexFromName("run #1 with [parameter: 1, parameter2: 5]");
      Assert.assertEquals(1, index);
      
      int index2 = KoPeMeJUnit5Starter.getIndexFromName("run #2 with [parameter: 1, parameter2: 5]");
      Assert.assertEquals(2, index2);
   }
   
   @Test
   public void testSimpleParsing() {
      int index = KoPeMeJUnit5Starter.getIndexFromName("[1] 0");
      Assert.assertEquals(1, index);
      
      int index2 = KoPeMeJUnit5Starter.getIndexFromName("[2] 2");
      Assert.assertEquals(2, index2);
   }
}
