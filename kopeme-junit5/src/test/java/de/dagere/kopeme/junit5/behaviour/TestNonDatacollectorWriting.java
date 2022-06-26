package de.dagere.kopeme.junit5.behaviour;

import java.io.File;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.junit5.JUnit5RunUtil;
import de.dagere.kopeme.junit5.exampletests.behaviour.ExampleExtension5NoneCollectorTest;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;

public class TestNonDatacollectorWriting {

   @Test
   public void testWriting() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtension5NoneCollectorTest.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = JSONDataLoader.loadData(file);
      List<DatacollectorResult> datacollectorResults = data.getMethods().get(0).getDatacollectorResults();
      
      MatcherAssert.assertThat(datacollectorResults, IsIterableWithSize.iterableWithSize(0));
   }
}
