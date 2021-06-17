package de.dagere.kopeme.kieker;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.kieker.writer.ChangeableFolderWriter;
import kieker.monitoring.core.controller.MonitoringController;

public class TestKoPeMeKiekerSupport {

   @Test
   public void testThatTheworldisnotSinkingIntoABlockHole() throws Exception {
      MonitoringController.getInstance();
      KoPeMeKiekerSupport.INSTANCE.useKieker(true, "myClass", "myTestCaseName");

      final ChangeableFolderWriter writer = ChangeableFolderWriter.getInstance();
      Assert.assertNotNull(writer);
   }
}
