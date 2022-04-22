package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import jakarta.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.datastorage.xml.XMLDataLoader;
import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleParameterizedTest;
import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleParameterizedTestChosenParameter;
import de.dagere.kopeme.junit4.rule.KoPeMeRule;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;

public class TestJUnit4Parameterized {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void initClass() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testParameterized() throws JAXBException {
      final String testClass = ExampleRuleParameterizedTest.class.getName();
      cleanup(testClass);
      
      final JUnitCore jc = new JUnitCore();
      jc.run(ExampleRuleParameterizedTest.class);
      
      
      for (int i : new int[] {0, 1, 2}) {
         checkExistingAndCorrect(testClass, i);
      }
   }
   
   @Test
   public void testParameterizedChosenParameter() throws JAXBException {
      final String testClass = ExampleRuleParameterizedTestChosenParameter.class.getName();
      cleanup(testClass);
      
      final JUnitCore jc = new JUnitCore();
      jc.run(ExampleRuleParameterizedTestChosenParameter.class);
      
      
      checkExistingAndCorrect(testClass, 1);
      
      for (int i : new int[] {0, 2}) {
         final File file = TestUtils.jsonFileForKoPeMeTest(testClass, "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         MatcherAssert.assertThat(file, Matchers.not(FileMatchers.anExistingFile()));
      }
   }

   private void cleanup(final String testClass) {
      for (int i : new int[] {0, 1, 2}) {
         final File file = TestUtils.jsonFileForKoPeMeTest(testClass, "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         file.delete();
      }
   }
   
   private void checkExistingAndCorrect(final String testClass, int i) throws JAXBException {
      final File file = TestUtils.jsonFileForKoPeMeTest(testClass, "testNormal(JUNIT_PARAMETERIZED-"+i+")");
      MatcherAssert.assertThat(file, FileMatchers.anExistingFile());
      Kopemedata kopemedata = JSONDataLoader.loadData(file);
      
      List<VMResult> results = kopemedata.getTestclazzes().get(0).getMethods().get(0).getDatacollectorResults().get(0).getResults();
      
      Entry<String, String> params = results.get(0).getParameters().entrySet().iterator().next();
      
      Assert.assertEquals(params.getKey(), KoPeMeConstants.JUNIT_PARAMETERIZED);
      Assert.assertEquals(params.getValue(), Integer.toString(i));
   }
}
