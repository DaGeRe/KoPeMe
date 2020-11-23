package de.dagere.kopeme.kieker;

import org.junit.Assert;
import org.junit.Test;

import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.ChangeableFolderWriter;

public class TestKoPeMeKiekerSupport {

	@Test
	public void testThatTheworldisnotSinkingIntoABlockHole() throws Exception {
		MonitoringController.getInstance();
		KoPeMeKiekerSupport.INSTANCE.useKieker(true, "myClass", "myTestCaseName");
		
		final ChangeableFolderWriter writer = ChangeableFolderWriter.getInstance();
		Assert.assertNotNull(writer);
	}
}
