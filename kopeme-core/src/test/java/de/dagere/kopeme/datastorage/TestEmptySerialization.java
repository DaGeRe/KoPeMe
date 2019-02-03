package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;

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

      result.setFirst10Percentile(null);
      result.setMin(null);

      final File tempFile = Files.createTempFile("start", "end").toFile();
      System.out.println("File: " + tempFile.getAbsolutePath());
      XMLDataStorer.storeData(tempFile, data);
      
      final List<String> gradleFileContents = Files.readAllLines(tempFile.toPath());
      
      for (final String line : gradleFileContents) {
         System.out.println(line);
      }
      
      Assert.assertThat(gradleFileContents, IsCollectionContaining.hasItem(Matchers.containsString("value")));
      Assert.assertThat(gradleFileContents, IsCollectionContaining.hasItem(Matchers.containsString("deviation")));
      Assert.assertThat(gradleFileContents, Matchers.not(IsCollectionContaining.hasItem(Matchers.containsString("min"))));
      Assert.assertThat(gradleFileContents, Matchers.not(IsCollectionContaining.hasItem(Matchers.containsString("max"))));
      Assert.assertThat(gradleFileContents, Matchers.not(IsCollectionContaining.hasItem(Matchers.containsString("fulldata"))));
   }
}
