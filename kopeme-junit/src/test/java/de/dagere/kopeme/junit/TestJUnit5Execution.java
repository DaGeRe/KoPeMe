package de.dagere.kopeme.junit;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import de.dagere.kopeme.datastorage.FolderProvider;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtension5Test;

/**
 * Tests just whether JUnit 5 execution works
 * 
 * @author reichelt
 * 
 */
public class TestJUnit5Execution {

   public static Logger log = LogManager.getLogger(TestJUnit5Execution.class);

   @Test
   public void testRegularExecution() throws JAXBException {
      String folder = FolderProvider.getInstance().getFolderFor("de.dagere.kopeme.junit5.exampletests.rules.ExampleRule5Test");
      File file = new File(folder, "testNormal.xml");
      if (file.exists()) {
         System.out.println("Deleting " + file.getAbsolutePath());
         file.delete();
      }

      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectClass(ExampleExtension5Test.class))
            .build();
      Launcher launcher = LauncherFactory.create();
      SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
      launcher.registerTestExecutionListeners(summaryGeneratingListener);
      launcher.execute(request);

      if (summaryGeneratingListener.getSummary().getFailures().size() > 0) {
         Failure failure = summaryGeneratingListener.getSummary().getFailures().get(0);
         failure.getException().printStackTrace();
      }
      MatcherAssert.assertThat(summaryGeneratingListener.getSummary().getFailures(), Matchers.empty());

      Assert.assertTrue(file.exists());

      Kopemedata data = XMLDataLoader.loadData(file);
      double averageDurationInMs = data.getTestcases().getTestcase().get(0).getDatacollector().get(0).getResult().get(0).getValue() / 1000000;
      System.out.println(file.getAbsolutePath() + "=" + averageDurationInMs);

      MatcherAssert.assertThat((int) averageDurationInMs, Matchers.greaterThan(25));
   }
}
