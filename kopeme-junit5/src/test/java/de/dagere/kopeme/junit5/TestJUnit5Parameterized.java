package de.dagere.kopeme.junit5;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtension5ParameterizedTest;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtension5ParameterizedTestChosenParameter;

public class TestJUnit5Parameterized {
   
   @Test
   public void testParameterizedExecution() throws JAXBException {
      for (int i : new int[] {1, 2}) {
         final File file = TestUtils.xmlFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         file.delete();
      }
      
      JUnit5RunUtil.runJUnit5TestOnly(ExampleExtension5ParameterizedTest.class);
      
      // JUnit 5 starts counting with 1 - whyever
      for (int i : new int[] {1, 2}) {
         final File file = TestUtils.xmlFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         MatcherAssert.assertThat(file, FileMatchers.anExistingFile());
         Kopemedata kopemedata = XMLDataLoader.loadData(file);
         
         List<Result> results = kopemedata.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult();
         
         Params params = results.get(0).getParams();
         
         Assert.assertEquals(params.getParam().get(0).getKey(), KoPeMeConstants.JUNIT_PARAMETERIZED);
         Assert.assertEquals(params.getParam().get(0).getValue(), Integer.toString(i));
      }
   }
   
   @Test
   public void testParameterizedExecutionChosenParameter() throws JAXBException {
      final File file1 = TestUtils.xmlFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-1)");
      final File file2 = TestUtils.xmlFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-2)");
      file1.delete();
      file2.delete();
      
      JUnit5RunUtil.runJUnit5TestOnly(ExampleExtension5ParameterizedTestChosenParameter.class);
      
      
      MatcherAssert.assertThat(file1, Matchers.not(FileMatchers.anExistingFile()));
      MatcherAssert.assertThat(file2, Matchers.not(FileMatchers.anExistingFile()));
   }
}
