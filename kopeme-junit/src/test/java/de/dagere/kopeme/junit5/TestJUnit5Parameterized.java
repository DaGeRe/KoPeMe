package de.dagere.kopeme.junit5;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.junit.rule.KoPeMeRule;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtension5ParameterizedTest;

public class TestJUnit5Parameterized {
   
   @Test
   public void testParameterizedExecution() throws JAXBException {
      JUnit5RunUtil.runJUnit5TestOnly(ExampleExtension5ParameterizedTest.class);
      
      // JUnit 5 starts counting with 1 - whyever
      for (int i : new int[] {1, 2}) {
         final File file = TestUtils.xmlFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         MatcherAssert.assertThat(file, FileMatchers.anExistingFile());
         Kopemedata kopemedata = XMLDataLoader.loadData(file);
         
         List<Result> results = kopemedata.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult();
         
         Params params = results.get(0).getParams();
         
         Assert.assertEquals(params.getParam().get(0).getKey(), KoPeMeRule.JUNIT_PARAMETERIZED);
         Assert.assertEquals(params.getParam().get(0).getValue(), Integer.toString(i));
      }
   }
}
