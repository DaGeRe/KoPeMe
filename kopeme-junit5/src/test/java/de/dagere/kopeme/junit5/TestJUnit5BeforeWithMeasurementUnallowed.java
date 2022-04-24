package de.dagere.kopeme.junit5;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.junit5.exampletests.rules.ExampleUnallowedBeforeWithMeasurement;

/**
 * Tests just whether JUnit 5 execution works
 * 
 * @author reichelt
 * 
 */
public class TestJUnit5BeforeWithMeasurementUnallowed {

   public static Logger log = LogManager.getLogger(TestJUnit5BeforeWithMeasurementUnallowed.class);

   @Test
   public void testRegularExecution() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleUnallowedBeforeWithMeasurement.class);

      MatcherAssert.assertThat("File " + file.getAbsolutePath() + " did not exist", file, 
            Matchers.not(FileMatchers.anExistingFile()));
   }
   
}
