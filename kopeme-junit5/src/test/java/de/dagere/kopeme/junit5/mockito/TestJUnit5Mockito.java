package de.dagere.kopeme.junit5.mockito;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.JUnit5RunUtil;
import de.dagere.kopeme.junit5.exampletests.mockito.ExampleCleanCachesByMockMethodTest;
import de.dagere.kopeme.junit5.exampletests.mockito.ExampleExtension5MockitoTest;
import de.dagere.kopeme.junit5.exampletests.mockito.ExampleExtensionInjectMockJUnit5Test;
import de.dagere.kopeme.junit5.exampletests.mockito.ExampleExtensionMockitoBeforeTest;
import de.dagere.kopeme.junit5.exampletests.mockito.InitializeInBeforeEach;
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
   
   @Test
   public void testBeforeBeforeWithMeasurementSplitted() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionMockitoBeforeTest.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertFalse(data.getFirstDatacollectorContent().get(0).isError());
   }
   
   @Test
   public void testBeforeEach() {
      File file = JUnit5RunUtil.runJUnit5Test(InitializeInBeforeEach.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertFalse(data.getFirstDatacollectorContent().get(0).isError());
   }

   @Test
   public void testCleanCachesByMockMethod() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleCleanCachesByMockMethodTest.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      Assert.assertFalse(data.getFirstDatacollectorContent().get(0).isError());
   }
}
