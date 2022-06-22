package de.dagere.kopeme.junit5;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.exampletests.ExampleExtension5MockitoTest;
import de.dagere.kopeme.junit5.exampletests.ExampleExtensionInjectMockJUnit5Test;
import de.dagere.kopeme.kopemedata.Kopemedata;

/**
 * Tests just whether JUnit 5 execution works
 * 
 * @author reichelt
 * 
 */
public class TestJUnit5Mockito {

   public static Logger log = LogManager.getLogger(TestJUnit5Mockito.class);

   @Test
   public void testRegularExecution() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtension5MockitoTest.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      double averageDurationInMs = data.getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0).getValue() /1000000;  
      System.out.println(file.getAbsolutePath() + "=" + averageDurationInMs);

      MatcherAssert.assertThat((int) averageDurationInMs, Matchers.greaterThan(20));
   }

   @Test
   public void testWithFinalInjectedField() {
      ExampleExtensionInjectMockJUnit5Test.finishCount = 0;
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionInjectMockJUnit5Test.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      double averageDurationInMs = data.getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0).getValue() /1000000;  
      System.out.println(file.getAbsolutePath() + "=" + averageDurationInMs);

      
      
      MatcherAssert.assertThat(ExampleExtensionInjectMockJUnit5Test.finishCount, Matchers.is(3));
   }
   
}
