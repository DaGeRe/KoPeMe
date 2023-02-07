package de.dagere.kopeme.junit5;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionTestFailure;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionTestThrowing;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;
import de.dagere.kopeme.kopemedata.Kopemedata;

class TestJUnit5Throwing {

   @Test
   void testCorrectThrowing() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestThrowing.class);

      Assertions.assertTrue(KoPeMeExtension.isLastRunFailed());

      Kopemedata data = JSONDataLoader.loadData(file);
      Assertions.assertTrue(data.getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0).isError());
   }

   @Test
   void testCorrectFailure() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestFailure.class);

      Assertions.assertTrue(KoPeMeExtension.isLastRunFailed());

      Kopemedata data = JSONDataLoader.loadData(file);
      Assertions.assertTrue(data.getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0).isFailure());
   }
}
