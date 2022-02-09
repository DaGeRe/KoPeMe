package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
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
import de.dagere.kopeme.generated.Result.Params.Param;
import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleParameterizedTest;
import de.dagere.kopeme.junit.rule.KoPeMeRule;

public class TestParameterized {

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
      final File file = TestUtils.xmlFileForKoPeMeTest(testClass, "testNormal");
      MatcherAssert.assertThat(file, FileMatchers.anExistingFile());

      Kopemedata kopemedata = XMLDataLoader.loadData(file);

      List<Result> results = kopemedata.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult();

      MatcherAssert.assertThat(results, IsIterableWithSize.iterableWithSize(3));
      List<Param> params = results.stream()
            .map(result -> result.getParams().getParam().get(0))
            .collect(Collectors.toList());
      
      Assert.assertEquals(params.get(0).getKey(), KoPeMeRule.JUNIT_PARAMETERIZED);
      Assert.assertEquals(params.get(0).getValue(), "0");
      
      Assert.assertEquals(params.get(1).getKey(), KoPeMeRule.JUNIT_PARAMETERIZED);
      Assert.assertEquals(params.get(1).getValue(), "1");
      
      Assert.assertEquals(params.get(2).getKey(), KoPeMeRule.JUNIT_PARAMETERIZED);
      Assert.assertEquals(params.get(2).getValue(), "2");
   }
}
