package de.dagere.kopeme.junit5;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.EnvironmentUtil;
import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.junit5.exampletests.ExampleExtension5ParameterizedTest;
import de.dagere.kopeme.junit5.exampletests.ExampleExtension5ParameterizedTestChosenParameter;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;

public class TestJUnit5Parameterized {

   @Test
   public void testParameterizedExecution() {
      for (int i : new int[] { 1, 2 }) {
         final File file = TestUtils.jsonFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-" + i + ")");
         file.delete();
      }

      JUnit5RunUtil.runJUnit5TestOnly(ExampleExtension5ParameterizedTest.class);

      // JUnit 5 starts counting with 1 - whyever
      for (int i : new int[] { 1, 2 }) {
         final File file = TestUtils.jsonFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-" + i + ")");
         MatcherAssert.assertThat(file, FileMatchers.anExistingFile());
         Kopemedata kopemedata = JSONDataLoader.loadData(file);

         List<VMResult> results = kopemedata.getMethods().get(0).getDatacollectorResults().get(0).getResults();

         Entry<String, String> params = results.get(0).getFirstParameter();

         Assert.assertEquals(params.getKey(), KoPeMeConstants.JUNIT_PARAMETERIZED);
         Assert.assertEquals(params.getValue(), Integer.toString(i));
      }
   }

   @Test
   public void testParameterizedExecutionChosenParameter() {
      if (isLinux()) {
         for (File file : new File("/tmp/").listFiles((FileFilter) new WildcardFileFilter("kopeme-*bin"))) {
            file.delete();
         }
      }

      final File file1 = TestUtils.jsonFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-1)");
      final File file2 = TestUtils.jsonFileForKoPeMeTest(ExampleExtension5ParameterizedTest.class.getName(), "testNormal(JUNIT_PARAMETERIZED-2)");
      file1.delete();
      file2.delete();

      JUnit5RunUtil.runJUnit5TestOnly(ExampleExtension5ParameterizedTestChosenParameter.class);

      MatcherAssert.assertThat(file1, Matchers.not(FileMatchers.anExistingFile()));
      MatcherAssert.assertThat(file2, Matchers.not(FileMatchers.anExistingFile()));

      if (isLinux()) {
         Assert.assertEquals(0, new File("/tmp/").listFiles((FileFilter) new WildcardFileFilter("kopeme-*bin")).length);
      }
   }

   public static boolean isLinux() {
      return !System.getProperty("os.name").startsWith("Windows") && !System.getProperty("os.name").startsWith("Mac");
   }
}
