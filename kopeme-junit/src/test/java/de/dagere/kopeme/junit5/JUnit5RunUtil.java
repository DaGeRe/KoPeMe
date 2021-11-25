package de.dagere.kopeme.junit5;

import java.io.File;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import de.dagere.kopeme.datastorage.FolderProvider;

public class JUnit5RunUtil {
   public static File runJUnit5Test(final Class<?> testedClass) {
      String folder = FolderProvider.getInstance().getFolderFor(testedClass.getCanonicalName());
      File file = new File(folder, "testNormal.xml");
      if (file.exists()) {
         System.out.println("Deleting " + file.getAbsolutePath());
         file.delete();
      }

      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectClass(testedClass))
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
      return file;
   }
}
