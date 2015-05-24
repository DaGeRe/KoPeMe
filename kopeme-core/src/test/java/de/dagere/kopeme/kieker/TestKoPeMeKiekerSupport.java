package de.dagere.kopeme.kieker;

import static org.junit.Assert.*;
import kieker.monitoring.core.controller.MonitoringController;

import org.junit.Test;

public class TestKoPeMeKiekerSupport {

	@Test
	public void testThatTheworldisnotSinkingIntoABlockHole() throws Exception {
		MonitoringController.getInstance();
		KoPeMeKiekerSupport.INSTANCE.useKieker(true, "myClass");
	}
}
