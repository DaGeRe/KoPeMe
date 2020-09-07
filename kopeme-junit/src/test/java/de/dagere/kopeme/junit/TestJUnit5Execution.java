package de.dagere.kopeme.junit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import de.dagere.kopeme.junit5.exampletests.rules.ExampleRule5Test;

/**
 * Tests just whether JUnit 5 execution works
 * 
 * @author reichelt
 * 
 */
public class TestJUnit5Execution {

   public static Logger log = LogManager.getLogger(TestJUnit5Execution.class);

   @Test
   public void testMethodTimeout() {
      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectClass(ExampleRule5Test.class))
            .build();
      Launcher launcher = LauncherFactory.create();
      launcher.registerTestExecutionListeners(new SummaryGeneratingListener());
      launcher.execute(request);
   }
}
