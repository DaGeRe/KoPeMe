package de.dagere.kopeme.junit5;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionTestFailure;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionTestThrowing;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import de.dagere.kopeme.kopemedata.Kopemedata;

public class TestJUnit5Throwing {
   
   @Test
   public void testCorrectThrowing() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestThrowing.class);
      
      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
      
      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertTrue(data.getMethods().isEmpty());
   }
   
   @Test
   public void testCorrectFailure() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestFailure.class);
      
      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
      
      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertTrue(data.getMethods().isEmpty());
   }
}
