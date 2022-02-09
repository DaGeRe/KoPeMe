package de.dagere.kopeme.junit;

import java.io.File;
import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.junit.exampletests.rules.ExampleRuleParameterizedTest;

public class TestParameterized {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @BeforeClass
   public static void initClass() throws IOException {
      TestUtils.cleanAndSetKoPeMeOutputFolder();
   }

   @Test
   public void testNormalWriting() {
      final JUnitCore jc = new JUnitCore();
      jc.run(ExampleRuleParameterizedTest.class);
      final String testClass = ExampleRuleParameterizedTest.class.getName();
      final File file = TestUtils.xmlFileForKoPeMeTest(testClass, "testNormal_0");
      MatcherAssert.assertThat(file, FileMatchers.anExistingFile());

      final File file2 = TestUtils.xmlFileForKoPeMeTest(testClass, "testNormal_1");
      MatcherAssert.assertThat(file2, FileMatchers.anExistingFile());

      final File file3 = TestUtils.xmlFileForKoPeMeTest(testClass, "testNormal_2");
      MatcherAssert.assertThat(file3, FileMatchers.anExistingFile());

   }
}
