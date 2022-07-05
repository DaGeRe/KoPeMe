package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsIterableContaining;
import org.junit.Test;

import de.dagere.kopeme.datacollection.TimeDataCollectorNoGC;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.TestMethod;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.kopeme.kopemedata.VMResultChunk;


public class TestEmptySerialization {

   @Test
   public void testSerialization() throws IOException {
      final Kopemedata data = buildData();

      final File tempFile = Files.createTempFile("start", "end").toFile();
      System.out.println("File: " + tempFile.getAbsolutePath());
      JSONDataStorer.storeData(tempFile, data);

      final List<String> jsonFileContents = Files.readAllLines(tempFile.toPath());

      for (final String line : jsonFileContents) {
         System.out.println(line);
      }

      MatcherAssert.assertThat(jsonFileContents, IsIterableContaining.hasItem(Matchers.containsString("value")));
      MatcherAssert.assertThat(jsonFileContents, IsIterableContaining.hasItem(Matchers.containsString("deviation")));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("min"))));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("max"))));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("fulldata"))));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("parameters"))));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("error"))));
      MatcherAssert.assertThat(jsonFileContents, Matchers.not(IsIterableContaining.hasItem(Matchers.containsString("failure"))));
   }
   
   @Test
   public void testErrorSerialization() throws IOException {
      final Kopemedata data = buildData();
      VMResult vmResult = data.getFirstTimeDataCollector().getChunks().get(0).getResults().get(0);
      vmResult.setError(true);
      vmResult.setFailure(true);
      vmResult.setParameters(new LinkedHashMap<>());
      vmResult.getParameters().put("Test", "Test");

      final File tempFile = Files.createTempFile("start", "end").toFile();
      System.out.println("File: " + tempFile.getAbsolutePath());
      JSONDataStorer.storeData(tempFile, data);

      final List<String> jsonFileContents = Files.readAllLines(tempFile.toPath());

      for (final String line : jsonFileContents) {
         System.out.println(line);
      }

      MatcherAssert.assertThat(jsonFileContents, IsIterableContaining.hasItem(Matchers.containsString("parameters")));
      MatcherAssert.assertThat(jsonFileContents, IsIterableContaining.hasItem(Matchers.containsString("error")));
      MatcherAssert.assertThat(jsonFileContents, IsIterableContaining.hasItem(Matchers.containsString("failure")));
   }

   private Kopemedata buildData() {
      final Kopemedata data = new Kopemedata("de.Test");
      TestMethod testcase = new TestMethod("test");
      data.getMethods().add(testcase);

      final DatacollectorResult datacollector = new DatacollectorResult(TimeDataCollectorNoGC.class.getCanonicalName());
      testcase.getDatacollectorResults().add(datacollector);

      final VMResultChunk chunk = new VMResultChunk();
      datacollector.getChunks().add(chunk);

      final VMResult result = new VMResult();
      chunk.getResults().add(result);

      result.setMin(null);
      return data;
   }
}
