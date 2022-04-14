package de.dagere.kopeme.junit5;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtension5Test;

/**
 * Tests just whether JUnit 5 execution works
 * 
 * @author reichelt
 * 
 */
public class TestRegularJUnit5Execution {

   public static Logger log = LogManager.getLogger(TestRegularJUnit5Execution.class);

   @Test
   public void testRegularExecution() throws JAXBException {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtension5Test.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, FileMatchers.anExistingFile());

      Kopemedata data = XMLDataLoader.loadData(file);
      double averageDurationInMs = data.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult().get(0).getValue() / 1000000;
      System.out.println(file.getAbsolutePath() + "=" + averageDurationInMs);

      MatcherAssert.assertThat((int) averageDurationInMs, Matchers.greaterThan(20));
   }

   
}
