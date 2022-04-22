package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsIterableContaining;
import org.junit.Test;

import de.dagere.kopeme.datastorage.xml.XMLDataStorer;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Chunk;

public class TestEmptySerialization {

   @Test
   public void testSerialization() throws IOException {
      final TestcaseType testcase = new TestcaseType();

      final Kopemedata data = new Kopemedata();
      data.setTestcases(new Testcases());
      data.getTestcases().getTestcase().add(testcase);

      final Datacollector datacollector = new Datacollector();
      testcase.getDatacollector().add(datacollector);

      final Chunk chunk = new Chunk();
      datacollector.getChunk().add(chunk);

      final Result result = new Result();
      chunk.getResult().add(result);

      result.setMin(null);

      final File tempFile = Files.createTempFile("start", "end").toFile();
      System.out.println("File: " + tempFile.getAbsolutePath());
      XMLDataStorer.storeData(tempFile, data);

      final List<String> gradleFileContents = Files.readAllLines(tempFile.toPath());

      for (final String line : gradleFileContents) {
         System.out.println(line);
      }

      MatcherAssert.assertThat(gradleFileContents, IsIterableContaining.hasItem(Matchers.containsString("value")));
      MatcherAssert.assertThat(gradleFileContents, IsIterableContaining.hasItem(Matchers.containsString("deviation")));
      MatcherAssert.assertThat(gradleFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("min"))));
      MatcherAssert.assertThat(gradleFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("max"))));
      MatcherAssert.assertThat(gradleFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("fulldata"))));
   }
}
