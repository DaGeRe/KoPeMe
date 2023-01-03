package de.dagere.kopeme.junit5;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.exampletests.ExampleExtension5TestWithoutWarmUpThrowingOOM;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionTestThrowingOOM;
import de.dagere.kopeme.junit5.exampletests.mockito.ExampleExtension5MockitoTestThrowingOOM;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import de.dagere.kopeme.kopemedata.Kopemedata;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestJUnit5ThrowingOOM {

   @Test
   public void testCorrectThrowingOOM() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestThrowingOOM.class);

      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
   }

   @Test
   public void testMockitoCorrectThrowingOOM() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtension5MockitoTestThrowingOOM.class);

      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
   }

   @Test
   public void testWithoutWarmUpCorrectThrowingOOM() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtension5TestWithoutWarmUpThrowingOOM.class);

      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
      
      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertTrue(data.getFirstResult().isError());
   }
}
