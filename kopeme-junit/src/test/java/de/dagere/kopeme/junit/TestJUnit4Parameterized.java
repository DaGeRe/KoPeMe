package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleParameterizedTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class TestJUnit4Parameterized {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void initClass() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testNormalWriting() throws JAXBException {
      final JUnitCore jc = new JUnitCore();
      jc.run(ExampleRuleParameterizedTest.class);
      final String testClass = ExampleRuleParameterizedTest.class.getName();
      
      for (int i : new int[] {0, 1, 2}) {
         final File file = TestUtils.xmlFileForKoPeMeTest(testClass, "testNormal(JUNIT_PARAMETERIZED-"+i+")");
         MatcherAssert.assertThat(file, FileMatchers.anExistingFile());
         Kopemedata kopemedata = XMLDataLoader.loadData(file);
         
         List<Result> results = kopemedata.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult();
         
         Params params = results.get(0).getParams();
         
         Assert.assertEquals(params.getParam().get(0).getKey(), KoPeMeRule.JUNIT_PARAMETERIZED);
         Assert.assertEquals(params.getParam().get(0).getValue(), Integer.toString(i));
      }
   }
}
